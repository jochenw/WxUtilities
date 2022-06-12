package wx.utilities.log.api;

import wx.utilities.log.api.ILogger.MetaData;
import wx.utilities.log.backend.IFormatter;


public interface IBackend {
	public ILogger create(MetaData pMetaData);
	public void reconfigure(ILogger pLogger, MetaData mdNew);
	public IFormatter create(String pPattern);
}
