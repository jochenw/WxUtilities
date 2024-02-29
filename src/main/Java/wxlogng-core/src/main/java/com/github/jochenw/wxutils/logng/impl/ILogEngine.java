package com.github.jochenw.wxutils.logng.impl;

import java.io.Closeable;
import java.io.IOException;

import com.github.jochenw.wxutils.logng.api.ILogEvent.Level;
import com.github.jochenw.wxutils.logng.api.ILoggerMetaData;

public interface ILogEngine<S extends ILogEngine.ILogSink> {
	public static interface ILogSink extends Closeable {
		public void log(Level pLevel, String pMsg) throws IOException;
	}
	public String getId();
	public S create(ILoggerMetaData pMetaData);
	public void close(S pLogSink);
}
