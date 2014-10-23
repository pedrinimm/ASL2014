package server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import Logging.LoggingSet;
import client.ClientMessage;
import client.Message;
import client.QueueCl;
import client.ClientMessage;
import database.messsage.CreateMessage;
import database.queue.CreateQueue;
import database.queue.GetQueue;


public class Server implements Runnable{
	
	
	public static LoggingSet lg=new LoggingSet(Server.class.getName());
	public static final Logger logger=lg.getLogger();
	
	private int connectionID;
	private int port;
	
	private Hashtable<Integer, ClientThread> mapClients=new Hashtable<Integer,ClientThread>();
	private LinkedList<ClientThread> clientList;
	private final ExecutorService poolClients;
	
	private Boolean stopCondition;
	private Hashtable<String,QueueCl>mapQueue=new Hashtable<String,QueueCl>();
	private SimpleDateFormat sdf;
	//limit the number of clients per server and db conections
	private int ClientCapacity;
	private int ConectionDbCapacity;
	private ArrayBlockingQueue<Connection> poolOfDBConnections;
	//database part
	private Connection conn;
	private final DBConnectorServer conDispatch;
	
	
	
	
	public Server(int port,int limit){
		this.port=port;
		QueueCl queue=new QueueCl();
		this.mapQueue.put("general", queue);
		
		conDispatch =new DBConnectorServer();
		ConectionDbCapacity=limit;
		ClientCapacity=limit;
		poolOfDBConnections=new ArrayBlockingQueue<Connection>(ConectionDbCapacity);
		poolClients= Executors.newFixedThreadPool(limit);
		conDispatch.setupDatabaseConnectionPool("postgres", "squirrel", "localhost", "messaging", 200);
		try {
			conn=conDispatch.getDatabaseConnection();
			if(!conn.isClosed()){
				logger.log(Level.INFO, "Connection to the database made");
				System.out.println("conencted!!");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, "Error: During getting connection from the database");
			e.printStackTrace();
		}
		
		if(GetQueue.execute_query("general", conn)==-1){
			CreateQueue.execute_query("general", this.mapQueue.get("general").queueId.toString(), conn);
			logger.log(Level.INFO, "General queue created");
		}
		
	}
	public void acceptinClient(Socket newClient){
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		stopCondition=true;
		conn=conDispatch.getDatabaseConnection();
		try{
			ServerSocket socket=new ServerSocket(port);
			while(stopCondition){
				System.out.println("Server waiting for Clients on port " + port + ".");
				Socket connection= socket.accept();
				logger.log(Level.INFO, "Connection from client accepted "+connection.getLocalAddress().getHostAddress());
				if(!stopCondition){
					break;
				}
				ClientThread ct=new ClientThread(connection,conn);
				poolClients.execute(ct);
				//
				System.out.println("Llego aqui\n");
				mapClients.put(ct.id,ct);
				//ct.run();
				
			}
			try {
				
				socket.close();
				poolClients.shutdown();
				/*for(int i = 0; i < mapClients.size(); ++i) {
					ClientThread tc = mapClients.get(i);
					try {
					tc.input.close();
					tc.output.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
						logger.log(Level.WARNING, "Exception during closing "+ioE);
					}
				}*/
			}
			catch(Exception e) {
				logger.log(Level.SEVERE, "Exception closing the server and clients: " + e);
				System.out.println("Exception closing the server and clients: " + e);
			}
		}catch(IOException e){
			String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			logger.log(Level.SEVERE, msg);
			System.out.println(msg);
		}
	}
	
	protected void stop() {
		stopCondition = false;
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			// nothing I can really do
			logger.log(Level.WARNING, "Exception during closing "+e);
		}
	}
	synchronized void deleteQueue(String queueName){
		mapQueue.remove(queueName);
	}
	synchronized void deleteClient(int id){
		mapClients.remove(id);
	}


	
	
	public int getNumberOfClients(){
		return mapQueue.size();
	}

	public static void main(String[] args) {
		
		int limit=200;
		int portNumber = 10033;
		switch(args.length) {
			case 1:
				try {
					portNumber = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					logger.log(Level.INFO, "Invalid port number.");
					System.out.println("Usage is: > java Server [portNumber]");
					return;
				}
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Server [portNumber]");
				return;
				
		}
		// create a server object and start it
		Server server = new Server(portNumber,limit);
		//server.conDispatch.setupDatabaseConnectionPool("message", "messages", "localhost", "messaging", 200);
		server.run();
	}
	class ClientThread implements Runnable {
		Socket socket;
		ObjectInputStream input;
		ObjectOutputStream output;
		int id;
		ClientMessage message;
		String username;
		ClientMessage ms;
		String date;
		Connection dbConn;
		
		ClientThread(Socket socket,Connection con) {
			id=connectionID++;
			this.socket=socket;
			try
			{

				output = new ObjectOutputStream(socket.getOutputStream());
				input  = new ObjectInputStream(socket.getInputStream());
				message = (ClientMessage) input.readObject();
				username=message.getString();
				//addUser();
				dbConn=con;
				
				
			}catch (IOException e) {
				return;
			}
			catch (ClassNotFoundException e) {
			}
			date = new Date().toString() + "\n";
		}

		// what will run forever
		public void run() {
			boolean stopCondi=true;
			ClientMessage answer;
			while(stopCondi){
				try {
					ms = (ClientMessage) input.readObject();
				}
				catch (IOException e) {
					System.out.println(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					System.out.println(e2);
					break;
				}
				
				if(ms.getType()==1){
					System.out.println("Llego mensaje\n");
					Message m=ms.getMessage();
					mapQueue.get("general").insertMessage(m);
					//int queueID=GetQueue.execute_query("general", mapQueue.get("general").queueId.toString(), dbConn);
					int queueID=GetQueue.execute_query("general", dbConn);
					CreateMessage.execute_query(m.sender, m.reciever, m.message, m.messageID.toString(), m.timestamp, queueID, dbConn);
					answer=new ClientMessage(ClientMessage.sendMessage,"Message sented");
					sendMessage(this.id,answer);

				}else if(ms.getType()==2){
					System.out.println("Creamos una nueva queue\n");
					String queueName=ms.getString();
					if(mapQueue.get(queueName)!=null){
						System.out.println("Queue exist");
						answer=new ClientMessage(ClientMessage.createQueue,"Queue existed before");
						sendMessage(this.id,answer);
					}else{
						mapQueue.put(queueName, new QueueCl(queueName));
						QueueCl tempQ=mapQueue.get(queueName);
						CreateQueue.execute_query(queueName, tempQ.queueId.toString(), dbConn);
						answer=new ClientMessage(ClientMessage.createQueue,"Queue created");
						sendMessage(this.id,answer);
						
					}
				}else if(ms.getType()==3){
					System.out.println("Borrar queue\n");
					String queueName=ms.getString();
					if(mapQueue.get(queueName)!=null){
						mapQueue.remove(queueName);
						System.out.println("Queue removed");
						answer=new ClientMessage(ClientMessage.deleteQueue,"Queue Deleted");
						sendMessage(this.id,answer);
					}else{
						System.out.println("Queue do not exist");
						answer=new ClientMessage(ClientMessage.deleteQueue,"Queue didn't exist");
						sendMessage(this.id,answer);
						//mapQueue.put(queueName, new QueueCl());
					}
				}else if(ms.getType()==4){
					Message m2=ms.getMessage();
					String queueName=ms.getQueueName();
					if(mapQueue.get(queueName)!=null){
						mapQueue.get(queueName).insertMessage(m2);
						QueueCl tempQ=mapQueue.get(queueName);
						//int queueID=GetQueue.execute_query(tempQ.getName(), tempQ.queueId.toString(), dbConn);
						int queueID=GetQueue.execute_query(tempQ.getName(), dbConn);
						CreateMessage.execute_query(m2.sender, m2.reciever, m2.message, m2.messageID.toString(), m2.timestamp, queueID, dbConn);
						answer=new ClientMessage(ClientMessage.sendPReciever,"Sent");
						sendMessage(this.id,answer);

					}else{
						mapQueue.put(queueName, new QueueCl(queueName));
						mapQueue.get(queueName).insertMessage(m2);
						
						QueueCl tempQ=mapQueue.get(queueName);
						CreateQueue.execute_query(queueName, tempQ.queueId.toString(), dbConn);
						//int queueID=GetQueue.execute_query(tempQ.getName(), tempQ.queueId.toString(), dbConn);
						int queueID=GetQueue.execute_query(tempQ.getName(), dbConn);
						CreateMessage.execute_query(m2.sender, m2.reciever, m2.message, m2.messageID.toString(), m2.timestamp, queueID, dbConn);
						
						answer=new ClientMessage(ClientMessage.sendPReciever,"Sent");
						sendMessage(this.id,answer);
					}
				}else if(ms.getType()==5){
					String senderName=ms.getString();
					Message m=findMessageBySender(senderName);
					answer=new ClientMessage(ClientMessage.querySender,m);
					sendMessage(this.id,answer);
				}else if(ms.getType()==6){
					String usrName=ms.getString();
					Message m=findMessage(usrName);
					answer=new ClientMessage(ClientMessage.queryQueue,m);
					sendMessage(this.id,answer);
				}else if(ms.getType()==7){
					Message m=readMessage();
					answer=new ClientMessage(ClientMessage.readMessage,m);
					sendMessage(this.id,answer);
				}
			}
			deleteClient(id);
			close();
		}
		private synchronized void sendMessage(int id,ClientMessage msg){
			
			if(!writeMsg(msg)) {
				//mapClients.remove(id);
				close();
				logger.log(Level.INFO, "Disconnected Client " + this.username + " removed from list.");
				System.out.println("Disconnected Client " + this.username + " removed from list.");
			}
			
		}
		private Message findMessageBySender(String username){
			Message msg=new Message();
			msg.message="Empyt new message";
			Message m=new Message();
			QueueCl q=new QueueCl();
			boolean stopping=true;
			for(String key: mapQueue.keySet()){
				q=mapQueue.get(key);
				for(int i=0;i<q.messages.size();i++){
					m=q.messages.get(i);
					if(m.sender.equals(username) && m.reciever.isEmpty()){
						msg=m;
						q.messages.remove(i);
						stopping=false;
						break;
					}
					
				}
				if(stopping==false){
					break;
				}
			}
			return msg;
		}
		private Message findMessage(String username){
			Message msg=new Message();
			msg.message="Empyt new message";
			Message m=new Message();
			QueueCl q=new QueueCl();
			boolean stopping=true;
			for(String key: mapQueue.keySet()){
				q=mapQueue.get(key);
				for(int i=0;i<q.messages.size();i++){
					m=q.messages.get(i);
					if(m.reciever.equals(username) && m.reciever!=null){
						msg=m;
						q.messages.remove(i);
						stopping=false;
						break;
					}
					
				}
				if(stopping==false){
					break;
				}
			}
			return msg;
		}
		private Message readMessage(){
			Message msg=new Message();
			msg.message="Empyt new message";
			QueueCl q=new QueueCl();
			if(mapQueue.containsKey("general")){
				q=mapQueue.get("general");
				if (q.noEmpty()) {
					msg=q.messages.get(0);
					q.messages.remove(0);
				}
			}else{
				mapQueue.put("general", new QueueCl());
			}
			return msg;
		}
		private void close() {
			// try to close the connection
			try {
				if(output != null) output.close();
			}
			catch(Exception e) {}
			try {
				if(input != null) input.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}
		private boolean writeMsg(ClientMessage msg) {
			if(!socket.isConnected()) {
				close();
				return false;
			}
			try {
				output.writeObject(msg);
			}
			catch(IOException e) {
				System.out.println("Error sending message to " + username);
				System.out.println(e.toString());
			}
			return true;
		}
			
	}
	public boolean isFull() {
		// TODO Auto-generated method stub
		if(mapClients.size()>=ClientCapacity){
			return true;
		}
		return false;
	}


	
	
}
