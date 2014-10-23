package database.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import Logging.LoggingSet;
import client.Message;

public class GetQueue {
	
	public static LoggingSet lg=new LoggingSet(GetQueue.class.getName());
	public static final Logger logger=lg.getLogger();
	public final static String QUERY_FETCH_QUEUE="SELECT * from queues WHERE name=? ";//AND \"queueID\"=?";
	
	public static int execute_query(String name,Connection con){
		PreparedStatement stmn=null;
		int id=-1;
		try {
			stmn=con.prepareStatement(QUERY_FETCH_QUEUE);
			stmn.setString(1, name);
			//stmn.setString(2, queueID);
			System.out.println(stmn.toString());
			ResultSet result=stmn.executeQuery();
			if(result.next()){
				id=result.getInt("id");
				//
			}else{
				logger.log(Level.WARNING, "Error during comming back after getting a queue");
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
