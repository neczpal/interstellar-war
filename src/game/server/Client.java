package game.server;

import game.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends Thread {
	private ServerConnection mServerConnection;
	private Socket mSocket;

	private ObjectOutputStream mOut;
	private ObjectInputStream mIn;

	private int mPort;
	private boolean mIsRunning;

	private Log log = new Log (this);


	public Client (ServerConnection server, Socket socket) {
		this.mSocket = socket;
		this.mServerConnection = server;

		mPort = socket.getPort ();

		try {
			mOut = new ObjectOutputStream (socket.getOutputStream ());
			mIn = new ObjectInputStream (socket.getInputStream ());
		} catch (IOException e) {
			log.e ("Client bibi" + e.getMessage ());
		}
	}

	public void run () {
		log.i ("Client ready, mPort: " + mPort);
		mIsRunning = true;
		try {
			while (mIsRunning) {
				try {
					Object object = mIn.readObject ();
					if (object instanceof Command) {
						Command command = (Command) object;
						mServerConnection.receive (command, mPort);
					}
				} catch (ClassNotFoundException ex) {
					System.err.println (ex.getMessage ());
				}
			}
		} catch (IOException e) {
			stopClient ();
			try {
				close ();
			} catch (IOException e1) {
				e1.printStackTrace ();
			}
		}
	}

	public void stopClient () {
		mIsRunning = false;
	}

	public void close () throws IOException {
		mSocket.close ();
	}

	public int getPort () {
		return mPort;
	}

	public boolean send (Command command) {
		try {
			if (!mSocket.isClosed ()) {
				mOut.writeObject (command);
				return true;
			} else {
				log.w ("Socket is closed cannot send " + command.type);
			}
		} catch (IOException e) {
			log.e ("Nem sikerült elküldeni ezt: " + command.type + "\n" + e.toString ());
		}
		return false;
	}
}
