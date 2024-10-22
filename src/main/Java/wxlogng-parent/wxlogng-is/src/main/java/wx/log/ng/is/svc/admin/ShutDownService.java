package wx.log.ng.is.svc.admin;

import com.github.jochenw.afw.core.data.Data.Accessible;
import com.github.jochenw.afw.di.api.ILifecycleController;

import wx.log.ng.core.app.WxLogNg;
import wx.log.ng.is.IsService;

public class ShutDownService extends IsService {
	@Override
	public Object[] run(Accessible pInput) {
		WxLogNg.getInstance().getComponentFactory().requireInstance(ILifecycleController.class).shutdown();
		return NO_RESULT;
	}
}
