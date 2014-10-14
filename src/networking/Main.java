package networking;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		server server = new server(10033);
		new Thread(server).start();

		try {
		    Thread.sleep(100 * 1000);
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		System.out.println("Stopping Server");
		server.stop();
	}

}
