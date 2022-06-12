package wx.utilities.log.svc;

import java.nio.file.Path;

import javax.inject.Inject;

import wx.utilities.log.api.BackendRegistry;
import wx.utilities.log.api.ILogger;
import wx.utilities.log.api.ILogger.Level;
import wx.utilities.log.api.ILogger.MetaData;
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
		final ILogger.MetaData mdNew = new ILogger.MetaData(pPath, pPattern, pLoggerId, pBackendId, pPackageId);
		mdNew.setLevel(pLevel);
		mdNew.setMaxGenerations(5);
		mdNew.setMaxSize(100000000);
		final ILogger logger = loggerRegistry.get(pLoggerId);
		if (logger == null) {
			final ILogger.MetaData md = loggerRegistry.getMetaData(pLoggerId);
			if (md == null) {
				throw new NullPointerException("No MetaData available for logger: " + pLoggerId);
			}
			if (md.getLoggerId().equals(pLoggerId)  &&  md.getPackageId().equals (pPackageId)) {
				backendRegistry.requireBackend(logger.getBackendId()).reconfigure(logger, mdNew);
			}
		} else {
			final ILogger log = loggerRegistry.get(mdNew);
		}
	}
}
