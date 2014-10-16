package database.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteQueue {
public final static String QUERY_DELETE_QUEUE="DELETE from queues WHERE name=? AND queueID=?";
	
	public static void execute_query(String name,String queueID,Connection con){
		PreparedStatement stmn=null;
		
		try {
			stmn=con.prepareStatement(QUERY_DELETE_QUEUE);
			stmn.setString(1, name);
			stmn.setString(2, queueID);
			ResultSet result=stmn.executeQuery();
			if(!result.next()){
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
