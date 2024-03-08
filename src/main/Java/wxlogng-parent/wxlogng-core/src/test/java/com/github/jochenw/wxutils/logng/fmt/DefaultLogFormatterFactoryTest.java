package com.github.jochenw.wxutils.logng.fmt;

import static org.junit.Assert.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import com.github.jochenw.wxutis.logng.api.ILogEvent;

public class DefaultLogFormatterFactoryTest {
	@Test
	public void testFormatEvent() {
		final ZonedDateTime now = ZonedDateTime.now();
		final String nowStr = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(now);
		final String threadName = Thread.currentThread().getName();
		final ILogEvent event = new ILogEvent() {
			@Override public String getLoggerId() { return "MyLoggerId"; }
			@Override public Level getLevel() { return Level.info; }
			@Override public String getPkgId() { return "MyPkgId"; }
			@Override public String getSvcId() { return "MySvcId"; }
			@Override public String getQSvcId() { return "fully.qualified.MySvcId"; }
			@Override public String getMsg() { return "The logging message."; }
			@Override public String getThreadId() { return threadName; }
			@Override public ZonedDateTime getDateTime() { return now; }
		};
		final ILogFormatter formatter = new DefaultLogFormatterFactory().getFormatter("%dt %lv [%ti, %li]: %pi,%sq,%si %ms");
		final String actualLogLine = formatter.format(event);
		assertEquals(nowStr + " INFO [" + threadName + ", MyLoggerId]: MyPkgId,fully.qualified.MySvcId,MySvcId The logging message.", actualLogLine);
	}
}
