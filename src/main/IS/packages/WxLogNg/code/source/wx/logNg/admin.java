package wx.logNg;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.github.jochenw.wxutils.logng.api.WxLogNg;
import com.github.jochenw.wxutils.logng.svc.IIsSvc;
// --- <<IS-END-IMPORTS>> ---

public final class admin

{
	// ---( internal utility methods )---

	final static admin _instance = new admin();

	static admin _newInstance() { return new admin(); }

	static admin _cast(Object o) { return (admin)o; }

	// ---( server methods )---




	public static final void shutDown (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(shutDown)>> ---
		// @sigtype java 3.5
		IIsSvc.run(AdminStartUp.class, pipeline);
			
		// --- <<IS-END>> ---

                
	}



	public static final void startUp (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(startUp)>> ---
		// @sigtype java 3.5
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	private static WxLogNg wxLogNg;
	// --- <<IS-END-SHARED>> ---
}

