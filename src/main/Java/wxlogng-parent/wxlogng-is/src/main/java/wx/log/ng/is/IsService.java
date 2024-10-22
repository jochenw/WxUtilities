package wx.log.ng.is;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import com.github.jochenw.afw.core.data.Data;
import com.github.jochenw.afw.core.data.Data.Accessible;
import com.github.jochenw.afw.di.util.Exceptions;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;

import wx.log.ng.core.app.WxLogNg;

public abstract class IsService {
	public static final Object[] NO_RESULT = null;
	public abstract Object[] run(Accessible pInput);
	protected Object[] result(Object... pKeyValuePairs) { return pKeyValuePairs; }

	public void run(IData pPipeline) {
		final IDataCursor crsr = pPipeline.getCursor();
		final Function<String,Object> keyValueFunction = (k) -> { return IDataUtil.get(crsr, k); };
		final Accessible input = Data.Accessible.of(keyValueFunction);
		final Object[] output = run(input);
		if (output != null) {
			if (output.length %2 == 1) {
				throw new IllegalStateException("The service output is supposed"
						+ " to be a list of key/value pairs, but has an odd number of elements.");
			}
			for (int i = 0;  i < output.length;  ) {
				Object k = output[i++];
				if (k instanceof String) {
					final String key = (String) k;
					IDataUtil.put(crsr, key, output[i++]);
				}
			}
		}
	}

	private static final ConcurrentMap<String,IsService> services = new ConcurrentHashMap<>();
	public static void run(Class<? extends IsService> pType, IData pPipeline) {
		final String key = pType.getName();
		final IsService svc = services.computeIfAbsent(key, (k) -> {
			try {
				@SuppressWarnings("unchecked")
				final Constructor<IsService> constructor = (Constructor<IsService>) pType.getConstructor();
				IsService instance = constructor.newInstance();
				WxLogNg.getInstance().getComponentFactory().init(instance);
				return instance;
			} catch (Throwable t) {
				throw Exceptions.show(t);
			}
		});
		svc.run(pPipeline);
	}
}
