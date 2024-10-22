package wx.log.ng.is.svc.params;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

import com.github.jochenw.afw.core.data.Data.Accessible;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;

import wx.log.ng.core.app.Level;
import wx.log.ng.core.util.StringFormatter;
import wx.log.ng.is.svc.plain.AbstractLogService;

public class ParamsLogService extends AbstractLogService {
	private final Level level;
	private final StringFormatter stringFormatter = StringFormatter.getInstance();

	public ParamsLogService() {
		this(null);
	}

	protected ParamsLogService(Level pLevel) {
		level = pLevel;
	}

	@Override
	protected String getLogMessage(Accessible pInput) {
		final String msg = pInput.requireString("message");
		final Object numberedParametersObj = pInput.getValue("numberedParameters");
		final Object[] numberedParametersArray;
		final List<Object> numberedParametersList;
		if (numberedParametersObj == null) {
			numberedParametersArray = null;
			numberedParametersList = null;
		} else if (numberedParametersObj instanceof Object[]) {
			numberedParametersArray = (Object[]) numberedParametersObj;
			numberedParametersList = null;
		} else if (numberedParametersObj instanceof List) {
			numberedParametersArray = null;
			@SuppressWarnings("unchecked")
			final List<Object> list = (List<Object>) numberedParametersObj;
			numberedParametersList = list;
		} else {
			numberedParametersArray = null;
			numberedParametersList = null;
		}
		final IntFunction<Object> numberedParameters = (i)-> {
			if (i < 0) {
				return null;
			}
			Object value = null;
			if (numberedParametersArray != null  &&  i < numberedParametersArray.length) {
				value = numberedParametersArray[i];
			}
			if (value == null  &&  numberedParametersList != null  &&  i < numberedParametersList.size()) {
				value = numberedParametersList.get(i);
			}
			if (value == null) {
				value = pInput.getValue("param" + i);
			}
			return value;
		};
		final Object variables = pInput.getValue("namedParameters");
		final Function<String,Object> namedParameters;
		if (variables == null) {
			namedParameters = pInput::getValue;
		} else {
			if (variables instanceof IData) {
				final IData variableData = (IData) variables;
				final IDataCursor crsr = variableData.getCursor();
				namedParameters = (k) -> {
					Object val = IDataUtil.get(crsr, k);
					if (val == null) {
						val = pInput.getValue(k);
					}
					return val;
				};
				final String result = stringFormatter.format(msg, namedParameters, numberedParameters);
				crsr.destroy();
				return result;
			} else {
				namedParameters = pInput::getValue;
			}
		}
		return stringFormatter.format(msg, namedParameters, numberedParameters);
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
