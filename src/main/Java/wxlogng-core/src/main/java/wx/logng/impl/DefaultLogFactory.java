package wx.logng.impl;

import com.github.jochenw.afw.core.util.NotImplementedException;

import wx.logng.api.ILogRegistry.LoggerMetaData;
import wx.logng.impl.DefaultLogRegistry.ILog;
import wx.logng.impl.DefaultLogRegistry.ILogFactory;

public class DefaultLogFactory implements ILogFactory {
	@Override
	public ILog create(LoggerMetaData pMetaData) {
		throw new NotImplementedException();
	}

}
