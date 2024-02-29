package com.github.jochenw.wxutils.logng.impl;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.jochenw.afw.di.api.IComponentFactory;
import com.github.jochenw.afw.di.api.IComponentFactoryAware;
import com.github.jochenw.wxutils.logng.api.ILogEvent;
import com.github.jochenw.wxutils.logng.api.ILogEvent.Level;
import com.github.jochenw.wxutils.logng.api.ILoggerMetaData;
import com.github.jochenw.wxutils.logng.api.ILoggerRegistry;
import com.github.jochenw.wxutils.logng.impl.ILogEngine.ILogSink;

public class DefaultLoggerRegistry implements ILoggerRegistry, IComponentFactoryAware {
	public class ILogWriter {
		private ILogFormatter formatter;
		private ILogEngine<?> engine;
		private ILogSink sink;
		public ILogFormatter getFormatter() { return formatter; }
		public ILogEngine<?> getLogEngine() { return engine; }
		public ILogSink getLogSink() { return sink; }
	}
	public static class DefaultLogger {
		private ILoggerMetaData metaData;
		private ILogWriter logWriter;
		public DefaultLogger(ILoggerMetaData pMetaData, ILogWriter pLogWriter) {
			metaData = pMetaData;
			logWriter = pLogWriter;
		}
	}
	private final ConcurrentMap<String,DefaultLogger> loggers = new ConcurrentHashMap<>();
	private IComponentFactory componentFactory;
	private Map<String,ILogEngine<?>> engines;

	@Override
	public void init(IComponentFactory pComponentFactory) {
		componentFactory = pComponentFactory;
		@SuppressWarnings("unchecked")
		final Map<String,ILogEngine<?>> engineMap = (Map<String,ILogEngine<?>>)
				componentFactory.requireInstance(Map.class, ILogEngine.class.getName());
		engines = engineMap;
	}

	@Override
	public boolean isLogEnabled(String pLoggerId, Level pLevel) {
		final DefaultLogger logger = loggers.get(pLoggerId);
		if (logger == null) {
			throw new IllegalArgumentException("Invalid logger id (No such logger registered): " + pLoggerId);
		}
		final Level level = logger.metaData.getLevel();
		return level.ordinal() >= pLevel.ordinal();
	}

	@Override
	public void log(ILogEvent pEvent) {
		final String loggerId = pEvent.getLoggerId();
		final DefaultLogger logger = loggers.get(loggerId);
		if (logger == null) {
			throw new IllegalArgumentException("Invalid logger id (No such logger registered): " + loggerId);
		}
		Level eventLevel = pEvent.getLevel();
		if (logger.metaData.getLevel().ordinal() >= eventLevel.ordinal()) {
			final String logMsg = logger.logWriter.formatter.format(pEvent);
			try {
				logger.logWriter.sink.log(eventLevel, logMsg);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	@Override
	public void removePackage(String pPkgName) {
		final List<ILogSink> sinks = new ArrayList<>();
		for (Iterator<Entry<String,DefaultLogger>> iter = loggers.entrySet().iterator();
			 iter.hasNext();  ) {
			final Entry<String,DefaultLogger> entry = iter.next();
			final DefaultLogger defaultLogger = entry.getValue();
			if (pPkgName.equals(defaultLogger.metaData.getPackageName())) {
				iter.remove();
				sinks.add(defaultLogger.logWriter.sink);
			}
		}
		for (ILogSink sink : sinks) {
			try {
				sink.close();
			} catch (IOException e) {
				log(e);
			}
		}
	}

	protected void log(Throwable pTh) {
		final ILogEvent event = new ILogEvent() {
			@Override
			public String getSvcId() {
				return null;
			}
			
			@Override
			public String getQSvcId() {
				return null;
			}
			
			@Override
			public String getPkgId() {
				return null;
			}
			
			@Override
			public String getMsg() {
				final StringWriter sw = new StringWriter();
				final PrintWriter pw = new PrintWriter(sw);
				pTh.printStackTrace(pw);
				pw.close();
				return sw.toString();
			}
			
			@Override
			public String getLoggerId() {
				return "WxLogNg";
			}
			
			@Override
			public Level getLevel() {
				return Level.error;
			}

			private ZonedDateTime dateTime;
			@Override
			public ZonedDateTime getDateTime() {
				if (dateTime == null) {
					dateTime = ZonedDateTime.now();
				}
				return dateTime;
			}
		};
		log(event);
	}
	
	@Override
	public void registerLogger(ILoggerMetaData pMetaData) throws DuplicateLoggerIdException {
		loggers.computeIfAbsent(pMetaData.getLoggerId(), (id) -> {
			return newLogger(pMetaData);
		});
	}

	protected DefaultLogger newLogger(ILoggerMetaData pMetaData) {
		final String engineId = pMetaData.getEngineId();
		final String layout = pMetaData.getLayout();
		final ILogEngine<?> logEngine = engines.get(engineId);
		if (logEngine == null) {
			throw new IllegalArgumentException("Invalid value for engineId"
					+ ": " + engineId + " (No such engine is registered)");
		}
		final ILogSink sink = logEngine.create(pMetaData);
		final ILogWriter logWriter = new ILogWriter();
		logWriter.engine = logEngine;
		logWriter.sink = sink;
		logWriter.formatter = componentFactory.requireInstance(ILogFormatterFactory.class).getFormatter(layout);
		return new DefaultLogger(pMetaData, logWriter);
	}
}
