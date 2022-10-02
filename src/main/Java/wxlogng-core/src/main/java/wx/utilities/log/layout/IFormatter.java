package wx.utilities.log.layout;

import java.time.ZonedDateTime;


public interface IFormatter {
	public interface ILogEvent {
		String getMessage();
		String getLoggerId();
		String getPackageId();
		String getServiceId();
		String getServiceName();
		String getLevel();
		String getThreadId();
		String getMessageId();
		ZonedDateTime getDateTime();
	}

	public void format(StringBuilder pSb, ILogEvent pLogEvent);
}
