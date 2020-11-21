package net.neczpal.interstellarwar.server;

import org.json.JSONObject;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {

	private ServerConnection mServerConnection;
	private Scanner mScanner;

	/**
	 * Erstellt ein Server
	 *
	 * @param port Das Port des Servers
	 */
	public Server (int port) {
		mServerConnection = new ServerConnection (port);
		mScanner = new Scanner (System.in);
	}

	public static void main (String[] args) {
		Server server = new Server (23233);
		server.start ();
	}

	/**
	 * Beendet das Server
	 */
	private void stopServer () {
		mServerConnection.stopServerConnection ();
		System.exit (0);
	}

	/**
	 * Einstellt das Log-Stuffe
	 *
	 * @param level Die Stuffe der Logging
	 */
	private void setLogLevel (String level) {
		Logger logger = Logger.getLogger (ServerConnection.class.getCanonicalName ());
		switch (level) {
			case "off":
				logger.setLevel (Level.OFF);
				break;
			case "info":
				logger.setLevel (Level.INFO);
				break;
			case "warning":
				logger.setLevel (Level.WARNING);
				break;
			case "error":
				logger.setLevel (Level.SEVERE);
				break;
			case "all":
				logger.setLevel (Level.ALL);
				break;
		}
	}

	/**
	 * Addiert ein Zimmer zu dem Server
	 *
	 * @param mapName Der Name der Mappe-File
	 */
	private void addRoom (String mapName) {
		mServerConnection.addRoom (mapName);
	}

	/**
	 * Listet die Benutzer, die auf dem Server sind
	 */
	private void listUsers () {
		System.out.println ("USERS:");
		for (Integer id : mServerConnection.getClients ().keySet ()) {
			User user = mServerConnection.getUser (id);
			if (user != null)
				System.out.println (user.getId () + ":\t" + user.getName ());
		}
	}

	/**
	 * Listet die Zimmer, die auf dem Server sind
	 */
	private void listRooms () {
		System.out.println ("ROOMS:");
		for (JSONObject room : mServerConnection.getAllRoomData ()) {
			System.out.println (room.getInt ("room-id") + ":\t" + room.getString ("map-name") + "\t" + room.getJSONArray ("users").length () + "/" + room.getInt ("max-user"));
		}
	}

	/**
	 * Interpretiert die Command-Line-Befehle
	 */
	@Override
	public void run () {
		String[] command;

		while (true) {
			command = mScanner.nextLine ().split (" ");

			switch (command[0]) {
				case "stop":
					stopServer ();
					return;
				case "log":
					if (command.length > 1)
						setLogLevel (command[1]);
					break;
				case "list":
					if (command.length < 2) {
						listRooms ();
						listUsers ();
					} else {
						if (command[1].equals ("rooms")) {
							listRooms ();
						} else if (command[1].equals ("users")) {
							listUsers ();
						}
					}
					break;
				case "add":
					if (command.length > 1)
						addRoom (command[1]);
					break;
			}
		}

	}
}
