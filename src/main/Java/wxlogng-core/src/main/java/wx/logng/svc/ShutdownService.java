package wx.logng.svc;

import com.github.jochenw.afw.di.api.ILifecycleController;
import com.softwareag.util.IDataMap;

public class ShutdownService extends AbstractWxLogNgService {
	@Override
	public Object[] run(IDataMap pInput) throws Exception {
		WxLogNg.getInstance().getComponentFactory().requireInstance(ILifecycleController.class).shutdown();
		return NO_RESULT;
	}
}
