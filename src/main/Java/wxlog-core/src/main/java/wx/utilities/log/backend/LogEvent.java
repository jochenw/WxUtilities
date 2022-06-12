package wx.utilities.log.backend;

import java.time.LocalDateTime;

import wx.utilities.log.api.ILogger.Level;

public interface LogEvent {
	public String getMessage();
	public Level getLevel();
	public LocalDateTime getDateTime();
	public String getLoggerId();

	public static LogEvent of(String pLoggerId, Level pLevel, String pMessage) {
		final LocalDateTime ldt = LocalDateTime.now();
		return new LogEvent() {
			@Override
			public String getMessage() {
				return pMessage;
			}

			@Override
			public Level getLevel() {
				return pLevel;
			}

			@Override
			public LocalDateTime getDateTime() {
				return ldt;
			}

			@Override
			public String getLoggerId() {
				return pLoggerId;
			}
		};
	}
	
}
