package wx.logng.layout;

import static org.junit.Assert.*;

import org.junit.Test;

import wx.logng.layout.LayoutParser.Visitor;

public class LayoutParserTest {
	@Test
	public void testEmptyPattern() {
		testPattern("");
	}
	@Test
	public void testBasePatterns() {
		testPattern("%lt");
		testPattern("%dt %ll [%pi;%sq;%mi;%ti] %ms%lt");
	}
	@Test
	public void testExtendedBasePatterns() {
		testPattern("%dt{yyyy-MM-dd hh:mm:ss.SSS} %ll [%pi;%sq;%mi;%ti] %ms%lt{\\n}");
	}
	@Test
	public void testIncompleteTokenId() {
		try {
			testPattern("%dt %ll [%pi;%sq;%mi;%ti] %ms%l");
		} catch (IllegalArgumentException e) {
			assertEquals("Invalid layout specification at offset 30: %dt %ll [%pi;%sq;%mi;%ti] %ms%l" +
		                 " (Incomplete token id, expected two characters after '%')", e.getMessage());
		}
	}
	@Test
	public void testInvalidTokenId() {
		testFailingPattern("%dt %ll [%pi;%sq;%mi;%ti] %ms%xy",
				"Invalid layout specification at offset 30: %p (Invalid token id xy)");
	}
	@Test
	public void testIncompleteTokenExtension() {
		testFailingPattern("%dt %ll [%pi;%sq;%mi;%ti] %ms%lt{",
				"Invalid layout specification at offset 30: %p (Unterminated extension string, expected '}' character)");

	}
	

	void testPattern(String pLayoutSpecification) {
		final StringBuilder sb = new StringBuilder();
		final Visitor visitor = new Visitor() {
			@Override
			public void lineTerminator(String pPattern) {
				sb.append("%lt");
				tokenExtension(pPattern);
			}

			private void tokenExtension(String pPattern) {
				if (pPattern != null) {
					sb.append('{');
					sb.append(pPattern);
					sb.append('}');
				}
			}

			@Override
			public void loggerId() {
				sb.append("%li");
			}

			@Override
			public void logLevel() {
				sb.append("%ll");
			}

			@Override
			public void message() {
				sb.append("%ms");
			}

			@Override
			public void msgId() {
				sb.append("%mi");
			}

			@Override
			public void packageId() {
				sb.append("%pi");
			}

			@Override
			public void plainText(String pText) {
				sb.append(pText);
			}

			@Override
			public void qServiceId() {
				sb.append("%sq");
			}

			@Override
			public void serviceId() {
				sb.append("%si");
			}

			@Override
			public void threadId() {
				sb.append("%ti");
			}

			@Override
			public void timestamp(String pPattern) {
				sb.append("%dt");
				tokenExtension(pPattern);
			}
		};
		new LayoutParser().parse(pLayoutSpecification, visitor);
		assertEquals(pLayoutSpecification, sb.toString());
	}

	void testFailingPattern(String pLayoutSpecification, String pMessage) {
		final Visitor visitor = new Visitor() {
		};
		try {
			new LayoutParser().parse(pLayoutSpecification, visitor);
			fail("Expected Exception");
		} catch (IllegalArgumentException e) {
			final String expectedErrorMessage = pMessage.replace("%p", pLayoutSpecification);
			assertEquals(expectedErrorMessage, e.getMessage());
		}
	}
}
