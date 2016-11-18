package net.neczpal.interstellarwar.clientcommon;

import net.neczpal.interstellarwar.common.connection.Command;
import net.neczpal.interstellarwar.common.game.InterstellarWarCommand;
import net.neczpal.interstellarwar.common.game.InterstellarWarCore;

import java.io.Serializable;
import java.util.ArrayList;

public class InterstellarWarClient {
	private InterstellarWarCore mInterstellarWarCore;
	private ClientConnection mClientConnection;

	/**
	 * Erstellt ein Spiel-Klient
	 *
	 * @param interstellarWarCore Das Spiel-Core
	 * @param clientConnection    Das Verbindung durch es kommunizieren kann
	 */
	public InterstellarWarClient (InterstellarWarCore interstellarWarCore, ClientConnection clientConnection) {
		mInterstellarWarCore = interstellarWarCore;
		mClientConnection = clientConnection;
	}

	// RECEIVE

	/**
	 * Bekommt ein Befehl
	 *
	 * @param command Der Befehl
	 */
	public void receive (Command command) {
		switch ((InterstellarWarCommand) command.data[0]) {
			case START_MOVE_SPACESHIP:
				mInterstellarWarCore.startMoveSpaceShip ((int) command.data[1], (int) command.data[2], (int) command.data[3], (int) command.data[4]);
				break;
			case REFRESH_WHOLE_MAP:
				mInterstellarWarCore.setData ((ArrayList <Serializable>) command.data[1]);
				break;
		}
	}

	// SEND

	/**
	 * Sendet zu dem Server, das ein Raumschiff abfährt
	 *
	 * @param fromIndex  Index der Planet woher das Raumschiff abfährt
	 * @param toIndex    Index der Planet wohin das Raumschiff fährt
	 * @param tickNumber die Zeitvariable
	 * @param unitNumber die Anzahl der Einheiten des Raumschiffes
	 */
	public void startMoveSpaceShip (int fromIndex, int toIndex, int tickNumber, int unitNumber) {
		mClientConnection.send (Command.Type.GAME_COMMAND, InterstellarWarCommand.START_MOVE_SPACESHIP, fromIndex, toIndex, tickNumber, unitNumber);
	}

	/**
	 * Verlasst das Zimmer
	 */
	public void leaveRoom () {
		mClientConnection.leaveRoom ();
	}

	//GETTERS

	public int getRoomIndex () {
		return mClientConnection.getRoomIndex ();
	}

	public InterstellarWarCore getCore () {
		return mInterstellarWarCore;
	}
}
