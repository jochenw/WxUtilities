package com.github.jochenw.wxutis.logng.api;

import com.github.jochenw.wxutis.logng.api.ILogEvent.Level;

public interface ILoggerRegistry {
	public static class DuplicateLoggerIdException extends RuntimeException {
		private static final long serialVersionUID = 6014685738692013739L;
		private final String loggerId, packageName;

		public DuplicateLoggerIdException(String pLoggerId, String pPackageName,
				                          String pMessage) {
			super(pMessage);
			loggerId = pLoggerId;
			packageName = pPackageName;
		}

		public String getLoggerId() { return loggerId; }
		public String getPackageName() { return packageName; }
	}
	public boolean isLogEnabled(String pLoggerId, Level pLevel);
	public void log(ILogEvent pEvent);
	/** Called to remove all loggers for the given package, when
	 * a package is shutting down.
	 * @param pPkgName The package name.
	 */
	public void removePackage(String pPkgName);
	/** Called to register a new logger.
	 */
	public void registerLogger(ILoggerMetaData pMetaData) throws DuplicateLoggerIdException;
}
