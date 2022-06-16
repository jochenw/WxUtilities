package wx.utilities.log.backend.db;

import java.lang.reflect.UndeclaredThrowableException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;


import com.github.jochenw.afw.core.inject.Types.Type;
import com.github.jochenw.afw.core.util.Objects;
import com.github.jochenw.afw.di.api.ComponentFactoryBuilder;
import com.github.jochenw.afw.di.api.IComponentFactory;

import wx.utilities.log.api.IBackend;
import wx.utilities.log.api.ILogger;
import wx.utilities.log.api.ILogger.MetaData;
import wx.utilities.log.backend.IFormatter;

public class DbBackend implements IBackend {
	private @Inject @Named(value="jdbc.ConnectionProvider") Supplier<Connection> dbConnectionProvider;
	private @Inject @Named(value="jdbc.tableName") String tableName;

	@Override
	public ILogger create(MetaData pMetaData) {
		return new DbLogger(pMetaData, dbConnectionProvider, tableName);
	}

	@Override
	public void reconfigure(ILogger pLogger, MetaData mdNew) {
		// TODO Auto-generated method stub

	}

	@Override
	public IFormatter create(String pPattern) {
		// TODO Auto-generated method stub
		return null;
	}

	public static DbBackend of(String pDbDriver, String pDbUrl, String pDbUser, String pDbPassword, String pTableName) {
		final String dbDriver = Objects.requireNonNull(pDbDriver, "JDBC Driver");
		final String dbUrl = Objects.requireNonNull(pDbUrl, "JDBC URL");
		final String dbUser = Objects.requireNonNull(pDbUser, "JDBC User");
		final String dbPass = Objects.requireNonNull(pDbPassword, "JDBC Passord");
		final String dbTable = Objects.requireNonNull(pTableName, "JDBC Table Name");
		final Supplier<Connection> connectionProvider = () -> {
			try {
				Class.forName(dbDriver);
				return DriverManager.getConnection(dbUrl, dbUser, dbPass);
			} catch (Exception e) {
				throw new UndeclaredThrowableException(e);
			}
		};
		return of(connectionProvider, dbTable);
	}
	public static DbBackend of(Supplier<Connection> pDbConnectionProvider, String pTableName) {
		final Supplier<Connection> dbConnectionProvider = Objects.requireNonNull(pDbConnectionProvider, "JDBC Connection Provider");
		final com.github.jochenw.afw.di.api.Module module = (b) -> {
			final Type<Supplier<Connection>> dbConnectionProviderType = new Type<Supplier<Connection>>() {};
			b.bind(dbConnectionProviderType, "dbConnectionProvider").toInstance(dbConnectionProvider);
			b.bind(String.class, "jdbc.tableName").toInstance(pTableName);
			b.bind(DbBackend.class);
		};
		final IComponentFactory cf = new ComponentFactoryBuilder().module(module).build();
		return cf.requireInstance(DbBackend.class);
	}
}
