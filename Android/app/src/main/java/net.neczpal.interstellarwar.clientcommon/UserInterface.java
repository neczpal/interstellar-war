package net.neczpal.interstellarwar.clientcommon;

import org.json.JSONArray;

public interface UserInterface {
	/**
	 * Die Verbindung ist aufgebaut
	 */
	void connectionReady();

	/**
	 * Die Verbindung ist verloren
	 */
	void connectionDropped();

    /**
     * Listet die Zimmer
     *
     * @param roomData Die Zimmerdata
     */
    void listRooms(JSONArray roomData);

	/**
	 * Einstellt ob es in einen Zimmer ist
	 *
	 * @param isInRoom Ist es in einem Zimmer?
	 */
	void setIsInRoom(boolean isInRoom);

	/**
	 * Startet das Spiel
	 *
	 * @param mapName Der Name des Spiels
	 */
	void startGame(String mapName);

	/**
	 * Beendet das Spiel
	 */
	void stopGame();
}
