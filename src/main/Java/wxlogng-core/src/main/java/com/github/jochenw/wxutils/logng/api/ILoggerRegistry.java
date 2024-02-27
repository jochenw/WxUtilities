package com.github.jochenw.wxutils.logng.api;

import com.github.jochenw.wxutils.logng.api.ILogEvent.Level;

public interface ILoggerRegistry {
	public boolean isLogEnabled(String pLoggerId, Level pLevel);
	public void log(ILogEvent pEvent);
	/** Called to remove all loggers for the given package, when
	 * a package is shutting down.
	 * @param pPkgName The package name.
	 */
	public void removePackage(String pPkgName);
}
