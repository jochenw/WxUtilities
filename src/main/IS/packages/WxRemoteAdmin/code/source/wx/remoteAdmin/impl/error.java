package wx.remoteAdmin.impl;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.softwareag.util.IDataMap;
// --- <<IS-END-IMPORTS>> ---

public final class error

{
	// ---( internal utility methods )---

	final static error _instance = new error();

	static error _newInstance() { return new error(); }

	static error _cast(Object o) { return (error)o; }

	// ---( server methods )---




	public static final void assertNotEmpty (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(assertNotEmpty)>> ---
		// @sigtype java 3.5
		// [i] field:0:required value
		// [i] field:0:required parameter
		final IDataMap pipe = new IDataMap(pipeline);
		final String value = pipe.getAsString("value");
		final String parameter = pipe.getAsString("parameter", "value");
		if (value == null) {
			throw new NullPointerException("Missing parameter: " + parameter);
		}
		if (value.length() == 0) {
			throw new NullPointerException("Empty parameter: " + parameter);
		}
			
		// --- <<IS-END>> ---

                
	}



	public static final void throwError (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(throwError)>> ---
		// @sigtype java 3.5
		// [i] field:0:required msg
		final IDataMap pipe = new IDataMap(pipeline);
		final String msg = pipe.getAsString("msg");
		if (msg == null) {
			throw new NullPointerException("Missing parameter: msg");
		}
		if (msg.length() == 0) {
			throw new IllegalArgumentException("Empty parameter: msg");
		}
		throw new RuntimeException(msg);
			
		// --- <<IS-END>> ---

                
	}
}

