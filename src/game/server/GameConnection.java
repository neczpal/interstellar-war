package game.server;

import game.Log;
import game.map.GameMap;

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

	private boolean mIsRunning;
	private Socket mSocket;
	private ObjectInputStream mIn;
	private ObjectOutputStream mOut;

	private Log log = new Log (this);

	public GameConnection (String ip) {
		super ("GameConnection");
		mIsRunning = false;
		mMap = new GameMap ();
		try {
			mSocket = new Socket (ip, 23232);

			if (mSocket.isConnected ()) {
				mIn = new ObjectInputStream (mSocket.getInputStream ());
				mOut = new ObjectOutputStream (mSocket.getOutputStream ());
			}
			//			me = new Player ((int) (Math.random () * 640), (int) (Math.random () * 480), (int) (Math.random () * 50), (int) (Math.random () * 50), mSocket.getPort ());

			//			players.add (me);
			send (Command.Type.ENTER_SERVER);

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
					action (msg);
				} catch (ClassNotFoundException | ClassCastException ex) {
					log.w (ex.getMessage ());
				}
			}
		} catch (IOException e) {

		}
	}

	public void close () {
		mIsRunning = false;
		sendExit ();
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

	private void send (Command.Type type, Serializable[] data) {
		send (new Command (type, data));
	}

	private void send (Command msg) {
		try {
			mOut.writeObject (msg);
		} catch (IOException e) {
			log.e ("Nem sikerült elküldeni(GameConnection):" + msg);
		}
	}

	/**
	 * Servertől kapott üzenet feldolgozása
	 *
	 * @param command
	 */
	public void action (Command command) {
		log.i ("Got msg from server: " + command.type.toString ());

		switch (command.type) {
			case ACCEPT_CONNECTION:
				mConnectionId = (int) command.data[0];
				log.i ("Connection established with the server... id:" + mConnectionId);
//				send (Type.NEED_SYNC, new Serializable[] {mConnectionId});
				break;
			case SYNC_MAP:
				mMap.update ((Integer[]) command.data);
				log.i ("Map is syncronised...");
				break;
		}
	}

	public void sendMove (int dx, int dy) {
		send (Command.Type.MOVE, new Serializable[] {mConnectionId, dx, dy});
	}

	private void sendExit () {
		send (Command.Type.EXIT_SERVER, new Serializable[] {mConnectionId});
	}

	public GameMap getGameMap () {
		return mMap;
	}

	private void setGameMap (GameMap map) {
		mMap = map;
	}
}
