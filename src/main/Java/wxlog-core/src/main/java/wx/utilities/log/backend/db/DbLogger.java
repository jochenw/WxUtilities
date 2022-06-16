package wx.utilities.log.backend.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.function.Supplier;

import com.github.jochenw.afw.core.util.Exceptions;

import wx.utilities.log.api.ILogger;

public class DbLogger implements ILogger {
	private final Supplier<Connection> dbConnectionProvider;
	private final String tableName;
	private MetaData metaData;

	public DbLogger(MetaData pMetaData, Supplier<Connection> pDbonnectionProvider, String pTableName) {
		dbConnectionProvider = pDbonnectionProvider;
		tableName = pTableName;
		metaData = pMetaData;
	}

	@Override
	public MetaData getMetaData() {
		return metaData;
	}

	@Override
	public boolean isEnabled(Level pLevel) {
		return metaData.getLevel().ordinal() >= pLevel.ordinal();
	}

	@Override
	public void log(Level pLevel, String pMessage) {
		if (isEnabled(pLevel)) {
			try (Connection conn = dbConnectionProvider.get()) {
				final String sql = "INSERT INTO " + tableName + "(dateTime, level, msg) VALUES (?, ?, ?)";
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					stmt.setTimestamp(1, Timestamp.from(Instant.now()));
					stmt.setInt(2, pLevel.ordinal());
					stmt.setString(3, pMessage);
					stmt.executeUpdate();
				}
			} catch (SQLException e) {
				throw Exceptions.show(e);
			}
		}
	}

}
