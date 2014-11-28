package database.messsage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import Logging.LoggingSet;

public class DeleteMessage {
	public static LoggingSet lg=new LoggingSet(DeleteMessage.class.getName());
	public static final Logger logger=lg.getLogger();
	public final static String QUERY_DELETE_MESSAGE="DELETE from messages WHERE \"messageID\"=?";
	
	public synchronized static void execute_query(String messageID,Connection con){
		PreparedStatement stmn=null;
		
		try {
			stmn=con.prepareStatement(QUERY_DELETE_MESSAGE);
			stmn.setString(1, messageID);
			int rowsDeleted = stmn.executeUpdate();
			System.out.println(rowsDeleted + " rows deleted"); 
			stmn.close();
//			ResultSet result=stmn.executeQuery();
//			if(!result.next()){
//				logger.log(Level.SEVERE, "Error during comming back after returning a message");
//				System.out.println("Something bad during returning id");
//			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, "Exception in the SQL"+e);
			e.printStackTrace();
		} finally {
			if(stmn!=null){
				try {
					stmn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					logger.log(Level.WARNING, "Error closing statement"+e);
					e.printStackTrace();
				}
			}
		}
	}

}
