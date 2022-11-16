package wx.logng.svc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.Assert;
import org.junit.Test;

import wx.logng.api.ILogEvent;
import wx.logng.api.ILogEvent.Level;

import com.github.jochenw.afw.core.ResourceLocator;
import com.github.jochenw.afw.core.util.Strings;
import com.softwareag.util.IDataMap;

import wx.logng.api.ILogRegistry;
import wx.logng.api.ILogRegistry.LoggerMetaData;
import wx.logng.impl.DefaultLogRegistry.ILog;
import wx.logng.impl.DefaultLogRegistry.ILogFactory;
import wx.logng.layout.ILayout;
import wx.logng.layout.ILayoutFactory;


public class LogServiceTest {
	@Test
	public void testLog() {
		final List<String> logLines = new ArrayList<>();
		final LogService logService = SvcTests.getService(LogService.class, (b) -> {
			b.bind(ILayoutFactory.class).toInstance(new ILayoutFactory() {
				@Override
				public ILayout create(String pLayoutSpecification) {
					return new ILayout() {
						@Override
						public String format(ILogEvent pEvent) {
							return "loggerId=" + pEvent.getLoggerId()
									+ ", logLevel=" + pEvent.getLevel()
									+ ", msgId=" + pEvent.getMsgId()
									+ ", serviceId=" + pEvent.getServiceId()
									+ ", qServiceId=" + pEvent.getQServiceId()
									+ ", threadId=" + pEvent.getThreadId()
									+ ", timeStamp=" + pEvent.getTimestamp()
									+ ", msg=" + pEvent.getMessage();
						}
					};
				}
			});
			b.bind(ILogFactory.class).toInstance(new ILogFactory() {
				@Override
				public ILog create(LoggerMetaData pMetaData) {
					return new ILog() {
						@Override
						public void close() throws Exception {
							logLines.add("closed");
						}

						@Override
						public void log(String pMsg) {
							logLines.add(pMsg);
							System.out.println(pMsg);
						}
					};
				}
			});
		});
		
		try {
			log(logService, Level.info, "Log service is not yet initialized");
			fail("Expected Exception");
		} catch (IllegalArgumentException e) {
			assertEquals("Logger Id has not been registered: MyLoggerId", e.getMessage());
		}
		final ILogRegistry.LoggerMetaData metaData
			= new ILogRegistry.LoggerMetaData("MyLoggerId", Level.debug, "MyLayout",
				                              "MyFile", 5, 1000000l);
		logService.getComponentFactory().requireInstance(ILogRegistry.class).register(metaData);
		assertNotNull(logService);
		log(logService, Level.info, "Log service is now initialized");
		log(logService, Level.trace, "Trace messages are not being logged.");
		log(logService, Level.debug, "But debug messages are.");
		log(logService, Level.warn, "So are warnings.");
		log(logService, Level.error, "And error messages.");
		assertEquals(4, logLines.size());
		assertLogLine(logLines, 0, Level.info, "Log service is now initialized");
		assertLogLine(logLines, 1, Level.debug, "But debug messages are.");
		assertLogLine(logLines, 2, Level.warn, "So are warnings.");
		assertLogLine(logLines, 3, Level.error, "And error messages.");
	}

	protected void assertLogLine(List<String> pLogLines, int pIndex, Level pLevel, String pMsg) {
		String logLevel = null, msg = null;
		for (String tok : Strings.tokenize(pLogLines.get(pIndex), ", ")) {
			final int offset = tok.indexOf('=');
			assertTrue(offset > 0);
			final String key = tok.substring(0, offset);
			final String value = tok.substring(offset+1);
			switch(key) {
			  case "loggerId": assertEquals("MyLoggerId", value); break;
			  case "logLevel": logLevel = value; break;
			  case "msgId": assertEquals("", value); break;
			  case "serviceId": assertEquals("mockService", value); break;
			  case "qServiceId": assertEquals("mock.pkg:mockService", value); break;
			  case "threadId": assertEquals(Thread.currentThread().getName(), value); break;
			  case "timeStamp": break; // Varies with every test, so not really testable.
			  case "msg": msg = value; break;
			  default: throw new IllegalStateException("Invalid event attribute: " + key);
			}
		}
		assertEquals(pLevel.name(), logLevel);
		assertEquals(pMsg, msg);
			
	}

	protected void log(LogService pService, Level pLevel, String pMsg) {
		final IDataMap map = new IDataMap();
		map.put("message", pMsg);
		map.put("level", pLevel.name());
		map.put("loggerId", "MyLoggerId");
		pService.run(map.getIData());
	}
}
