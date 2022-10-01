package wx.utilities.log.api;

import java.io.File;
import java.io.IOException;

public interface ILogger extends AutoCloseable {
	public enum Level {
		none, trace, debug, info, warn, error, fatal, all;
	}

	public boolean isEnabled(Level pLevel);
	public void log(Level pLevel, String pMsg);
	public default String getLineSeparator() { return File.separator; }
	public void close() throws IOException;
	public LoggerMetaData getMetaData();
}
