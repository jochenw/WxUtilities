package com.github.jochenw.wxutils.logng.fmt;

public interface ILogFormatterFactory {
	public ILogFormatter getFormatter(String pLayout);
}
