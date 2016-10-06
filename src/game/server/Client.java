package game.server;

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
	private int mId;
	private boolean mIsRunning;


	public Client (ServerConnection servet, Socket socket) {
		this.mSocket = socket;
		this.mServerConnection = servet;

		mPort = socket.getPort ();

		try {
			mOut = new ObjectOutputStream (socket.getOutputStream ());
			mIn = new ObjectInputStream (socket.getInputStream ());
		} catch (IOException e) {
			System.err.println ("Client bibi" + e.getMessage ());
		}
	}

	public void run () {
		System.out.println ("INFO: Client ready, mPort: " + mPort);
		mIsRunning = true;
		try {
			while (mIsRunning) {
				try {
					Command command = (Command) mIn.readObject ();
					mServerConnection.action (command, mPort);
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

	public void send (Command msg) {
		try {
			mOut.writeObject (msg);
		} catch (IOException e) {
			System.err.println ("Nem sikerült elküldeni ezt: " + msg.type);
			e.printStackTrace ();
		}
	}


	public void close () throws IOException {
		mSocket.close ();
	}


	//GETTERS SETTERS

	public int getPort () {
		return mPort;
	}

	public int getClientId () {
		return mId;
	}

	public void setClientId (int i) {
		mId = i;
	}
}
