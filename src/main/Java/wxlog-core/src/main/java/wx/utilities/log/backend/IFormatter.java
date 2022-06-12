package wx.utilities.log.backend;

import java.util.function.Function;

public interface IFormatter extends Function<LogEvent,byte[]> {
}
