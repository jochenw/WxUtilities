package wx.log.ng.is.svc.plain;

import wx.log.ng.core.app.Level;

public class PlainErrorLogService extends PlainLogService {
	public PlainErrorLogService() {
		super(Level.ERROR);
	}
}
