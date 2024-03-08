package com.github.jochenw.wxutils.logng.fmt;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.github.jochenw.wxutils.logng.fmt.LayoutParser.PCtx;
import com.github.jochenw.wxutis.logng.api.ILogEvent;

public class DefaultLogFormatterFactory implements ILogFormatterFactory {
	public static class DefaultFormatter implements ILogFormatter {
		private final List<Function<ILogEvent,String>> formatters;
	
		public DefaultFormatter(List<Function<ILogEvent,String>> pFormatters) {
			formatters = pFormatters;
		}

		@Override
		public String format(ILogEvent pEvent) {
			final StringBuilder sb = new StringBuilder();
			formatters.forEach((f) -> sb.append(f.apply(pEvent)));
			return sb.toString();
			
		}
	}

	@Override
	public ILogFormatter getFormatter(String pLayout) {
		final List<Function<ILogEvent,String>> formatters = new ArrayList<>();
		final LayoutParser.Listener listener = new LayoutParser.Listener() {
			@Override
			public void dateTime(PCtx pCtx, DateTimeFormatter pDateTimeFormat) {
				formatters.add((e) -> pDateTimeFormat.format(e.getDateTime()));
			}

			@Override
			public void loggerId(PCtx pCtx) {
				formatters.add(ILogEvent::getLoggerId);
			}

			@Override
			public void logLevel(PCtx pCtx) {
				formatters.add((e) -> e.getLevel().name().toUpperCase());
			}

			@Override
			public void pkgId(PCtx pCtx) {
				formatters.add(ILogEvent::getPkgId);
			}

			@Override
			public void svcId(PCtx pCtx) {
				formatters.add(ILogEvent::getSvcId);
			}

			@Override
			public void qSvcId(PCtx pCtx) {
				formatters.add(ILogEvent::getQSvcId);
			}

			@Override
			public void threadId(PCtx pCtx) {
				formatters.add(ILogEvent::getThreadId);
			}

			@Override
			public void msg(PCtx pCtx) {
				formatters.add(ILogEvent::getMsg);
			}

			@Override
			public void literal(PCtx pCtx, String pLiteral) {
				formatters.add((e) -> pLiteral);
			}
		};
		new LayoutParser().parse(pLayout, listener, null);
		return new DefaultFormatter(formatters);
	}
}
