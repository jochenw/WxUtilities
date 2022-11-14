package wx.logng.api;

import java.time.ZonedDateTime;

public interface ILogEvent {
	public static enum Level {
		trace, debug, info, warn, error, fatal;
	}
	String getLoggerId();
	String getMessage();
	Level getLevel();
	ZonedDateTime getTimestamp();
	String getPackageId();
	String getServiceId();
	String getQServiceId();
	String getThreadId();
	String getMsgId();
}
