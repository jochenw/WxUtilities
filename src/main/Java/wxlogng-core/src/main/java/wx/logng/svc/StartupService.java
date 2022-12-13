package wx.logng.svc;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.Properties;
import java.util.function.Supplier;

import com.github.jochenw.afw.core.io.IReadable;
import com.github.jochenw.afw.core.log.ILogFactory;
import com.github.jochenw.afw.core.log.log4j.Log4j2LogFactory;
import com.github.jochenw.afw.core.log.simple.SimpleLogFactory;
import com.github.jochenw.afw.core.props.DefaultPropertyFactory;
import com.github.jochenw.afw.core.props.IPropertyFactory;
import com.github.jochenw.afw.di.api.Application;
import com.github.jochenw.afw.di.api.Scopes;
import com.softwareag.util.IDataMap;

import wx.logng.impl.DefaultIsEnvironment;


public class StartupService extends AbstractWxLogNgService {
	@Override
	public Object[] run(IDataMap pInput) throws Exception {
		final IIsEnvironment isEnvironment = newIsEnvironment();
		WxLogNg.setInstance(Application.of(WxLogNg.class, WxLogNg.MODULE.extend((b) -> {
			b.bind(IIsEnvironment.class).toInstance(isEnvironment);
			b.bind(IPropertyFactory.class).toSupplier(() -> newPropertyFactory(isEnvironment)).in(Scopes.SINGLETON);
			b.bind(ILogFactory.class).toSupplier(() -> newLogFactory(isEnvironment)).in(Scopes.SINGLETON);
		})));
		return NO_RESULT;
	}

	protected IIsEnvironment newIsEnvironment() {
		return new DefaultIsEnvironment();
	}

	protected IPropertyFactory newPropertyFactory(IIsEnvironment pIsEnvironment) {
		final String packageName = pIsEnvironment.getCallingPackageName(null);
		final Supplier<InputStream> factoryPropertiesSupplier = pIsEnvironment.requireFile("./packages/" + packageName + "/config/wxlogng-factory.properties"); 
		final Supplier<InputStream> localPropertiesSupplier = pIsEnvironment.findFile("./config/packages/" + packageName + "/wxlogng.properties"); 
		final Properties properties = load(factoryPropertiesSupplier);
		if (localPropertiesSupplier != null) {
			properties.putAll(load(localPropertiesSupplier));
		}
		return new DefaultPropertyFactory(properties);
	}

	protected Properties load(Supplier<InputStream> pInputStreamSupplier) {
		try (InputStream in = pInputStreamSupplier.get()) {
			final Properties props = new Properties();
			props.load(in);
			return props;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	protected ILogFactory newLogFactory(IIsEnvironment pIsEnvironment) {
		final String packageName = pIsEnvironment.getCallingPackageName(null);
		String uri = "./config/packages/" + packageName + "/log4j2.xml";
		Supplier<InputStream> log4j2Xml = pIsEnvironment.findFile(uri);
		if (log4j2Xml == null) {
			uri = "./packages/" + packageName + "/config/log4j2.xml";
			log4j2Xml = pIsEnvironment.findFile(uri);
		}
		if (log4j2Xml == null) {
			final OutputStream out = pIsEnvironment.createFile("./logs/packages/" + packageName + "/wxlogng.log");
			return SimpleLogFactory.of(new PrintStream(new BufferedOutputStream(out)));
		} else {
			final Supplier<InputStream> supplier = log4j2Xml;
			final IReadable readable = IReadable.of(packageName, () -> supplier.get());
			return Log4j2LogFactory.of(readable);
		}
	}
}
