package wx.logng.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;

import com.github.jochenw.afw.core.util.Objects;

import wx.logng.api.ILogEvent;
import wx.logng.api.ILogEvent.Level;
import wx.logng.layout.ILayout;
import wx.logng.layout.ILayoutFactory;
import wx.logng.api.ILogRegistry;

public class DefaultLogRegistry implements ILogRegistry {
	public static interface ILog extends AutoCloseable {
		void log(String pMsg);
	}
	public static interface ILogFactory {
		DefaultLogRegistry.ILog create(ILogRegistry.LoggerMetaData pMetaData);
	}

	public static class Logger {
		private final ILogRegistry.LoggerMetaData metaData;
		private final ILayout layout;
		private final ILog log;
		public Logger(ILogRegistry.LoggerMetaData pMetaData, ILayout pLayout, ILog pLog) {
			metaData = pMetaData;
			layout = pLayout;
			log = pLog;
		}
		public ILogRegistry.LoggerMetaData getMetaData() { return metaData; }
		public ILayout getLayout() { return layout; }
		public ILog getLog() { return log; }
	}

	private @Inject ILayoutFactory layoutFactory; 
	private @Inject ILogFactory logFactory; 
	private final ConcurrentMap<String, Logger> loggers = new ConcurrentHashMap<>();
	
	@Override
	public void log(ILogEvent pEvent) {
		final String loggerId = pEvent.getLoggerId();
		final Logger logger = loggers.get(loggerId);
		if (logger == null) {
			throw new IllegalArgumentException("Logger Id has not been registered: " + loggerId);
		}
		final Level configuredLevel = logger.getMetaData().getLogLevel();
		final Level requestedLevel = pEvent.getLevel();
		if (requestedLevel.ordinal() >= configuredLevel.ordinal()) {
			final String msg = logger.getLayout().format(pEvent);
			logger.getLog().log(msg);
		}
	}

	@Override
	public void register(ILogRegistry.LoggerMetaData pMetaData) {
		final ILogRegistry.LoggerMetaData metaData = Objects.requireNonNull(pMetaData, "LoggerMetaData");
		final String loggerId = Objects.requireNonNull(metaData.getLoggerId(), "LoggerMetaData.loggerId");
		Objects.requireNonNull(metaData.getLogLevel(), "LoggerMetaData.logLevel");
		Objects.requireNonNull(metaData.getFileName(), "LoggerMetaData.fileName");
		final String layoutStr = Objects.requireNonNull(metaData.getLayout(), "LoggerMetaData.layout");
		final ILayout layout = layoutFactory.create(layoutStr);
		ILog log = logFactory.create(metaData);
		loggers.compute(loggerId, (s, l) -> new Logger(pMetaData, layout, log));
	}
}
