package com.github.jochenw.wxutils.logng.fmt;

import static org.junit.Assert.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import com.github.jochenw.afw.core.util.Holder;
import com.github.jochenw.wxutils.logng.fmt.LayoutParser.InvalidLayoutException;
import com.github.jochenw.wxutils.logng.fmt.LayoutParser.Listener;
import com.github.jochenw.wxutils.logng.fmt.LayoutParser.PCtx;

import junit.framework.AssertionFailedError;

public class LayoutParserTest {
	public static class TestListener implements LayoutParser.Listener {
		private final StringBuilder sb = new StringBuilder();
		@Override
		public void dateTime(PCtx pCtx, DateTimeFormatter pFormat) {
			// Do nothing.
		}

		@Override
		public void dateTime(PCtx pCtx, String pFormat) {
			if (pFormat == null) {
				sb.append("%dt");
			} else {
				sb.append("%dt{");
				sb.append(pFormat);
				sb.append("}");
			}
			Listener.super.dateTime(pCtx, pFormat);
		}

		@Override
		public void loggerId(PCtx pCtx) {
			sb.append("%li");
			Listener.super.loggerId(pCtx);
		}

		@Override
		public void logLevel(PCtx pCtx) {
			sb.append("%lv");
			Listener.super.logLevel(pCtx);
		}

		@Override
		public void pkgId(PCtx pCtx) {
			sb.append("%pi");
			Listener.super.pkgId(pCtx);
		}

		@Override
		public void svcId(PCtx pCtx) {
			sb.append("%si");
			Listener.super.svcId(pCtx);
		}

		@Override
		public void qSvcId(PCtx pCtx) {
			sb.append("%sq");
			Listener.super.qSvcId(pCtx);
		}

		@Override
		public void threadId(PCtx pCtx) {
			sb.append("%ti");
			Listener.super.threadId(pCtx);
		}

		@Override
		public void msg(PCtx pCtx) {
			sb.append("%ms");
			Listener.super.msg(pCtx);
		}

		@Override
		public void literal(PCtx pCtx, String pLiteral) {
			sb.append(pLiteral);
			Listener.super.literal(pCtx, pLiteral);
		}

		String getLayout() {
			return sb.toString();
		}
	}

	@Test
	public void testMinimalLayout() {
		testParseOkay("%ms");
	}

	@Test
	public void testLiteral() {
		testParseOkay("%dt %ms");
	}

	@Test
	public void testDefaultLayout() {
		testParseOkay("%dt{yyyy-MM-dd HH:mm:ss.SSS} %lv [%ti, %li]: %pi,%sq,%si %ms");
	}

	@Test
	public void testConstantDateTimeFormatterDefault() {
		final DateTimeFormatter dtf = testDateTimeFormatterReference("%dt %ms");
		assertSame(DateTimeFormatter.ISO_ZONED_DATE_TIME, dtf);
	}

	@Test
	public void testConstantDateTimeFormatterReference() {
		final DateTimeFormatter dtf = testDateTimeFormatterReference("%dt{ISO_DATE_TIME} %ms");
		assertSame(DateTimeFormatter.ISO_DATE_TIME, dtf);
	}

	@Test
	public void testConstantDateTimeFormatterPattern() {
		final DateTimeFormatter dtf = testDateTimeFormatterReference("%dt{yyyy-MM-dd HH:mm:ss.SSS} %ms");
		final ZonedDateTime now = ZonedDateTime.now();
		assertEquals(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(now), dtf.format(now));
	}

	@Test
	public void testLfInToken() {
		final InvalidLayoutException ile = testInvalidLayout("%dt\n%ms");
		assertNull(ile.getUri());
		assertEquals(1, ile.getLineNumber());
		assertEquals(4, ile.getColNumber());
		assertEquals("Invalid LineFeed character (0xa) in layout string.", ile.getMessage());
	}
	@Test
	public void testCrInToken() {
		testInvalidLayout("%dt\r%ms", 4, "Invalid CarriageReturn character (0xd) in layout string.");
	}
	@Test
	public void testIncompleteTokenException1() {
		testInvalidLayout("%/ %ms", 2,
				"Incomplete token, expected dt|li|lv|pi|si|sq|ti|ms after '%' character.");
	}
	@Test
	public void testIncompleteTokenException2() {
		testInvalidLayout("%d/ %ms", 3,
				"Incomplete token, expected dt|li|lv|pi|si|sq|ti|ms after '%' character.");
	}
	@Test
	public void testIncompleteTokenException3() {
		testInvalidLayout("%dt{yyyy %ms", 4,
				"Incomplete token, expected '}' character after '{' character.");
	}
	@Test
	public void testUnknownToken() {
		testInvalidLayout("%xy %ms", 1,
				"Unknown token reference '%xy', expected dt|li|lv|pi|si|sq|ti|ms after '%'");
	}
	@Test
	public void testUnexpectedDetails() {
		testInvalidLayout("%li{} %ms", 5,
				"Unexpected detail string ('{}' after token li");
	}
	@Test
	public void testLayoutNotEndingWithMessage() {
		testInvalidLayout("%dt %li", 7,
				          "Layout must end with %ms");
	}
	@Test
	public void testDuplicateMessageToken() {
		testInvalidLayout("%dt %ms%ms", 10,
				          "Duplicate message token.");
	}
	@Test
	public void testEmptyLayout() {
		testInvalidLayout("", 0,
				          "Layout must end with %ms");
	}
	@Test
	public void invalidDateTimePattern() {
		final String invalidDateTimePattern = "b";
		try {
			DateTimeFormatter.ofPattern(invalidDateTimePattern);
			throw new AssertionFailedError("Expected Exception");
		} catch (IllegalArgumentException e) {
			// Do nothing, make sure, that the date/time pattern is, indeed, invalid.
		}
		testInvalidLayout("%dt{" + invalidDateTimePattern + "} %ms", 6,
				"Invalid date/time pattern: %dt{" + invalidDateTimePattern + "}");
	}
	protected void testParseOkay(final String pLayout) {
		final TestListener tl = new TestListener();
		new LayoutParser().parse(pLayout, tl, null);
		assertEquals(pLayout, tl.getLayout());
	}

	protected InvalidLayoutException testInvalidLayout(String pLayout) {
		try {
			final TestListener tl = new TestListener();
			new LayoutParser().parse(pLayout, tl, null);
			throw new AssertionFailedError("Expected Exception");
		} catch (InvalidLayoutException e) {
			assertEquals(pLayout, e.getLayout());
			return e;
		}
	}
	protected InvalidLayoutException testInvalidLayout(String pLayout, int pColumnNumber,
			String pExpectedMessage) {
		try {
			final TestListener tl = new TestListener();
			new LayoutParser().parse(pLayout, tl, null);
			throw new AssertionFailedError("Expected Exception");
		} catch (InvalidLayoutException e) {
			assertEquals(pLayout, e.getLayout());
			assertEquals(1, e.getLineNumber());
			assertEquals(pColumnNumber, e.getColNumber());
			assertEquals(pExpectedMessage, e.getMessage());
			return e;
		}
	}
	protected DateTimeFormatter testDateTimeFormatterReference(String pLayout) {
		final Holder<DateTimeFormatter> dtfHolder = Holder.of(null);
		final LayoutParser.Listener listener = new LayoutParser.Listener() {
			@Override
			public void dateTime(PCtx pCtx, DateTimeFormatter pFormat) {
				if (dtfHolder.get() != null) {
					throw new IllegalStateException("Multiple DateTimeFormatter references");
				}
				dtfHolder.set(pFormat);
			}
		};
		new LayoutParser().parse(pLayout, listener, null);
		return dtfHolder.require();
	}
}
