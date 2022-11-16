package wx.logng.layout;

import wx.logng.api.ILogEvent;

public interface ILayout {
	String format(ILogEvent pEvent);
}
