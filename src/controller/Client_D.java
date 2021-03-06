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

//this client creates messages to general queue for 30 second and then for other 30 second send messages to particular
//clients in particular queues 

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
	
	
	
	private ServerListener myServer;
	private static String server, username;
	private int port;
	
	Client_D(String server, int port, String username) {
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
	void sendMessage(ClientMessage mns) {
		//Message mns=new Message(message,username);
		ClientMessage msg=mns;
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
	public void sendMsgQueue(ClientMessage mns){
		//Message msg2=new Message(message,username2,reciever);
		ClientMessage msg=mns;
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
	public void changeAcknow(){
		this.myServer.aknowledge=1;
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
		//Scanner scan = new Scanner(System.in);
		boolean forever=true;
		
		//while for 30 min
		long start = System.currentTimeMillis();
		long end = start + 600*1000;
		long half = start + 600*500;
		long cycle=start+120*1000;
		while(System.currentTimeMillis() < end){
			//get sending option
			if(System.currentTimeMillis() < cycle){
				
			}else{
				log_mes.log(Level.INFO,"\t"+"cycle"+"\t"+cycle);
				cycle=cycle+120*1000;
			}
			int option=0;
			//
			if(System.currentTimeMillis() < half){
				option=1;
			}else{
				option=4;
			}
			if(option==1){
				String text="";
				text="En un lugar de la Mancha, de cuyo nombre no quiero acordarme, no ha mucho tiempo que vivía un hidal";
				Message mns=new Message(text,username);
				ClientMessage msg=new ClientMessage(ClientMessage.sendMessage,mns);
				log_mes.log(Level.INFO,"\t"+ClientMessage.sendMessage+"\t"+new Date().getTime());
				client.sendMessage(msg);
				UUID mine=msg.getClientMessageID();
				UUID returned=client.myServer.getMessageUUID();
				System.out.println("I requested "+mine+" I got "+returned);
				while(!mine.equals(returned)){
					System.out.println(returned);
					returned=client.myServer.getMessageUUID();
				}
				System.out.println("I moved on "+mine+" I got "+returned);
				client.changeAcknow();
				
		
			}else if(option==4){
				String text="";
				text="En un lugar de la Mancha, de cuyo nombre no quiero acordarme, no ha mucho tiempo que vivía un hidal";
					String reciever="";
					String queueName="";
					reciever=recieverName.getRandomrecieverName().toString();
			        queueName=queueNameEnum.getRandomQueueName().toString();
			        
			        Message msg2=new Message(text,username,reciever);
					ClientMessage msg=new ClientMessage(ClientMessage.sendPReciever,msg2,queueName);
			        log_mes.log(Level.INFO,"\t"+ClientMessage.sendPReciever+"\t"+new Date().getTime());
					//Message msg2=new Message(text,username,reciever);
					client.sendMsgQueue(msg);
					UUID mine=msg.getClientMessageID();
					UUID returned=client.myServer.getMessageUUID();
					System.out.println("I requested "+mine+" I got "+returned);
					while(!mine.equals(returned)){
						System.out.println(returned);
						returned=client.myServer.getMessageUUID();
					}
					System.out.println("I moved on "+mine+" I got "+returned);
					client.changeAcknow();
							
				
			}
			
		}
		//end of 30 min
		System.out.println("termino por que yo quiero");
		client.disconnect();
		
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
		//this is for answer checking
		public int aknowledge=0;
		//
		public void changeAknowledge(){
			this.aknowledge=1;
		}
		
		public UUID getMessageUUID(){
			return msg.getClientMessageID();
		}
		public void run() {
			while(true) {
				try {
					this.aknowledge=0;
					//System.out.println(msg.getClientMessageID());
					msg = (ClientMessage) input.readObject();
					
					//dependiendo del tipo de mensaje que reciba es la accion
					int messageType=msg.type;
					if(messageType==5 || messageType==6 || messageType==7){
						System.out.println(msg.msg.message);
						System.out.println(msg.getClientMessageID());

					}else{
						System.out.println(msg.getString());
						System.out.println(msg.getClientMessageID());

					}
					log_mes.log(Level.INFO,"\t"+messageType+"\t"+new Date().getTime());
					//log_mes.info("\t"+messageType+"\t"+new Date().getTime());
					//System.out.println("Mensaje de tipo "+msg.getType()+"\n");
					//System.out.print("> ");
					
					while(this.aknowledge==0){
						System.out.println("estoy en limbo");
					}
	
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
