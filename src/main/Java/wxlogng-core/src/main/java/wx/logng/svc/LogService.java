package wx.logng.svc;

import java.time.ZonedDateTime;

import javax.inject.Inject;

import com.github.jochenw.afw.core.data.Data;
import com.github.jochenw.afw.core.util.Strings;
import com.softwareag.util.IDataMap;

import wx.logng.api.ILogEvent;
import wx.logng.api.ILogRegistry;
import wx.logng.api.ILogEvent.Level;

public class LogService extends AbstractWxLogNgService {
	private @Inject IIsEnvironment isEnv;
	private @Inject ILogRegistry loggerRegistry;
	
	@Override
	public Object[] run(IDataMap pInput) throws Exception {
		final String msg = Data.requireString(pInput, "message");
		final String loggerId = Data.requireString(pInput, "loggerId");
		final String levelStr = Data.requireString(pInput, "level");
		final Level level = Level.valueOf(levelStr.toLowerCase());
		final ZonedDateTime zdt = ZonedDateTime.now();
		final ILogEvent event = new ILogEvent() {
			@Override
			public String getLoggerId() { return loggerId; }
			@Override
			public String getMessage() { return msg; }
			@Override
			public Level getLevel() { return level; }
			@Override
			public ZonedDateTime getTimestamp() { return zdt; }
			@Override
			public String getPackageId() {
				return Strings.notEmpty(Data.getString(pInput, "packageId"),
						                isEnv::getCallingPackageName);
			}
			@Override
			public String getServiceId() {
				return Strings.notEmpty(Data.getString(pInput, "serviceId"),
		                isEnv::getCallingServiceId);
			}
			@Override
			public String getQServiceId() {
				return Strings.notEmpty(Data.getString(pInput, "serviceQId"),
		                isEnv::getCallingServiceQId);
			}
			@Override
			public String getThreadId() {
				return Strings.notEmpty(Data.getString(pInput, "serviceQId"),
		                () ->Thread.currentThread().getName());
			}
			@Override
			public String getMsgId() {
				return Strings.notEmpty(Data.getString(pInput, "msgId"), "");
			}
		};
		loggerRegistry.log(event);
		return NO_RESULT;
	}
}
