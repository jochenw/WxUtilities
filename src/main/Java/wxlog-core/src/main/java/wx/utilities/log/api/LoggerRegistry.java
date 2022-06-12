package wx.utilities.log.api;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;

public class LoggerRegistry {
	private @Inject BackendRegistry backendRegistry;
	private final ConcurrentMap<String,ILogger> loggers = new ConcurrentHashMap<String,ILogger>();
	private final ConcurrentMap<String,ILogger.MetaData> loggerMetaData = new ConcurrentHashMap<String,ILogger.MetaData>();

	public ILogger get(ILogger.MetaData pMetaData) {
		final String loggerId = pMetaData.getLoggerId();
		return loggers.computeIfAbsent(loggerId, (id) -> {
			loggerMetaData.compute(id, (id2,md) -> {
				return pMetaData;
			});
			final IBackend backend = backendRegistry.requireBackend(pMetaData.getBackendId());
			return backend.create(pMetaData);
		});
	}

	public ILogger get(String pLoggerId) {
		return loggers.get(pLoggerId);
	}
}
