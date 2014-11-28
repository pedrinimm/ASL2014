package database.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import Logging.LoggingSet;
import database.queue.GetQueue;

public class GetClient {
	public static LoggingSet lg=new LoggingSet(GetQueue.class.getName());
	public static final Logger logger=lg.getLogger();
	public final static String QUERY_FETCH_CLIENT="SELECT * from clients WHERE name=? ";//AND \"queueID\"=?";
	
	public synchronized static int execute_query(String name,Connection con){
		PreparedStatement stmn=null;
		int id=-1;
		try {
			stmn=con.prepareStatement(QUERY_FETCH_CLIENT);
			stmn.setString(1, name);
			//stmn.setString(2, queueID);
			System.out.println(stmn.toString());
			ResultSet result=stmn.executeQuery();
			if(result.next()){
				id=result.getInt("id");
				//
			}else{
				logger.log(Level.WARNING, "Error during comming back after getting a clients");
				System.out.println("Something bad during returning id");
			}
			//id=result.getInt("id");
			return id;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, "Error closing statement"+e);
			e.printStackTrace();
		} finally {
			if(stmn!=null){
				try {
					stmn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.log(Level.SEVERE, "Error closing statement"+e);
					e.printStackTrace();
				}
			}
		}
		return -1;
	}
}
