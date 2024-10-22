package wx.log.ng.core.app;

import java.io.InputStream;

import com.github.jochenw.afw.core.function.Functions.FailableConsumer;
import com.github.jochenw.afw.core.function.Functions.FailableFunction;

public interface IsFacade {
	public static interface IServiceName {
		public String getPackageName();
		public String getServiceName();
		public String getServiceQName();
	}
	public boolean hasFile(String pUri);
	public void readFile(String pUri, FailableConsumer<InputStream,?> pReader);
	public <O> O readFile(String pUri, FailableFunction<InputStream,O,?> pReader);
	public String getWxLogNgPackageName();
	String getCurrentPackageName();
	public IServiceName getCallingServiceName();
}
