package com.github.jochenw.wxutils.logng.impl;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;

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
