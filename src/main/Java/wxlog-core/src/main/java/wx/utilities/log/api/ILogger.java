package wx.utilities.log.api;

import java.nio.file.Path;

import com.github.jochenw.afw.core.util.Exceptions;

public interface ILogger {
	public enum Level {
		trace, debug, info, warn, error, fatal;
	}

	public static class MetaData {
		private final Path activeLogFile;
		private final String pattern;
		private final String loggerId, backendId, packageId;
		private final Level level;
		private final int maxGenerations;
		private final long maxSize;
		public MetaData(Path pActiveLogFile, String pPattern, String pLoggerId, String pPackageId, String pBackendId, Level pLevel, int pMaxGenerations, long pMaxSize) {
			activeLogFile = pActiveLogFile;
			pattern = pPattern;
			loggerId = pLoggerId;
			backendId = pBackendId;
			packageId = pPackageId;
			maxGenerations = pMaxGenerations;
			maxSize = pMaxSize;
			level = pLevel;
		}
		public Level getLevel() {
			return level;
		}
		public int getMaxGenerations() {
			return maxGenerations;
		}
		public long getMaxSize() {
			return maxSize;
		}
		public Path getActiveLogFile() {
			return activeLogFile;
		}
		public String getPattern() {
			return pattern;
		}
		public String getLoggerId() {
			return loggerId;
		}
		public String getBackendId() {
			return backendId;
		}
		public String getPackageId() {
			return packageId;
		}
	}
	MetaData getMetaData();
	boolean isEnabled(Level pLevel);
	void log(Level pLevel, String pMessage);
	default void log(Level pLevel, Throwable pTh) {
		if (isEnabled(pLevel)) {
			log(pLevel, Exceptions.toString(pTh));
		}
	}
}
