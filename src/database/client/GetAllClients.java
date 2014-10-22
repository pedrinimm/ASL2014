package database.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

import client.Message;

public class GetAllClients {

	public final static String QUERY_FETCH_CLIENTS="SELECT * from clients";
	
	public static void execute_query(Connection con){
		PreparedStatement stmn=null;
		Message msg=new Message();
		try {
			stmn=con.prepareStatement(QUERY_FETCH_CLIENTS);
			ResultSet result=stmn.executeQuery();
			if(result.next()){
				msg.message=result.getString("id");
				msg.sender=result.getString("name");
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
