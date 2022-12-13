package wx.logng.svc;

import org.junit.Test;

import com.wm.data.IData;
import com.wm.data.IDataFactory;

public class StartupServiceTest {
	@Test
	public void testStartup() {
		final StartupService ssvc = SvcTests.getService(StartupService.class, null);
		final IData data = IDataFactory.create();
		ssvc.run(data);
	}
}
