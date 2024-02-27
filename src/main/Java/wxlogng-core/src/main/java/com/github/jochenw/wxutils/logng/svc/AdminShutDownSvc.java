package com.github.jochenw.wxutils.logng.svc;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.github.jochenw.afw.di.api.IComponentFactory;
import com.github.jochenw.afw.di.api.ILifecycleController;
import com.github.jochenw.wxutils.logng.api.ILogEvent.Level;
import com.softwareag.util.IDataMap;


public class AdminShutDownSvc extends IIsSvc {
	@Override
	public Object[] run(IDataMap pInput) throws Exception {
		logWxLogMsg(Level.info, "WxLogNg shutting down at " + DateTimeFormatter.BASIC_ISO_DATE.format(ZonedDateTime.now()));
		final IComponentFactory cf = getComponentFactory();
		cf.requireInstance(ILifecycleController.class).shutdown();
		return NO_RESULT;
	}
}
