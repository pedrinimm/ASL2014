package server;

import java.sql.*;


import org.postgresql.ds.PGPoolingDataSource;

public class DBConnectorServer {
	
	
	private final PGPoolingDataSource connectionPool;

    public DBConnectorServer() {
        connectionPool = new PGPoolingDataSource();
    }

    
    public void setupDatabaseConnectionPool(String username,
            String password, String server, String database, int maxConnections) {
        		connectionPool.setUser(username);
        		connectionPool.setPassword(password);
        		connectionPool.setServerName(server);
        		connectionPool.setDatabaseName(database);
        		connectionPool.setMaxConnections(maxConnections);
    }


    public Connection getDatabaseConnection() throws SQLException {
        Connection con = connectionPool.getConnection();
        con.setAutoCommit(false);
        return con;
    }


    public void closePool() {
        connectionPool.close();
    }
}
