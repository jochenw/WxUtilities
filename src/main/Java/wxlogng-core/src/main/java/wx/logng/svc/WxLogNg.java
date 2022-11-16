package wx.logng.svc;

import com.github.jochenw.afw.di.api.Application;
import com.github.jochenw.afw.di.api.Module;
import com.github.jochenw.afw.di.api.Scopes;

import wx.logng.api.ILogRegistry;
import wx.logng.impl.DefaultIsEnvironment;
import wx.logng.impl.DefaultLogFactory;
import wx.logng.impl.DefaultLogRegistry;
import wx.logng.impl.DefaultLogRegistry.ILogFactory;
import wx.logng.layout.DefaultLayoutFactory;
import wx.logng.layout.ILayoutFactory;

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
		b.bind(IIsService.class, LogService.class.getName()).to(LogService.class).in(Scopes.SINGLETON);
		b.bind(ILogRegistry.class).to(DefaultLogRegistry.class).in(Scopes.SINGLETON);
		b.bind(ILayoutFactory.class).to(DefaultLayoutFactory.class).in(Scopes.SINGLETON);
		b.bind(ILogFactory.class).to(DefaultLogFactory.class).in(Scopes.SINGLETON);
	};

	public WxLogNg(Module pModule) {
		super(() -> MODULE.extend(pModule));
	}
}
