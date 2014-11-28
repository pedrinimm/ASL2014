package database.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.UUID;

import client.QueueCl;

public class GetAllQueues {

	public final static String QUERY_FETCH_QUEUE="SELECT * from queues";
	
	public synchronized static Hashtable<Integer,QueueCl> execute_query(Connection con){
		Hashtable<Integer,QueueCl>Queue=new Hashtable<Integer,QueueCl>();
		PreparedStatement stmn=null;
		int id=-1;
		try {
			stmn=con.prepareStatement(QUERY_FETCH_QUEUE);
			ResultSet result=stmn.executeQuery();
			
			if(result.next()){
				do{
					id=result.getInt("id");
					String name=result.getString("name");
					String uuid_t=result.getString("queueID");
					System.out.println("id "+id+" name "+name+" UUID "+uuid_t);
					Queue.put(id, new QueueCl(name,uuid_t));
				}while(result.next());
				//id=result.getInt("id");
				//System.out.println("Something bad during returning id");
			}
			//id=result.getInt("id");
			return Queue;
			
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
		return Queue;
	}
}
