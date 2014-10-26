package server;

//import java.sql;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.postgresql.ds.PGPoolingDataSource;

import Logging.LoggingSet;
import database.queue.GetQueue;

public class DBConnectorServer {
	
	public static LoggingSet lg=new LoggingSet(DBConnectorServer.class.getName());
	public static final Logger logger=lg.getLogger();
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


    public Connection getDatabaseConnection() {
        Connection con;
		try {
			
			con = connectionPool.getConnection();
			
			con.setAutoCommit(true);
	        return con;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE,"error getting conection from connection pool "+e);
			e.printStackTrace();
		}
      return null;  
    }


    public void closePool() {
        connectionPool.close();
    }
}
