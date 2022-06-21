package wx.utilities.log.svc;

import java.nio.file.Path;

import javax.inject.Inject;

import wx.utilities.log.api.BackendRegistry;
import wx.utilities.log.api.ILogger.Level;
import wx.utilities.log.api.LoggerRegistry;
import wx.utilities.log.api.WxLogApplication;


public class WxLogController {
	private static final WxLogController THE_INSTANCE = WxLogApplication.getInstance().getComponentFactory().requireInstance(WxLogController.class);

	public static WxLogController geInstance() {
		return THE_INSTANCE;
	}

	private @Inject BackendRegistry backendRegistry;
	private @Inject LoggerRegistry loggerRegistry;

	public void registerLogger(String pLoggerId, String pPackageId, String pBackendId, Level pLevel, Path pPath,
			String pPattern) {
	}
}
