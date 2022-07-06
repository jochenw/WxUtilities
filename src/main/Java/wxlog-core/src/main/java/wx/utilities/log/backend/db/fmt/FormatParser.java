package wx.utilities.log.backend.db.fmt;

import java.time.format.DateTimeFormatter;

import com.github.jochenw.afw.core.util.Objects;

/** A parser for format patterns, like
 * "%dt [%lv] %pi:%sq %ms%lt". In summary, the format pattern consists of pattern identifiers, like
 * %dt, %lv, %pi, and so on. These patterns may be interspersed with plain text snippets. Available
 * pattern identifers are
 * <dl>
 *   <dt>%dt</dt>
 *   <dd>Time, and date, when the message was logged. An extended version of this would be
 *     {@code {%dt\{yyyy-MM-dd HH:mm:ss.nnnnnnnnn\}}, where the part between the curly
 *     braces would be a valid format pattern for a {@link {@link DateTimeFormatter}}.
 *     By default (if the curly braces part is missing),
 *     {@link {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}} will be used.
 *   </dd>
 *   <dt>%lv</dt>
 *   <dd>The log level.</dd>
 *   <dt>%ti</dt>
 *   <dd>The thread id.</dd>
 *   <dt>%pi</dt>
 *   <dd>The package id/name.</dd>
 *   <dt>%si</dt>
 *   <dd>The service id (Unqualified service name, aka service name without the folder part).</dd>
 *   <dt>%sq</dt>
 *   <dd>The qualified service id (Full service name, aka service name with the folder part).</dd>
 *   <dt>%ms</dt>
 *   <dd>The log message</dd>
 *   <dt>%lt</dt>
 *   <dd>The line terminator. An extended version of this would be {@code %lt\{\\r\\n\}}, or
 *     {@code %lt\{\\n\\}}, where the line terminator is explicitly given as CRLF, or LF, respectively.
 *     By default, the systems default line terminator will be used, if the curly braces part is
 *     absent.</dd>
 *   <dt>%%</dt>
 *   <dd>The character '%' itself.</dd>
 * </dl
 */
public class FormatParser {
	public interface Visitor {
		public void plainText(String pText);
		public void dateTime(String pPattern);
		public void logLevel();
		public void threadId();
		public void packageId();
		public void serviceId();
		public void serviceIdQ();
		public void message();
		public void messageId();
		public void lineTerminator(String pTerminator);
	}

	public void parse(String pFormatPattern, Visitor pVisitor) {
		final String formatPattern = Objects.requireNonNull(pFormatPattern, "Format pattern");
		final Visitor visitor = Objects.requireNonNull(pVisitor, "Visitor");
		int offset = 0;
		final StringBuilder sb = new StringBuilder();
		final Runnable clearBuffer = () -> {
			if (sb.length() > 0) {
				visitor.plainText(sb.toString());
				sb.setLength(0);
			}
		};
		while (offset < formatPattern.length()) {
			final char c = formatPattern.charAt(offset++);
			if (c == '%') {
				if (offset < formatPattern.length()) {
					final char c1 = formatPattern.charAt(offset++);
					if (c1 == '%') {
						sb.append(c);
					} else {
						if (offset < formatPattern.length()) {
							final int off = offset;
							final char c2 = formatPattern.charAt(offset++);
							String details = null;
							if (offset < formatPattern.length()  &&  formatPattern.charAt(offset) == '{') {
								final StringBuilder sb2 = new StringBuilder();
								++offset;
								while (offset < formatPattern.length()) {
									final char c3 = formatPattern.charAt(offset++);
									if (c3 == '}') {
										details = sb2.toString();
										break;
									} else {
										sb2.append(c3);
									}
								}
								if (details == null) {
									throw new IllegalArgumentException("Unterminated format pattern details (mising '}') at offset " + off + ": " + formatPattern);
								}
							}
							final String patternId = "" + c1 + c2;
							clearBuffer.run();
							switch(patternId) {
							case "dt":
								visitor.dateTime(details);
								break;
							case "lv":
								visitor.logLevel();
								break;
							case "ti":
								visitor.threadId();
								break;
							case "pi":
								visitor.packageId();
								break;
							case "si":
								visitor.serviceId();
								break;
							case "sq":
								visitor.serviceIdQ();
								break;
							case "mi":
								visitor.messageId();
								break;
							case "ms":
								visitor.message();
								break;
							case "lt":
								if (details != null) {
									final StringBuilder sb3 = new StringBuilder();
									for (int i = 0; i < details.length();  ) {
										final char c4 = details.charAt(i++);
										if (c4 == '\\') {
											if (i < details.length()) {
												final char c5 = details.charAt(i++);
												switch (c5) {
												case 'r':
													sb3.append('\r');
													break;
												case 'n':
													sb3.append('\n');
													break;
												case 't':
													sb3.append('\t');
													break;
												case 'f':
													sb3.append('\f');
													break;
												default:
													sb3.append(c5);
													break;
												}
											} else {
												throw new IllegalArgumentException("Unexpected end of line terminator details "
														+ " in format pattern " + formatPattern);
											}
										} else {
											sb.append(c4);
										}
									}
									details = sb3.toString();
								}
								visitor.lineTerminator(details);
								break;
							default:
								throw new IllegalArgumentException("Invalid format pattern, unknowm pattern id "
										+ patternId + " at offset " + (off-2) + " of " + formatPattern);
							}
								
						} else {
							throw new IllegalArgumentException("Invalid format pattern, incomplete pattern id at offset " + (offset-2) + ": " + formatPattern);
						}
					}
				} else {
					throw new IllegalArgumentException("Invalid format pattern, missing pattern id at offset " + (offset-1) + ": " + formatPattern);
				}
			} else {
				sb.append(c);
			}
		}
	}
}
