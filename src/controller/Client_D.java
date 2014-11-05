package controller;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

import Logging.LoggingSet;
import client.ClientMessage;
import client.Message;
import database.MonitorDB;

public class Client_D {
	
	public static LoggingSet lg=new LoggingSet(Client_D.class.getName());
	public static final Logger logger=lg.getLogger();
	//--for measuring reasons 
	public static LoggingSet l_measure=new LoggingSet(Client_D.class.getName()+"-tracing-");
	public static final Logger log_mes=l_measure.getLogger();
	//---end
	private ObjectInputStream input;		
	private ObjectOutputStream output;		
	private Socket socket;
	
	private static String server, username;
	private int port;
	
	Client_D(String server, int port, String username) {
		//log_mes.setUseParentHandlers(false);
		this.server = server;
		this.port = port;
		this.username = username;
	}
	public boolean start(){
		try {
			log_mes.log(Level.INFO," Connecting\t"+new Date().getTime());
			socket = new Socket(server, port);
			log_mes.log(Level.INFO," Connected\t"+new Date().getTime());
		} 
		catch(Exception ec) {
			logger.log(Level.SEVERE,"Error connectiong to server:" + ec);
			System.out.println("Error connectiong to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		System.out.println(msg);
		try
		{
			input  = new ObjectInputStream(socket.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			logger.log(Level.SEVERE, "Exception creating new Input/output Streams: " + eIO);
			System.out.println("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// creates the Thread to listen from the server 
		new ServerListener().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		try
		{
			output.writeObject(new ClientMessage(0,username));
			
		}
		catch (IOException eIO) {
			logger.log(Level.SEVERE, "Exception doing login : " + eIO);
			System.out.println("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		return true;
	}
	
	private void disconnect() {
		try { 
			if(input != null) input.close();
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Exception closing inbut buffer : " + e);
		}
		try {
			if(output != null) output.close();
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Exception closing output buffer : " + e);
		}
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Exception closing socket : " + e);
		}
		
	}
	void sendMessage(String username,String message) {
		Message mns=new Message(message,username);
		ClientMessage msg=new ClientMessage(ClientMessage.sendMessage,mns);
		try {
			output.writeObject(msg);
		}
		catch(IOException e) {
			logger.log(Level.SEVERE,"Exception writing to server: " + e);
			System.out.println("Exception writing to server: " + e);
		}
	}
	public void createQueue(String nameQueue){
		ClientMessage msg=new ClientMessage(ClientMessage.createQueue,nameQueue);
		try {
			output.writeObject(msg);
		}
		catch(IOException e) {
			logger.log(Level.SEVERE,"Exception writing to server: " + e);
			System.out.println("Exception writing to server: " + e);
		}
	}
	public void sendMsgQueue(String reciever,String queue, String message,String username2){
		Message msg2=new Message(message,username2,reciever);
		ClientMessage msg=new ClientMessage(ClientMessage.sendPReciever,msg2,queue);
		try {
			output.writeObject(msg);
		}
		catch(IOException e) {
			logger.log(Level.SEVERE,"Exception writing to server: " + e);
			System.out.println("Exception writing to server: " + e);
		}
	}
	public void deleteQueue(String nameQueue){
		ClientMessage msg=new ClientMessage(ClientMessage.deleteQueue,nameQueue);
		try {
			output.writeObject(msg);
		}
		catch(IOException e) {
			logger.log(Level.SEVERE,"Exception writing to server: " + e);
			System.out.println("Exception writing to server: " + e);
		}
		
	}
	public void getMsgSender(String sender){
		ClientMessage msg=new ClientMessage(ClientMessage.querySender,sender);
		try {
			output.writeObject(msg);
		}
		catch(IOException e) {
			logger.log(Level.SEVERE,"Exception writing to server: " + e);
			System.out.println("Exception writing to server: " + e);
		}
	}
	public void readMessage(String myswlf){
		ClientMessage msg=new ClientMessage(ClientMessage.readMessage,myswlf);
		try {
			output.writeObject(msg);
		}
		catch(IOException e) {
			logger.log(Level.SEVERE,"Exception writing to server: " + e);
			System.out.println("Exception writing to server: " + e);
		}
	}
	public void readMessageAnyqueue(String myswlf){
		ClientMessage msg=new ClientMessage(ClientMessage.queryQueue,myswlf);
		try {
			output.writeObject(msg);
		}
		catch(IOException e) {
			logger.log(Level.SEVERE,"Exception writing to server: " + e);
			System.out.println("Exception writing to server: " + e);
		}
	}
	public static void main(String[] args) {
		// default values
		int portNumber = 10033;
		String serverAddress = "localhost";
		String userName = "Pedro";

		switch(args.length) {
			case 3:
				serverAddress = args[2];
				portNumber= Integer.parseInt(args[1]);
				userName=args[0];
				break;
			case 2:
				try {
					portNumber = Integer.parseInt(args[1]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
					return;
				}
				break;
			case 1: 
				userName = args[0];
				break;
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
			return;
		}
		Client_D client = new Client_D(serverAddress, portNumber, userName);
		if(!client.start()){
			return;
		}
		
		// wait for messages from user
		Scanner scan = new Scanner(System.in);
		boolean forever=true;
		
		while(forever) {
			System.out.print(">Input the number you want\n "
					+ "Menu: 1-. Send message 2.Create Queue 3.Delete Queue "
					+ "4.Send Message Particular Reciever particular queue 5.Query Message from Sender "
					+ "6.Query queues with my messages 7.Read message\n\n");
			// read message from user
			int option = scan.nextInt();
			// logout if message is LOGOUT
			System.out.println("El numero es "+option);
			
			
			if(option==1){
				System.out.println("Tipe the message:");
				BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
				String text="";
				try {
					text = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//String text=scan.nextLine();
				log_mes.log(Level.INFO,"\t"+option+"\t"+new Date().getTime());
				client.sendMessage(username,text);
		
			}else if(option==2){
				System.out.println("Tipe the queue name:");
				BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
				String text="";
				try {
					text = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				log_mes.log(Level.INFO,"\t"+option+"\t"+new Date().getTime());
				client.createQueue(text);
			}else if(option==3){
				System.out.println("Tipe the queue name:");
				BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
				String text="";
				try {
					text = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				log_mes.log(Level.INFO,"\t"+option+"\t"+new Date().getTime());
				client.deleteQueue(text);
			}else if(option==4){
				System.out.println("Tipe the message:");
				BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
				String text="";
				String reciever="";
				String queueName="";
				try {
					text = br.readLine();
					System.out.println("Tipe reciever:");
					reciever=br.readLine();
					System.out.println("Tipe queue name:");
					queueName=br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}					
				log_mes.log(Level.INFO,"\t"+option+"\t"+new Date().getTime());
				//Message msg2=new Message(text,username,reciever);
				client.sendMsgQueue(reciever, queueName, text, username);
			}else if(option==5){
				System.out.println("Tipe the sender name:");
				BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
				String sender="";
				try {
					sender = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				client.getMsgSender(sender);
			}else if(option==6){
				log_mes.log(Level.INFO,"\t"+option+"\t"+new Date().getTime());
				client.readMessageAnyqueue(username);;
			}
			else if(option==7){
				log_mes.log(Level.INFO,"\t"+option+"\t"+new Date().getTime());
				client.readMessage(username);
			}else if(option==666){
				client.disconnect();
			}
		}
		client.disconnect();
		
	}
	class ServerListener extends Thread {

		public void run() {
			while(true) {
				try {
					ClientMessage msg = (ClientMessage) input.readObject();
					
					//dependiendo del tipo de mensaje que reciba es la accion
					int messageType=msg.type;
					if(messageType==5 || messageType==6 || messageType==7){
						System.out.println(msg.msg.message);
					}else{
						System.out.println(msg.getString());
					}
					log_mes.log(Level.INFO,"\t"+messageType+"\t"+new Date().getTime());
					//log_mes.info("\t"+messageType+"\t"+new Date().getTime());
					//System.out.println("Mensaje de tipo "+msg.getType()+"\n");
					//System.out.print("> ");
	
				}
				catch(IOException e) {
					logger.log(Level.SEVERE,"Server has close the connection: " + e);
					System.out.println("Server has close the connection: " + e);
					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch(ClassNotFoundException e2) {
					logger.log(Level.SEVERE,"Class not found " + e2);
				}
			}
		}
	}
}
