package networking;

import java.net.*;
import java.io.*;

public class client_s {

	public String host;
	public int port;
	Socket socketClient;
	
	public client_s(String host,int port){
		this.host=host;
		this.port=port;
	}
	public void connect() throws UnknownHostException,IOException{
		System.out.println("Attempting to connect to "+host+":"+port);
        socketClient = new Socket(host,port);
        System.out.println("Connection Established");
	}
	public void readResponse() throws IOException{
		String userInput;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

        System.out.println("Response from server:");
        while ((userInput = stdIn.readLine()) != null) {
            System.out.println(userInput);
        }
	}
	public static void main(String arg[]){
        //Creating a SocketClient object
        client_s client = new client_s ("localhost",9990);
        try {
            //trying to establish connection to the server
            client.connect();
            //if successful, read response from server
            client.readResponse();

        } catch (UnknownHostException e) {
            System.err.println("Host unknown. Cannot establish connection");
        } catch (IOException e) {
            System.err.println("Cannot establish connection. Server may not be up."+e.getMessage());
        }
    }
}
