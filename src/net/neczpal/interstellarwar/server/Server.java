package net.neczpal.interstellarwar.server;

import java.util.Scanner;

public class Server {

	public static void main (String[] args) {
		ServerConnection srv = new ServerConnection (23233);
		Scanner scanner = new Scanner (System.in);
		while (!scanner.nextLine ().equals ("stop"))
			;
		srv.stopServerConnection ();
	}
}
