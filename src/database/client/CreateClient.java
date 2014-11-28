package database.client;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import Logging.LoggingSet;
import database.MonitorDB;

public class CreateClient {
	
	
	public static LoggingSet lg=new LoggingSet(CreateClient.class.getName());
	public static final Logger logger=lg.getLogger();

	private static final String myQuery= "INSERT INTO clients (name) VALUES (?) returning id";
	

	public synchronized static void execute_query(String name,Connection con){
		PreparedStatement stmt = null;
        try {
            stmt = con.prepareStatement(myQuery);
            stmt.setString(1, name);
            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
            	logger.log(Level.SEVERE, "Error creating Client SQLException.");
                throw new RuntimeException("Error creating Client SQLException.");
            }
            int ForResult = result.getInt(1);
            //return ForResult;
        } catch (SQLException e) {
			// TODO Auto-generated catch block
        	logger.log(Level.SEVERE, ""+e);
			e.printStackTrace();
		} finally {
            if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.log(Level.WARNING, "Error closing statement"+e);
					e.printStackTrace();
				}
        }
	}
}
