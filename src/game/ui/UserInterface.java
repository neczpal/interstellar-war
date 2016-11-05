package game.ui;

import game.connection.RoomData;

/**
 * Created by neczp on 2016. 11. 05..
 */
public interface UserInterface {
	void connectionReady ();

	void connectionDropped ();

	void listRooms (RoomData[] roomData);

	void setIsInRoom (boolean isInRoom);

	void startGame (String mapName);

	void stopGame ();
}
