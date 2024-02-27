package wx.logNg;

// -----( IS Java Code Template v1.2

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.github.jochenw.wxutils.logng.api.IIsFacade;
import com.github.jochenw.wxutils.logng.is.DefaultIsFacade;
import com.github.jochenw.wxutils.logng.svc.AdminShutDownSvc;
import com.github.jochenw.wxutils.logng.svc.AdminStartUpSvc;
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
		IIsSvc.run(AdminShutDownSvc.class, pipeline);
			
		// --- <<IS-END>> ---

                
	}



	public static final void startUp (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(startUp)>> ---
		// @sigtype java 3.5
		final IIsFacade facade = new DefaultIsFacade();
		AdminStartUpSvc.init(facade.getCurrentPkgId(), facade, (b) -> {
			
		});
			
			
		// --- <<IS-END>> ---

                
	}
}

