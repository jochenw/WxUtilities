package com.github.jochenw.wxutils.logng.engine.dflt;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.github.jochenw.wxutis.logng.api.ILogEngine;
import com.github.jochenw.wxutis.logng.api.ILogEvent.Level;
import com.github.jochenw.wxutis.logng.api.ILoggerMetaData;


/** Default implementation of a log engine.
 */
public class DefaultLogEngine implements ILogEngine<DefaultLogEngine.DefaultLogSink> {
	/** Default implementation of a logger.
	 * <em>Note:</em> This implementation assumes exclusive access on the log file
	 * while being invoked. The {@link DefaultLoggerRegistry} ensures this by
	 * synchronizing on the {@link DefaultLoggerRegistry.DefaultLogger}.
	 */
	public class DefaultLogSink implements ILogEngine.ILogSink {
		private final ILoggerMetaData metaData;
		private OutputStream out;
		private long size;

		DefaultLogSink(ILoggerMetaData pMetaData, OutputStream pOut, long pSize) {
			metaData = pMetaData;
		}

		@Override
		public boolean log(Level pLevel, String pMsg) throws IOException {
			boolean result = false;
			if (pMsg != null) {
				final byte[] bytes = pMsg.getBytes(StandardCharsets.UTF_8);
				size += (bytes.length+1);
				if (size >= metaData.getMaxFileSize()) {
					result = true;
				}
				out.write(bytes);
				out.write((int) '\n');
				out.flush();
			}
			return result;
		}

		@Override
		public ILogEngine<?> getEngine() {
			return DefaultLogEngine.this;
		}
	}

	@Override
	public String getDescription() {
		return "Simple, small, builtin, standalone logging engine.";
	}

	@Override
	public DefaultLogSink create(ILoggerMetaData pMetaData, String pFile) {
		final Path path = pMetaData.getDir().resolve(pFile);
		long size;
		final OutputStream out;
		try {
			final Path dir = path.getParent();
			if (dir != null) {
				Files.createDirectories(dir);
			}
			if (Files.isRegularFile(path)) {
				size = Files.size(path);
				out = Files.newOutputStream(path, StandardOpenOption.APPEND);
			} else {
				size = 0;
				out = Files.newOutputStream(path, StandardOpenOption.CREATE);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return new DefaultLogSink(pMetaData, new BufferedOutputStream(out), size);
	}

	@Override
	public void close(DefaultLogSink pLogSink) throws IOException {
		pLogSink.out.close();
	}
}
