package wx.log.ng.is.svc.plain;

import java.time.ZonedDateTime;

import com.github.jochenw.afw.core.data.Data.Accessible;
import com.github.jochenw.afw.core.log.ILog;
import com.github.jochenw.afw.di.api.LogInject;

import jakarta.inject.Inject;
import wx.log.ng.core.app.ILogEvent;
import wx.log.ng.core.app.ILoggerRegistry;
import wx.log.ng.core.app.ILoggerRegistry.ILogger;
import wx.log.ng.core.app.IsFacade;
import wx.log.ng.core.app.IsFacade.IServiceName;
import wx.log.ng.core.app.Level;
import wx.log.ng.is.IsService;

public abstract class AbstractLogService extends IsService {
	private @Inject IsFacade isFacade;
	private @Inject ILoggerRegistry loggerRegistry;
	private @LogInject ILog log;

	protected abstract String getLogMessage(Accessible pInput);
	protected Level getDefaultLogLevel() { return Level.INFO; }

	protected Level getLevel(Accessible pInput) {
		final String levelStr = pInput.getString("level", "level (Log Level)");
		if (levelStr == null  ||  levelStr.length() == 0) {
			return getDefaultLogLevel();
		}
		try {
			return Level.valueOf(levelStr.toUpperCase());
		} catch (IllegalArgumentException iae) {
			throw new IllegalArgumentException("Invalid argument for parameter level: Expected "
					+ "TRACE. DEBUG, INFO, WARN, ERROR, or FATAL, got " + levelStr);
		}
	}

	protected String getLoggerId(Accessible pInput) {
		return pInput.requireString("loggerId");
	}

	@Override
	public Object[] run(Accessible pInput) {
		final Level level = getLevel(pInput);
		final String loggerId = getLoggerId(pInput);
		final ILogger logger = loggerRegistry.getLogger(loggerId);
		if (logger == null) {
			throw new IllegalArgumentException("Invalid value for parameter loggerId: "
					+ "No logger has been registered with logger id " + loggerId);
		}
		if (logger.isEnabledFor(level)) {
			final String logMessage = getLogMessage(pInput);
			final ILogEvent event = newLogEvent(logMessage, level, loggerId);
			logger.log(event);
		}
		return NO_RESULT;
	}

	protected ILogEvent newLogEvent(String pLogMessage, final Level level, final String loggerId) {
		final ZonedDateTime logTime = ZonedDateTime.now();
		final ILogEvent event = new ILogEvent() {
			private IServiceName serviceName;
			protected IServiceName getServiceNodeName() {
				if (serviceName == null) {
					serviceName = isFacade.getCallingServiceName();
				}
				return serviceName;
			}
			@Override public String getLoggerId() { return loggerId; }
			@Override public Level getLogLevel() { return level; }
			@Override public String getLogMessage() { return pLogMessage; }
			@Override public String getPackageName() { return getServiceNodeName().getPackageName(); }
			@Override public String getServiceName() { return getServiceNodeName().getServiceName(); }
			@Override public String getServiceQName() { return getServiceNodeName().getServiceQName(); }
			@Override public String getThreadName() { return Thread.currentThread().getName(); }
			@Override public ZonedDateTime getLogTime() { return logTime; }
		};
		return event;
	}

}
