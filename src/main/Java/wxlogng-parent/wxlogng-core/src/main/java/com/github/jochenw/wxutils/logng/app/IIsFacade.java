package com.github.jochenw.wxutils.logng.app;

import java.io.InputStream;

public interface IIsFacade {
	public String getCurrentPkgId();
	public String getCurrentSvcId();
	public String getCurrentQSvcId();
	public String getCallingPkgId();
	public String getCallingSvcId();
	public String getCallingQSvcId();
	public boolean hasFile(String pRelativePath);
	public InputStream read(String pRelativePath);
	public String[] findFilesInDir(String pRelativePath);
}
