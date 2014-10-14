package networking;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class server implements Runnable{

    protected int          port   = 10033;
    protected ServerSocket socket = null;
    protected boolean      stop    = false;
    protected Thread       thread= null;
    protected ExecutorService threadPool =
        Executors.newFixedThreadPool(10);

    public server(int port){
        this.port = port;
    }

    public void run(){
        synchronized(this){
            this.thread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.socket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            this.threadPool.execute(new networking.Client2(clientSocket,"Thread Pooled Server"));
        }
        this.threadPool.shutdown();
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.stop;
    }

    public synchronized void stop(){
        this.stop = true;
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.socket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 10033", e);
        }
    }
}
