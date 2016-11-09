package net.neczpal.interstellarwar.clientcommon;

import net.neczpal.interstellarwar.common.RoomData;

import java.util.ArrayList;

public interface UserInterface {
	void connectionReady ();

	void connectionDropped ();

	void listRooms (ArrayList <RoomData> roomData);

	void setIsInRoom (boolean isInRoom);

	void startGame (String mapName);

	void stopGame ();
}
