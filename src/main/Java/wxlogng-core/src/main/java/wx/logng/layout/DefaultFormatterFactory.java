package wx.logng.layout;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import wx.logng.api.ILogEvent;
import wx.logng.layout.LayoutParser.Visitor;

public class DefaultFormatterFactory implements IFormatterFactory {
	@Override
	public IFormatter create(String pLayoutSpecification) {
		final List<BiConsumer<ILogEvent, StringBuilder>> consumers = parseLayout(pLayoutSpecification);
		final IFormatter formatter = new IFormatter() {
			@Override
			public String format(ILogEvent pEvent) {
				final StringBuilder sb = new StringBuilder();
				consumers.forEach((c) -> c.accept(pEvent, sb));
				return sb.toString();
			}
		};
		return formatter;
	}

	protected List<BiConsumer<ILogEvent, StringBuilder>> parseLayout(String pLayoutSpecification) {
		final List<BiConsumer<ILogEvent,StringBuilder>> consumers = new ArrayList<>();
		final Visitor visitor = new Visitor() {
			@Override
			public void lineTerminator(String pPattern) {
				final String lineTerminator; 
				if (pPattern == null) {
					lineTerminator = System.lineSeparator();
				} else {
					// Handle escape sequences, like \n, or \r
					final StringBuilder sb = new StringBuilder();
					lineTerminator = sb.toString();
				}
				final BiConsumer<ILogEvent,StringBuilder> consumer = (ev, sb) -> sb.append(lineTerminator);
				consumers.add(consumer);
			}

			@Override
			public void loggerId() {
				consumers.add((ev, sb) -> sb.append(ev.getLoggerId()));
			}

			@Override
			public void logLevel() {
				consumers.add((ev, sb) -> sb.append(ev.getLevel().name().toUpperCase()));
			}

			@Override
			public void message() {
				consumers.add((ev, sb) -> sb.append(ev.getMessage()));
			}

			@Override
			public void msgId() {
				consumers.add((ev, sb) -> sb.append(ev.getMsgId()));
			}

			@Override
			public void packageId() {
				consumers.add((ev, sb) -> sb.append(ev.getPackageId()));
			}

			@Override
			public void plainText(String pText) {
				consumers.add((ev, sb) -> sb.append(pText));
			}

			@Override
			public void qServiceId() {
				consumers.add((ev, sb) -> sb.append(ev.getQServiceId()));
			}

			@Override
			public void serviceId() {
				consumers.add((ev, sb) -> sb.append(ev.getServiceId()));
			}

			@Override
			public void threadId() {
				consumers.add((ev, sb) -> sb.append(ev.getThreadId()));
			}

			@Override
			public void timestamp(String pPattern) {
				final DateTimeFormatter dtf;
				if (pPattern == null) {
					dtf = DateTimeFormatter.ISO_DATE_TIME;
				} else {
					try {
						dtf = DateTimeFormatter.ofPattern(pPattern);
					} catch (IllegalArgumentException iae) {
						throw new IllegalArgumentException("Invalid timestamp pattern: " + pPattern + " ("
								+ iae.getMessage() + ")", iae);
					}
				}
				consumers.add((ev, sb) -> sb.append(dtf.format(ev.getTimestamp())));
			}
		};
		new LayoutParser().parse(pLayoutSpecification, visitor);
		return consumers;
	}
}
