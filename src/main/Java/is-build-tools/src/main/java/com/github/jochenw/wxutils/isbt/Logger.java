package com.github.jochenw.wxutils.isbt;

import com.github.jochenw.afw.core.util.Exceptions;
import com.github.jochenw.afw.core.util.Objects;
import com.github.jochenw.afw.core.util.Strings;

public abstract class Logger {
	public static enum Level {
		trace, debug, info, warn, error, fatal;
	}
	private Level level = Level.info;

	protected abstract void log(Level pLevel, String pMsg);
	protected int getLevel(String pLevel) {
		switch(pLevel) {
		  case "trace": return 0;
		  case "debug": return 1;
		  case "info":  return 2;
		  case "warn":  return 3;
		  case "error": return 4;
		  case "fatal": return 5;
		  default: throw new IllegalArgumentException("Invalid level: " + pLevel);
		}
	}
	protected boolean isEnabled(Level pLevel) {
		return pLevel.ordinal() >= level.ordinal();
	}
	protected void log(Level pLevel, String pMsg, Object... pParams) {
		if (isEnabled(pLevel)) {
			final String msg = Strings.formatCb(pMsg, pParams);
			log(pLevel, msg);
		}
	}
	protected void log(Level pLevel, Throwable pTh) {
		if (isEnabled(pLevel)) {
			final String msg = Exceptions.toString(pTh);
			log(pLevel, msg);
		}
	}
	public void setLevel(Level pLevel) {
		level = Objects.requireNonNull(pLevel);
	}
	public void trace(String pMsg, Object... pParams) { log(Level.trace, pMsg, pParams); }
	public void debug(String pMsg, Object... pParams) { log(Level.debug, pMsg, pParams); }
	public void info(String pMsg, Object... pParams) { log(Level.info, pMsg, pParams); }
	public void warn(String pMsg, Object... pParams) { log(Level.warn, pMsg, pParams); }
	public void error(String pMsg, Object... pParams) { log(Level.error, pMsg, pParams); }
	public void fatal(String pMsg, Object... pParams) { log(Level.fatal, pMsg, pParams); }

	public static final Logger NULL_LOGGER = new Logger() {
		@Override
		protected void log(Level pLevel, String pMsg) { /* Do nothing. */ }
	};
}
