package wx.utilities.log.api;

import java.util.function.Supplier;

import com.github.jochenw.afw.di.api.Application;
import com.github.jochenw.afw.di.api.Module;
import com.github.jochenw.afw.di.api.Scopes;

import wx.utilities.log.backend.simple.SimpleBackend;


public class WxLogApplication extends Application {
	private static WxLogApplication THE_INSTANCE = Application.of(WxLogApplication.class, WxLogApplication::newModule);
	public static WxLogApplication getInstance() {
		return THE_INSTANCE;
	}
	public static void setInstance(WxLogApplication pInstance) {
		THE_INSTANCE = pInstance;
	}

	public WxLogApplication(Supplier<Module> pModule) {
		super(pModule);
	}

	public static Module newModule() {
		return (b) -> {
			b.bind(LoggerRegistry.class).in(Scopes.SINGLETON);
			b.bind(IBackend.class, "simple").toClass(SimpleBackend.class).in(Scopes.SINGLETON);
		};
	}

	public LoggerRegistry getLoggerRegistry() {
		return getComponentFactory().requireInstance(LoggerRegistry.class);
	}
}
