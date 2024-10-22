package wx.log.ng.is.svc.admin;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;

import com.github.jochenw.afw.core.function.Functions.FailableConsumer;
import com.github.jochenw.afw.core.function.Functions.FailableFunction;
import com.github.jochenw.afw.di.util.Exceptions;
import com.wm.app.b2b.server.InvokeState;
import com.wm.lang.ns.NSService;

import wx.log.ng.core.app.IsFacade;

public class DefaultIsFacade implements IsFacade {
	private String wxLogNgPackageName;

	protected NSService getCurrentNSService() {
		@SuppressWarnings("unchecked")
		final Stack<NSService> callStack = (Stack<NSService>) InvokeState.getCurrentState().getCallStack();
		if (callStack.isEmpty()) {
			throw new IllegalStateException("Insufficient elements in the call stack.");
		}
		return callStack.peek();
	}

	protected NSService getCallingNSService() {
		@SuppressWarnings("unchecked")
		final Stack<NSService> callStack = (Stack<NSService>) InvokeState.getCurrentState().getCallStack();
		if (callStack.size() > 1) {
			return callStack.get(callStack.size()-2);
		} else {
			throw new IllegalStateException("Insufficient elements in the call stack.");
		}
	}
	@Override
	public String getCurrentPackageName() {
		final NSService svc = getCurrentNSService();
		return svc.getPackage().getName();
	}

	@Override
	public IServiceName getCallingServiceName() {
		final NSService svc = getCallingNSService();
		return new IServiceName() {
			@Override
			public String getServiceQName() {
				return svc.getNSName().getFullName();
			}
			
			@Override
			public String getServiceName() {
				return svc.getNSName().getValue();
			}
			
			@Override
			public String getPackageName() {
				return svc.getPackage().getName();
			}
		};
	}

	@Override
	public boolean hasFile(String pUri) {
		final Path path = Paths.get(pUri);
		return Files.isRegularFile(path);
	}

	@Override
	public void readFile(String pUri, FailableConsumer<InputStream, ?> pReader) {
		final Path path = Paths.get(pUri);
		try (InputStream in = Files.newInputStream(path)) {
			pReader.accept(in);
		} catch (Throwable t) {
			throw Exceptions.show(t);
		}
	}

	@Override
	public <O> O readFile(String pUri, FailableFunction<InputStream, O, ?> pReader) {
		final Path path = Paths.get(pUri);
		try (InputStream in = Files.newInputStream(path)) {
			return pReader.apply(in);
		} catch (Throwable t) {
			throw Exceptions.show(t);
		}
	}

	@Override
	public String getWxLogNgPackageName() { return wxLogNgPackageName; }

	public void setWxLogNgPackageName(String pWxLogNgPackageName) { wxLogNgPackageName = pWxLogNgPackageName; }
}
