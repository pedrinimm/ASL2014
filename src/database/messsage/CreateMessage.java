package database.messsage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import Logging.LoggingSet;
import database.client.CreateClient;

public class CreateMessage {
	public static LoggingSet lg=new LoggingSet(CreateMessage.class.getName());
	public static final Logger logger=lg.getLogger();
	public final static String QUERY_INSERT_MESSAGE="INSERT INTO messages (sender,reciever,message,\"messageID\",timestamp,\"queueID\") "
			+ "VALUES (?,?,?,?,?,?) returning id ";
	
	public static void execute_query(String sender,String reciever,String message,String messageID,Timestamp timestamp
			,int queueID,Connection con){
		PreparedStatement stmn=null;
		
		try {
			stmn=con.prepareStatement(QUERY_INSERT_MESSAGE);
			stmn.setString(1, sender);
			stmn.setString(2,reciever);
			stmn.setString(3, message);
			stmn.setString(4, messageID);
			stmn.setTimestamp(5, timestamp);
			stmn.setInt(6, queueID);
			ResultSet result=stmn.executeQuery();
			if(!result.next()){
				logger.log(Level.SEVERE, "Error during comming back after reating a message");
				System.out.println("Something bad during returning id");
			}
			int id=result.getInt("id");
			System.out.println("id is "+id);
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
