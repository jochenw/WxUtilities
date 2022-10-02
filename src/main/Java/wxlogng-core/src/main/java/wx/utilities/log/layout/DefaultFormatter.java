package wx.utilities.log.layout;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.github.jochenw.afw.core.util.Objects;

import wx.utilities.log.layout.LayoutParser.Listener;

public class DefaultFormatter implements IFormatter {
	private final String layoutSpecification;
	private final BiConsumer<StringBuilder,ILogEvent> serializer;

	public DefaultFormatter(String pLayoutSpecification) {
		layoutSpecification = pLayoutSpecification;
		serializer = newSerializer(layoutSpecification);
	}

	public String getLayoutSpecification() {
		return layoutSpecification;
	}

	@Override
	public void format(StringBuilder pSb, ILogEvent pLogEvent) {
		serializer.accept(pSb, pLogEvent);
	}

	protected BiConsumer<StringBuilder,ILogEvent> newSerializer(String pLayoutSpecification) {
		final List<BiConsumer<StringBuilder,ILogEvent>> serializers = new ArrayList<>();
		new LayoutParser().parse(pLayoutSpecification, new Listener() {

			@Override
			public void plainText(String pText) {
				serializers.add((sb,e) -> {
					sb.append(pText);
				});
			}

			@Override
			public void message() {
				serializers.add((sb,e) -> {
					sb.append(e.getMessage());
				});
			}

			@Override
			public void loggerId() {
				serializers.add((sb,e) -> {
					sb.append(e.getLoggerId());
				});
			}

			@Override
			public void messageId() {
				serializers.add((sb,e) -> {
					sb.append(e.getMessageId());
				});
			}

			@Override
			public void threadId() {
				serializers.add((sb,e) -> {
					sb.append(e.getThreadId());
				});
			}

			@Override
			public void packageId() {
				serializers.add((sb,e) -> {
					sb.append(e.getPackageId());
				});
			}

			@Override
			public void serviceId() {
				serializers.add((sb,e) -> {
					sb.append(e.getServiceId());
				});
			}

			@Override
			public void serviceName() {
				serializers.add((sb,e) -> {
					sb.append(e.getServiceName());
				});
			}

			@Override
			public void logLevel() {
				serializers.add((sb,e) -> {
					sb.append(e.getLevel());
				});
			}

			@Override
			public void lineTerminator(String pTerminator) {
				final String  terminator = Objects.notNull(pTerminator, System.lineSeparator());
				serializers.add((sb,e) -> {
					sb.append(terminator);
				});
			}

			@Override
			public void dateTime(String pPattern) {
				final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pPattern);
				serializers.add((sb,e) -> {
					sb.append(dtf.format(e.getDateTime()));
				});
			}
		});
		return  (sb,e) -> {
			serializers.forEach((serializer) -> serializer.accept(sb, e));
		};
	}
}
