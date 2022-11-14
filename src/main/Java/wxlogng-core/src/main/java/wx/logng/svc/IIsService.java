package wx.logng.svc;

import com.github.jochenw.afw.core.util.Objects;
import com.github.jochenw.afw.di.api.IComponentFactory;
import com.github.jochenw.afw.di.util.Exceptions;
import com.softwareag.util.IDataMap;
import com.wm.data.IData;

public interface IIsService {
	Object[] run(IDataMap pInput) throws Exception;

	public static Object[] NO_RESULT = null;

	public default void run(IData pPipeline) {
		final IDataMap pipe = new IDataMap(pPipeline);
		final Object[] result;
		try {
			result = run(pipe);
		} catch (Throwable t) {
			throw Exceptions.show(t);
		}
		if (result != null) {
			for (int i = 0;  i < result.length; ) {
				final String key = (String) result[i++];
				final Object value = result[i++];
				if (value != null) {
					pipe.put(key, value);
				}
			}
		}
	}

	public static void run(Class<?extends IIsService> pServiceType, IData pPipeline) {
		final Class<? extends IIsService> serviceType = Objects.requireNonNull(pServiceType, "Service Type");
		final IData pipeline = Objects.requireNonNull(pPipeline, "Pipeline");
		final IComponentFactory cf = WxLogNg.getInstance().getComponentFactory();
		cf.requireInstance(IIsService.class, serviceType.getName()).run(pipeline);
	}
}
