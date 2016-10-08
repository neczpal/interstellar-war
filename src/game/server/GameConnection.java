package game.server;

import game.Log;
import game.map.GameMap;
import game.map.GameMap2D;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Beszélget a szerverrel
 *
 * @author neczpal
 */
public class GameConnection extends Thread {

	private GameMap mMap;
	private int mConnectionId;
	private String mUserName;

	private boolean mIsRunning;
	private Socket mSocket;
	private ObjectInputStream mIn;
	private ObjectOutputStream mOut;

	private Log log = new Log (this);

	public GameConnection (String ip, String userName) {
		super ("GameConnection " + userName);
		mUserName = userName;
		mIsRunning = false;
		mMap = new GameMap2D ();
		try {
			mSocket = new Socket (ip, 23232);

			if (mSocket.isConnected ()) {
				mIn = new ObjectInputStream (mSocket.getInputStream ());
				mOut = new ObjectOutputStream (mSocket.getOutputStream ());
			}
			send (Command.Type.ENTER_SERVER, mConnectionId, mUserName);

			this.start ();
		} catch (IOException e) {
			log.e ("Hibás ip cím: " + ip);
		}
	}

	public void run () {
		mIsRunning = true;

		try {
			while (mIsRunning) {
				try {
					Command msg = (Command) mIn.readObject ();
					receive (msg);
				} catch (ClassNotFoundException | ClassCastException ex) {
					log.w (ex.getMessage ());
				}
			}
		} catch (IOException e) {

		}
	}

	public void close () {
		mIsRunning = false;
		send (Command.Type.EXIT_SERVER, mConnectionId);
		try {
			mOut.close ();
			mIn.close ();
			mSocket.close ();
		} catch (IOException e) {
			log.e (e.getMessage ());
		}
	}

	/**
	 * Servernek üzenet küldés
	 *
	 * @param type
	 */
	private void send (Command.Type type) {
		send (new Command (type));
	}

	private void send (Command.Type type, Serializable... data) {
		send (new Command (type, data));
	}

	private void send (Command command) {
		try {
			mOut.writeObject (command);
		} catch (IOException e) {
			log.e ("Nem sikerült elküldeni:" + command);
		}
	}

	/**
	 * Servertől kapott üzenet feldolgozása
	 *
	 * @param command
	 */
	public void receive (Command command) {
		log.i ("Got msg from server: " + command.type.toString ());

		switch (command.type) {
			case ACCEPT_CONNECTION:
				mConnectionId = (int) command.data[0];
				log.i ("Connection succesful id: " + mConnectionId);
				mMap.loadData ((Serializable[]) command.data[1]);
				log.i ("Map loaded...");
				break;
			case DECLINE_CONNECTION:
				log.i ("Connection failed!");
				//			case SYNC_MAP:
				//				mMap.update ((Integer[]) command.data);
				//				log.i ("Map is syncronised...");
				//				break;
		}
	}

	//	public void sendMove (int dx, int dy) {
	////		send (Command.Type.MOVE, new Serializable[] {mConnectionId, dx, dy});
	//	}

	public GameMap getGameMap () {
		return mMap;
	}

	private void setGameMap (GameMap map) {
		mMap = map;
	}
}
