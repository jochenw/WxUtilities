package com.github.jochenw.wxutis.logng.api;

import java.nio.file.Path;

import com.github.jochenw.wxutis.logng.api.ILogEvent.Level;

public interface ILoggerMetaData {
	public String getLoggerId();
	public Level getLevel();
	public Path getDir();
	public String getFile();
	public int getMaxGenerations();
	public long getMaxFileSize();
	public String getLayout();
	public String getPackageName();
	public String getEngineId();
}
