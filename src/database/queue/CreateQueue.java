package database.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateQueue {
	public final static String QUERY_INSERT_QUEUE="INSERT INTO queues (name,queueID) "
			+ "VALUES (?,?) returning id ";
	
	public static void execute_query(String name,String queueID,Connection con){
		PreparedStatement stmn=null;
		
		try {
			stmn=con.prepareStatement(QUERY_INSERT_QUEUE);
			stmn.setString(1, name);
			stmn.setString(2, queueID);
			ResultSet result=stmn.executeQuery();
			if(!result.next()){
				System.out.println("Something bad during returning id");
			}
			int id=result.getInt("id");
			System.out.println("id is "+id);
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
