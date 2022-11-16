package wx.logng.svc;

import com.github.jochenw.afw.di.api.Module;

public class SvcTests {

	public static <O extends IIsService> O getService(Class<O> pType, Module pModule) {
		final Module module = (b) -> {
			b.bind(IIsEnvironment.class).toInstance(new IIsEnvironment() {
				@Override
				public Object getContext() {
					return null;
				}

				@Override
				public String getCallingPackageName(Object pCtx) {
					return "MockPackage";
				}

				@Override
				public String getCallingQServiceId(Object pCtx) {
					return "mock.pkg:mockService";
				}

				@Override
				public String getCallingServiceId(Object pCtx) {
					return "mockService";
				}
			});
		};
		final WxLogNg wxLogNg = new WxLogNg(module.extend(pModule));
		@SuppressWarnings("unchecked")
		final O o = (O) wxLogNg.getComponentFactory().requireInstance(IIsService.class, pType.getName());
		return o;
	}

}
