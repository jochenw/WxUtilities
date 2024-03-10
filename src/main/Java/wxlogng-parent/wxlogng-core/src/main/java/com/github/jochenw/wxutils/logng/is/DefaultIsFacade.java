package com.github.jochenw.wxutils.logng.is;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.github.jochenw.wxutils.logng.app.IIsFacade;
import com.wm.app.b2b.server.InvokeState;
import com.wm.lang.ns.NSService;

public class DefaultIsFacade implements IIsFacade {
	protected NSService getCurrentService() {
		@SuppressWarnings("unchecked")
		final Stack<NSService> stack = InvokeState.getCurrentState().getCallStack();
		if (stack.isEmpty()) {
			throw new IllegalStateException("Call stack is empty.");
		}
		return stack.lastElement();
	}
	protected NSService getCallingService() {
		@SuppressWarnings("unchecked")
		final Stack<NSService> stack = InvokeState.getCurrentState().getCallStack();
		if (stack.size() > 1) {
			return stack.elementAt(stack.size()-2);
		} else {
			return null;
		}
	}

	@Override
	public String getCurrentPkgId() {
		return getCurrentService().getPackage().getName();
	}

	@Override
	public String getCurrentSvcId() {
		return getCurrentService().getNSName().getNodeName().toString();
	}

	@Override
	public String getCurrentQSvcId() {
		return getCurrentService().getNSName().getFullName();
	}

	@Override
	public String getCallingPkgId() {
		final NSService svc = getCallingService();
		return svc == null ? null : svc.getPackage().getName();
	}

	@Override
	public String getCallingSvcId() {
		final NSService svc = getCallingService();
		return svc == null ? null : svc.getNSName().getNodeName().toString();
	}

	@Override
	public String getCallingQSvcId() {
		final NSService svc = getCallingService();
		return svc == null ? null : svc.getNSName().getFullName();
	}

	@Override
	public boolean hasDir(String pRelativePath) {
		return Files.isDirectory(Paths.get(pRelativePath));
	}

	@Override
	public boolean hasFile(String pRelativePath) {
		return Files.isRegularFile(Paths.get(pRelativePath));
	}

	@Override
	public InputStream read(String pRelativePath) {
		try {
			return Files.newInputStream(Paths.get(pRelativePath));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public String[] findFilesInDir(String pRelativePath) {
		final Path currentDir = Paths.get(".");
		final Path dir = Paths.get(pRelativePath);
		final List<String> list = new ArrayList<>();
		if (Files.isDirectory(dir)) {
			try {
				Files.list(dir).forEach((p) -> {
					if (Files.isRegularFile(p)) {
						list.add(currentDir.relativize(p).toString());
					}
				});
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		return list.toArray(new String[list.size()]);
	}
}
