package wx.log.ng.is.svc.admin;

import com.github.jochenw.afw.core.data.Data.Accessible;

import wx.log.ng.core.app.IsFacade;
import wx.log.ng.core.app.WxLogNg;
import wx.log.ng.is.IsService;

public class StartUpService extends IsService {
	@Override
	public Object[] run(Accessible pInput) {
		final DefaultIsFacade isFacade = new DefaultIsFacade();
		final String wxLogNgPackageName = isFacade.getCurrentPackageName();
		isFacade.setWxLogNgPackageName(wxLogNgPackageName);
		final String log4j2XmlPath = findLog4j2XmlFile(isFacade, wxLogNgPackageName);
		final String wxLogNgPropertiesPath = findWxLogNgPropertiesFile(isFacade, wxLogNgPackageName);
		WxLogNg.init(isFacade, log4j2XmlPath, wxLogNgPropertiesPath, (b) -> {
		});
		return NO_RESULT;
	}

	protected String findFile(IsFacade pIsFacade, String... pPaths) {
		for (String path : pPaths) {
			if (pIsFacade.hasFile(path)) {
				return path;
			}
		}
		throw new IllegalStateException("Neither of the following files found: "
				+ String.join(", ", pPaths));
	}

	protected String findLog4j2XmlFile(IsFacade pIsFacade, String pWxLogNgPackageName) {
		return findFile(pIsFacade,
				        "config/packages/" + pWxLogNgPackageName + "/log4j2.xml",
				        "packages/" + pWxLogNgPackageName + "/config/log4j2.xml");
	}

	protected String findWxLogNgPropertiesFile(IsFacade pIsFacade, String pWxLogNgPackageName) {
		return findFile(pIsFacade,
		                "config/packages/" + pWxLogNgPackageName + "/wxLogNg.properties",
		                "packages/" + pWxLogNgPackageName + "/config/wxLogNg.properties");
	}
}
