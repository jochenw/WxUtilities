package com.github.jochenw.wxutils.logng.api;

import com.github.jochenw.wxutils.logng.api.ILogEvent.Level;

public interface ILoggerRegistry {
	public boolean isLogEnabled(String pLoggerId, Level pLevel);
	public void log(ILogEvent pEvent);
}
