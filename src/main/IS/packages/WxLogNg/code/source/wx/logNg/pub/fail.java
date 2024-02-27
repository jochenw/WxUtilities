package wx.logNg.pub;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.github.jochenw.wxutils.logng.svc.FailLogSvc;
import com.github.jochenw.wxutils.logng.svc.IIsSvc;
import com.github.jochenw.wxutils.logng.svc.ParmsLogSvc;
import com.github.jochenw.wxutils.logng.svc.PlainLogEnabledSvc;
import com.github.jochenw.wxutils.logng.svc.PlainLogSvc;
// --- <<IS-END-IMPORTS>> ---

public final class fail

{
	// ---( internal utility methods )---

	final static fail _instance = new fail();

	static fail _newInstance() { return new fail(); }

	static fail _cast(Object o) { return (fail)o; }

	// ---( server methods )---




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
		// [i] field:1:optional numberedParameters
		// [i] record:0:optional namedParameters
		IIsSvc.run(FailLogSvc.class, pipeline);
		// --- <<IS-END>> ---

                
	}
}

