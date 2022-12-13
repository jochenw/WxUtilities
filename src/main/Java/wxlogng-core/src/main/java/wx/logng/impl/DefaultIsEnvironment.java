package wx.logng.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;
import java.util.function.Supplier;

import com.github.jochenw.afw.core.util.FileUtils;
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

	@Override
	public Supplier<InputStream> findFile(String pUri) {
		final Path p = Paths.get(pUri);
		if (Files.isRegularFile(p)) {
			return () -> {
				try {
					return Files.newInputStream(p);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			};
		} else {
			return null;
		}
	}
	@Override
	public OutputStream createFile(String pUri) {
		final Path p = Paths.get(pUri);
		try {
			FileUtils.createDirectoryFor(p);
			return Files.newOutputStream(p);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
