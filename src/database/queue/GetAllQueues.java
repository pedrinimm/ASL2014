package database.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class GetAllQueues {

	public final static String QUERY_FETCH_QUEUE="SELECT * from queues";
	
	public static void execute_query(Connection con){
		PreparedStatement stmn=null;
		int id=-1;
		try {
			stmn=con.prepareStatement(QUERY_FETCH_QUEUE);
			ResultSet result=stmn.executeQuery();
			if(result.next()){
				while(result.next()){
					id=result.getInt("id");
					String name=result.getString("name");
					String uuid_t=result.getString("queueID");
					System.out.println("id "+id+" name "+name+" UUID "+uuid_t);
				}
				//id=result.getInt("id");
				//System.out.println("Something bad during returning id");
			}
			//id=result.getInt("id");
			
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
