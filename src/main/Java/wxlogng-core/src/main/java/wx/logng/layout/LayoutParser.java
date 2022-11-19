package wx.logng.layout;

import java.util.function.Consumer;

import com.github.jochenw.afw.core.function.BiIntFunction;
import com.github.jochenw.afw.core.util.Objects;

/** This is a parser for layout specification patterns, which is internally
 * used by the {@link DefaultLayoutFactory}.
 * A typical specification pattern
 * would be "%dt %ll [%pi;%sq;%mi;%ti] %ms%lt".
 * Within the specification pattern, the following id's may be used:
 * <pre>
 *   Id                  Description
 *   ------------------  --------------
 *   %dt                 Date, and time of the log event. The extended
 *   %dt{<datePattern>}  form is used to specify a particular date/time
 *                       datePattern, which must be suitable for
 *                       DateTimeFormatter.ofPattern string. By default
 *                       (without an explicitly specified pattern),
 *                       the format of DateTimeFormatter.ISO_DATE_TIME
 *                       will be used.
 *   %li                 The logger id; generally redundant, because
 *                       all log events within a single file share the
 *                       same logger id.
 *   %ll                 The log events log level.
 *   %pi                 The package id (aka package name), that has
 *                       created the log event.
 *   %si                 The service id (unqualified service name,
 *                       without the folder part) of the service, that
 *                       has created the log event.
 *   %sq                 The qualified service id (qualified service name,
 *                       with the folder part), that has created the log
 *                       event.
 *   %ti                 The thread id (thread name, that has created the
 *                       log event.
 *   %mi                 The message id, if any, or null. In general, a
 *                       message id will only be present for audit
 *                       logging.
 *   %ms                 The actual message.
 *   %lt                 The line terminator. The extended form specifies
 *   %lt{\n}             a specific line terminator, the system default
 *                       will be used for the simple form.
 * </pre>
 * To use the parser, one must create a {@link LayoutParser.Visitor}, and
 * invoke {@link #parse(String, LayoutParser.Visitor)}.
 */
public class LayoutParser {
	public interface Visitor {
		default void lineTerminator(String pPattern) {}
		default void loggerId() {}
		default void logLevel() {}
		default void message() {}
		default void msgId() {}
		default void packageId() {}
		default void plainText(String pText) {}
		default void qServiceId() {}
		default void serviceId() {}
		default void threadId() {}
		default void timestamp(String pPattern) {}
		public static Visitor proxyOf(Visitor pVisitor) {
			final Visitor visitor = Objects.requireNonNull(pVisitor, "Visitor");
			return new Visitor() {
				@Override public void lineTerminator(String pPattern) { visitor.lineTerminator(pPattern); }
				@Override public void loggerId() { visitor.loggerId(); }
				@Override public void logLevel() { visitor.logLevel(); }
				@Override public void message() { visitor.message(); }
				@Override public void msgId() { visitor.msgId(); }
				@Override public void packageId() { visitor.packageId(); }
				@Override public void plainText(String pText) { visitor.plainText(pText); }
				@Override public void qServiceId() { visitor.qServiceId(); }
				@Override public void serviceId() { visitor.serviceId(); }
				@Override public void threadId() { visitor.threadId(); }
				@Override public void timestamp(String pPattern) { visitor.timestamp(pPattern); }
			};
		}
	}

	public void parse(String pPattern, Visitor pVisitor) {
		final String pattern = Objects.requireNonNull(pPattern, "Pattern");
		final Visitor visitor = Objects.requireNonNull(pVisitor, "Visitor");
		int offset = 0;
		final BiIntFunction<String,IllegalArgumentException> error = (off, s) -> {
			return new IllegalArgumentException("Invalid layout specification at offset "
					+ off + ": " + pattern + " (" + s + ")");
		};
		final StringBuilder sb = new StringBuilder();
		final Consumer<Consumer<Visitor>> notifier = (c) -> {
			if (sb.length() > 0) {
				final String text = sb.toString();
				visitor.plainText(text);
				sb.setLength(0);
			}
			if (c != null) {
				c.accept(visitor);
			}
		};
		while (offset < pattern.length()) {
			final char c0 = pattern.charAt(offset++);
			if (c0 =='%') {
				final int tokenIdOffset = offset;
				final char c1;
				final char c2;
				try {
					c1 = pattern.charAt(offset++);
					c2 = pattern.charAt(offset++);
				} catch (StringIndexOutOfBoundsException e) {
					throw error.apply(tokenIdOffset, "Incomplete token id, expected two characters after '%'");
				}
				final String tokenId = "" + c1 + c2;
				String tokenExtension;
				if (offset < pattern.length()  &&  pattern.charAt(offset) == '{') {
					offset++;
					final int extensionStart = offset;
					final int extensionEnd = pattern.indexOf('}', extensionStart);
					if (extensionEnd == -1) {
						throw error.apply(tokenIdOffset, "Unterminated extension string, expected '}' character"); 
					}
					tokenExtension = pattern.substring(extensionStart,extensionEnd);
					offset = extensionEnd+1;
				} else {
					tokenExtension = null;
				}
				final Runnable noTokenExtensionVerifier = () -> {
					if (tokenExtension != null) {
						throw error.apply(tokenIdOffset, "Invalid token extension for token id %" + tokenId);
					}
				};
				switch (tokenId) {
				case "dt":
					notifier.accept((v) -> v.timestamp(tokenExtension));
					break;
				case "li":
					noTokenExtensionVerifier.run();
					notifier.accept((v) -> v.loggerId());
					break;
				case "ll":
					noTokenExtensionVerifier.run();
					notifier.accept((v) -> v.logLevel());
					break;
				case "lt":
					notifier.accept((v) -> v.lineTerminator(tokenExtension));
					break;
				case "pi":
					noTokenExtensionVerifier.run();
					notifier.accept((v) -> v.packageId());
					break;
				case "mi":
					noTokenExtensionVerifier.run();
					notifier.accept((v) -> v.msgId());
					break;
				case "ms":
					noTokenExtensionVerifier.run();
					notifier.accept((v) -> v.message());
					break;
				case "si":
					noTokenExtensionVerifier.run();
					notifier.accept((v) -> v.serviceId());
					break;
				case "sq":
					noTokenExtensionVerifier.run();
					notifier.accept((v) -> v.qServiceId());
					break;
				case "ti":
					noTokenExtensionVerifier.run();
					notifier.accept((v) -> v.threadId());
					break;
				default:
					throw error.apply(tokenIdOffset, "Invalid token id " + tokenId);
				}
			} else {
				sb.append(c0);
			}
		}
		notifier.accept(null);
	}
}
