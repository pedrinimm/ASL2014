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

public class Client_B {
	
	public static LoggingSet lg=new LoggingSet(Client_B.class.getName());
	public static final Logger logger=lg.getLogger();
	//--for measuring reasons 
	public static LoggingSet l_measure=new LoggingSet(Client_B.class.getName()+"-tracing-");
	public static final Logger log_mes=l_measure.getLogger();
	//---end
	private ObjectInputStream input;		
	private ObjectOutputStream output;		
	private Socket socket;
	
	private ServerListener myServer;
	private static String server, username;
	private int port;
	
	Client_B(String server, int port, String username) {
		//log_mes.setUseParentHandlers(false);
		this.server = server;
		this.port = port;
		this.username = username;
		this.myServer=new ServerListener();
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
		//ServerListener myServer=new ServerListener();
		myServer.start();
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
	public void getMsgSender(ClientMessage sender){
		ClientMessage msg=sender;
		try {
			output.writeObject(msg);
		}
		catch(IOException e) {
			logger.log(Level.SEVERE,"Exception writing to server: " + e);
			System.out.println("Exception writing to server: " + e);
		}
	}
	public void readMessage(ClientMessage myswlf){
		ClientMessage msg=myswlf;
		try {
			output.writeObject(msg);
		}
		catch(IOException e) {
			logger.log(Level.SEVERE,"Exception writing to server: " + e);
			System.out.println("Exception writing to server: " + e);
		}
	}
	public void readMessageAnyqueue(ClientMessage myswlf){
		ClientMessage msg=myswlf;
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
		Client_B client = new Client_B(serverAddress, portNumber, userName);
		if(!client.start()){
			return;
		}
		
		// wait for messages from user
		Scanner scan = new Scanner(System.in);
		boolean forever=true;
		
		//while for 30 min
		long start = System.currentTimeMillis();
		long end = start + 60*1000;
		long half = start + 60*500;
		while(System.currentTimeMillis() < end){
			//get sending option
			String option=requestOption.getRandomRequestOption().toString();
			//
			
			if(option.equals("A")){
				String sender=recieverName.getRandomrecieverName().toString();
				log_mes.log(Level.INFO,"\t"+ClientMessage.querySender+"\t"+new Date().getTime());
				ClientMessage msg=new ClientMessage(ClientMessage.querySender,sender);
				client.getMsgSender(msg);
				//System.out.println("I requested "+msg.getClientMessageID()+" I got "+client.myServer.getMessageUUID());
				//while(!client.myServer.getMessageUUID().equals(msg.getClientMessageID())){
					//System.out.println(client.myServer.getMessageUUID());
				//}
				//System.out.println("I moved on "+msg.getClientMessageID()+" I got "+client.myServer.getMessageUUID());
			}
			if(option.equals("B")){
				log_mes.log(Level.INFO,"\t"+ClientMessage.queryQueue+"\t"+new Date().getTime());
				ClientMessage msg=new ClientMessage(ClientMessage.queryQueue,username);
				client.readMessageAnyqueue(msg);
				//System.out.println("I requested "+msg.getClientMessageID()+" I got "+client.myServer.getMessageUUID());
				//while(!client.myServer.getMessageUUID().equals(msg.getClientMessageID())){
					//System.out.println(client.myServer.getMessageUUID());
				//}
				//System.out.println("I moved on "+msg.getClientMessageID()+" I got "+client.myServer.getMessageUUID());
			}
			if(option.equals("C")){
				log_mes.log(Level.INFO,"\t"+ClientMessage.readMessage+"\t"+new Date().getTime());
				ClientMessage msg=new ClientMessage(ClientMessage.readMessage,username);
				client.readMessage(msg);
				//System.out.println("I requested "+msg.getClientMessageID()+" I got "+client.myServer.getMessageUUID());
				//while(!client.myServer.getMessageUUID().equals(msg.getClientMessageID())){
					//System.out.println(client.myServer.getMessageUUID());
				//}
				//System.out.println("I moved on "+msg.getClientMessageID()+" I got "+client.myServer.getMessageUUID());
			}
			
		}
		//end of 30 min
		System.out.println("termino por que yo quiero");
		client.disconnect();
		
	}
	//random option to request
	private enum requestOption {
		A,
		B,
		C;
		public static requestOption getRandomRequestOption() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
	}
	//random reciver name
	private enum recieverName {
        Pedro,
        Pepe,
        Paco,
        Pato,
        Patricio,
        Peter,
        Pancho,
        Petra,
        Phillip,
        Phin;
 
       
        public static recieverName getRandomrecieverName() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
	//random queue name
	private enum queueNameEnum {
        nomral,
        high,
        standar,
        lower,
        fisrt,
        general;
 
        
        public static queueNameEnum getRandomQueueName() {
            Random random = new Random();
            return values()[random.nextInt(values().length)];
        }
    }
	class ServerListener extends Thread {
		ClientMessage msg =new ClientMessage(0);
		public UUID getMessageUUID(){
			return msg.getClientMessageID();
		}
		public void run() {
			while(true) {
				try {
					msg = (ClientMessage) input.readObject();
					
					//dependiendo del tipo de mensaje que reciba es la accion
					int messageType=msg.type;
					if(messageType==5 || messageType==6 || messageType==7){
						System.out.println(msg.msg.message);
						System.out.println(msg.getClientMessageID());
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
