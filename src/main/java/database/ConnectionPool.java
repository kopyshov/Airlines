package database;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionPool {
    Connection getConnection();
    boolean releaseConnection(Connection connection);
    void shutdown() throws SQLException;
}
