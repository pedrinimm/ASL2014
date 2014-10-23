package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import Logging.LoggingSet;
import client.Message;
import client.QueueCl;
import database.client.GetAllClients;
import database.messsage.CreateMessage;
import database.messsage.GetAllMessages;
import database.messsage.GetMessage;
import database.queue.CreateQueue;
import database.queue.GetAllQueues;
import database.queue.GetQueue;
import server.DBConnectorServer;

public class MonitorDB {
	
	public static LoggingSet lg=new LoggingSet(MonitorDB.class.getName());
	public static final Logger logger=lg.getLogger();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connection conn;
		DBConnectorServer conDispatch;
		conDispatch =new DBConnectorServer();
		conDispatch.setupDatabaseConnectionPool("postgres", "squirrel", "localhost", "messaging", 200);
		Message testMessage=null;
		//QueueCl testQueue=new QueueCl();
		//testQueue.name="general";
		try {
			conn=conDispatch.getDatabaseConnection();
			if(!conn.isClosed()){
				logger.log(Level.INFO, "conection to database");
				System.out.println("conencted!!");
			}
			//CreateQueue.execute_query(testQueue.name, testQueue.queueId.toString(), conn);
			GetAllQueues.execute_query(conn);
			
			//int queueId=GetQueue.execute_query("general","24081d61-7ae8-4aba-a564-5c50114a3a93", conn);
			int queueId=GetQueue.execute_query("general", conn);
			System.out.println("id retruned "+queueId);
			//CreateMessage.execute_query(testMessage.sender, testMessage.reciever, testMessage.message, testMessage.messageID.toString(),testMessage.timestamp, queueId, conn);
			testMessage=GetMessage.execute_query("884dab65-fac7-4edd-8daa-297f0689357d", conn);
			System.out.println("timestamp "+testMessage.timestamp);
			GetAllMessages.execute_query(conn);
			GetAllClients.execute_query(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
