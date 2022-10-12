package wx.utilities.log.app;

import java.util.Properties;

import javax.annotation.Nonnull;

import com.github.jochenw.afw.core.util.Objects;
import com.github.jochenw.afw.di.api.Application;
import com.github.jochenw.afw.di.api.Module;
import com.github.jochenw.afw.di.api.Scopes;

import wx.utilities.log.api.ILoggerRegistry;
import wx.utilities.log.impl.LoggerRegistry;

public class WxLogNg extends Application {
	private static @Nonnull WxLogNg THE_INSTANCE = new WxLogNg(null);

	public static synchronized @Nonnull WxLogNg getInstance() { return THE_INSTANCE; }
	public static synchronized void setInstance(@Nonnull WxLogNg pInstance) { THE_INSTANCE = Objects.requireNonNull(pInstance, "Instance"); }

	private String wxLogNgPackageName;
	private Properties defaultProperties;
	private ILoggerRegistry loggerRegistry;

	public WxLogNg(Module pModule) {
		super(() -> newModule(pModule));
	}

	protected static Module newModule(Module pModule) {
		return (b) -> {
			b.bind(ILoggerRegistry.class).toClass(LoggerRegistry.class).in(Scopes.SINGLETON);
			if (pModule != null) {
				pModule.configure(b);
			}
		};
	}

	public ILoggerRegistry getLoggerRegistry() {
		synchronized (this) {
			if (loggerRegistry == null) {
				loggerRegistry = getComponentFactory().requireInstance(ILoggerRegistry.class);
			}
			return loggerRegistry;
		}
	}
	public synchronized String getWxLogNgPackageName() {
		return wxLogNgPackageName;
	}
	public synchronized void setWxLogNgPackageName(String pWxLogNgPackageName) {
		wxLogNgPackageName = pWxLogNgPackageName;
	}
	public synchronized Properties getDefaultProperties() {
		return defaultProperties;
	}
	public synchronized void setDefaultProperties(Properties pProperties) {
		defaultProperties = pProperties;
	}
}
