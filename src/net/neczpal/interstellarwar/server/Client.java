package net.neczpal.interstellarwar.server;

import net.neczpal.interstellarwar.common.connection.Command;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends Thread {
	private ServerConnection mServerConnection;
	private Socket mSocket;

	private ObjectOutputStream mOut;
	private ObjectInputStream mIn;

	private int mPort;
	private boolean mIsRunning;

	private Logger mLogger = Logger.getLogger (Client.class.getCanonicalName ());

	/**
	 * Macht eine Klient-Verbindung bei dem server,
	 * damit kann das Server mit Klienten kommunizieren
	 *
	 * @param server Die Server-Verbindung
	 * @param socket Die Socket der Server
	 * @throws IOException falls es kann nicht verbinden
	 */
	public Client (ServerConnection server, Socket socket) throws IOException {
		mSocket = socket;
		mServerConnection = server;

		mPort = socket.getPort ();

		mOut = new ObjectOutputStream (socket.getOutputStream ());
		mIn = new ObjectInputStream (socket.getInputStream ());

		mLogger.setParent (Logger.getLogger (ServerConnection.class.getCanonicalName ()));
		mLogger.setLevel (null);
	}

	/**
	 * Kommunizierung Schleife,
	 * liest die einkommende Befehle zu dem Server
	 */
	@Override
	public void run () {
		mLogger.log (Level.INFO, "Client ready with the Port (" + mPort + ")");
		mIsRunning = true;
		try {
			while (mIsRunning) {
				try {
					Object object = mIn.readObject ();
					if (object instanceof Command) {
						Command command = (Command) object;
						mServerConnection.receive (command, mPort);
					} else {
						mLogger.log (Level.WARNING, "-> Couldn't read " + object + ", because it wasn't a " + Command.class.getSimpleName ());
					}
				} catch (ClassNotFoundException ex) {
					mLogger.log (Level.SEVERE, "-> Couldn't read an object: " + ex.getMessage ());
				}
			}
		} catch (IOException ex2) {
			mLogger.log (Level.WARNING, "Client stopped: " + ex2.getMessage ());
			stopClient ();
		}
	}

	/**
	 * Beendet das Kommunikation
	 */
	public void stopClient () {
		mIsRunning = false;
		try {
			mSocket.close ();
		} catch (IOException ex) {
			mLogger.log (Level.WARNING, "Coulnd't close socket: " + ex.getMessage ());
		}
	}

	/**
	 * Sendet ein Befehl zu dem Klient
	 * @param command der Befehl
	 * @return ob es gesendet ist oder gibt es eine Fehler
	 */
	public boolean send (Command command) {
		try {
			if (!mSocket.isClosed ()) {
				mOut.writeObject (command);
				return true;
			} else {
				mLogger.log (Level.WARNING, "<- Couldn't send Command (" + command + "), because socket was closed.");
			}
		} catch (IOException ex) {
			mLogger.log (Level.WARNING, "<- Couldn't send Command (" + command + "): " + ex.getMessage ());
		}
		return false;
	}

	//GETTERS

	public int getPort () {
		return mPort;
	}

}
