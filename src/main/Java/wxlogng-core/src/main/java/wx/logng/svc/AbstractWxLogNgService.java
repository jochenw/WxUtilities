package wx.logng.svc;

import javax.inject.Inject;

import com.github.jochenw.afw.di.api.IComponentFactory;


public abstract class AbstractWxLogNgService implements IIsService {
	private @Inject IComponentFactory componentFactory;

	public IComponentFactory getComponentFactory() {
		return componentFactory;
	}
}
