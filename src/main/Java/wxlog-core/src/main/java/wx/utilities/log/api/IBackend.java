package wx.utilities.log.api;

import wx.utilities.log.api.ILogger.MetaData;


public interface IBackend {
	public ILogger create(MetaData pMetaData);
	public void reconfigure(ILogger pLogger, MetaData mdNew);
}
