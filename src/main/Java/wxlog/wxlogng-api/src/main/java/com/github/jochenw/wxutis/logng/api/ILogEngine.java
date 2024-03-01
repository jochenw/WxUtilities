package com.github.jochenw.wxutis.logng.api;

import java.io.IOException;

import com.github.jochenw.wxutis.logng.api.ILogEvent.Level;


public interface ILogEngine<S extends ILogEngine.ILogSink> {
	public static interface ILogSink {
		public boolean log(Level pLevel, String pMsg) throws IOException;
		public ILogEngine<?> getEngine();
	}
	public String getDescription();
	public S create(ILoggerMetaData pMetaData, String pFile);
	public void close(S pLogSink) throws IOException;
	
}
