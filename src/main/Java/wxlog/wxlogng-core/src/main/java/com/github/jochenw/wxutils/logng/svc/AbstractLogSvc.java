package com.github.jochenw.wxutils.logng.svc;

import java.time.ZonedDateTime;

import com.github.jochenw.afw.core.data.Data;
import com.github.jochenw.afw.core.util.Objects;
import com.github.jochenw.afw.di.api.IComponentFactory;
import com.github.jochenw.wxutis.logng.api.ILogEvent;
import com.github.jochenw.wxutis.logng.api.ILoggerRegistry;
import com.github.jochenw.wxutis.logng.api.ILogEvent.Level;
import com.softwareag.util.IDataMap;

public abstract class AbstractLogSvc extends IIsSvc {
	private ILoggerRegistry loggerRegistry;

	protected abstract String getLogMsg(IDataMap pInput);
	
	@Override
	public void init(IComponentFactory pFactory) throws Exception {
		super.init(pFactory);
		loggerRegistry = pFactory.requireInstance(ILoggerRegistry.class);
	}

	public ILoggerRegistry getLoggerRegistry() {
		return loggerRegistry;
	}
	
	protected ILogEvent getLogEvent(Data.Accessible pInput, String pLoggerId, Level pLevel, String pMsg) {
		return new ILogEvent() {
			@Override
			public String getLoggerId() {
				return pLoggerId;
			}

			@Override
			public Level getLevel() {
				return pLevel;
			}

			@Override
			public String getPkgId() {
				return Objects.notNull(pInput.getString("pkgId"), getIsFacade().getCurrentPkgId());
			}

			@Override
			public String getSvcId() {
				return Objects.notNull(pInput.getString("svcId"), getIsFacade().getCurrentSvcId());
			}

			@Override
			public String getQSvcId() {
				return Objects.notNull(pInput.getString("qSvcId"), getIsFacade().getCurrentQSvcId());
			}

			@Override
			public String getMsg() {
				return pMsg;
			}

			@Override
			public ZonedDateTime getDateTime() {
				return ZonedDateTime.now();
			}
		};
	}

	@Override
	public Object[] run(IDataMap pInput) throws Exception {
		final Data.Accessible data = new Data.Accessible(pInput::get) {};
		final String loggerId = data.requireString("loggerId");
		final String levelStr = data.requireString("level");
		final Level level;
		try {
			level = Level.valueOf(levelStr);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid value for parameter level: Expected"
					+ "trace|debug|info|warn|error|fatal, got " + levelStr, e);
		}
		if (getLoggerRegistry().isLogEnabled(loggerId, level)) {
			final String msg = getLogMsg(pInput);
			final ILogEvent event = getLogEvent(data, loggerId, level, msg);
			getLoggerRegistry().log(event);
		}
		return NO_RESULT;
	}
}
