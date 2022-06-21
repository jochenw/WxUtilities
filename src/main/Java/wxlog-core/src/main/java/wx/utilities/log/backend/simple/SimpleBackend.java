package wx.utilities.log.backend.simple;

import com.github.jochenw.afw.core.util.NotImplementedException;

import wx.utilities.log.api.IBackend;
import wx.utilities.log.api.ILogger;
import wx.utilities.log.api.ILogger.Level;
import wx.utilities.log.api.ILogger.MetaData;
import wx.utilities.log.backend.IFormatter;

public class SimpleBackend implements IBackend {
	@Override
	public ILogger create(MetaData pMetaData) {
	    final SimpleLogger sl = new SimpleLogger(pMetaData);
	    return sl;
	}

	@Override
	public IFormatter create(String pPattern) {
		// TODO Auto-generated method stub
		return null;
	}

}
