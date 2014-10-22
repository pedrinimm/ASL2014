package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerMaker {
	public static ServerAdmin createAdmin(){
		BufferedReader br = new BufferedReader( new InputStreamReader(System.in));
		int NoServers=0;
		int NoClientsPerServer=0;
		int portForServer=0;
		String DBUser="";
		String DBPsw="";
		String DBServer="";
		String DBName="";
		int DBNoConn=0;
		
		try {
			System.out.println("Tipe # of Messaging Server:");
			NoServers = br.read();
			System.out.println("Tipe # of Clients per Server:");
			NoClientsPerServer=br.read();
			System.out.println("Tipe the Listening port");
			portForServer=br.read();
			System.out.println("Tipe database User:");
			DBUser=br.readLine();
			System.out.println("Tipe database password:");
			DBPsw=br.readLine();
			System.out.println("Tipe database Server:");
			DBServer=br.readLine();
			System.out.println("Tipe database name:");
			DBName=br.readLine();
			System.out.println("Tipe # database connections:");
			DBNoConn=br.read();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return null;
	}
	
}
