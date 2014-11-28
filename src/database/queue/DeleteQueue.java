package database.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import Logging.LoggingSet;

public class DeleteQueue {
	public static LoggingSet lg=new LoggingSet(DeleteQueue.class.getName());
	public static final Logger logger=lg.getLogger();
	
	public final static String QUERY_DELETE_QUEUE="DELETE from queues WHERE name=? AND \"queueID\"=?";
	
	public synchronized static void execute_query(String name,String queueID,Connection con){
		PreparedStatement stmn=null;
		
		try {
			stmn=con.prepareStatement(QUERY_DELETE_QUEUE);
			stmn.setString(1, name);
			stmn.setString(2, queueID);
			//ResultSet result=stmn.executeQuery();
			int rowsDeleted = stmn.executeUpdate();
			System.out.println(rowsDeleted + " rows deleted"); 
			stmn.close();
//			if(!result.next()){
//				logger.log(Level.WARNING, "Error during comming back after deleting a queue");
//				System.out.println("Something bad during returning id");
//			}
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
	}
}
