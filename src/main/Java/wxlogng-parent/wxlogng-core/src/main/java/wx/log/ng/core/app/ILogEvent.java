package wx.log.ng.core.app;

import java.time.ZonedDateTime;

public interface ILogEvent {
	public String getLoggerId();
	public Level getLogLevel();
	public String getLogMessage();
	public String getPackageName();
	public String getServiceName();
	public String getServiceQName();
	public String getThreadName();
	public ZonedDateTime getLogTime();
}
