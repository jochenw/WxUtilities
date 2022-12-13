package wx.logng.svc;

import org.junit.Test;

import com.wm.data.IData;
import com.wm.data.IDataFactory;

public class ShutdownServiceTest {
	@Test
	public void test() {
		final StartupService ssvc = SvcTests.getService(StartupService.class, null);
		final IData data = IDataFactory.create();
		ssvc.run(data);
		final ShutdownService shsvc = SvcTests.getService(ShutdownService.class, null);
		final IData data2 = IDataFactory.create();
		shsvc.run(data2);
	}
}
