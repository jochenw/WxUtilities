package wx.utilities.log.impl;

import java.io.File;
import java.io.IOException;

import wx.utilities.log.api.Level;
import wx.utilities.log.api.LoggerMetaData;

public interface ILogger extends AutoCloseable {
	public void log(Level pLevel, String pMsg);
	public default String getLineSeparator() { return File.separator; }
	public void close() throws IOException;
	public LoggerMetaData getMetaData();
}
