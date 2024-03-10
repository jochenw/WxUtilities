package com.github.jochenw.wxutis.logng.api;

import java.time.ZonedDateTime;

public abstract class ILogEvent {
	public static enum Level {
		trace, debug, info, warn, error, fatal;
	}

	/** In general, we make the event data available on-demand: It
	 * should typically be created within the respective method.
	 * The thread name, however, is an exception, because the thread
	 * creating the event (the logging service) might be different
	 * from the thread, that's writing out the log message.
	 * Likewise, the date/time should typically reflect the time,
	 * when the event is created, and not the time, when it is
	 * being written. 
	 */
	private final String threadId;
	private final ZonedDateTime dateTime;

	public ILogEvent() {
		this(Thread.currentThread().getName(), ZonedDateTime.now());
	}
	public ILogEvent(String pThreadId, ZonedDateTime pDateTime) {
		threadId = pThreadId;
		dateTime = pDateTime;
	}
	public abstract String getLoggerId();
	public abstract Level getLevel();
	public abstract String getPkgId();
	public abstract String getSvcId();
	public abstract String getQSvcId();
	public abstract String getMsg();
	public String getThreadId() { return threadId; }
	public ZonedDateTime getDateTime() { return dateTime; }

	public static boolean isEnabled(Level pEventLevel, Level pConfiguredLevel) {
		return pEventLevel.ordinal() >= pConfiguredLevel.ordinal();
	}

	public static final String DEFAULT_LAYOUT = "%dt{yyyy-MM-dd HH:mm:ss.SSS} %lv [%ti, %li]: %pi,%sq %ms\"";
}
