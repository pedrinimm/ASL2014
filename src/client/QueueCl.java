package client;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

public class QueueCl implements Serializable{
	
	public String name;
	public UUID queueId;
	public List<Message> messages;
	
	public QueueCl(){
		this.name="";
		this.queueId=UUID.randomUUID();
		this.messages=new LinkedList<Message>();
	}
	public QueueCl(String queueName){
		this.name=queueName;
		this.queueId=UUID.randomUUID();
		this.messages=new LinkedList<Message>();
	}
	public QueueCl(String queueName,String queueID){
		this.name=queueName;
		this.queueId=UUID.fromString(queueID);
		this.messages=new LinkedList<Message>();
	}
	public void insertMessage(Message newMessage){
		this.messages.add(newMessage);
	}
	public Message getMessage(){
		Message lastMessage=new Message();
		if(!this.messages.isEmpty()){
		return lastMessage= this.messages.get(0);
		}
		else{
			return null;
		}
	}
	public String getName(){
		return this.name;
	}
	public void removeMessage(){
		this.messages.remove(this.messages.size()-1);
	}
	public boolean noEmpty(){
		if(this.messages.isEmpty()){
			return false;
		}
		return true;
	}
}
