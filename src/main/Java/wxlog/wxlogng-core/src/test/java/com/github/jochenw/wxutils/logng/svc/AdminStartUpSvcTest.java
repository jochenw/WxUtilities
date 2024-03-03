package com.github.jochenw.wxutils.logng.svc;

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

import com.github.jochenw.wxutils.logng.app.IIsFacade;

public class AdminStartUpSvcTest {
	public static class MockIsFacade implements IIsFacade {
		@Override
		public String getCurrentPkgId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getCurrentSvcId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getCurrentQSvcId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getCallingPkgId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getCallingSvcId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getCallingQSvcId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasFile(String pRelativePath) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public InputStream read(String pRelativePath) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] findFilesInDir(String pRelativePath) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	@Test
	public void test() {
		final AdminStartUpSvc adminStartUpSvc = new AdminStartUpSvc();
		adminStartUpSvc.initWxLogLogger(new MockIsFacade());
	}
}
