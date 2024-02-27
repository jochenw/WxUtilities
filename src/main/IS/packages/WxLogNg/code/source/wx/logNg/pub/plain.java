package wx.logNg.pub;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.github.jochenw.wxutils.logng.svc.IIsSvc;
import com.github.jochenw.wxutils.logng.svc.PlainLogEnabledSvc;
import com.github.jochenw.wxutils.logng.svc.PlainLogSvc;
// --- <<IS-END-IMPORTS>> ---

public final class plain

{
	// ---( internal utility methods )---

	final static plain _instance = new plain();

	static plain _newInstance() { return new plain(); }

	static plain _cast(Object o) { return (plain)o; }

	// ---( server methods )---




	public static final void isEnabled (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(isEnabled)>> ---
		// @sigtype java 3.5
		// [i] field:0:required loggerId
		// [i] field:0:required level {"trace","debug","info","warn","error","fatal"}
		// [o] field:0:required enabled
		IIsSvc.run(PlainLogEnabledSvc.class, pipeline);
		// --- <<IS-END>> ---

                
	}



	public static final void log (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(log)>> ---
		// @sigtype java 3.5
		// [i] field:0:required loggerId
		// [i] field:0:required level {"trace","debug","info","warn","error","fatal"}
		// [i] field:0:required msg
		// [i] field:0:optional pkgName
		// [i] field:0:optional svcId
		// [i] field:0:optional qSvcId
		IIsSvc.run(PlainLogSvc.class, pipeline);
		// --- <<IS-END>> ---

                
	}
}

