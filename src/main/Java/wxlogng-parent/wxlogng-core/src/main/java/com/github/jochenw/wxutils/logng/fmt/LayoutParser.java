package com.github.jochenw.wxutils.logng.fmt;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.util.function.IntSupplier;

import com.github.jochenw.afw.core.util.MutableBoolean;
import com.github.jochenw.afw.core.util.MutableInteger;
import com.github.jochenw.afw.core.util.Objects;
import com.github.jochenw.afw.di.util.Exceptions;
import com.github.jochenw.wxutis.logng.api.ILogEvent;

/** <p>The layout parser reads a layout definition string, like
 * "&percnt;dt{yyyy-MM-dd HH:mm:ss.SSS} &percnt;lv &percnt;tn &percnt;ms",
 * parses it, and converts it into a stream of events, that can be used
 * to build a parser, or formatter.</p>
 * <p>A layout string is a basic text string, which may contain the
 * following tokens:
 * <table>
 *   <tr>
 *     <th>Token</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <th>&percnt;dt, &percnt;dt{yyyy-MM-dd HH:mm:ss.SSS}, &percnt;dt{ISO_ZONED_DATE_TIME}</th>
 *     <td>The {@link ILogEvent#getDateTime() date, and time} of the
 *       logging event in the given format:
 *       <ul>
 *         <li>The format may be omitted (plain %dt), in which case
 *           {@link DateTimeFormatter#ISO_DATE_TIME} formatting will
 *           be used.</li>
 *         <li>The format may include a format pattern, like
 *           "yyyy-MM-dd HH:mm:ss.SSS", in which case
 *           {@link DateTimeFormatter#ofPattern(String)} will
 *           be used to create a formatter. Example:
 *           %dt{yyyy-MM-dd HH:mm:ss.SSS}</li>
 *         <li>The format may include the name of a formatter constant,
 *           like {@link DateTimeFormatter#ISO_ZONED_DATE_TIME}, or
 *           {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}, in which
 *           case the given formatter will be used. Example:
 *           %dt{ISO_ZONED_DATE_TIME}</li>
 *       </ul>
 *     </td>
 *   </tr>
 *   <tr>
 *     <th>&percnt;li</th>
 *     <td>The {@link ILogEvent#getLoggerId() logger id}, which is being
 *       used to reference a log file in the logging services.</td>
 *   </tr>
 *   <tr>
 *     <th>&percnt;lv</th>
 *     <td>The {@link ILogEvent#getLevel() log level}, a string like
 *       "trace", "debug", "info", "warn" or "error". (Without the
 *       quotes.)</td>
 *   </tr>
 *   <tr>
 *     <th>&percnt;pi</th>
 *     <td>The {@link ILogEvent#getPkgId() package id} (package name).</td>
 *   </tr>
 *   <tr>
 *     <th>&percnt;si</th>
 *     <td>The {@link ILogEvent#getSvcId() service id} (unqualified
 *       service name, excluding the namespace).</td>
 *   </tr>
 *   <tr>
 *     <th>&percnt;sq</th>
 *     <td>The {@link ILogEvent#getQSvcId() qualified service id} (qualified service name, including
 *       the namespace).</td>
 *   </tr>
 *   <tr>
 *     <th>&percnt;ti</th>
 *     <td>The thread id (thread name).</td>
 *   </tr>
 *   <tr>
 *     <th>&percnt;ms</th>
 *     <td>The log message. A valid layout string <em>must</em> end
 *       with this token.</td>
 *   </tr>
 * <table></p>
 */
public class LayoutParser {
	/** An Exception, which is being thrown by {@link LayoutParser#parse}
	 */
	public static class InvalidLayoutException extends IllegalArgumentException {
		private static final long serialVersionUID = -8436665838971266721L;
		private final String uri, layout;
		private final int lineNumber, colNumber;

		public InvalidLayoutException(PCtx pCtx, String pLayout, String pMessage) {
			super(pMessage);
			layout = pLayout;
			uri = pCtx.getUri();
			lineNumber = pCtx.getLineNumber();
			colNumber = pCtx.getColumnNumber();
		}

		public String getLayout() { return layout; }
		public String getUri() { return uri; }
		public int getLineNumber() { return lineNumber; }
		public int getColNumber() { return colNumber; }
	}

	/** A context object, which is being supplied as the first parameter of
	 * the {@link LayoutParser.Listener} methods.
	 */
	public interface PCtx {
		String getUri();
		int getLineNumber();
		int getColumnNumber();
	}
	public interface Listener {
		public default void dateTime(PCtx pCtx, DateTimeFormatter pFormat) {}
		public default void dateTime(PCtx pCtx, String pFormat) {
			final DateTimeFormatter dtf;
			try {
				dtf = LayoutParser.asDateTimeFormat(pFormat);
			} catch (IllegalArgumentException e) {
				final StringBuilder sb = new StringBuilder();
				String sep = "At ";
				if (pCtx.getUri() != null  &&  pCtx.getUri().length() != 0) {
					sb.append(sep);
					sb.append(pCtx.getUri());
					sep = ", ";
				}
				if (pCtx.getLineNumber() != -1) {
					sb.append(sep);
					sb.append(" line ");
					sb.append(pCtx.getLineNumber());
					sep = ", ";
				}
				if (pCtx.getColumnNumber() != -1) {
					sb.append(sep);
					sb.append(" column ");
					sb.append(pCtx.getColumnNumber());
					sep = ", ";
				}
				if ("At ".equals(sep)) {
					throw e;
				} else {
					sb.append(" of layout string: ");
					sb.append(e.getMessage());
					throw new IllegalArgumentException(sb.toString(), e);
				}
			}
			dateTime(pCtx, dtf);
		}
		public default void loggerId(PCtx pCtx) {}
		public default void logLevel(PCtx pCtx) {}
		public default void pkgId(PCtx pCtx) {}
		public default void svcId(PCtx pCtx) {}
		public default void qSvcId(PCtx pCtx) {}
		public default void threadId(PCtx pCtx) {}
		public default void msg(PCtx pCtx) {}
		public default void literal(PCtx pCtx, String pLiteral) {}
	}

	public void parse(String pLayout, Listener pListener, String pUri) {
		final String layoutStr = Objects.requireNonNull(pLayout, "Layout String");
		final Listener lstnr = Objects.requireNonNull(pListener, "Listener");
		final MutableBoolean lastTokenIsMessage = new MutableBoolean();
		final Listener listener = new Listener() {
			@Override
			public void dateTime(PCtx pCtx, DateTimeFormatter pFormat) {
				lastTokenIsMessage.unset();
				lstnr.dateTime(pCtx, pFormat);
			}

			@Override
			public void dateTime(PCtx pCtx, String pFormat) {
				lastTokenIsMessage.unset();
				lstnr.dateTime(pCtx, pFormat);
			}

			@Override
			public void loggerId(PCtx pCtx) {
				lastTokenIsMessage.unset();
				lstnr.loggerId(pCtx);
			}

			@Override
			public void logLevel(PCtx pCtx) {
				lastTokenIsMessage.unset();
				lstnr.logLevel(pCtx);
			}

			@Override
			public void pkgId(PCtx pCtx) {
				lastTokenIsMessage.unset();
				lstnr.pkgId(pCtx);
			}

			@Override
			public void svcId(PCtx pCtx) {
				lastTokenIsMessage.unset();
				lstnr.svcId(pCtx);
			}

			@Override
			public void qSvcId(PCtx pCtx) {
				lastTokenIsMessage.unset();
				lstnr.qSvcId(pCtx);
			}

			@Override
			public void threadId(PCtx pCtx) {
				lastTokenIsMessage.unset();
				lstnr.threadId(pCtx);
			}

			@Override
			public void msg(PCtx pCtx) {
				lastTokenIsMessage.set();
				lstnr.msg(pCtx);
			}

			@Override
			public void literal(PCtx pCtx, String pLiteral) {
				// TODO Auto-generated method stub
				lstnr.literal(pCtx, pLiteral);
			}
		};
		final MutableInteger colNumber = new MutableInteger();
		colNumber.setValue(0);
		final PCtx pCtx = new PCtx() {
			@Override
			public String getUri() { return pUri; }
			@Override
			public int getLineNumber() { return 1; }
			@Override
			public int getColumnNumber() { return colNumber.getValue(); }
		};
		StringBuilder sbLiteral = new StringBuilder();
		final Runnable sbLiteralHandler = () -> {
			if (sbLiteral.length() > 0) {
				listener.literal(pCtx, sbLiteral.toString());
				sbLiteral.setLength(0);
			}
		};
		final IntSupplier charSupplier = () -> {
			final int index = colNumber.inc();
			if (layoutStr.length() > index-1) {
				final int c = (int) layoutStr.charAt(index-1);
				if (c == 10) {
					throw new InvalidLayoutException(pCtx, layoutStr,
							"Invalid LineFeed character (0xa) in layout string.");
				} else if (c == 13) {
					throw new InvalidLayoutException(pCtx, layoutStr,
							"Invalid CarriageReturn character (0xd) in layout string.");
				}
				return c;
			} else {
				return -1;
			}
		};

		for (;;) {
			final int c1 = charSupplier.getAsInt();
			if (c1 == '%') {
				final char tokenChar1, tokenChar2;
				final int c2 = charSupplier.getAsInt();
				if (Character.isAlphabetic(c2)) {
					tokenChar1 = (char) c2;
				} else {
					throw new InvalidLayoutException(pCtx, pLayout,
							"Incomplete token, expected "
							+ "dt|li|lv|pi|si|sq|ti|ms after '%' character.");
				}
				final int c3 = charSupplier.getAsInt();
				if (Character.isAlphabetic(c3)) {
					tokenChar2 = (char) c3;
				} else {
					throw new InvalidLayoutException(pCtx, pLayout,
							"Incomplete token, expected "
									+ "dt|li|lv|pi|si|sq|ti|ms after '%' character.");
				}
				final String token = "" + tokenChar1 + tokenChar2;
				String details = null;
				final int c4 = charSupplier.getAsInt();
				if (c4 == '{') {
					final StringBuilder detailsSb = new StringBuilder();
					for (;;) {
						final int c5 = charSupplier.getAsInt();
						if (c5 == '}') {
							break;
						} else if (c5 == -1) {
							throw new InvalidLayoutException(pCtx, pLayout,
									"Incomplete token, expected "
									+ "'}' character after '}' character.");
						} else {
							detailsSb.append((char) c5);
						}
					}
					details = detailsSb.toString();
				} else if (c4 == -1) {
					details = null;
				} else {
					// Let this character be processed in the next
					// iteration of the loop.
					colNumber.dec();
				}
				sbLiteralHandler.run();
				if (!"dt".equals(token)
				    &&  details != null
				    &&  details.length() > 0) {
					throw new InvalidLayoutException(pCtx, pLayout,
							"Unexpected detail string ('{"
							+ details + "}' after token "
							+ token);
				}
				// dt|li|lv|pi|si|sq|ti|ms
				switch (token) {
				case "dt":
					listener.dateTime(pCtx, details);
					break;
				case "li":
					listener.loggerId(pCtx);
					break;
				case "lv":
					listener.logLevel(pCtx);
					break;
				case "pi":
					listener.pkgId(pCtx);
					break;
				case "si":
					listener.svcId(pCtx);
					break;
				case "sq":
					listener.qSvcId(pCtx);
					break;
				case "ti":
					listener.threadId(pCtx);
					break;
				case "ms":
					listener.msg(pCtx);
					break;
				default:
					throw new InvalidLayoutException(pCtx, pLayout,
							"Unknown token reference '%" + token
							+ " expected dt|li|lv|pi|si|sq|ti|ms"
							+ " after '%'");
				}
			} else if (c1 == -1) {
				break;
			} else {
				sbLiteral.append((char) c1);
			}
		}
		if (!lastTokenIsMessage.isSet()) {
			throw new InvalidLayoutException(pCtx, pLayout,
					"Layout must end with %ms");
		}
	}
	
	public static DateTimeFormatter asDateTimeFormat(String pFormat) {
		if (pFormat == null  ||  pFormat.length() == 0) {
			return DateTimeFormatter.ISO_DATE_TIME;
		} else {
			Field field;
			try {
				field = DateTimeFormatter.class.getField(pFormat);
			} catch (NoSuchFieldException e) {
				field = null;
			}
			if (field != null) {
				final MethodHandle mh;
				try {
					final Lookup lookup = MethodHandles.publicLookup();
					mh = lookup.unreflectGetter(field);
					return (DateTimeFormatter) mh.invokeWithArguments();
				} catch (Throwable t) {
					throw Exceptions.show(t);
				}
			}
			try {
				return DateTimeFormatter.ofPattern(pFormat);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid date/time format %dt{"
			         + pFormat + "} in layout string.");
			}
		}
	}
}
