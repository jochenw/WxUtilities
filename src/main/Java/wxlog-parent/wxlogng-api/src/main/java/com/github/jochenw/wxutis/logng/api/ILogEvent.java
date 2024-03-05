package com.github.jochenw.wxutis.logng.api;

import java.time.ZonedDateTime;

public interface ILogEvent {
	public static enum Level {
		trace, debug, info, warn, error, fatal;
	}
	public String getLoggerId();
	public Level getLevel();
	public String getPkgId();
	public String getSvcId();
	public String getQSvcId();
	public String getMsg();
	public ZonedDateTime getDateTime();

	public static boolean isEnabled(Level pEventLevel, Level pConfiguredLevel) {
		return pEventLevel.ordinal() >= pConfiguredLevel.ordinal();
	}
}
