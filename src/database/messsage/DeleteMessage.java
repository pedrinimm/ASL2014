package database.messsage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteMessage {
	
	public final static String QUERY_DELETE_MESSAGE="DELETE from messages WHERE messageID=?";
	
	public static void execute_query(String messageID,Connection con){
		PreparedStatement stmn=null;
		
		try {
			stmn=con.prepareStatement(QUERY_DELETE_MESSAGE);
			stmn.setString(1, messageID);
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
