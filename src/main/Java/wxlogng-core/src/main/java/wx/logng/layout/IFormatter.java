package wx.logng.layout;

import wx.logng.api.ILogEvent;

public interface IFormatter {
	String format(ILogEvent pEvent);
}
