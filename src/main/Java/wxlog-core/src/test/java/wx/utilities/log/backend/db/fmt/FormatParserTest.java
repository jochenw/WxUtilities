package wx.utilities.log.backend.db.fmt;

import static org.junit.Assert.*;

import org.junit.Test;

public class FormatParserTest {
	@Test
	public void testParseSuccess() {
		testParse("%dt %lv [%pi,%si,%sq,%mi] %ms%lt");
		testParse("%dt{yyyy-MM-dd HH:mm:ss.SSS} %lv [%pi,%si,%sq,%mi] %ms%lt{\\r\\n}");
	}

	@Test
	public void testUnknownPatternId() {
		testParse("%dt %lv [%pi,%si,%xy,%sq,%mi] %ms%lt", "Invalid format pattern, unknowm pattern id xy at offset 17 of %dt %lv [%pi,%si,%xy,%sq,%mi] %ms%lt");
	}

	@Test
	public void testIncompletePatternId() {
		testParse("%dt %lv [%pi,%si,%sq,%mi] %ms%l", "Invalid format pattern, incomplete pattern id at offset 29: %dt %lv [%pi,%si,%sq,%mi] %ms%l");
	}

	@Test
	public void testUnterminatedDetails() {
		testParse("%dt{yyyy-MM-dd HH:mm:ss.SSS", "Unterminated format pattern details (mising '}') at offset 2: %dt{yyyy-MM-dd HH:mm:ss.SSS");
	}

	private void testParse(String pFormatPattern) {
		testParse(pFormatPattern, null);
	}

	private void testParse(String pFormatPattern, String pError) {
		final StringBuilder sb = new StringBuilder();
		final FormatParser.Visitor visitor = new FormatParser.Visitor() {
			@Override
			public void plainText(String pText) {
				sb.append(pText);
			}

			@Override
			public void dateTime(String pPattern) {
				sb.append("%dt");
				if (pPattern != null  &&  pPattern.length() > 0) {
					sb.append('{');
					sb.append(pPattern);
					sb.append('}');
				}
			}

			@Override
			public void logLevel() {
				sb.append("%lv");
			}

			@Override
			public void threadId() {
				sb.append("%ti");
			}

			@Override
			public void packageId() {
				sb.append("%pi");
			}

			@Override
			public void serviceId() {
				sb.append("%si");
			}

			@Override
			public void serviceIdQ() {
				sb.append("%sq");
			}

			@Override
			public void message() {
				sb.append("%ms");
			}

			@Override
			public void messageId() {
				sb.append("%mi");
			}

			@Override
			public void lineTerminator(String pTerminator) {
				sb.append("%lt");
				String term = pTerminator;
				if (term != null  &&  term.length() > 0) {
					term = term.replace("\r", "\\r").replace("\n", "\\n");
					sb.append('{');
					sb.append(term);
					sb.append('}');
				}
			}
		};
		if (pError != null) {
			try {
				new FormatParser().parse(pFormatPattern, visitor);
				fail("Expected Exception");
			} catch (IllegalArgumentException e) {
				assertEquals(pError, e.getMessage());
			}
			
		} else {
			new FormatParser().parse(pFormatPattern, visitor);
			assertEquals(pFormatPattern, sb.toString());
		}
	}
}
