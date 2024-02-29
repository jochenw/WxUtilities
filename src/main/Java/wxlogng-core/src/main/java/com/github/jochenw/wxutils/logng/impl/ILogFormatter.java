package com.github.jochenw.wxutils.logng.impl;

import com.github.jochenw.wxutils.logng.api.ILogEvent;

public interface ILogFormatter {
	public String format(ILogEvent pEvent);
}
