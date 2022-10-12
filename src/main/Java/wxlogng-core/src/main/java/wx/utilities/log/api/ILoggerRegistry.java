package wx.utilities.log.api;

import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

public interface ILoggerRegistry {
	public void log(@Nonnull String pLoggerId, @Nonnull Level pLevel, @Nonnull Supplier<String> pMsg);
	public boolean isLevelEnabled(@Nonnull String pLoggerId, @Nonnull Level pLevel);
	public void registerLogger(@Nonnull LoggerMetaData pMetaData);
	public void updateLogger(@Nonnull LoggerMetaData pMetaData);
	public void foreach(@Nonnull Consumer<LoggerMetaData> pConsumer);
}
