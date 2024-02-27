package com.github.jochenw.wxutils.logng.svc;

import com.softwareag.util.IDataMap;

public class FailLogSvc extends AbstractLogSvc {
	@Override
	protected String getLogMsg(IDataMap pInput) {
		throw new IllegalStateException("Not implemented.");
	}
}
