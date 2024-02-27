package com.github.jochenw.wxutils.logng.svc;

import com.github.jochenw.afw.core.data.Data;
import com.softwareag.util.IDataMap;

public class PlainLogSvc extends AbstractLogSvc {
	@Override
	protected String getLogMsg(IDataMap pInput) {
		return Data.MAP_ACCESSOR.requireString(pInput, "msg");
	}
}
