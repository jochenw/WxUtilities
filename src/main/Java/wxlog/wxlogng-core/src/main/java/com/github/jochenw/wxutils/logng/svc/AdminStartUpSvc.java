package com.github.jochenw.wxutils.logng.svc;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.jochenw.afw.core.util.MutableBoolean;
import com.github.jochenw.afw.di.api.IComponentFactory;
import com.github.jochenw.afw.di.api.ILifecycleController;
import com.github.jochenw.afw.di.api.Module;
import com.github.jochenw.wxutils.logng.app.IIsFacade;
import com.github.jochenw.wxutils.logng.app.WxLogNg;
import com.github.jochenw.wxutis.logng.api.ILogEngine;
import com.github.jochenw.wxutis.logng.api.ILoggerMetaData;
import com.github.jochenw.wxutis.logng.api.ILoggerRegistry;
import com.github.jochenw.wxutis.logng.api.ILogEvent.Level;
import com.github.jochenw.wxutis.logng.api.ILoggerRegistry.DuplicateLoggerIdException;
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
		final Properties wxLogProperties = getWxLogProperties(pPackageName, pFacade);
		final Map<String,ILogEngine<?>> engines = getLogEngines(wxLogProperties);
		// May be, test code wants us to use a different module?
		final Module m = WxLogNg.MODULE.extend((b) -> {
			b.bind(IIsFacade.class).toInstance(pFacade);
			b.bind(Properties.class).toInstance(wxLogProperties);
			b.addFinalizer((cf) -> { engines.values().forEach((e) -> cf.init(e)); });
		});
		if (pModule == null) {
			return m;
		} else {
			return m.extend(pModule);
		}
	}

	public static Map<String,ILogEngine<?>> getLogEngines(Properties pProperties) {
		final Map<String,ILogEngine<?>> map = new HashMap<>();
		for (int i = 0;  ; i++) {
			final String key = "log.engine" + (i > 0 ? ("." + i) : "");
			final String value = pProperties.getProperty(key);
			if (value == null  ||  value.length() == 0) {
				break;
			}
			final int offset = value.indexOf('=');
			if (offset <= 0) {
				throw new IllegalStateException("Invalid engine definition in property "
						+ key + ": Expected id=className, got " + value);
			}
			final String id = value.substring(0, offset);
			final String className = value.substring(offset+1);
			final Class<?> clazz;
			try {
				clazz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException("Invalid engine definition in property "
						+ key + ": Class '" + className + "' not found.");
			}
			if (!ILogEngine.class.isAssignableFrom(clazz)) {
				throw new IllegalStateException("Invalid engine definition in property "
						+ key + ": Class " + clazz.getName() + " doesn't implement ILogEngine.");
			}
			@SuppressWarnings("unchecked")
			final Class<? extends ILogEngine<?>> cl = (Class<? extends ILogEngine<?>>) clazz;
			final Constructor<ILogEngine<?>> cons;
			try {
				@SuppressWarnings("unchecked")
				final Constructor<ILogEngine<?>> cns = (Constructor<ILogEngine<?>>) cl.getConstructor();
				cons = cns;
			} catch (NoSuchMethodException e) {
				throw new IllegalStateException("Invalid engine definition in property "
						+ key + ": Class " + clazz.getName() + " doesn't have a public default constructor.");
			}
			try {
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
		final IIsFacade facade = getIsFacade();
		initWxLogLogger(facade);
		final MutableBoolean initialized = new MutableBoolean();
		final BiConsumer<Level,String> logger = (lv,msg) -> {
			if (initialized.isSet()) {
				logWxLogMsg(lv, msg);
			}
		};
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
				registerLoggersFor(logger, pPkgName);
			}
		};
		final ILifecycleController.TerminableListener shutDownHook = new ILifecycleController.TerminableListener() {
			@Override
			public void start() {
				// Does nothing.
			}
			
			@Override
			public void shutdown() {
				logger.accept(Level.info, "Uninstalling package listener from IS server");
				PackageManager.removePackageListener(pkgListener);
			}
		}; 
		cf.requireInstance(ILifecycleController.class).addListener(shutDownHook);
		logger.accept(Level.info, "Installing package listener in IS server");
		PackageManager.addPackageListener(pkgListener);
		for (com.wm.app.b2b.server.Package pkg : PackageManager.getAllPackages()) {
			if (pkg.isEnabled()) {
				registerLoggersFor(logger, pkg.getName());
			}
		}
		logger.accept(Level.info, "WxLogNg package is started at " + DateTimeFormatter.BASIC_ISO_DATE.format(ZonedDateTime.now()));
		return NO_RESULT;
	}

	protected void initWxLogLogger(IIsFacade pFacade) {
		final BiConsumer<Level,String> nullLogger = (lv,msg) -> {/* Do nothing */};
		final IComponentFactory cf = getComponentFactory();
		final String packageName = pFacade.getCurrentPkgId();
		final String uri = "./packages/" + packageName
				+ "/config/wxLogNg/WxLogNg-logger.properties";
		readLogger(nullLogger, packageName, 
				   cf.requireInstance(Properties.class),
				   pFacade, uri);
	}

	protected void registerLoggersFor(BiConsumer<Level,String> pLogger, String pPackageName) {
		pLogger.accept(Level.debug, "registerLoggersFor: -> " + pPackageName);
		final IComponentFactory cf = getComponentFactory();
		final Properties defaultProperties = cf.requireInstance(Properties.class);
		findLoggerDescriptors(pLogger, pPackageName, defaultProperties);
		pLogger.accept(Level.debug, "registerLoggersFor: <- " + pPackageName);
	}

	protected void findLoggerDescriptors(BiConsumer<Level,String> pLogger,
			                             String pPackageName,
			                             Properties pDefaultProperties) {
		final IIsFacade facade = getIsFacade();
		final String[] uris = facade.findFilesInDir("./packages/" + pPackageName + "/config/wxLogNg");
		if (uris != null) {
			for (String uri : uris) {
				readLogger(pLogger, pPackageName, pDefaultProperties, facade, uri);
			}
		}
	}

	protected void readLogger(BiConsumer<Level, String> pLogger, String pPackageName,
			                  Properties pDefaultProperties,
			                  final IIsFacade facade, String uri) {
		Properties props = null;
		if (uri.endsWith(".properties.xml")) {
			try (InputStream is = facade.read(uri)) {
				props = new Properties();
				props.loadFromXML(is);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		} else if (uri.endsWith(".properties")) {
			try (InputStream is = facade.read(uri)) {
				props = new Properties();
				props.load(is);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		if (props == null) {
			pLogger.accept(Level.trace, "Ignoring logger descriptor file: " + uri);
		}
		if (props != null) {
			pLogger.accept(Level.debug, "Found logger descriptor file: " + uri);
			try {
				registerLogger(pLogger, pPackageName, uri, props, pDefaultProperties);
			} catch (DuplicateLoggerIdException|NullPointerException|IllegalArgumentException e) {
				pLogger.accept(Level.error,
						e.getClass().getSimpleName() + ": " + e.getMessage());
			}
		}
	}
	
	protected void registerLogger(BiConsumer<Level,String> pLogger,
                                  String pPackageName, String pUri,
                                  Properties pProps, Properties pDefaultProps) {
		@SuppressWarnings("unchecked")
		final Map<String,ILogEngine<?>> engineMap = (Map<String,ILogEngine<?>>)
				getComponentFactory().requireInstance(Map.class, ILogEngine.class.getName());

		final String loggerId = pProps.getProperty("loggerId");
		if (loggerId == null) {
			throw new NullPointerException("Missing property in logger descriptor file " + pUri + ": loggerId");
		}
		if (loggerId.length() == 0) {
			throw new IllegalArgumentException("Empty property in logger descriptor file " + pUri + ": loggerId");
		}
		final String levelStr = pProps.getProperty("logLevel", pDefaultProps.getProperty("default.logLevel"));
		if (levelStr == null) {
			throw new NullPointerException("Missing property in logger descriptor file " + pUri + ": logLevel");
		}
		if (levelStr.length() == 0) {
			throw new IllegalArgumentException("Empty property in logger descriptor file " + pUri + ": logLevel");
		}
		final Level level;
		try {
			level = Level.valueOf(levelStr.toLowerCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid value for property logLevel in logger descriptor file " + pUri + ": " + levelStr);
		}
		final String fileName = pProps.getProperty("logFileName");
		final String maxFileSizeStr = pProps.getProperty("maxSize", pDefaultProps.getProperty("default.maxSize"));
		if (maxFileSizeStr == null) {
			throw new NullPointerException("Missing property in logger descriptor file " + pUri + ": maxSize");
		}
		if (maxFileSizeStr.length() == 0) {
			throw new IllegalArgumentException("Empty property in logger descriptor file " + pUri + ": maxSize");
		}
		final long maxFileSize;
		try {
			maxFileSize = Long.parseLong(maxFileSizeStr);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid value for property maxFileSize in logger descriptor file " + pUri + ": " + maxFileSizeStr);
		}
		final String maxGenerationsStr = pProps.getProperty("maxGenerations");
		if (maxGenerationsStr == null) {
			throw new NullPointerException("Missing property in logger descriptor file " + pUri + ": maxGenerations");
		}
		if (maxGenerationsStr.length() == 0) {
			throw new IllegalArgumentException("Empty property in logger descriptor file " + pUri + ": maxGenerations");
		}
		final int maxGenerations;
		try {
			maxGenerations = Integer.parseInt(maxGenerationsStr);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid value for property maxGenerations in logger descriptor file " + pUri + ": " + maxGenerationsStr);
		}
		final String layout = pProps.getProperty("layout", pDefaultProps.getProperty("default.layout"));
		if (layout == null) {
			throw new NullPointerException("Missing property in logger descriptor file " + pUri + ": layout");
		}
		if (layout.length() == 0) {
			throw new IllegalArgumentException("Empty property in logger descriptor file " + pUri + ": layout");
		}
		final String dirStr = pProps.getProperty("dir", pDefaultProps.getProperty("default.logDir"));
		if (dirStr == null) {
			throw new NullPointerException("Missing property in logger descriptor file " + pUri + ": dir");
		}
		if (dirStr.length() == 0) {
			throw new IllegalArgumentException("Empty property in logger descriptor file " + pUri + ": dir");
		}
		final Path dir = Paths.get(dirStr);
		if (!Files.isDirectory(dir)) {
			throw new IllegalArgumentException("Invalid value for property dir in logger descriptor file " + pUri
					                           + ": " + dirStr
					                           + " (Does not exist, or is not a directory)");
		}
		final String engineId = pProps.getProperty("engineId", pDefaultProps.getProperty("default.engineId"));
		if (engineId == null) {
			throw new NullPointerException("Missing property in logger descriptor file " + pUri + ": engineId");
		}
		if (engineId.length() == 0) {
			throw new IllegalArgumentException("Empty property in logger descriptor file " + pUri + ": engiineId");
		}
		if (!engineMap.containsKey(engineId)) {
			throw new IllegalArgumentException("Invalid value for property engineId in logger descriptor file " + pUri
					                           + ": " + engineId
					                           + " (No such engine is registered)");
		}
		final ILoggerMetaData lmd = new ILoggerMetaData() {
			@Override public int getMaxGenerations() { return maxGenerations; }
			@Override public long getMaxFileSize() { return maxFileSize; }
			@Override public String getLoggerId() { return loggerId; }
			@Override public Level getLevel() { return level; }
			@Override public String getLayout() { return layout; }
			@Override public String getFile() { return fileName; }
			@Override public Path getDir() { return dir; }
			@Override public String getPackageName() { return pPackageName; }
			@Override public String getEngineId() { return engineId; }
		};
		pLogger.accept(Level.info, "Logger registration: loggerId=" + loggerId
				                + ", packageName=" + pPackageName
				                + ", level=" + level.name()
				                + ", layout=" + layout
				                + ", fileName=" + fileName
				                + ", maxFileSize=" + maxFileSize
				                + ", maxGenerations=" + maxGenerations
				                + ", dir=" + dir.toAbsolutePath().toString());
		getComponentFactory().requireInstance(ILoggerRegistry.class).registerLogger(lmd);
	}
}
