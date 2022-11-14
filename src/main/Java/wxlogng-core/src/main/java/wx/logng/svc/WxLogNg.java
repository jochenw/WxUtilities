package wx.logng.svc;

import com.github.jochenw.afw.di.api.Application;
import com.github.jochenw.afw.di.api.Module;
import com.github.jochenw.afw.di.api.Scopes;

import wx.logng.impl.DefaultIsEnvironment;

public class WxLogNg extends Application {
	private static WxLogNg THE_INSTANCE = new WxLogNg(null);

	public static synchronized WxLogNg getInstance() {
		return THE_INSTANCE;
	}

	public static synchronized void setInstance(WxLogNg pInstance) {
		THE_INSTANCE = pInstance;
	}

	public static Module MODULE = (b) -> {
		b.bind(IIsEnvironment.class).to(DefaultIsEnvironment.class).in(Scopes.SINGLETON);
	};

	public WxLogNg(Module pModule) {
		super(() -> MODULE.extend(pModule));
	}
}
