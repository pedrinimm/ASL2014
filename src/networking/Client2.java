package networking;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;

import client.Message;
import client.QueueCl;

public class Client2 implements Runnable{
	
	public Client2(){
		
	}
	protected Socket socket = null;
    protected String message   = null;

    public Client2(Socket socket, String message) {
        this.socket = socket;
        this.message   = message;
    }

    public void run() {
        try {
            InputStream input  = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            long time = System.currentTimeMillis();
            output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
                    this.message + " - " +
                    time +
                    "").getBytes());
            output.close();
            input.close();
            System.out.println("Request processed: " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
	public QueueCl createQueueCl(){
		return null;
	}
	
	public Message createMessage(){
		return null;
	}
	public void deleteQueueCl(){
		
	}
	public void connectServer(){
		
	}
    //public static void main(String[] args) {
        //System.out.println("Hello World");
    //}
}