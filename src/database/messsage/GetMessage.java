package database.messsage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import client.Message;

public class GetMessage {
	public final static String QUERY_FETCH_MESSAGE="SELECT * from messages WHERE messageID=?";
	
	public static Message execute_query(String messageID,Connection con){
		PreparedStatement stmn=null;
		Message msg=new Message();
		try {
			stmn=con.prepareStatement(QUERY_FETCH_MESSAGE);
			stmn.setString(1, messageID);
			ResultSet result=stmn.executeQuery();
			if(!result.next()){
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
				e.printStackTrace();
			}
			java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
			msg.timestamp=timestamp;
			msg.messageID=UUID.fromString(result.getString("messageID"));
			return msg;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(stmn!=null){
				try {
					stmn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
