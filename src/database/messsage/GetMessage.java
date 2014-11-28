package database.messsage;

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

public class GetMessage {
	
	public static LoggingSet lg=new LoggingSet(GetMessage.class.getName());
	public static final Logger logger=lg.getLogger();
	public final static String QUERY_FETCH_MESSAGE="SELECT * from messages WHERE \"messageID\"=?";
	
	public synchronized static Message execute_query(String messageID,Connection con){
		PreparedStatement stmn=null;
		Message msg=new Message();
		try {
			stmn=con.prepareStatement(QUERY_FETCH_MESSAGE);
			stmn.setString(1, messageID);
			ResultSet result=stmn.executeQuery();
			if(!result.next()){
				logger.log(Level.SEVERE, "Error during comming back after fetching a message");
				System.out.println("Something bad during returning id");
			}
			msg.message=result.getString("message");
			msg.sender=result.getString("sender");
			msg.reciever=result.getString("reciever");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			java.util.Date date=new java.util.Date();
			try {
				date = sdf.parse(result.getString("timestamp"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				logger.log(Level.WARNING,"Error parsing timestamp");
				e.printStackTrace();
			}
			java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
			msg.timestamp=timestamp;
			msg.messageID=UUID.fromString(result.getString("messageID"));
			return msg;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.log(Level.WARNING, "Error closing statement"+e);
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
		return null;
	}
}
