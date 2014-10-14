package client;

import java.io.Serializable;
import java.sql.*;
import java.util.*;

public class Message implements Serializable{
	public String message;
	public Timestamp timestamp;
	public String sender;
	public String reciever;
	public UUID messageID;
	public Message(String message,String sender,String reciever){
		this.message=message;
		this.sender=sender;
		this.reciever=reciever;
		this.messageID=UUID.randomUUID();
		long time = System.currentTimeMillis();
		this.timestamp=new Timestamp(time);
	}
	public Message(String message,String sender){
		this.message=message;
		this.sender="";
		this.reciever=reciever;
		this.messageID=UUID.randomUUID();
		long time = System.currentTimeMillis();
		this.timestamp=new Timestamp(time);
	}
	public Message(){
		this.message="";
		this.sender="";
		this.reciever="";
		long time = System.currentTimeMillis();
		this.timestamp=new Timestamp(time);
		this.messageID=UUID.randomUUID();
	}
	public String getReciever(){
		return this.reciever;
	}
	
}
