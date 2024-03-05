package com.github.jochenw.wxutils.logng.svc;

import com.github.jochenw.wxutils.logng.util.Formatter;
import com.softwareag.util.IDataMap;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;

public class ParmsLogSvc extends AbstractLogSvc {
	private final Formatter formatter = new Formatter();

	@Override
	protected String getLogMsg(IDataMap pInput) {
		final String msg = pInput.getAsString("msg");
		if (msg == null) {
			throw new NullPointerException("Missing parameter: msg");
		}
		if (msg.length() == 0) {
			throw new IllegalArgumentException("Empty parameter: msg");
		}
		final String[] numberedParameters = pInput.getAsStringArray("numberedParameters");
		final IData namedParametersDocument = pInput.getAsIData("namedParameters");
		final IDataCursor namedParameters;
		if (namedParametersDocument == null) {
			namedParameters = null;
		} else {
			namedParameters = namedParametersDocument.getCursor();
		}
		return formatter.format(msg, i -> {
			if (numberedParameters == null  ||  numberedParameters.length == 0  ||  i < 0  ||  i >= numberedParameters.length) {
				return null;
			} else {
				return numberedParameters[i];
			}
		}, (k) -> {
			if (namedParameters == null) {
				return null;
			} else {
				return IDataUtil.getString(namedParameters, k);
			}
		});
	}
}
