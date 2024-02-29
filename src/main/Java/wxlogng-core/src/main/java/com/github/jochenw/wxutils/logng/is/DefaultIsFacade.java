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

import com.github.jochenw.wxutils.logng.api.IIsFacade;
import com.wm.app.b2b.server.InvokeState;
import com.wm.lang.ns.NSService;

public class DefaultIsFacade implements IIsFacade {
	protected NSService getCallingService() {
		@SuppressWarnings("unchecked")
		final Stack<NSService> stack = InvokeState.getCurrentState().getCallStack();
		if (stack.isEmpty()) {
			throw new IllegalStateException("Call stack is empty.");
		}
		return stack.peek();
	}

	@Override
	public String getCurrentPkgId() {
		return getCallingService().getPackage().getName();
	}

	@Override
	public String getCurrentSvcId() {
		return getCallingService().getNSName().getNodeName().toString();
	}

	@Override
	public String getCurrentQSvcId() {
		return getCallingService().getNSName().getFullName();
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
