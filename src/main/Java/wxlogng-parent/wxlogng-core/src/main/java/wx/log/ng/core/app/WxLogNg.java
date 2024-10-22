package wx.log.ng.core.app;

import java.io.InputStream;
import java.util.Properties;

import com.github.jochenw.afw.core.function.Functions;
import com.github.jochenw.afw.core.function.Functions.FailableFunction;
import com.github.jochenw.afw.core.inject.AfwCoreOnTheFlyBinder;
import com.github.jochenw.afw.core.io.IReadable;
import com.github.jochenw.afw.core.log.ILogFactory;
import com.github.jochenw.afw.core.log.log4j.Log4j2LogFactory;
import com.github.jochenw.afw.core.props.DefaultPropertyFactory;
import com.github.jochenw.afw.core.props.IPropertyFactory;
import com.github.jochenw.afw.core.util.Streams;
import com.github.jochenw.afw.di.api.Application;
import com.github.jochenw.afw.di.api.Binder;
import com.github.jochenw.afw.di.api.IComponentFactory;
import com.github.jochenw.afw.di.api.Module;

public class WxLogNg {
	private static WxLogNg THE_INSTANCE;
	public static synchronized WxLogNg getInstance() {
		if (THE_INSTANCE == null) {
			throw new NullPointerException("The init method has not been invoked.");
		}
		return THE_INSTANCE;
	}
	public static void init(IsFacade pIsFacade, String pLog4j2XmlPath, String pWxLogNgPropertiesPath, Module pModule) {
		final WxLogNg instance = new WxLogNg();
		instance.initialize(pIsFacade, pLog4j2XmlPath, pWxLogNgPropertiesPath, pModule);
		init(instance);
	}

	public static void init(IsFacade pFacade, Module pModule, ILogFactory pLogFactory, Properties pProperties) {
		final WxLogNg instance = new WxLogNg();
		instance.initialize(pFacade, pModule, pLogFactory, pProperties);
		init(instance);
	}

	public static synchronized void init(WxLogNg pInstance) {
		THE_INSTANCE = pInstance;
	}

	private Application application;

	public Application getApplication() { return application; }
	public IComponentFactory getComponentFactory() { return application.getComponentFactory(); }
	public ILogFactory getLogFactory() { return getComponentFactory().requireInstance(ILogFactory.class); }
	public IPropertyFactory getPropertyFactory() { return getComponentFactory().requireInstance(IPropertyFactory.class); }

	protected void initialize(IsFacade pIsFacade, String pLog4j2XmlPath, String pWxLogNgPropertiesPath, Module pModule) {
		final ILogFactory lf = newLogFactory(pIsFacade, pLog4j2XmlPath);
		final Properties properties = newProperties(pIsFacade, pWxLogNgPropertiesPath);
		initialize(pIsFacade, pModule, lf, properties);
	}
	protected void initialize(IsFacade pIsFacade, Module pModule, final ILogFactory pLogFactory, final Properties properties) {
		final IPropertyFactory pf = new DefaultPropertyFactory(properties);
		final Module module = (b) -> {
			b.bind(ILogFactory.class).toInstance(pLogFactory);
			b.bind(Properties.class).toInstance(properties);
			b.bind(IPropertyFactory.class).toInstance(pf);
			b.bind(IsFacade.class).toInstance(pIsFacade);
		};

		application = Application.of(module.extend(pModule), "jakarta", new AfwCoreOnTheFlyBinder());
	}

	protected ILogFactory newLogFactory(IsFacade pIsFacade, String pLog4j2XmlPath) {
		if (!pIsFacade.hasFile(pLog4j2XmlPath)) {
			throw new IllegalStateException("Log4j2 configuration file not found: " + pLog4j2XmlPath);
		}
		final FailableFunction<InputStream,ILogFactory,?> reader = (in) -> {
			return Log4j2LogFactory.of(IReadable.of(pLog4j2XmlPath, () -> in));
		};
		return pIsFacade.readFile(pLog4j2XmlPath, reader);
	}

	protected Properties newProperties(IsFacade pIsFacade, String pWxLogNgPropertiesPath) {
		if (!pIsFacade.hasFile(pWxLogNgPropertiesPath)) {
			throw new IllegalStateException("WxLogNg property file not found: " + pWxLogNgPropertiesPath);
		}
		final FailableFunction<InputStream,Properties,?> reader = (in) -> {
			return Streams.load(in, pWxLogNgPropertiesPath);
		};
		return pIsFacade.readFile(pWxLogNgPropertiesPath, reader);
	}
}
