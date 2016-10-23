package game.server;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by neczp on 2016. 09. 22..
 */
public class Server {


	public static void main (String[] args) {
		try {
			ServerConnection srv = new ServerConnection ();
			Socket s = new Socket ("152.66.180.66", 23232);
			Client client = new Client(srv,s);
			client.start ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
	}
}
