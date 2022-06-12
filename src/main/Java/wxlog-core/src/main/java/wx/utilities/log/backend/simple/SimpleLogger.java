package wx.utilities.log.backend.simple;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

import com.github.jochenw.afw.core.util.Exceptions;
import com.github.jochenw.afw.core.util.NotImplementedException;

import wx.utilities.log.api.ILogger;
import wx.utilities.log.backend.IFormatter;
import wx.utilities.log.backend.LogEvent;


public class SimpleLogger implements ILogger {
	private MetaData metaData;
	private IFormatter formatter;
	private OutputStream out;
	private long size;

	@Override
	public synchronized MetaData getMetaData() {
		return metaData;
	}

	@Override
	public boolean isEnabled(Level pLevel) {
		final Level myLevel = getMetaData().getLevel();
		return pLevel.ordinal() >= myLevel.ordinal();
	}

	@Override
	public void log(Level pLevel, String pMessage) {
		final MetaData metaData;
		final IFormatter fmt;
		OutputStream os;
		synchronized(this) {
			metaData = this.metaData;
			fmt = formatter;
			if (out == null) {
				try {
					out = open(metaData);
				} catch (Throwable t) {
					throw Exceptions.show(t);
				}
			}
			os = out;
		}
		final byte[] bytes = fmt.apply(LogEvent.of(metaData.getLoggerId(), pLevel, pMessage));
		synchronized(this) {
			if ((size += bytes.length) > metaData.getMaxSize()) {
				rotate();
				log(pLevel, pMessage);
				return;
			}
		}
		synchronized(os) {
			try {
				os.write(bytes);
			} catch (Throwable t) {
				throw Exceptions.show(t);
			}
		}
	}

	protected void rotate() {
		throw new NotImplementedException();
	}

	protected OutputStream open(MetaData pMetaData) throws IOException {
		final Path path = pMetaData.getActiveLogFile();
		BasicFileAttributes bfa;
		try {
			bfa = Files.readAttributes(path, BasicFileAttributes.class);
		} catch (IOException e) {
			if ( e instanceof FileNotFoundException) {
				bfa = null;
			} else {
				throw Exceptions.show(e);
			}
		}
		if (bfa == null) {
			size = 0;
			out = Files.newOutputStream(path);
		} else {
			size = bfa.size();
			out = Files.newOutputStream(path, StandardOpenOption.APPEND);
		}
		return out;
	}
}
