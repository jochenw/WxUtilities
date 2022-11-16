package wx.logng.api;

import wx.logng.api.ILogEvent.Level;


public interface ILogRegistry {
	public static class LoggerMetaData {
		private final String loggerId;
		private final Level logLevel;
		private final String layout;
		private final String fileName;
		private final int maxNumberOfGenerations;
		private final long maxSizeInBytes;
	
		public LoggerMetaData(String pLoggerId, Level pLogLevel, String pLayout, String pFileName,
				int pMaxNumberOfGenerations, long pMaxSizeInBytes) {
			loggerId = pLoggerId;
			logLevel = pLogLevel;
			layout = pLayout;
			fileName = pFileName;
			maxNumberOfGenerations = pMaxNumberOfGenerations;
			maxSizeInBytes = pMaxSizeInBytes;
		}
	
		public String getLoggerId() { return loggerId; }
		public Level getLogLevel() { return logLevel; }
		public String getLayout() { return layout; }
		public String getFileName() { return fileName; }
		public int getMaxNumberOfGenerations() { return maxNumberOfGenerations; }
		public long getMaxSizeInBytes() { return maxSizeInBytes; }
	}
	void log(ILogEvent event);
	void register(ILogRegistry.LoggerMetaData metaData);
}
