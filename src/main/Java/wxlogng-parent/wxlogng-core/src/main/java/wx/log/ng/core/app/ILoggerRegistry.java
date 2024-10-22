package wx.log.ng.core.app;

public interface ILoggerRegistry {
	public static class MetaData {
		private final String id;
		private final Level level;

		public MetaData(String pLoggerId, Level pLevel) {
			id = pLoggerId;
			level = pLevel;
		}

		public String getId() { return id; }
		public Level getLevel() { return level; }
	}

	public interface ILogger {
		public MetaData getMetaData();
		public void log(ILogEvent pEvent);
		public boolean isEnabledFor(Level pLevel);
	}

	public ILogger getLogger(String pId);
}
