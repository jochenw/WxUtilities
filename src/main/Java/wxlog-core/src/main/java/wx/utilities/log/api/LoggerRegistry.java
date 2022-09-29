package wx.utilities.log.api;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import com.github.jochenw.afw.core.util.Objects;

import wx.utilities.log.api.ILogger.Level;

public class LoggerRegistry {
	private @Inject ILoggerFactory loggerFactory;
	private final ConcurrentMap<String,ILogger> loggers = new ConcurrentHashMap<>();

	public void log(String pLoggerId, Level pLevel, String pMsg) {
		ILogger logger = loggers.get(pLoggerId);
		if (logger == null) {
			throw new NullPointerException("Logger not registered: " + pLoggerId);
		}
		synchronized(logger) {
			logger.log(pLevel, pMsg);
		}
	}

	public void registerLogger(@Nonnull LoggerMetaData pMetaData) {
		final @Nonnull LoggerMetaData metaData = Objects.requireNonNull(pMetaData, "MetaData");
		loggers.computeIfAbsent(pMetaData.getLoggerId(), (s) -> {
			return loggerFactory.create(metaData);
		});
	}

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

	public void foreach(Consumer<LoggerMetaData> pConsumer) {
		loggers.forEach((s,l) -> pConsumer.accept(l.getMetaData()));
	}
}
