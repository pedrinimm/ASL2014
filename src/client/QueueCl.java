package client;

import java.io.Serializable;
import java.util.*;

public class QueueCl implements Serializable{
	
	public UUID queueId;
	public List<Message> messages;
	
	public QueueCl(){
		this.queueId=UUID.randomUUID();
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
