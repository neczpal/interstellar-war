package net.neczpal.interstellarwar.server;

import net.neczpal.interstellarwar.common.connection.RoomData;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends Thread {

	private ServerConnection mServerConnection;
	private Scanner mScanner;

	public Server (int port) {
		mServerConnection = new ServerConnection (port);
		mScanner = new Scanner (System.in);
	}

	public static void main (String[] args) {
		Server server = new Server (23233);
		server.start ();
	}

	private void stopServer () {
		mServerConnection.stopServerConnection ();
		System.exit (0);
	}

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

	private void addRoom (String mapName) {
		mServerConnection.addRoom (mapName);
	}

	private void listUsers () {
		System.out.println ("USERS:");
		for (Integer id : mServerConnection.getClients ().keySet ()) {
			User user = mServerConnection.getUser (id);
			if (user != null)
				System.out.println (user.getId () + ":\t" + user.getName ());
		}
	}

	private void listRooms () {
		System.out.println ("ROOMS:");
		for (RoomData room : mServerConnection.getRoomData ()) {
			System.out.println (room.getRoomId () + ":\t" + room.getMapName () + "\t" + room.getUsers ().size () + "/" + room.getMaxUserCount ());
		}
	}

	@Override
	public void run () {
		String[] command;

		while (true) {
			command = mScanner.nextLine ().split (" ");

			switch (command[0]) {
				case "stop":
					stopServer ();
					break;
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
