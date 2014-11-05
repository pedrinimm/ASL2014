package client;

import java.io.Serializable;
import java.util.UUID;

public class ClientMessage implements Serializable{
	
	public static final int norma=0;
	public static final int sendMessage = 1;
	public static final int createQueue = 2;
	public static final int deleteQueue = 3;
	public static final int sendPReciever=4;
	public static final int querySender=5;
	public static final int queryQueue=6;
	public static final int readMessage=7;
	public int type;
	public String message;
	public Message msg=new Message();
	public String queueName;
	public UUID clientMessageID;
	
	// constructor
	public ClientMessage(int type, String message,UUID idUUID) {
		this.type = type;
		this.message = message;
		this.clientMessageID=idUUID;
	}
	public ClientMessage(int type, String message) {
		this.type = type;
		this.message = message;
		this.clientMessageID=UUID.randomUUID();
	}
	public ClientMessage(int type, Message message,UUID idUUID) {
		this.type = type;
		this.msg = message;
		this.clientMessageID=idUUID;
	}
	public ClientMessage(int type, Message message) {
		this.type = type;
		this.msg = message;
		this.clientMessageID=UUID.randomUUID();
	}
	public ClientMessage(int type, Message message,String queueName) {
		this.type = type;
		this.msg = message;
		this.queueName=queueName;
		this.clientMessageID=UUID.randomUUID();
	}
	public ClientMessage(int type) {
		this.type = type;
		this.clientMessageID=UUID.randomUUID();
	}
	public ClientMessage(String username){
		this.message=username;
		this.clientMessageID=UUID.randomUUID();
	}
	public int getType() {
		return type;
	}
	public Message getMessage(){
		return msg;
	}
	public String getString(){
		return message;
	}
	public String getQueueName(){
		return this.queueName;
	}
	public UUID getClientMessageID(){
		return this.clientMessageID;
	}

}
