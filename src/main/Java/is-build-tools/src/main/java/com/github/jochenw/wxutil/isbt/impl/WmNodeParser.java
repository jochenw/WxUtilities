package com.github.jochenw.wxutil.isbt.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class WmNodeParser {
	public abstract static class NamedObject {
		private final Object value;
		private final String name;
		public NamedObject(String pName, Object pValue) {
			name = pName;
			value = pValue;
		}
		public String getName() { return name; }
		public Object getObject() { return value; }
		public abstract Object getValue();
	}
	public static class NamedMap extends NamedObject {
		public NamedMap(String pName) {
			super(pName, new HashMap<>());
		}
		public Map<String,Object> getObject() {
			@SuppressWarnings("unchecked")
			final Map<String,Object> map = (Map<String,Object>) super.getObject();
			return map;
		}
		public Map<String,Object> getValue() { return getObject(); }
	}
	public static class NamedList extends NamedObject {
		public NamedList(String pName) {
			super(pName, new ArrayList<>());
		}
		public List<Object> getObject() {
			@SuppressWarnings("unchecked")
			final List<Object> list = (List<Object>) super.getObject();
			return list;
		}
		public List<Object> getValue() { return getObject(); }
	}
	public static class NamedString extends NamedObject {
		public NamedString(String pName) {
			this(pName, new StringBuilder());
		}
		protected NamedString(String pName, StringBuilder pSb) {
			super(pName, pSb);
		}
		public StringBuilder getObject() { return (StringBuilder) super.getObject(); }
		public String getValue() {
			final StringBuilder sb = getObject();
			if (sb == null) { return null; } else { return sb.toString(); }
		}
	}
	public static class NamedNull extends NamedString {
		public NamedNull(String pName) {
			super(pName, null);
		}
		public StringBuilder getObject() { return null; }
		public String getValue() { return null; }
	}
	public static class Handler implements ContentHandler {
		private int level;
		private Locator locator;
		private List<NamedObject> stack = new ArrayList<>();
		private NamedObject currentElement;
		private Map<String,Object> parserResult;

		protected void addCurrentElement(NamedObject pElement) {
			if (currentElement != null) {
				stack.add(currentElement);
			}
			currentElement = pElement;
		}

		protected void addCurrentElementToParent(NamedObject pCurrentElement) throws SAXException {
			if (stack.isEmpty()) {
				throw new IllegalStateException("No parent element is available");
			}
			final NamedObject parentElement = stack.get(stack.size()-1);
			if (parentElement == null) {
				throw new NullPointerException("Parent element is null.");
			}
			final String name = pCurrentElement.getName();
			if (parentElement instanceof NamedMap) {
				if (name == null) {
					throw new NullPointerException("Name is null for record entry.");
				}
				final NamedMap map = (NamedMap) parentElement;
				if (map.getValue().containsKey(name)) {
					throw error("Duplicate record entry name: " + name);
				}
				map.getValue().put(name, pCurrentElement.getValue());
			} else if (parentElement instanceof NamedList) {
				if (name != null) {
					throw new IllegalStateException("Unexpected element name in array.");
				}
				final NamedList list = (NamedList) parentElement;
				list.getValue().add(pCurrentElement.getValue());
			} else {
				throw new IllegalStateException("Expected parent element to be a map, or array, got " + parentElement.getClass().getName());
			}
			removeCurrentElement();
		}
		
		protected NamedObject removeCurrentElement() {
			NamedObject element;
			if (stack.isEmpty()) {
				element = null;
			} else {
				element = stack.remove(stack.size()-1);
			}
			final NamedObject result = currentElement;
			currentElement = element;
			return result;
		}
		
		@Override
		public void setDocumentLocator(Locator pLocator) {
			locator = pLocator;
		}

		@Override
		public void startDocument() throws SAXException {
			stack.clear();
			level = 0;
			currentElement = null;
		}

		protected SAXParseException error(String pMsg) {
			return new SAXParseException(pMsg, locator);
		}
		protected SAXParseException error(String pMsg, Throwable pCause) {
			final SAXParseException spe = new SAXParseException(pMsg, locator);
			spe.initCause(pCause);
			return spe;
		}
		protected String qName(String pUri, String pLocalName) {
			if (pUri == null  ||  pUri.length() == 0) {
				return pLocalName;
			} else {
				return "{" + pUri + "}" + pLocalName;
			}
		}
	
		@Override
		public void endDocument() throws SAXException {
			if (level != 0) {
				throw error("endDocument: Expected level=0, got level=" + level);
			}
			if (!stack.isEmpty()) {
				throw error("endDocument: Expected empty stack, got stack.size=" + stack.size());
			}
		}

		@Override
		public void startPrefixMapping(String pPrefix, String pUri) throws SAXException {
			// Do nothing.
		}

		@Override
		public void endPrefixMapping(String pPrefix) throws SAXException {
			// Do nothing.
		}

		@Override
		public void startElement(String pUri, String pLocalName, String pQName, Attributes pAttrs) throws SAXException {
			final int lvl = level++;
			if (pUri != null  &&  pUri.length() > 0) {
				throw error("Expected default namespace, got " + pUri + " at level " + lvl + ", " + qName(pUri, pLocalName));
			}
			if (lvl == 0) {
				switch (pLocalName) {
				case "Values":
					addCurrentElement(new NamedMap(null));
					break;
				default:
					throw error("Expected root element 'Values' got " + qName(pUri, pLocalName));
					
				}
			} else {
				final String name = pAttrs.getValue("name");
				switch (pLocalName) {
				case "value":
					addCurrentElement(new NamedString(name));
					break;
				case "null":
					addCurrentElement(new NamedNull(name));
					break;
				case "record":
					addCurrentElement(new NamedMap(name));
					break;
				case "array":
					addCurrentElement(new NamedList(name));
					break;
				default:
					throw error("Expected value|record|array element at level " + lvl + ", got " + qName(pUri, pLocalName));
				}
			}
		}

		@Override
		public void endElement(String pUri, String pLocalName, String pQName) throws SAXException {
			final int lvl = --level;
			if (pUri != null  &&  pUri.length() > 0) {
				throw error("Expected default namespace, got " + pUri + " at level " + lvl + ", " + qName(pUri, pLocalName));
			}
			if (lvl == 0) {
				@SuppressWarnings("unchecked")
				final NamedMap result = (NamedMap) removeCurrentElement();
				parserResult = result.getObject();
			} else {
				switch (pLocalName) {
				case "value":
					final NamedString sb = (NamedString) currentElement;
					addCurrentElementToParent(sb);
					break;
				case "null":
					final NamedNull nullSb = (NamedNull) currentElement;
					addCurrentElementToParent(nullSb);
					break;
				case "record":
					final NamedMap map = (NamedMap) currentElement;
					addCurrentElementToParent(map);
					break;
				case "array":
					final NamedList list = (NamedList) currentElement;
					addCurrentElementToParent(list);
					break;
				default:
					throw error("Expected value|record|array element at level " + lvl + ", got " + qName(pUri, pLocalName));
				}
			}
		}

		@Override
		public void characters(char[] pChars, int pOffset, int pLength) throws SAXException {
			if (currentElement != null  &&  currentElement instanceof NamedString) {
				final StringBuilder sb = ((NamedString) currentElement).getObject();
				if (sb != null) {
					sb.append(pChars, pOffset, pLength);
				}
			}
		}

		@Override
		public void ignorableWhitespace(char[] pChars, int pOffset, int pLength) throws SAXException {
			if (currentElement != null  &&  currentElement instanceof NamedString) {
				final StringBuilder sb = ((NamedString) currentElement).getObject();
				if (sb != null) {
					sb.append(pChars, pOffset, pLength);
				}
			}
		}

		@Override
		public void processingInstruction(String pTarget, String pData) throws SAXException {
			throw error("Unexpected processing instruction, target=" + pTarget
					+ ", data=" + pData);
		}

		@Override
		public void skippedEntity(String pName) throws SAXException {
			throw error("Unexpected skipped entity, name=" + pName);
		}
	}

	public static Map<String,Object> parse(InputSource pInputSource) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			spf.setValidating(false);
			final XMLReader xr = spf.newSAXParser().getXMLReader();
			final Handler handler = new Handler();
			xr.setContentHandler(handler);
			xr.parse(pInputSource);
			if (handler.parserResult == null) {
				throw new IllegalStateException("Parser result is not available.");
			}
			return handler.parserResult;
		} catch (ParserConfigurationException pce) {
			throw new UndeclaredThrowableException(pce);
		} catch (SAXException se) {
			throw new UndeclaredThrowableException(se);
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

	public static Map<String,Object> parse(Path pFile) {
		try (InputStream is = Files.newInputStream(pFile)) {
			final InputSource iSource = new InputSource(is);
			iSource.setSystemId(pFile.toString());
			return parse(iSource);
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}
}
