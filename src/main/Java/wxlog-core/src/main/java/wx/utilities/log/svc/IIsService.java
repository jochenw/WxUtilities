package wx.utilities.log.svc;

import java.io.IOException;

import com.github.jochenw.afw.core.util.Exceptions;
import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.ServiceException;
import com.wm.data.IData;

public interface IIsService {
	public Object[] run(IDataMap pInput) throws Exception;
	public default void run(IData pPipeline) throws IOException, ServiceException {
		final IDataMap input = new IDataMap(pPipeline);
		final Object[] output;
		try {
			output = run(input);
		} catch (Exception e) {
			throw Exceptions.show(e, IOException.class, ServiceException.class);
		}
		if (output != null) {
			for (int i = 0;  i < output.length;  i += 2) {
				final String key = (String) output[i];
				final Object value = output[i+1];
				if (value != null) {
					input.put(key, value);
				}
			}
		}
	}

	public static void run(Class<? extends IIsService> pServiceClass, IData pPipeline) throws IOException, ServiceException {
		final IIsService svc;
		try {
			svc = pServiceClass.getConstructor().newInstance();
		} catch (Throwable t) {
			throw Exceptions.show(t, IOException.class, ServiceException.class);
		}
		svc.run(pPipeline);
	}
}
