package wx.utilities.log.svc;

import com.softwareag.util.IDataMap;
import com.wm.data.IData;

public interface IIsService {
	public default Object[] noResult() { return null; }
	public default Object[] result(Object... pValues) { return pValues; }
	public Object[] run(IDataMap pPipeline);
	public default void run(IData pPipeline) {
		final IDataMap map = new IDataMap(pPipeline);
		final Object[] result = run(map);
		if (result != null) {
			for (int i = 0;  i < result.length;  i += 2) {
				final String key = (String) result[i];
				map.put(key, result[i+1]);
			}
		}
	}
}
