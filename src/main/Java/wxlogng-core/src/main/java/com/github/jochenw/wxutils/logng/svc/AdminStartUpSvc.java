package com.github.jochenw.wxutils.logng.svc;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.jochenw.afw.core.data.Data;
import com.github.jochenw.afw.di.api.IComponentFactory;
import com.github.jochenw.afw.di.api.ILifecycleController;
import com.github.jochenw.afw.di.api.Module;
import com.github.jochenw.wxutils.logng.api.IIsFacade;
import com.github.jochenw.wxutils.logng.api.WxLogNg;
import com.github.jochenw.wxutils.logng.api.ILogEvent.Level;
import com.github.jochenw.wxutils.logng.api.ILoggerRegistry;
import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.PackageListener;
import com.wm.app.b2b.server.PackageManager;


public class AdminStartUpSvc extends IIsSvc {
	protected static Properties getWxLogProperties(String pPackageName, IIsFacade pFacade) {
		final String configPropertiesUri = "config/packages/" + pPackageName + "/wxLogNg.properties";
		final String deployedPropertiesUri = "packages/" + pPackageName + "/config/wxLogNg.properties";
		final String factoryPropertiesUri = "packages/" + pPackageName + "/config/wxLogNg-factory.properties";
		final Properties properties = new Properties();
		final Consumer<String> propertyReader = (s) -> {
			final String sXml = s + ".xml";
			if (pFacade.hasFile(sXml)) {
				try (InputStream in = pFacade.read(s)) {
					final Properties pr = new Properties();
					pr.loadFromXML(in);
					properties.putAll(pr);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			} else if (pFacade.hasFile(s)) {
				try (InputStream in = pFacade.read(s)) {
					final Properties pr = new Properties();
					pr.load(in);
					properties.putAll(pr);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			}
		};
		propertyReader.accept(factoryPropertiesUri);
		propertyReader.accept(deployedPropertiesUri);
		propertyReader.accept(configPropertiesUri);
		if (properties.isEmpty()) {
			throw new IllegalStateException("Neither of the following properties has been found: "
					+ configPropertiesUri + ", " + deployedPropertiesUri + ", " + configPropertiesUri);
		}
		return properties;
	}

	public static synchronized Module getModule(String pPackageName, IIsFacade pFacade, Module pModule) {
		// May be, test code wants us to use a different module?
		final Module m = WxLogNg.MODULE.extend((b) -> {
			final Properties wxLogProperties = getWxLogProperties(pPackageName, pFacade);
			b.bind(IIsFacade.class).toInstance(pFacade);
			b.bind(Properties.class).toInstance(wxLogProperties);
		});
		if (pModule == null) {
			return m;
		} else {
			return m.extend(pModule);
		}
	}

	public static void init(String pPackageName, IIsFacade pFacade, Module pModule) {
		Objects.requireNonNull(pPackageName, "Package name");
		Objects.requireNonNull(pFacade, "Is Facade");
		Objects.requireNonNull(pModule, "Module");
		WxLogNg.getInstance(getModule(pPackageName, pFacade, pModule));
	}

	@Override
	public Object[] run(IDataMap pInput) throws Exception {
		logWxLogMsg(Level.info, "WxLogNg service is starting at " + DateTimeFormatter.BASIC_ISO_DATE.format(ZonedDateTime.now()));
		final IComponentFactory cf = getComponentFactory();
		final PackageListener pkgListener = new PackageListener() {
			@Override
			public void preunload(String pPkgName) throws Exception {
				// Does nothing.
			}
			
			@Override
			public void preload(String pPkgName) throws Exception {
				// Does nothing.
			}
			
			@Override
			public void postunload(String pPkgName) throws Exception {
				cf.requireInstance(ILoggerRegistry.class).removePackage(pPkgName);
			}
			
			@Override
			public void postload(String pPkgName) throws Exception {
				registerLoggersFor(pPkgName);
			}
		};
		final ILifecycleController.TerminableListener shutDownHook = new ILifecycleController.TerminableListener() {
			@Override
			public void start() {
				// Does nothing.
			}
			
			@Override
			public void shutdown() {
				PackageManager.removePackageListener(pkgListener);
			}
		}; 
		cf.requireInstance(ILifecycleController.class).addListener(shutDownHook);
		PackageManager.addPackageListener(pkgListener);
		for (com.wm.app.b2b.server.Package pkg : PackageManager.getAllPackages()) {
			if (pkg.isEnabled()) {
				registerLoggersFor(pkg.getName());
			}
		}
		
		return NO_RESULT;
	}

	protected void registerLoggersFor(String pPackageName) {
		logWxLogMsg(Level.debug, "registerLoggersFor: -> " + pPackageName);
		final IComponentFactory cf = getComponentFactory();
		final Properties defaultProperties = cf.requireInstance(Properties.class);
		final BiConsumer<String, Properties> loggerRegistrator = (uri, props) -> {
			final String loggerId = props.getProperty("loggerId");
			if (loggerId == null) {
				throw new NullPointerException("Missing property in logger descriptor file " + uri + ": loggerId");
			}
			if (loggerId.length() == 0) {
				throw new IllegalArgumentException("Empty property in logger descriptor file " + uri + ": loggerId");
			}
			final String levelStr = props.getProperty("logLevel", defaultProperties.getProperty("default.logLevel"));
			if (levelStr == null) {
				throw new NullPointerException("Missing property in logger descriptor file " + uri + ": logLevel");
			}
			if (levelStr.length() == 0) {
				throw new IllegalArgumentException("Empty property in logger descriptor file " + uri + ": logLevel");
			}
			final Level level;
			try {
				level = Level.valueOf(levelStr.toLowerCase());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid value for property logLevel in logger descriptor file " + uri + ": " + levelStr);
			}
			
		};
		logWxLogMsg(Level.debug, "registerLoggersFor: <- " + pPackageName);
	}
}
