package wx.utilities.log.api;

public interface ILoggerFactory {
	public ILogger create(LoggerMetaData pLoggerMetaData);
}
