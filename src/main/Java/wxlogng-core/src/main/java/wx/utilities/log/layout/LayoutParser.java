package wx.utilities.log.layout;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.annotation.Nonnull;

import com.github.jochenw.afw.core.util.MutableBoolean;

/** This class is parsing a layout specification string, like
 * <pre>
 *   "%dt %lv [%ti;%pi;%si;%mi] %ms%lt"
 * </pre>
 * and notifies a {@link LayoutParser.Listener listener}
 * for every pattern, that it has detected. Valid patterns include:
 * <table>
 *   <thead>
 *    <tr>
 *      <th>Pattern</th>
 *      <th>Listener method</th>
 *      <th>Description</th>
 *    </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <td>%dt, or %dt{date/time pattern}</td>
 *       <td>{@link LayoutParser.Listener#dateTime(datePattern)}</td>
 *       <td>The date/time string. The notification method will receive the date/time pattern,
 *         if that is specified, or null, in which case {@link DateTimeFormatter#ISO_DATE_TIME} will be used as the default.
 *         The date/time pattern <em>must</em> be a pattern, which is suitable for {@link DateTimeFormatter#ofPattern(String)}.</td>
 *     </tr>
 *     <tr>
 *       <td>%li</td>
 *       <td>{@link LayoutParser.Listener#loggerId()}</td>
 *       <td>The logger id.</td>
 *     </tr>
 *     <tr>
 *       <td>%ll</td>
 *       <td>{@link LayoutParser.Listener#logLevel()}</td>
 *       <td>The log level.</td>
 *     </tr>
 *     <tr>
 *       <td>%lt, or %lt{terminatorString}</td>
 *       <td>{@link LayoutParser.Listener#lineTerminator(String)}</td>
 *       <td>The line terminator, that concludes the logging line.
 *         This pattern, if used, must itself terminate the layout specification string.
 *         If no line terminator is used, then the systems default line terminator
 *         will be used, and the notification method will receive a null argument.
 *       <td>
 *     </tr>
 *     <tr>
 *       <td>%mi</td>
 *       <td>{@link LayoutParser.Listener#messageId()}</td>
 *       <td>The message id, if any, or the empty string.</td>
 *     </tr>
 *     <tr>
 *       <td>%ms</td>
 *       <td>{@link LayoutParser.Listener#message()}</td>
 *       <td>The message string, that the application (WxLogNg user) is logging.</td>
 *     </tr>
 *     <tr>
 *       <td>%pi</td>
 *       <td>{@link LayoutParser.Listener#packageId()}</td>
 *       <td>The package name.</td>
 *     </tr>
 *     <tr>
 *       <td>%si</td>
 *       <td>{@link LayoutParser.Listener#serviceId()}</td>
 *       <td>The service id. (Fully qualified service name.)</td>
 *     </tr>
 *     <tr>
 *       <td>%sn</td>
 *       <td>{@link LayoutParser.Listener#serviceName()}</td>
 *       <td>The service name. (With the namespace removed, so only the part after the ':'.)</td>
 *     </tr>
 *     <tr>
 *       <td>%ti</td>
 *       <td>{@link LayoutParser.Listener#threadId()}</td>
 *       <td>The thread id (the value, which is returned by {@link Thread#getName() Thread.currentThread().getName()}.</td>
 *     </tr>
 *   </tbody>
 * </table>
 */
public class LayoutParser {
	public interface Listener {
		public default void plainText(String pText) {}
		public default void message() {}
		public default void lineTerminator() { lineTerminator(null); }
		public default void loggerId() {}
		public default void messageId() {}
		public default void threadId() {}
		public default void packageId() {}
		public default void serviceId() {}
		public default void serviceName() {}
		public default void logLevel() {}
		public default void lineTerminator(String pTerminator) {}
		public default void dateTime(String pPattern) {}
		public default void dateTime() { dateTime(null); }
	}

	public void parse(@Nonnull String pLayoutSpecification, @Nonnull Listener pListener) {
		final String layoutSpecification = Objects.requireNonNull(pLayoutSpecification, "Layout Specification");
		final MutableBoolean lineTerminatorSeen = new MutableBoolean();
		final MutableBoolean lastPatternIsLineTerminator = new MutableBoolean();
		final Listener l = Objects.requireNonNull(pListener, "Listener");
		final Listener listener = new Listener() {
			@Override
			public void plainText(String pText) {
				lastPatternIsLineTerminator.setValue(false);
				l.plainText(pText);
			}

			@Override
			public void message() {
				lastPatternIsLineTerminator.setValue(false);
				l.message();
			}

			@Override
			public void loggerId() {
				lastPatternIsLineTerminator.setValue(false);
				l.loggerId();
			}

			@Override
			public void messageId() {
				lastPatternIsLineTerminator.setValue(false);
				l.messageId();
			}

			@Override
			public void threadId() {
				lastPatternIsLineTerminator.setValue(false);
				l.threadId();
			}

			@Override
			public void packageId() {
				lastPatternIsLineTerminator.setValue(false);
				l.packageId();
			}

			@Override
			public void serviceId() {
				lastPatternIsLineTerminator.setValue(false);
				l.serviceId();
			}

			@Override
			public void serviceName() {
				lastPatternIsLineTerminator.setValue(false);
				l.serviceName();
			}

			@Override
			public void logLevel() {
				lastPatternIsLineTerminator.setValue(false);
				l.logLevel();
			}

			@Override
			public void lineTerminator(String pTerminator) {
				lastPatternIsLineTerminator.setValue(true);
				lineTerminatorSeen.setValue(true);
				l.lineTerminator(pTerminator);
			}

			@Override
			public void dateTime(String pPattern) {
				lastPatternIsLineTerminator.setValue(false);
				l.dateTime(pPattern);
			}
		};
		int index = 0;
		if (layoutSpecification.length() == 0) {
			throw new IllegalArgumentException("The layout specification is empty.");
		}
		StringBuilder plainText = new StringBuilder();
		final Runnable plainTextNotifier = () -> {
			if (plainText.length() > 0) {
				listener.plainText(plainText.toString());
				plainText.setLength(0);
			}
		};
		while (index < layoutSpecification.length()) {
			final char c = layoutSpecification.charAt(index++);
			if (c == '%') {
				if (index+2 > layoutSpecification.length()) {
					throw new IllegalArgumentException("Incomplete message pattern at offset " + (index-1)); 
				} else {
					final int messagePatternIndex = index;
					final String messagePattern = layoutSpecification.substring(index, index+2);
					index += 2;
					String messagePatternInfo = null;
					if (index < layoutSpecification.length()  &&  layoutSpecification.charAt(index) == '{') {
						StringBuilder messagePatternInfoSb = new StringBuilder();
						++index;
						while (index < layoutSpecification.length()) {
							final char ch = layoutSpecification.charAt(index++);
							if (ch == '}') {
								messagePatternInfo = messagePatternInfoSb.toString();
								break;
							} else {
								messagePatternInfoSb.append(ch);
							}
						}
						if (messagePatternInfo == null) {
							throw new IllegalArgumentException("Unterminated message pattern at offset " + (messagePatternIndex-1)
									+ ": " + layoutSpecification);
						}
					}
					final String messagePatternParameter = messagePatternInfo;
					final Runnable messagePatternInfoValidator = () -> {
						if (messagePatternParameter != null) {
							throw new IllegalArgumentException("Invalid layout specification at offset " + (messagePatternIndex-1)
									+ ": The layout pattern " + messagePattern + " doesn't permit a parameter: "
									+ layoutSpecification);
						}
					};
					switch (messagePattern) {
					case "dt":
						plainTextNotifier.run();
						listener.dateTime(messagePatternInfo);
						break;
					case "li":
						messagePatternInfoValidator.run();
						plainTextNotifier.run();
						listener.loggerId();
						break;
					case "ll":
						messagePatternInfoValidator.run();
						plainTextNotifier.run();
						listener.logLevel();
						break;
					case "lt":
						plainTextNotifier.run();
						pListener.lineTerminator(messagePatternInfo);
						break;
					case "mi":
						messagePatternInfoValidator.run();
						plainTextNotifier.run();
						pListener.messageId();
						break;
					case "ms":
						messagePatternInfoValidator.run();
						plainTextNotifier.run();
						pListener.message();
						break;
					case "pi":
						messagePatternInfoValidator.run();
						plainTextNotifier.run();
						pListener.packageId();
						break;
					case "si":
						messagePatternInfoValidator.run();
						plainTextNotifier.run();
						pListener.serviceId();
						break;
					case "sn":
						messagePatternInfoValidator.run();
						plainTextNotifier.run();
						pListener.serviceName();
						break;
					case "ti":
						messagePatternInfoValidator.run();
						plainTextNotifier.run();
						pListener.threadId();
						break;
					default:
						throw new IllegalArgumentException("Unknown layout pattern at offset " + (messagePatternIndex-1)
								+ ": " + layoutSpecification);
					}
				}
			} else {
				plainText.append(c);
			}
		}
	}
}
