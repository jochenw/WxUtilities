package wx.utilities.log.api;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BackendRegistry {
	private final ConcurrentMap<String,IBackend> backends = new ConcurrentHashMap<String,IBackend>();

	public @Nullable IBackend getBackend(String pBackendId) {
		return backends.get(pBackendId);
	}
	public @Nonnull IBackend requireBackend(String pBackendId) {
		final IBackend backend = getBackend(pBackendId);
		if (backend == null) {
			throw new NoSuchElementException("Unknown backend: " + pBackendId);
		}
		return backend;
	}
}
