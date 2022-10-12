package wx.utilities.log.svc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.Supplier;

import com.github.jochenw.afw.core.data.Data;
import com.github.jochenw.afw.core.util.Objects;
import com.github.jochenw.afw.core.util.Streams;
import com.softwareag.util.IDataMap;
import com.wm.app.b2b.server.ns.Namespace;
import com.wm.lang.ns.NSPackage;

import wx.utilities.log.app.WxLogNg;
import wx.utilities.log.utils.IsUtils;

public class AdminRegisterLoggerIdSvc extends AbstractIIsSvc {
	@Override
	public Object[] run(IDataMap pPipeline) {
		final String loggerId = Data.MAP_ACCESSOR.requireString(pPipeline, "loggerId");
		final String packageName = Objects.notNull(Data.MAP_ACCESSOR.getString(pPipeline, "packageName"),
												   () -> IsUtils.getCurrentCallingPackageName());
		final NSPackage pkg = Namespace.current().getPackage(packageName);
		if (pkg == null) {
			throw new NullPointerException("Invalid value for parameter packageName: "
					+ "Expected existing, loaded package, got " + packageName);
		}
		final Path packageConfigDir = Paths.get("./packages", packageName, "config");
		final Path packageFactoryPropertiesFile = packageConfigDir.resolve("wxlogng-" + loggerId + ".properties");
		final Properties packageFactoryProperties;
		if (Files.isRegularFile(packageFactoryPropertiesFile)) {
			packageFactoryProperties = Streams.load(packageFactoryPropertiesFile);
		} else {
			packageFactoryProperties = new Properties();
		}
		final Path localConfigDir = Paths.get("./config", "packages", WxLogNg.getInstance().getWxLogNgPackageName(),
				                              "loggers");
		final Path localPropertiesFile = localConfigDir.resolve(loggerId + ".properties");
		final Properties localPackageProperties;
		if (Files.isRegularFile(localPropertiesFile)) {
			localPackageProperties = Streams.load(localPropertiesFile);
		} else {
			localPackageProperties = new Properties();
		}
		
		return noResult();
	}
}
