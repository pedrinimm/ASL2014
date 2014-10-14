package client;

import java.io.Serializable;

public class ClientMessage implements Serializable{
	
	static final int norma=0,sendMessage = 1, createQueue = 2, deleteQueue = 3, sendPReciever=4,querySender=5,queryQueue=6,readMessage=7;
	public int type;
	public String message;
	public Message msg=new Message();
	public String queueName;
	
	// constructor
	ClientMessage(int type, String message) {
		this.type = type;
		this.message = message;
	}
	ClientMessage(int type, Message message) {
		this.type = type;
		this.msg = message;
	}
	ClientMessage(int type, Message message,String queueName) {
		this.type = type;
		this.msg = message;
		this.queueName=queueName;
	}
	ClientMessage(int type) {
		this.type = type;
	}
	ClientMessage(String username){
		this.message=username;
	}
	int getType() {
		return type;
	}
	Message getMessage(){
		return msg;
	}
	String getString(){
		return message;
	}
	String getQueueName(){
		return this.queueName;
	}

}
