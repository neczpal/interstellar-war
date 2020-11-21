package net.neczpal.interstellarwar.clientcommon;

import net.neczpal.interstellarwar.common.connection.CommandParamKey;
import net.neczpal.interstellarwar.common.connection.CommandType;
import net.neczpal.interstellarwar.common.game.InterstellarWarCore;
import org.json.JSONObject;

import static net.neczpal.interstellarwar.common.game.InterstellarWarCommandParamKey.*;
import static net.neczpal.interstellarwar.common.game.InterstellarWarCommandType.REFRESH_WHOLE_MAP;
import static net.neczpal.interstellarwar.common.game.InterstellarWarCommandType.START_MOVE_SPACESHIP;

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
    public void receive (JSONObject command) {
        String type = command.getString (GAME_COMMAND_TYPE_KEY);

        switch (type) {
            case REFRESH_WHOLE_MAP: {
	            JSONObject mapData = command.getJSONObject (CommandParamKey.MAP_DATA_KEY);
	            mInterstellarWarCore.setData (mapData);
	            break;
            }
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
		JSONObject command = new JSONObject ();
		command.put (CommandParamKey.COMMAND_TYPE_KEY, CommandType.GAME_COMMAND);
		command.put (GAME_COMMAND_TYPE_KEY, START_MOVE_SPACESHIP);
		command.put (FROM_ID_KEY, fromIndex);
		command.put (TO_ID_KEY, toIndex);
		command.put (TICK_NUMBER_KEY, tickNumber);
		command.put (UNIT_NUMBER_KEY, unitNumber);

		mClientConnection.send (command);
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
