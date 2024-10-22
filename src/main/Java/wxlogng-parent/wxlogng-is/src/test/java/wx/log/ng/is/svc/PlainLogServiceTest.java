package wx.log.ng.is.svc;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import wx.log.ng.core.app.Level;
import wx.log.ng.is.svc.Tests.LogTest;
import wx.log.ng.is.svc.plain.PlainDebugLogService;
import wx.log.ng.is.svc.plain.PlainErrorLogService;
import wx.log.ng.is.svc.plain.PlainFatalLogService;
import wx.log.ng.is.svc.plain.PlainInfoLogService;
import wx.log.ng.is.svc.plain.PlainLogService;
import wx.log.ng.is.svc.plain.PlainTraceLogService;
import wx.log.ng.is.svc.plain.PlainWarnLogService;


class PlainLogServiceTest {
	@Test void testPlainLogService() {
		final LogTest logTester = newLogTester();
		// Log level is INFO, so this should work.
		logTester.assertLogged(PlainLogService.class, logTester.plainPipeline(Level.INFO, "foo", "First Log Line"),
				               Level.INFO, "foo", "First Log Line");
		// Log level is INFO, so logging with TRACE shouldn't work.
		logTester.assertNotLogged(PlainLogService.class, logTester.plainPipeline(Level.TRACE, "foo", "This line won't be logged."), "foo");
		// Log level is INFO, so logging with DEBUG shouldn't work.
		logTester.assertNotLogged(PlainLogService.class, logTester.plainPipeline(Level.DEBUG, "foo", "This line won't be logged."), "foo");
		// No Log level given, default log level INFO, so this should work.
		logTester.assertLogged(PlainLogService.class, logTester.plainPipeline(null, "foo", "Second Log Line"),
				               Level.INFO, "foo", "Second Log Line");
		// Log level WARN is above INFO, so this should work.
		logTester.assertLogged(PlainLogService.class, logTester.plainPipeline(Level.WARN, "foo", "This line is also logged"),
				               Level.WARN, "foo", "This line is also logged");
		// Log level ERROR is above INFO, so this should work.
		logTester.assertLogged(PlainLogService.class, logTester.plainPipeline(Level.ERROR, "foo", "This line is also logged"),
				               Level.ERROR, "foo", "This line is also logged");
		// Log level WARN is above INFO, so this should work.
		logTester.assertLogged(PlainLogService.class, logTester.plainPipeline(Level.FATAL, "foo", "So is this line"),
				               Level.FATAL, "foo", "So is this line");
	}

	@Test void testPlainTraceLogService() {
		final LogTest logTesterWithInfoLevel = newLogTester();
		// No log level given, default is TRACE, so this should not be logged.
		logTesterWithInfoLevel.assertNotLogged(PlainTraceLogService.class, logTesterWithInfoLevel.plainPipeline(Level.TRACE, "foo", "Nothing to log"), "foo");
		// Trying to trick by explicitly specifying WARN. PlainTraceLogService should ignore this, so this should not be logged.
		logTesterWithInfoLevel.assertNotLogged(PlainTraceLogService.class, logTesterWithInfoLevel.plainPipeline(Level.WARN, "foo", "Nothing to log"), "foo");
		final LogTest logTesterWithTraceLevel = newLogTester(Level.TRACE, "bar");
		logTesterWithTraceLevel.assertLogged(PlainTraceLogService.class, logTesterWithTraceLevel.plainPipeline(Level.TRACE, "bar", "First log line"),
				                             Level.TRACE, "bar", "First Log Line");
	}

	@Test void testPlainDebugLogService() {
		final LogTest logTester = newLogTester();
		// No log level given, default is DEBUG, so this should not be logged.
		logTester.assertNotLogged(PlainDebugLogService.class, logTester.plainPipeline(Level.TRACE, "foo", "Nothing to log"), "foo");
		// Trying to trick by explicitly specifying WARN. PlainDebugLogService should ignore this, so this should not be logged.
		logTester.assertNotLogged(PlainDebugLogService.class, logTester.plainPipeline(Level.WARN, "foo", "Nothing to log"), "foo");
	}

	@Test void testPlainInfoLogService() {
		final LogTest logTester = newLogTester();
		// No log level given, default log level is INFO, so this should be logged.
		logTester.assertLogged(PlainInfoLogService.class, logTester.plainPipeline(null, "foo", "Also logged."),
				               Level.INFO, "foo", "Also logged.");
		// Log level given, but PlainInfoLogservice should override this, expecting logged with INFO.
		logTester.assertLogged(PlainInfoLogService.class, logTester.plainPipeline(Level.WARN, "foo", "Also logged."),
				               Level.INFO, "foo", "Also logged.");
	}

	@Test void testPlainWarnLogService() {
		final LogTest logTester = newLogTester();
		// No log level given, default log level is INFO, so this should be logged.
		logTester.assertLogged(PlainWarnLogService.class, logTester.plainPipeline(null, "foo", "Also logged."),
				               Level.WARN, "foo", "Also logged.");
		// Log level given, but PlainWarnLogservice should override this, expecting logged with WARN.
		logTester.assertLogged(PlainWarnLogService.class, logTester.plainPipeline(Level.ERROR, "foo", "Also logged."),
				               Level.WARN, "foo", "Also logged.");
	}

	@Test void testPlainErrorLogService() {
		final LogTest logTester = newLogTester();
		// No log level given, default log level is INFO, so this should be logged.
		logTester.assertLogged(PlainErrorLogService.class, logTester.plainPipeline(null, "foo", "Also logged."),
				               Level.ERROR, "foo", "Also logged.");
		// Log level given, but PlainErrorLogservice should override this, expecting logged with WARN.
		logTester.assertLogged(PlainErrorLogService.class, logTester.plainPipeline(Level.FATAL, "foo", "Also logged."),
				               Level.ERROR, "foo", "Also logged.");
	}

	@Test void testPlainFatalLogService() {
		final LogTest logTester = newLogTester();
		// No log level given, default log level is INFO, so this should be logged.
		logTester.assertLogged(PlainFatalLogService.class, logTester.plainPipeline(null, "foo", "Also logged."),
				               Level.FATAL, "foo", "Also logged.");
		// Log level given, but PlainFatalLogservice should override this, expecting logged with WARN.
		logTester.assertLogged(PlainFatalLogService.class, logTester.plainPipeline(Level.WARN, "foo", "Also logged."),
				               Level.FATAL, "foo", "Also logged.");
	}

	private LogTest newLogTester() {
		return newLogTester(Level.INFO, "foo");
	}

	private LogTest newLogTester(Level pLevel, String pLoggerId) {
		final Properties properties = new Properties();
		properties.put("logger." + pLoggerId + ".level", pLevel.name());
		final LogTest logTester = Tests.of(PlainLogServiceTest.class, properties);
		return logTester;
	}
}
