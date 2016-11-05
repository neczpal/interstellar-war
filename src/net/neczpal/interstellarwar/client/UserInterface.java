package net.neczpal.interstellarwar.client;

import net.neczpal.interstellarwar.common.RoomData;

public interface UserInterface {
	void connectionReady ();

	void connectionDropped ();

	void listRooms (RoomData[] roomData);

	void setIsInRoom (boolean isInRoom);

	void startGame (String mapName);

	void stopGame ();
}
