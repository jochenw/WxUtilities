package wx.log.ng.is.svc.plain;

import com.github.jochenw.afw.core.data.Data.Accessible;

import wx.log.ng.core.app.Level;

public class PlainLogService extends AbstractLogService {
	private final Level level;

	public PlainLogService() {
		this(null);
	}

	protected PlainLogService(Level pLevel) {
		level = pLevel;
	}

	@Override
	protected String getLogMessage(Accessible pInput) {
		return pInput.requireString("message");
	}

	@Override
	protected Level getLevel(Accessible pInput) {
		if (level == null) {
			return super.getLevel(pInput);
		} else {
			return level;
		}
	}

	
}
