package wx.logNg.pub;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.github.jochenw.wxutils.logng.svc.IIsSvc;
import com.github.jochenw.wxutils.logng.svc.ParmsLogSvc;
import com.github.jochenw.wxutils.logng.svc.PlainLogEnabledSvc;
import com.github.jochenw.wxutils.logng.svc.PlainLogSvc;
import com.softwareag.util.IDataMap;
// --- <<IS-END-IMPORTS>> ---

public final class parm

{
	// ---( internal utility methods )---

	final static parm _instance = new parm();

	static parm _newInstance() { return new parm(); }

	static parm _cast(Object o) { return (parm)o; }

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
		IIsSvc.run(ParmsLogSvc.class, pipeline);
		// --- <<IS-END>> ---

                
	}
}

