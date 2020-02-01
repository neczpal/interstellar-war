package net.neczpal.interstellarwar.clientcommon;

import net.neczpal.interstellarwar.common.connection.RoomData;

import java.util.ArrayList;

public class NoUserInterface implements UserInterface {

    @Override
    public void connectionReady () {

    }

    @Override
    public void connectionDropped () {

    }

    @Override
    public void listRooms (ArrayList<RoomData> roomData) {

    }

    @Override
    public void setIsInRoom (boolean isInRoom) {

    }

    @Override
    public void startGame (String mapName) {

    }

    @Override
    public void stopGame () {

    }
}