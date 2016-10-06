package game.server;

import game.map.GameMap;
import game.Log;

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

	public GameConnection (String ip) {
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
			Log.e ("Hibás ip cím: " + ip);
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
					Log.w (ex.getMessage ());
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
			System.out.println (e.getMessage ());
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
			Log.e ("Nem sikerült elküldeni(GameConnection):" + msg);
		}
	}

	/**
	 * Servertől kapott üzenet feldolgozása
	 *
	 * @param command
	 */
	public void action (Command command) {
		System.out.println ("Got msg from server: " + command.type.toString ());

		switch (command.type) {
			case ACCEPT_CONNECTION:
				mConnectionId = (int) command.data[0];
				System.out.println ("Connection established with the server... id:" + mConnectionId);
//				send (Type.NEED_SYNC, new Serializable[] {mConnectionId});
				break;
			//			case ENTER_SERVER://#TODO HELYE
			//				cport = command.data[0];
			//				if (cport != mSocket.getLocalPort ())
			//					players.add (new Player (cport));
			//
			//				break;
			//			case MOVE:
			//				System.out.println ("Player moved, sending the sendMove to the server...");
			//				Player myPlayer = mMap.findPlayerById (mConnectionId);
			////				myPlayer.sendMove ((int) command.data[0], (int) command.data[1]);
			//				sendToId(Type.PLAYER_MOVED, new Serializable[] {mConnectionId, command.data[0], command.data[1]});
			////				cport = command.data[0];
			////				if (cport != mSocket.getLocalPort ()) {
			////					Player p = findPlayerById (cport);
			////					if (p != null)
			////						p.sendMove (command.data[1], command.data[2]);
			////				}
			//				break;
			//			case EXIT_SERVER:
			//				cport = command.data[0];
			//				int index = findIndexOfPlayerById (cport);
			//				if (index > -1)
			//					players.remove (index);
			//
			//				break;
			//			case SYNC_ONE:
			//				Player me = players.get (0);
			//				sendToId (Type.SYNC_MAP, new int[] {(int) me.getX (), (int) me.getY (), (int) me.getWidth (), (int) me.getHeight ()});
			//				break;
			case SYNC_MAP:
				mMap.update ((Integer[]) command.data);
				System.out.println ("Map is syncronised...");
				break;
		}
	}

	public void sendMove (int dx, int dy) {
		send (Command.Type.MOVE, new Serializable[] {mConnectionId, dx, dy});
	}

	private void sendExit () {
		send (Command.Type.EXIT_SERVER, new Serializable[] {mConnectionId});
	}

	private void setGameMap (GameMap map) {
		mMap = map;
	}

	public GameMap getGameMap () {
		return mMap;
	}
}
