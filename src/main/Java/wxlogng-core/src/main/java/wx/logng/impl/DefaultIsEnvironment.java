package wx.logng.impl;

import java.util.Stack;

import com.github.jochenw.afw.core.util.Holder;
import com.wm.app.b2b.server.InvokeState;
import com.wm.lang.ns.NSService;

import wx.logng.svc.IIsEnvironment;

public class DefaultIsEnvironment implements IIsEnvironment {
	public Object getContext() {
		return new Holder<NSService>();
	}
	@Override
	public String getCallingPackageName(Object pCtx) {
		final NSService callingService = getCallingService(pCtx);
		return callingService.getPackage().getName();
	}

	private NSService getCallingService(Object pCtx) {
		@SuppressWarnings("unchecked")
		final Holder<NSService> holder = (Holder<NSService>) pCtx;
		if (holder != null) {
			final NSService svc = holder.get();
			if (svc != null) {
				return svc;
			}
		}
		final Stack<NSService> callStack = InvokeState.getCurrentState().getCallStack();
		if (callStack.isEmpty()) {
			throw new IllegalStateException("Call stack is empty");
		}
		final NSService svc = callStack.peek();
		if (holder != null) {
			holder.set(svc);
		}
		return svc;
	}

	@Override
	public String getCallingServiceId(Object pCtx) {
		return getCallingService(pCtx).getNSName().getValue();
	}

	@Override
	public String getCallingQServiceId(Object pCtx) {
		return getCallingService(pCtx).getNSName().getFullName();
	}
}
