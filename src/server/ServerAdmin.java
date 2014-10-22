package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;



public class ServerAdmin implements Runnable {
	
    private static int uniqueServerID = 0;
    
    private final int port;
    private final String serverName; 
    private final LinkedList<Server> servers;
    private final ExecutorService poolOfServers;
    private final int noServerThreads;
    private final int noClientsPerServer;
    private final DBConnectorServer connDispatch;
    private boolean stayAlive;

    public ServerAdmin(int noServer, int noClients,
            int Lport) {
        // 1. Create a thread pool with nWorkerThreads
        noServerThreads = noServer;
        noClientsPerServer = noClients;
        poolOfServers = Executors.newFixedThreadPool(noServerThreads);

        // 2. Create a database connection pool
        connDispatch = new DBConnectorServer();

        // 3. Configure other attributes
        port = Lport;
        String hostname = null;
        try {
        	hostname = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			hostname = "unknownhost";
		} finally {
			synchronized (this) {
				serverName = String.format("server%d@%s:%d", uniqueServerID++, hostname, port);
			}
		}
        servers = new LinkedList<Server>();
        stayAlive = true;
    }

    public void setConnectionPool(String username,
        String password, String server, String database, int maxConnections) {
        connDispatch.setupDatabaseConnectionPool(username,password, server, database, maxConnections);
    }

    public void run() {
    	
    	
        Selector serverSelector = null;
        ServerSocketChannel ssc = null;
        try {
            serverSelector = Selector.open();
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);

            ServerSocket ss = ssc.socket();
            InetSocketAddress address = new InetSocketAddress(port);
            ss.bind(address);
            ssc.register(serverSelector, SelectionKey.OP_ACCEPT);
            
            
            
            while (!Thread.interrupted() && stayAlive) {
            	
            	
                serverSelector.select();
                Set<SelectionKey> selectedKeys = serverSelector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isAcceptable()) {
                        // This selector only has one channel registered to it,
                        // so
                        // we can assume which one it is.
                        SocketChannel sc = ssc.accept();
                        sc.configureBlocking(false);
                        
                        // Check if the server is full or not, if it is then
                        // register the socket for writing so we can inform the
                        // client that we can't accept the connection. Otherwise
                        // delegate to the first available thread.
                        if (serverFull()) {
                        	System.out.println("Registering connection for write, server is full");
                        	sc.register(serverSelector, SelectionKey.OP_WRITE);
                        } else {
                        	System.out.println("Delegating connection to available server");                            
                            delegateSocketToWorker(sc);
                        }
                    }/* else if (key.isWritable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        RequestResponse response = new RequestResponse(
                                Status.FULL_SERVER);
                        ByteBuffer responseBuffer = ProtocolMessage
                                .toBytes(response);
                        // TODO: Make unblocking
                        while (responseBuffer.hasRemaining()) {
                            sc.write(responseBuffer);
                        }
                        
                        System.out.println("Refused connection from because the server is full.");
                        key.cancel();
                        sc.close();
                    }*/
                }
            }
        } catch (IOException e) {
        	System.out.println("There was an IO error while running the server, shutting it down."+e);
        } finally {
            if (ssc != null) {
                try {
                    ssc.close();
                } catch (IOException e) {
                	System.out.println("IO exception while closing server channel, it is unrecoverable."+e);
                }
            }
            if (serverSelector != null) {
                try {
                    serverSelector.close();
                } catch (IOException e) {
                	System.out.println("IO exception while closing the server selector, it is unrecoverable."+e);
                }
            }
        }
    }

    public void stop() {
        stayAlive = false;
    }

    public void shutdown() {
    	
        
    }


    private void delegateSocketToWorker(SocketChannel sc) {
    	
    }

    /**
     * Check the existing workers and determine if we can accept an incoming
     * connection. A connection can be accepted if the number of workers is less
     * than the configure maximum or at least one of the current workers has
     * less clients than the configured maximum.
     * 
     * @return true if the server is full, false otherwise.
     */
    private boolean serverFull() {
        if (servers.size() < noServerThreads)
            return false;
        for (Server srv : servers)
            if (!srv.isFull())
                return false;
        return true;
    }

    public String getServerName() {
		return serverName;
	}
    
    
    /**
     * Entry point for the server. Build a server manager using the standard
     * factory and start it in a separate thread.
     * 
     * @param args
     *            command line arguments.
     */
    public static void main(String[] args) {
        if (args.length < 1)
            System.exit(1);
        final ServerAdmin manager = ServerMaker.createAdmin();
        final Thread targetThread = new Thread(manager);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                manager.stop();
                try {
                    targetThread.join(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                manager.shutdown();
            }
        });
        targetThread.start();
    }
}
