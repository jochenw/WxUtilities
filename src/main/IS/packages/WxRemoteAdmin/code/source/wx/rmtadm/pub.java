package wx.rmtadm;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.wm.app.b2b.server.Server;
import com.wm.util.ServerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import com.github.jochenw.afw.core.data.Data;
import com.github.jochenw.afw.core.util.FileUtils;
import com.softwareag.util.IDataMap;
// --- <<IS-END-IMPORTS>> ---

public final class pub

{
	// ---( internal utility methods )---

	final static pub _instance = new pub();

	static pub _newInstance() { return new pub(); }

	static pub _cast(Object o) { return (pub)o; }

	// ---( server methods )---




	public static final void editConfigFile (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(editConfigFile)>> ---
		// @sigtype java 3.5
		// [i] field:0:required fileName
		// [i] field:0:required destFileName
		// [i] field:0:required text
		// [i] field:0:required format {"properties","xml"}
		// [i] field:0:required createNew {"false","true"}
		// [o] field:0:required text
		// [o] field:0:required format {"properties","xml"}
		final IDataMap pipe = new IDataMap(pipeline);
		editConfigFileForPackage("WxRemoteAdmin", pipe);
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	
	private static void editConfigFileForPackage(String pPackage, IDataMap pipe) throws ServiceException {
		final Path instanceDir = Server.getHomeDir().toPath();
		try {
			final String fileName = Data.MAP_ACCESSOR.requireString(pipe, "fileName");
			final Path defaultConfigDir = Paths.get("./packages/" + pPackage + "/config");
			final Path localConfigDir = Paths.get("./config/packages/" + pPackage);
			final Path filePath = assertFileInDirs(instanceDir, fileName, defaultConfigDir, localConfigDir);
			final String userText = pipe.getAsString("text");
			final String text;
			if (userText == null) {
				// Open an existing file.
				if (!Files.isRegularFile(filePath)) {
					final boolean createNew = Data.MAP_ACCESSOR.getBoolean(pipe, "createNew").booleanValue();
					if (!createNew) {
						throw new IllegalArgumentException("Invalid value for parameter fileName: "
								+ " Expected existing file, and createNew is false, got " + fileName);
					} else {
						text = "";
					}
				} else {
					text = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
				}
			} else {
				text = userText;
				final String format = Data.MAP_ACCESSOR.getString(pipe, "format");
				if (format == null  ||  "".equals(format)) {
					// No validation.
				} else {
					final byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
					if ("properties".equals(format)) {
						final Properties props = new Properties();
						try (InputStream in = new ByteArrayInputStream(bytes)) {
							try {
								props.load(in);
							} catch (IOException e) {
								throw new IllegalArgumentException("Invalid value for parameter text: "
										+ "Expected valid property file, got error " + e.getMessage());
							}
						}
					} else if ("xml".contentEquals(format)) {
						final SAXParserFactory spf = SAXParserFactory.newInstance();
						spf.setValidating(false);
						spf.setNamespaceAware(true);
						final ContentHandler ch = new XMLFilterImpl();
						final XMLReader xr = spf.newSAXParser().getXMLReader();
						xr.setContentHandler(ch);
						try {
							xr.parse(new InputSource(new ByteArrayInputStream(bytes)));
						} catch (SAXException se) {
							throw new IllegalArgumentException("Invalid value for parameter text: "
									+ "Expected valid XML file, got error " + se.getMessage());
						}
					}
				}
			}
			final String destFileName = Data.MAP_ACCESSOR.getString(pipe, "destFileName");
			final Path destFilePath;
			if (destFileName == null  ||  destFileName.length() == 0) {
				destFilePath = filePath;
			} else {
				destFilePath = assertFileInDirs(instanceDir, destFileName, defaultConfigDir, localConfigDir);
			}
			final String formatFileName = destFilePath.getFileName().toString();
			final String destFormat;
			if (formatFileName.endsWith(".xml")) {
				destFormat = "xml";
			} else if (formatFileName.endsWith(".properties")) {
				destFormat = "properties";
			} else {
				destFormat = "";
			}
			
			FileUtils.createDirectoryFor(destFilePath);
			try (OutputStream out = Files.newOutputStream(destFilePath)) {
				out.write(text.getBytes(StandardCharsets.UTF_8));
			}
			pipe.put("text", text);
			pipe.put("format", destFormat);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	
	}
	
	private static Path assertFileInDirs(Path pInstanceDir, String pFileName, Path... pValidDirs) {
		// Security: Make sure, that noone is tricking us into editing
		// other files by using a fileName like "../../../../etc/passwd".
		final Path filePath = pInstanceDir.resolve(pFileName);
		boolean valid = false;
		for (Path dir : pValidDirs) {
			if (FileUtils.isWithin(dir, filePath)) {
				valid = true;
			}
		}
		if (!valid) {
			throw new IllegalArgumentException("Invalid file name: "
					+ " Expected path within a valid directory, got "
					+ pFileName + " -> " + filePath.toAbsolutePath());
		}
		return filePath;
	}
	// --- <<IS-END-SHARED>> ---
}

