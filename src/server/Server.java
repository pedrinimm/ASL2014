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

import controller.Client;
import Logging.LoggingSet;
import client.ClientMessage;
import client.Message;
import client.QueueCl;
import client.ClientMessage;
import database.client.CreateClient;
import database.client.GetClient;
import database.messsage.CreateMessage;
import database.queue.CreateQueue;
import database.queue.GetQueue;


public class Server implements Runnable{
	
	
	public static LoggingSet lg=new LoggingSet(Server.class.getName());
	public static final Logger logger=lg.getLogger();
	//--for measuring reasons 
	public static LoggingSet l_measure=new LoggingSet(Server.class.getName()+"-tracing-");
	public static final Logger log_mes=l_measure.getLogger();
	//---end
	
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
	
	
	
	
	public Server(int port,int limit,String hostDB){
		
		this.port=port;
		QueueCl queue=new QueueCl();
		this.mapQueue.put("general", queue);
		
		conDispatch =new DBConnectorServer();
		ConectionDbCapacity=limit;
		ClientCapacity=limit;
		
		poolOfDBConnections=new ArrayBlockingQueue<Connection>(ConectionDbCapacity);
		poolClients= Executors.newFixedThreadPool(limit);
		conDispatch.setupDatabaseConnectionPool("postgres", "squirrel", hostDB, "messaging", ConectionDbCapacity);
		try {
			log_mes.log(Level.INFO," ConnectingDB \t"+new Date().getTime());
			conn=conDispatch.getDatabaseConnection();
			if(!conn.isClosed()){
				log_mes.log(Level.INFO," ConnectedDB \t"+new Date().getTime());
				logger.log(Level.INFO, "Connection to the database made");
				System.out.println("conencted!!");
				int queueID_temp=GetQueue.execute_query("general", conn);
				if(queueID_temp==-1){
					CreateQueue.execute_query("general", this.mapQueue.get("general").queueId.toString(), conn);
					System.out.println("General queue created");
					logger.log(Level.INFO, "General queue created");
				}else{
					System.out.println("Queue in database "+queueID_temp);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, "Error: During getting connection from the database");
			e.printStackTrace();
		}
		//System.out.println("LLego aqui"+ port);
		
		
	}
	public void acceptinClient(Socket newClient){
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//System.out.println("LLego aqui");
		stopCondition=true;
		conn=conDispatch.getDatabaseConnection();
		
		try{
			System.out.println("LLego aqui"+ this.port);
			ServerSocket socket=new ServerSocket(this.port);
			
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
				//System.out.println("Llego aqui\n");
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
		String hostDB="localhost";
		switch(args.length) {
			case 3:
				limit = Integer.parseInt(args[0]);
				portNumber= Integer.parseInt(args[1]);
				hostDB=args[2];
				System.out.println(limit);
				System.out.println(portNumber);
				System.out.println(hostDB);
				break;
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
				break;
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Server [portNumber]");
				return;
				
		}
		// create a server object and start it
		Server server = new Server(portNumber,limit,hostDB);
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
				int clientId=GetClient.execute_query(username, dbConn);
				if(clientId==-1){
					CreateClient.execute_query(username, dbConn);
				}
				
			}catch (IOException e) {
				logger.log(Level.SEVERE, "Error drung construction of client handler"+e);
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
					log_mes.log(Level.INFO,"Request\t"+ms.getType()+"\t"+new Date().getTime());
				}
				catch (IOException e) {
					logger.log(Level.SEVERE, username + " Exception reading Streams: " + e);
					System.out.println(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					logger.log(Level.SEVERE, ""+e2);
					System.out.println(e2);
					break;
				}
				
				if(ms.getType()==1){
					System.out.println("Llego mensaje\n");
					Message m=ms.getMessage();
					mapQueue.get("general").insertMessage(m);
					//int queueID=GetQueue.execute_query("general", mapQueue.get("general").queueId.toString(), dbConn);
					int queueID=GetQueue.execute_query("general", dbConn);
					int clientID=GetClient.execute_query(m.sender, dbConn);
					CreateMessage.execute_query(m.sender, m.reciever, m.message, m.messageID.toString(), m.timestamp, queueID,clientID, dbConn);
					answer=new ClientMessage(ClientMessage.sendMessage,"Message sented",ms.getClientMessageID());
					sendMessage(this.id,answer);
					log_mes.log(Level.INFO,"Done\t"+ms.getType()+"\t"+new Date().getTime());

				}else if(ms.getType()==2){
					System.out.println("Creamos una nueva queue\n");
					String queueName=ms.getString();
					if(mapQueue.get(queueName)!=null){
						System.out.println("Queue exist");
						answer=new ClientMessage(ClientMessage.createQueue,"Queue existed before",ms.getClientMessageID());
						sendMessage(this.id,answer);
						log_mes.log(Level.INFO,"Done\t"+ms.getType()+"\t"+new Date().getTime());
					}else{
						mapQueue.put(queueName, new QueueCl(queueName));
						QueueCl tempQ=mapQueue.get(queueName);
						CreateQueue.execute_query(queueName, tempQ.queueId.toString(), dbConn);
						answer=new ClientMessage(ClientMessage.createQueue,"Queue created",ms.getClientMessageID());
						sendMessage(this.id,answer);
						log_mes.log(Level.INFO,"Done\t"+ms.getType()+"\t"+new Date().getTime());
					}
				}else if(ms.getType()==3){
					System.out.println("Borrar queue\n");
					String queueName=ms.getString();
					if(queueName.equals("general")){
						QueueCl temp =mapQueue.get(queueName);
						temp.messages.clear();
						answer=new ClientMessage(ClientMessage.deleteQueue,"Queue Deleted",ms.getClientMessageID());
						sendMessage(this.id,answer);
						log_mes.log(Level.INFO,"Done\t"+ms.getType()+"\t"+new Date().getTime());
					}else if(mapQueue.get(queueName)!=null){
						mapQueue.remove(queueName);
						System.out.println("Queue removed");
						answer=new ClientMessage(ClientMessage.deleteQueue,"Queue Deleted",ms.getClientMessageID());
						sendMessage(this.id,answer);
						log_mes.log(Level.INFO,"Done\t"+ms.getType()+"\t"+new Date().getTime());
					}else{
						System.out.println("Queue do not exist");
						answer=new ClientMessage(ClientMessage.deleteQueue,"Queue didn't exist",ms.getClientMessageID());
						sendMessage(this.id,answer);
						log_mes.log(Level.INFO,"Done\t"+ms.getType()+"\t"+new Date().getTime());
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
						int clientID=GetClient.execute_query(m2.sender, dbConn);
						CreateMessage.execute_query(m2.sender, m2.reciever, m2.message, m2.messageID.toString(), m2.timestamp, queueID,clientID, dbConn);
						answer=new ClientMessage(ClientMessage.sendPReciever,"Sent",ms.getClientMessageID());
						sendMessage(this.id,answer);
						log_mes.log(Level.INFO,"Done\t"+ms.getType()+"\t"+new Date().getTime());

					}else{
						mapQueue.put(queueName, new QueueCl(queueName));
						mapQueue.get(queueName).insertMessage(m2);
						
						QueueCl tempQ=mapQueue.get(queueName);
						CreateQueue.execute_query(queueName, tempQ.queueId.toString(), dbConn);
						//int queueID=GetQueue.execute_query(tempQ.getName(), tempQ.queueId.toString(), dbConn);
						int queueID=GetQueue.execute_query(tempQ.getName(), dbConn);
						int clientID=GetClient.execute_query(m2.sender, dbConn);
						CreateMessage.execute_query(m2.sender, m2.reciever, m2.message, m2.messageID.toString(), m2.timestamp, queueID,clientID, dbConn);
						
						answer=new ClientMessage(ClientMessage.sendPReciever,"Sent",ms.getClientMessageID());
						sendMessage(this.id,answer);
						log_mes.log(Level.INFO,"Done\t"+ms.getType()+"\t"+new Date().getTime());
					}
				}else if(ms.getType()==5){
					String senderName=ms.getString();
					Message m=findMessageBySender(senderName);
					answer=new ClientMessage(ClientMessage.querySender,m,ms.getClientMessageID());
					sendMessage(this.id,answer);
					log_mes.log(Level.INFO,"Done\t"+ms.getType()+"\t"+new Date().getTime());
				}else if(ms.getType()==6){
					String usrName=ms.getString();
					Message m=findMessage(usrName);
					answer=new ClientMessage(ClientMessage.queryQueue,m,ms.getClientMessageID());
					sendMessage(this.id,answer);
					log_mes.log(Level.INFO,"Done\t"+ms.getType()+"\t"+new Date().getTime());
				}else if(ms.getType()==7){
					Message m=readMessage();
					answer=new ClientMessage(ClientMessage.readMessage,m,ms.getClientMessageID());
					sendMessage(this.id,answer);
					log_mes.log(Level.INFO,"Done\t"+ms.getType()+"\t"+new Date().getTime());
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
