package wx.log.ng.is.svc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import com.github.jochenw.afw.core.data.Data;
import com.github.jochenw.afw.core.data.Data.Accessible;
import com.github.jochenw.afw.core.function.Functions.FailableConsumer;
import com.github.jochenw.afw.core.function.Functions.FailableFunction;
import com.github.jochenw.afw.core.inject.AfwCoreOnTheFlyBinder;
import com.github.jochenw.afw.core.log.ILog.Level;
import com.github.jochenw.afw.core.log.ILogFactory;
import com.github.jochenw.afw.core.log.simple.SimpleLogFactory;
import com.github.jochenw.afw.core.util.NotImplementedException;
import com.github.jochenw.afw.di.api.Application;
import com.github.jochenw.afw.di.api.IComponentFactory;
import com.github.jochenw.afw.di.api.Module;
import com.github.jochenw.afw.di.util.Exceptions;
import com.softwareag.util.IDataMap;
import com.wm.data.IData;

import wx.log.ng.core.app.ILogEvent;
import wx.log.ng.core.app.ILoggerRegistry;
import wx.log.ng.core.app.ILoggerRegistry.ILogger;
import wx.log.ng.core.app.IsFacade;
import wx.log.ng.core.app.WxLogNg;
import wx.log.ng.is.IsService;
import wx.log.ng.is.svc.plain.AbstractLogService;

public class Tests {
	public static class LogTest {
		private final ITestLoggerRegistry testLoggerRegistry;
		
		LogTest(IComponentFactory pComponentFactory) {
			testLoggerRegistry = pComponentFactory.requireInstance(ITestLoggerRegistry.class);
		}

		public IData plainPipeline(wx.log.ng.core.app.Level pLevel, String pLoggerId, String pMsg) {
			final IDataMap pipelineMap = new IDataMap();
			pipelineMap.put("message", pMsg);
			pipelineMap.put("loggerId", pLoggerId);
			if (pLevel != null) {
				pipelineMap.put("level", pLevel.name());
			}
			return pipelineMap.getIData();
		}

		public void log(Class<? extends AbstractLogService> pService, IData pPipeline) {
			IsService.run(pService, pPipeline);
		}

		public void assertLogged(Class<? extends AbstractLogService> pService, IData pPipeline,
				                 wx.log.ng.core.app.Level pLevel, String pLoggerId, String pMsg) {
			final List<ILogEvent> logFile = testLoggerRegistry.requireLogFile(pLoggerId);
			final int size = logFile.size();
			IsService.run(pService, pPipeline);
			assertEquals(size+1, logFile.size());
			final ILogEvent event = logFile.get(size);
			assertEquals(pLevel, event.getLogLevel());
			assertEquals(pLoggerId, event.getLoggerId());
			assertEquals(pMsg, event.getLogMessage());
		}

		public void assertNotLogged(Class<? extends AbstractLogService> pService, IData pPipeline,
				                    String pLoggerId) {
			final List<ILogEvent> logFile = testLoggerRegistry.getLogFile(pLoggerId);
			final int size = logFile.size();
			IsService.run(pService, pPipeline);
			assertEquals(size, logFile.size());
		}
	}

	public interface ITestLoggerRegistry extends ILoggerRegistry {
		public List<ILogEvent> getLogFile(String pLoggerId);
		public default List<ILogEvent> requireLogFile(String pLoggerId) {
			final List<ILogEvent> logFile = getLogFile(pLoggerId);
			if (logFile == null) {
				throw new NoSuchElementException("No such log file: " + pLoggerId);
			}
			return logFile;
		}
	}
	public static LogTest of(Class<?> pTestClass, Properties pProperties) {
		return of(pTestClass, pProperties, null);
	}
	public static LogTest of(Class<?> pTestClass, Properties pProperties, Module pModule) {
		final Path testDir = Paths.get("target/unit-tests", pTestClass.getSimpleName());
		try {
			Files.createDirectories(testDir);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		final Map<String,List<ILogEvent>> logFilesById = new HashMap<>();
		final ITestLoggerRegistry loggerRegistry = newLoggerRegistry(logFilesById);
		final Module module = (b) -> {
			b.bind(Path.class, "testsDir").toInstance(testDir.getParent());
			b.bind(Path.class, "testDir").toInstance(testDir);
			b.bind(ITestLoggerRegistry.class).toInstance(loggerRegistry);
			b.bind(ILoggerRegistry.class).toInstance(loggerRegistry);
		};
		final Level logLevel = Accessible.of(pProperties).getEnum(Level.class, "logLevel");
		final ILogFactory lf = SimpleLogFactory.ofSystemOut(logLevel == null ? Level.TRACE : logLevel);
		WxLogNg.init(newIsFacade(pProperties), module.extend(pModule), lf, pProperties); 
		final Application application = Application.of(module.extend(pModule), "jakarta", new AfwCoreOnTheFlyBinder());
		return new LogTest(application.getComponentFactory());
	}

	private static IsFacade newIsFacade(Properties pProperties) {
		return new IsFacade() {
			@Override
			public boolean hasFile(String pUri) {
				return "test.properties".equals(pUri);
			}

			@Override
			public void readFile(String pUri, FailableConsumer<InputStream, ?> pReader) {
			}

			@Override
			public <O> O readFile(String pUri, FailableFunction<InputStream, O, ?> pReader) {
				if ("test.properties".equals(pUri)) {
					final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					try {
						pProperties.store(baos, null);
						final byte[] bytes = baos.toByteArray();
						try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
							return pReader.apply(bais);
						} catch (Throwable t) {
							throw Exceptions.show(t);
						}
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				} else {
					throw new IllegalStateException("No such file: " + pUri);
				}
			}

			@Override
			public String getWxLogNgPackageName() { return "WxLogNg"; }
			@Override
			public String getCurrentPackageName() { throw new NotImplementedException(); }
			@Override
			public IServiceName getCallingServiceName() { throw new NotImplementedException(); }
		};
	}
	
	private static ITestLoggerRegistry newLoggerRegistry(Map<String,List<ILogEvent>> pMap) {
		final Map<String,ILogger> loggers = new HashMap<>();
		return new ITestLoggerRegistry() {
			@Override
			public ILogger getLogger(String pId) {
				return loggers.computeIfAbsent(pId, (k) -> {
					final Properties properties = WxLogNg.getInstance().getComponentFactory().requireInstance(Properties.class);
					final String levelStr = Data.requireString(properties, "logger." + k + ".level");
					final wx.log.ng.core.app.Level level = wx.log.ng.core.app.Level.valueOf(levelStr); 
					final MetaData metaData = new MetaData(k, level);
					return new ILogger() {
						@Override public MetaData getMetaData() { return metaData; }

						@Override
						public void log(ILogEvent pEvent) {
							final String loggerId = pEvent.getLoggerId();
							final List<ILogEvent> logFile = pMap.computeIfAbsent(loggerId, (k) -> new ArrayList<>());
							logFile.add(pEvent);
						}

						@Override
						public boolean isEnabledFor(wx.log.ng.core.app.Level pLevel) {
							return pLevel.ordinal() >= level.ordinal();
						}
					};
				});
			}

			@Override
			public List<ILogEvent> getLogFile(String pLoggerId) {
				return pMap.computeIfAbsent(pLoggerId, (k) -> new ArrayList<ILogEvent>());
			}
		};
	}
}
