package database.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import client.Message;

public class GetQueue {
	public final static String QUERY_FETCH_QUEUE="SELECT * from queues WHERE name=? AND queueID=?";
	
	public static int execute_query(String name,String queueID,Connection con){
		PreparedStatement stmn=null;
		int id=0;
		try {
			stmn=con.prepareStatement(QUERY_FETCH_QUEUE);
			stmn.setString(1, name);
			stmn.setString(2, queueID);
			ResultSet result=stmn.executeQuery();
			if(!result.next()){
				System.out.println("Something bad during returning id");
			}
			id=result.getInt("id");
			return id;
			
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
		return 0;
	}
}
