package wx.utilities.log.impl;

import wx.utilities.log.api.LoggerMetaData;

public interface ILoggerFactory {
	ILogger create(LoggerMetaData pMetaData);
}
