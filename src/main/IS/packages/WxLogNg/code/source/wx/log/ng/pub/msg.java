package wx.log.ng.pub;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
// --- <<IS-END-IMPORTS>> ---

public final class msg

{
	// ---( internal utility methods )---

	final static msg _instance = new msg();

	static msg _newInstance() { return new msg(); }

	static msg _cast(Object o) { return (msg)o; }

	// ---( server methods )---




	public static final void asString (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(asString)>> ---
		// @sigtype java 3.5
		// [i] recref:0:required msg wx.log.ng.pub.msg:msg
		// [o] field:0:required msgString
		// --- <<IS-END>> ---

                
	}
}

