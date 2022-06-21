package wx.utilities.log.api;

import java.nio.file.Path;
import java.util.function.Supplier;

import com.github.jochenw.afw.di.api.Application;
import com.github.jochenw.afw.di.api.Module;
import com.github.jochenw.afw.di.api.Scopes;

import wx.utilities.log.api.ILogger.Level;
import wx.utilities.log.backend.simple.SimpleBackend;


public class WxLogApplication extends Application {
	private static WxLogApplication THE_INSTANCE = Application.of(WxLogApplication.class, WxLogApplication::newModule);
	public static WxLogApplication getInstance() {
		return THE_INSTANCE;
	}
	public static void setInstance(WxLogApplication pInstance) {
		THE_INSTANCE = pInstance;
	}

	private String defaultBackendId, defaultPattern;
	private Level defaultLogLevel;
	private Path logDirectory;

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

	public String getDefaultBackendId() {
		return defaultBackendId;
	}
	public Level getDefaultLogLevel() {
		return defaultLogLevel;
	}
	public Path getLogDirectory() {
		return logDirectory;
	}
	public String getDefaultPattern() {
		return defaultPattern;
	}
}
