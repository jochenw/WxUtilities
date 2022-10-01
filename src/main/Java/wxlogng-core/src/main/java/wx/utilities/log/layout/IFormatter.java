package wx.utilities.log.layout;

import java.time.LocalDateTime;

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
		LocalDateTime getDateTime();
	}

	public void format(StringBuilder pSb, ILogEvent pLogEvent);
}
