package com.github.jochenw.wxutils.logng.svc;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.jochenw.afw.core.util.MutableBoolean;
import com.github.jochenw.afw.di.api.Application;
import com.github.jochenw.afw.di.api.IComponentFactory;
import com.github.jochenw.wxutils.logng.app.IIsFacade;
import com.github.jochenw.wxutils.logng.app.WxLogNg;
import com.softwareag.util.IDataMap;
import com.wm.data.IData;
import com.wm.data.IDataFactory;

public class AdminStartUpSvcTest {
	public static class MockIsFacade implements IIsFacade {
		private final String pkgName;
		private final Map<String, byte[]> files = new HashMap<>();

		public MockIsFacade(String pPkgName) {
			pkgName = pPkgName;
		}
		
		public void registerFile(String pPath, String pContent) {
			files.put(canonicalize(pPath), pContent.getBytes(StandardCharsets.UTF_8));
		}
		@Override
		public String getCurrentPkgId() {
			return pkgName;
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
			final String path = canonicalize(pRelativePath);
			return files.containsKey(path);
		}

		@Override
		public InputStream read(String pRelativePath) {
			final String path = canonicalize(pRelativePath);
			final byte[] bytes = files.get(path);
			if (bytes == null) {
				throw new IllegalStateException("File not found: " + pRelativePath);
			}
			return new ByteArrayInputStream(bytes);
		}

		protected String canonicalize(String pPath) {
			if (pPath.startsWith("./")) {
				return pPath.substring(2);
			} else {
				return pPath;
			}
		}
		
		@Override
		public String[] findFilesInDir(String pRelativePath) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	@Test
	public void test() throws Exception {
		MockIsFacade isFacade = new MockIsFacade("WxLogNgTst");
		isFacade.registerFile("packages/WxLogNgTst/config/wxLogNg.properties",
				"log.engine.0=default=com.github.jochenw.wxutils.logng.engine.dflt.DefaultLogEngine");
		isFacade.registerFile("packages/WxLogNgTst/config/wxLogNg/WxLogNg-logger.properties",
				"");
		final MutableBoolean invoked = new MutableBoolean();
		final AdminStartUpSvc adminStartUpSvc = new AdminStartUpSvc() {
			@Override
			public Object[] run(IDataMap pInput) throws Exception {
				final Object[] result = super.run(pInput);
				invoked.set();
				return result;
			}
			
		};
		adminStartUpSvc.init("WxLogNgTst", isFacade, (b) -> {
			
		});
		final IData pipeline = IDataFactory.create();
		
		adminStartUpSvc.run(pipeline);
		assertTrue(invoked.isSet());
		final IComponentFactory cf = adminStartUpSvc.getComponentFactory();
		assertNotNull(cf);
		assertSame(cf.requireInstance(Application.class), WxLogNg.getInstance());
	}
}
