package wx.rmtadm.impl.utils;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import com.github.jochenw.afw.core.data.Data;
import com.github.jochenw.afw.core.util.Streams;
import com.softwareag.util.IDataMap;
// --- <<IS-END-IMPORTS>> ---

public final class properties

{
	// ---( internal utility methods )---

	final static properties _instance = new properties();

	static properties _newInstance() { return new properties(); }

	static properties _cast(Object o) { return (properties)o; }

	// ---( server methods )---




	public static final void getPropertiesAsDocument (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getPropertiesAsDocument)>> ---
		// @sigtype java 3.5
		// [i] field:0:required fileName1
		// [i] field:0:required fileName2
		// [o] record:0:required properties1
		// [o] record:0:required properties2
		// [o] record:0:required mergedProperties
		final IDataMap pipe = new IDataMap(pipeline);
		final String fileName1 = Data.MAP_ACCESSOR.requireString(pipe, "fileName1");
		final String fileName2 = Data.MAP_ACCESSOR.requireString(pipe, "fileName2");
		final Path filePath1 = Paths.get(fileName1);
		final Path filePath2 = Paths.get(fileName2);
		if (!Files.isRegularFile(filePath1)) {
			throw new IllegalArgumentException("Invalid value for parameter fileName1: "
					+ "Expected existing property file, got " + filePath1);
		}
		final Properties properties1 = Streams.load(filePath1);
		final Properties properties2;
		if (Files.isRegularFile(filePath2)) {
			properties2 = Streams.load(filePath2);
		} else {
			properties2 = new Properties();
		}
		final Properties mergedProperties = new Properties();
		mergedProperties.putAll(properties1);
		mergedProperties.putAll(properties2);
		final Function<Properties,IData> toData = (props) -> {
			final IDataMap map = new IDataMap();
			props.forEach((k,v) -> map.put((String) k, (String) v));
			return map.getIData();
		};
		pipe.put("properties1", toData.apply(properties1));
		pipe.put("properties2", toData.apply(properties2));
		pipe.put("mergedProperties", toData.apply(mergedProperties));
			
		// --- <<IS-END>> ---

                
	}
}

