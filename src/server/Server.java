package server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import client.ClientMessage;
import client.Message;
import client.QueueCl;
import client.ClientMessage;


public class Server {
	
	private int connectionID;
	private int port;
	private Hashtable<Integer, ClientThread> mapClients=new Hashtable<Integer,ClientThread>();
	private Boolean stopCondition;
	private Hashtable<String,QueueCl>mapQueue=new Hashtable<String,QueueCl>();
	private SimpleDateFormat sdf;
	
	//database part
	private Connection conn;
	
	
	public Server(int port){
		this.port=port;
		QueueCl queue=new QueueCl();
		this.mapQueue.put("general", queue);
		
		conn=null;
		
	}
	
	public synchronized void conectDatabase(){
		try
	    {
	      Class.forName("org.postgresql.Driver");
	      String url = "jdbc:postgresql://localhost/messaging";
	      conn = DriverManager.getConnection(url,"message", "message");
	      System.out.println("se conecto");
	    }
	    catch (ClassNotFoundException e)
	    {
	      e.printStackTrace();
	      System.exit(1);
	    }
	    catch (SQLException e)
	    {
	      e.printStackTrace();
	      System.exit(2);
	    }
	}
	public void start(){
		stopCondition=true;
		//conecting to database
		conectDatabase();
		try{
			ServerSocket socket=new ServerSocket(port);
			while(stopCondition){
				System.out.println("Server waiting for Clients on port " + port + ".");
				Socket connection= socket.accept();
				if(!stopCondition){
					break;
				}
				ClientThread ct=new ClientThread(connection,conn);
				System.out.println("Llego aqui\n");
				mapClients.put(ct.id,ct);
				ct.start();
				
			}
			try {
				socket.close();
				for(int i = 0; i < mapClients.size(); ++i) {
					ClientThread tc = mapClients.get(i);
					try {
					tc.input.close();
					tc.output.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				System.out.println("Exception closing the server and clients: " + e);
			}
		}catch(IOException e){
			String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
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
		}
	}
	synchronized void deleteQueue(String queueName){
		mapQueue.remove(queueName);
	}
	synchronized void deleteClient(int id){
		mapClients.remove(id);
	}


	private synchronized void sendMessage(int id,ClientMessage msg){
		
		ClientThread ct=mapClients.get(id);
		if(!ct.writeMsg(msg)) {
			mapClients.remove(id);
			System.out.println("Disconnected Client " + ct.username + " removed from list.");
		}
		
	}

	public static void main(String[] args) {

		int portNumber = 10033;
		switch(args.length) {
			case 1:
				try {
					portNumber = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
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
		Server server = new Server(portNumber);
		server.start();
	}
	class ClientThread extends Thread {
		Socket socket;
		ObjectInputStream input;
		ObjectOutputStream output;
		int id;
		ClientMessage message;
		String username;
		ClientMessage ms;
		String date;
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
				
				
			}catch (IOException e) {
				return;
			}
			catch (ClassNotFoundException e) {
			}
			date = new Date().toString() + "\n";
		}
		//check user
		synchronized void addUser(){
			try 
		    {
		      Statement st = conn.createStatement();
		      ResultSet rs = st.executeQuery("SELECT * FROM clients WHERE clients.name='"+username+"'");
		      if(rs.getString("name").equals(username)){
		    	  System.out.println("User exist");
		      }else{
		    	  st.executeQuery("INSERT INTO clients (name) VALUES ('"+username+"');");
		      }
		      rs.close();
		      st.close();
		    }
		    catch (SQLException se) {
		      System.err.println("Threw a SQLException creating the user.");
		      System.err.println(se.getMessage());
		    }
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
						mapQueue.put(queueName, new QueueCl());
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
						answer=new ClientMessage(ClientMessage.sendPReciever,"Sent");
						sendMessage(this.id,answer);

					}else{
						mapQueue.put(queueName, new QueueCl());
						mapQueue.get(queueName).insertMessage(m2);
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
					if(m.reciever.equals(username)){
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
	
}
