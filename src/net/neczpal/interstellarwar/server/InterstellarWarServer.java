package net.neczpal.interstellarwar.server;

import net.neczpal.interstellarwar.common.connection.Command;
import net.neczpal.interstellarwar.common.game.InterstellarWarCommand;
import net.neczpal.interstellarwar.common.game.InterstellarWarCore;

public class InterstellarWarServer {
	private InterstellarWarCore mInterstellarWarCore;
	private Room mConnection;

	/**
	 * Erstellt ein Spiel-Server, es wird mit das Spiel-Klient kommunizieren
	 *
	 * @param interstellarWarCore Das Spiel
	 * @param roomConnection      Das Zimmer der Server-Verbindung
	 */
	public InterstellarWarServer (InterstellarWarCore interstellarWarCore, Room roomConnection) {
		mInterstellarWarCore = interstellarWarCore;
		mConnection = roomConnection;
	}

	//RECEIVE

	/**
	 * Bekommt ein Befehl von dem Spiel Klient
	 *
	 * @param command Der Spiel-Befehl
	 */
	public void receive (Command command) {
		switch ((InterstellarWarCommand) command.data[1]) {
			case START_MOVE_SPACESHIP:
				mInterstellarWarCore.startMoveSpaceShip ((int) command.data[2], (int) command.data[3]);

				mConnection.send (Command.Type.GAME_COMMAND, InterstellarWarCommand.REFRESH_WHOLE_MAP, mInterstellarWarCore.getData ());
				break;
		}
	}

	//GETTERS

	public InterstellarWarCore getCore () {
		return mInterstellarWarCore;
	}
}
