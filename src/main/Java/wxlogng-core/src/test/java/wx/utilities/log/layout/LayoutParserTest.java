package wx.utilities.log.layout;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import wx.utilities.log.layout.LayoutParser.Listener;

class LayoutParserTest {
	@Test
	void testSuccess() {
		testOk("%dt %ll [%pi;%si;%ti;%mi] %ms%lt{\\n}");
		testOk("%dt%li%ll%mi%ms%pi%si%sn%ti%lt");
		testOk("%ti%sn%si%pi%ms%mi%ll%li%dt%lt");
	}

	@Test
	void testErrors() {
		testError("", IllegalArgumentException.class, "The layout specification is empty.");
		testError("%dt%xy", IllegalArgumentException.class, "Unknown layout pattern at offset 3: %dt%xy");
		testError("%dt{abc", IllegalArgumentException.class, "Unterminated message pattern at offset 0: %dt{abc");
		testError("%dt%ll{abc}", IllegalArgumentException.class, "Invalid layout specification at offset 3: The layout pattern ll doesn't permit a parameter: %dt%ll{abc}");
	}

	private void testError(String pLayoutSpecification, Class<? extends Throwable> pType, String pMessage) {
		try {
			new LayoutParser().parse(pLayoutSpecification, new Listener() {});
			fail("Expected Exception");
		} catch (Throwable t) {
			assertSame(pType, t.getClass());
			assertEquals(pMessage, t.getMessage());
		}
	}

	private void testOk(String pLayoutSpecification) {
		final StringBuilder sb = new StringBuilder();
		new LayoutParser().parse(pLayoutSpecification, new Listener() {
			@Override
			public void plainText(String pText) {
				sb.append(pText);
			}

			@Override
			public void message() {
				sb.append("%ms");
			}

			@Override
			public void loggerId() {
				sb.append("%li");
			}

			@Override
			public void messageId() {
				sb.append("%mi");
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
			public void serviceName() {
				sb.append("%sn");
			}

			@Override
			public void logLevel() {
				sb.append("%ll");
			}

			@Override
			public void lineTerminator(String pTerminator) {
				sb.append("%lt");
				if (pTerminator != null) {
					sb.append("{");
					sb.append(pTerminator);
					sb.append("}");
				}
			}

			@Override
			public void dateTime(String pPattern) {
				sb.append("%dt");
				if (pPattern != null) {
					sb.append("{");
					sb.append(pPattern);
					sb.append("}");
				}
			}
		});
		assertEquals(pLayoutSpecification, sb.toString());
	}
}
