package wx.logng.impl;

import java.util.Stack;

import com.wm.app.b2b.server.InvokeState;
import com.wm.lang.ns.NSService;

import wx.logng.svc.IIsEnvironment;

public class DefaultIsEnvironment implements IIsEnvironment {
	@Override
	public String getCallingPackageName() {
		final NSService callingService = getCallingService();
		return callingService.getPackage().getName();
	}

	private NSService getCallingService() {
		final Stack<NSService> callStack = InvokeState.getCurrentState().getCallStack();
		if (callStack.isEmpty()) {
			throw new IllegalStateException("Call stack is empty");
		}
		return callStack.peek();
	}

	@Override
	public String getCallingServiceId() {
		return getCallingService().getNSName().getValue();
	}

	@Override
	public String getCallingServiceQId() {
		return getCallingService().getNSName().getFullName();
	}

}
