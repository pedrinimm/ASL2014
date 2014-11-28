package database.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.UUID;

import client.Message;

public class GetAllClients {

	public final static String QUERY_FETCH_CLIENTS="SELECT * from clients";
	public static LinkedList<String> clientName;
	
	public synchronized static LinkedList<String> execute_query(Connection con){
		clientName=new LinkedList<String>();
		PreparedStatement stmn=null;
		String name="";
		try {
			stmn=con.prepareStatement(QUERY_FETCH_CLIENTS);
			ResultSet result=stmn.executeQuery();
			while(result.next()){
				if(result.next()){
					//msg.message=result.getString("id");
					name=result.getString("name");
					clientName.add(name);
				}else{
					System.out.println("Something bad during returning id");
				}
			}
			
			return clientName;
			
			
			
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
