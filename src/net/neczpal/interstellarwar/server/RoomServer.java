package net.neczpal.interstellarwar.server;

public class RoomServer extends Thread {

	ServerConnection mServerConnection;
	private volatile boolean mIsRunning;

	/**
	 * Erstellt ein Zimmer-Server,
	 * die sendet die Zimmer-Daten
	 *
	 * @param serverConnection Die Server-Verbindung
	 */
	public RoomServer (ServerConnection serverConnection) {
		this.mServerConnection = serverConnection;
		mIsRunning = false;
	}

	/**
	 * Sendet jede 10 Sekunde die Zimmer-Daten
	 */
	@Override
	public void run () {
		mIsRunning = true;
		while (mIsRunning) {
			try {
				Thread.sleep (10000);
				mServerConnection.listRoom ();
			} catch (InterruptedException e) {
				e.printStackTrace ();
			}
		}
	}

	/**
	 * Beendet das Zimmer-Server
	 */
	public void stopRoomServer () {
		mIsRunning = false;
	}
}
