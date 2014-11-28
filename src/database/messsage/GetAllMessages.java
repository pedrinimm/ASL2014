package database.messsage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import client.Message;

public class GetAllMessages {

	public final static String QUERY_FETCH_CLIENTS="SELECT * from messages";
	
	public synchronized void  execute_query(Connection con){
		PreparedStatement stmn=null;
		Message msg=new Message();
		try {
			stmn=con.prepareStatement(QUERY_FETCH_CLIENTS);
			ResultSet result=stmn.executeQuery();
			if(result.next()){
				while(result.next()){
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
					//System.out.println("message id  "+msg.messageID+" sender "+msg.sender);
				}
			}else{
				System.out.println("Something bad during returning id");
			}
			
			
			
			
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
		
	}
}
