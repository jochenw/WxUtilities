package com.github.jochenw.wxutils.logng.api;

import java.nio.file.Path;

import com.github.jochenw.wxutils.logng.api.ILogEvent.Level;

public interface ILoggerMetaData {
	public String getLoggerId();
	public Level getLevel();
	public Path getDir();
	public String getFile();
	public int getMaxGenerations();
	public long getMaxFileSize();
	public String getLayout();
}
