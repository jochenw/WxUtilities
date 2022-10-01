package wx.utilities.log.api;

import wx.utilities.log.api.ILogger.Level;

public class LoggerMetaData {
	private final String loggerId;
	private final String packageId;
	private final String logFile;
	private final Level logLevel;
	private final String layout;
	private final int numGenerations;
	private final long maxBytesPerGeneration;

	public LoggerMetaData(LoggerMetaData pMetaData) {
		this(pMetaData.getLoggerId(), pMetaData.getPackageId(), pMetaData.getLogFile(),
			 pMetaData.getLogLevel(), pMetaData.getLayout(), pMetaData.getNumGenerations(),
			 pMetaData.getMaxBytesPerGeneration());
	}
	public LoggerMetaData(String pLoggerId, String pPackageId, String pLogFile, Level pLogLevel,
			              String pLayout, int pNumGenerations, long pMaxBytesPerGeneration) {
		loggerId = pLoggerId;
		packageId = pPackageId;
		logFile = pLogFile;
		logLevel = pLogLevel;
		layout = pLayout;
		numGenerations = pNumGenerations;
		maxBytesPerGeneration = pMaxBytesPerGeneration;
	}

	public String getLoggerId() { return loggerId; }
	public String getPackageId() { return packageId; }
	public String getLogFile() { return logFile; }
	public Level getLogLevel() { return logLevel; }
	public String getLayout() { return layout; }
	public int getNumGenerations() { return numGenerations; }
	public long getMaxBytesPerGeneration() { return maxBytesPerGeneration; }
}
