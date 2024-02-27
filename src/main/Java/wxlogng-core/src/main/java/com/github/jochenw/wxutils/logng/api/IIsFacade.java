package com.github.jochenw.wxutils.logng.api;

import java.io.InputStream;

public interface IIsFacade {
	public String getCurrentPkgId();
	public String getCurrentSvcId();
	public String getCurrentQSvcId();
	public boolean hasFile(String pRelativePath);
	public InputStream read(String pRelativePath);
}
