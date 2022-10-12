package wx.utilities.log.impl;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.github.jochenw.afw.core.util.Objects;

import wx.utilities.log.api.ILoggerRegistry;
import wx.utilities.log.api.Level;
import wx.utilities.log.api.LoggerMetaData;

public class LoggerRegistry implements ILoggerRegistry {
	private @Inject ILoggerFactory loggerFactory;
	private final ConcurrentMap<String,ILogger> loggers = new ConcurrentHashMap<>();

	@Override
	public void log(String pLoggerId, Level pLevel, Supplier<String> pMsgSuplier) {
		ILogger logger = loggers.get(pLoggerId);
		if (logger == null) {
			throw new NullPointerException("Logger not registered: " + pLoggerId);
		}
		synchronized(logger) {
			final String msg = Objects.requireNonNull(pMsgSuplier.get(), "Message");
			logger.log(pLevel, msg);
		}
	}

	@Override
	public void registerLogger(@Nonnull LoggerMetaData pMetaData) {
		final @Nonnull LoggerMetaData metaData = Objects.requireNonNull(pMetaData, "MetaData");
		loggers.computeIfAbsent(pMetaData.getLoggerId(), (s) -> {
			return loggerFactory.create(metaData);
		});
	}

	@Override
	public void updateLogger(@Nonnull LoggerMetaData pMetaData) {
		final @Nonnull LoggerMetaData metaData = Objects.requireNonNull(pMetaData, "MetaData");
		loggers.compute(pMetaData.getLoggerId(), (s,l) -> {
			if (l != null) {
				synchronized (l) {
					try {
						l.close();
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				}
			}
			return loggerFactory.create(metaData);
		});
	}

	@Override
	public void foreach(Consumer<LoggerMetaData> pConsumer) {
		loggers.forEach((s,l) -> pConsumer.accept(l.getMetaData()));
	}

	@Override
	public boolean isLevelEnabled(@Nonnull String pLoggerId, @Nonnull Level pLevel) {
		switch (pLevel) {
		default:
			break;
		}
		final ILogger logger = loggers.get(pLoggerId);
		if (logger == null) {
			throw new NullPointerException("Logger not registered: " + pLoggerId);
		}
		final LoggerMetaData metaData = logger.getMetaData();
		final Level configuredLevel = metaData.getLogLevel();
		return configuredLevel.ordinal() >= pLevel.ordinal();
	}
}
