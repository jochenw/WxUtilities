package com.github.jochenw.wxutils.logng.fmt;

import com.github.jochenw.wxutis.logng.api.ILogEvent;

public interface ILogFormatter {
	public String format(ILogEvent pEvent);
}
