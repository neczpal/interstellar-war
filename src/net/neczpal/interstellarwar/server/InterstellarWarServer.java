package net.neczpal.interstellarwar.server;

import net.neczpal.interstellarwar.common.connection.CommandParamKey;
import net.neczpal.interstellarwar.common.connection.CommandType;
import net.neczpal.interstellarwar.common.game.InterstellarWarCore;
import org.json.JSONObject;

import static net.neczpal.interstellarwar.common.game.InterstellarWarCommandParamKey.*;
import static net.neczpal.interstellarwar.common.game.InterstellarWarCommandType.REFRESH_WHOLE_MAP;
import static net.neczpal.interstellarwar.common.game.InterstellarWarCommandType.START_MOVE_SPACESHIP;


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
	public void receive (JSONObject command) {
		String type = command.getString (GAME_COMMAND_TYPE_KEY);

		switch (type) {
			case START_MOVE_SPACESHIP: {
				int fromIndex = command.getInt (FROM_ID_KEY);
				int toIndex = command.getInt (TO_ID_KEY);
				mInterstellarWarCore.startMoveSpaceShip (fromIndex, toIndex);
				{
					JSONObject roomCommand = new JSONObject ();
					roomCommand.put (CommandParamKey.COMMAND_TYPE_KEY, CommandType.GAME_COMMAND);
					roomCommand.put (GAME_COMMAND_TYPE_KEY, REFRESH_WHOLE_MAP);
					roomCommand.put (CommandParamKey.MAP_DATA_KEY, mInterstellarWarCore.getData ());

					mConnection.send (roomCommand);
				}
				break;
			}
		}
	}

	//GETTERS

	public InterstellarWarCore getCore () {
		return mInterstellarWarCore;
	}
}
