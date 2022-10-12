package wx.utilities.log.utils;

import java.util.Stack;

import com.wm.app.b2b.server.InvokeState;
import com.wm.lang.ns.NSService;

public class IsUtils {
	public static String getCurrentCallingPackageName() {
		@SuppressWarnings("unchecked")
		final Stack<NSService> stack = (Stack<NSService>) InvokeState.getCurrentState().getCallStack();
		if (stack == null  ||  stack.isEmpty()) {
			throw new IllegalStateException("Current call stack not found.");
		}
		final NSService service = stack.peek();
		if (service == null) {
			throw new IllegalStateException("Calling service not found.");
		}
		return service.getPackage().getName();
	}

	public static String getCurrentCallingService() {
		@SuppressWarnings("unchecked")
		final Stack<NSService> stack = (Stack<NSService>) InvokeState.getCurrentState().getCallStack();
		if (stack == null  ||  stack.isEmpty()) {
			throw new IllegalStateException("Current call stack not found.");
		}
		final NSService service = stack.peek();
		if (service == null) {
			throw new IllegalStateException("Calling service not found.");
		}
		return service.getNSName().getFullName();
	}
}
