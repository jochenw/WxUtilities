package wx.utilities.log.backend.simple;

import com.github.jochenw.afw.core.util.NotImplementedException;

import wx.utilities.log.api.IBackend;
import wx.utilities.log.api.ILogger;
import wx.utilities.log.api.ILogger.Level;

public class SimpleBackend implements IBackend {
	@Override
	public ILogger create(String pId, Level pLevel) {
		throw new NotImplementedException();
	}

}
