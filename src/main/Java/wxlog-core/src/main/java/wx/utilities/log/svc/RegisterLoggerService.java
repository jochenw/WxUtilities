package wx.utilities.log.svc;

import java.nio.file.Path;

import com.github.jochenw.afw.core.data.Data;
import com.github.jochenw.afw.core.util.Strings;
import com.softwareag.util.IDataMap;

import wx.utilities.log.api.ILogger.Level;
import wx.utilities.log.api.WxLogApplication;


/** Implementation of service {@code wx.log2.pub.admin:registerLoggerId}.
 * Input Parameters:
 *   - loggerId Id of the logger, that's being created.
 *   - packageId Id of the package, that's responsible for controlling the logger.
 *   - backendId Id of the backend, that ought to create the logger.
 *   - level Logger's level (optional, default level may be used).
 *   - file - Log file to use (optional, default will be "loggerId.log")
 *   - pattern - Pattern of a log file line (optional, default pattern may
 *       be used).
 */
public class RegisterLoggerService extends AbstractIsService {
	@Override
	public Object[] run(IDataMap pInput) throws Exception {
		final String loggerId = Data.requireString(pInput, "loggerId");
		final String packageId = Data.requireString(pInput, "packageId");
		final String backendIdStr = Data.getString(pInput, "backendId");
		final String backendId;
		if (backendIdStr == null  ||  backendIdStr.length() == 0) {
			backendId = WxLogApplication.getInstance().getDefaultBackendId();
		} else {
			backendId = backendIdStr;
		}
		final String levelStr = Strings.notNull(Data.getString(pInput, "level"));
		final Level level;
		if (levelStr.length() == 0) {
			level = WxLogApplication.getInstance().getDefaultLogLevel();
		} else {
			try {
				level = Level.valueOf(levelStr.toLowerCase());
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid value for parameter level: Expected trace|debug|info|warn|error|fatal, got " + levelStr);
			}
		}
		final String fileStr = Data.getString(pInput, "file");
		final String fileName;
		if (fileStr == null  ||  fileStr.length() == 0) {
			fileName = loggerId + ".log";
		} else {
			fileName = fileStr;
		}
		final Path path = WxLogApplication.getInstance().getLogDirectory().resolve(fileName);
		final String patternStr = Data.getString(pInput, "pattern");
		final String pattern;
		if (patternStr == null  ||  patternStr.length() == 0) {
			pattern = WxLogApplication.getInstance().getDefaultPattern();
		} else {
			pattern = patternStr;
		}
		final WxLogController wxLogController = WxLogApplication.getInstance().getComponentFactory().requireInstance(WxLogController.class);
		wxLogController.registerLogger(loggerId, packageId, backendId, level, path, pattern);
		return super.voidResult();
	}
	
}
