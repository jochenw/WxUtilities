package wx.log.ng.pub;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class plain

{
	// ---( internal utility methods )---

	final static plain _instance = new plain();

	static plain _newInstance() { return new plain(); }

	static plain _cast(Object o) { return (plain)o; }

	// ---( server methods )---




	public static final void isLevelEnabled (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(isLevelEnabled)>> ---
		// @sigtype java 3.5
		// [i] field:0:required level {"info","debug","trace","warn","error","fatal"}
		// [o] field:0:required enabled {"false","true"}
	
		// --- <<IS-END>> ---

                
	}



	public static final void log (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(log)>> ---
		// @sigtype java 3.5
		// [i] field:0:required message
		// [i] field:0:required level {"info","debug","trace","warn","error","fatal"}
		// [i] field:0:required packageName
		// [i] field:0:required serviceName
		// --- <<IS-END>> ---

                
	}
}

