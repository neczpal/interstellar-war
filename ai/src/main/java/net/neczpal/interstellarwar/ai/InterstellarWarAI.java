package net.neczpal.interstellarwar.ai;

import net.neczpal.interstellarwar.clientcommon.ClientConnection;
import net.neczpal.interstellarwar.clientcommon.InterstellarWarClient;
import net.neczpal.interstellarwar.clientcommon.UserInterface;
import net.neczpal.interstellarwar.common.game.Planet;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//#TODO why AI's stuck in rooms
public class InterstellarWarAI extends Thread implements UserInterface {

    private ClientConnection mConnection;
    private InterstellarWarClient mGameClient;

    private int mRoomId;
    private int mUserId;
    private volatile boolean mIsRunning;

    public InterstellarWarAI (String host, String userName, int roomId) {
        try {
            mUserId = 0;
            mRoomId = roomId;
            mIsRunning = false;
            mConnection = new ClientConnection (host, userName);
            mConnection.setUserInterface (this);

        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    @Override
    public void run () {
        mIsRunning = true;

        while (mIsRunning) {
            try {
                sleep (1250);//WAIT FOR COMMAND

                //Search OWN nodes that has more POINT then any near NON-OWN nodes
                //IF FOUND move all to there
                findOurPlanets ();
                for (Planet planet : ourPlanets) {
                    Planet target = getNearNeutralSmallerPlanet (planet);
                    try {
                        if (target != null) {
                            if (planet.getOwnedBy () == mConnection.getRoomIndex ()) {
                                mGameClient.startMoveSpaceShip (planet.getId (),
                                        target.getId (),
                                        mGameClient.getCore ().getTickNumber (),
                                        planet.getUnitsNumber ());
                                break;
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                sleep(500);//WAIT FOR COMMAND
                for(Planet planet : ourPlanets) {
                    Planet target = getNearAlliedSmallerPlanet(planet);
                    try {
                        if (target != null) {
                            if (planet.getOwnedBy () == mConnection.getRoomIndex ()) {
                                int fromIndex = planet.getId ();
                                int toIndex = target.getId ();
                                if (fromIndex != -1 && toIndex != -1) {
                                    mGameClient.startMoveSpaceShip (fromIndex,
                                            toIndex,
                                            mGameClient.getCore ().getTickNumber (),
                                            planet.getUnitsNumber ());
                                    break;
                                }
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private List<Planet> ourPlanets = new ArrayList<>();

    private void findOurPlanets () {
        ourPlanets.clear();
        for(Planet planet : mGameClient.getCore().getPlanets()) {
            if (planet.getOwnedBy () == mConnection.getRoomIndex ()) {
                ourPlanets.add (planet);
            }
        }
    }

    private Planet getNearNeutralSmallerPlanet(Planet from) {
        for(Planet planet : mGameClient.getCore().getPlanets()) {
            if (planet.getOwnedBy () != mConnection.getRoomIndex () &&
                    planet.isNeighbor (from) &&
                    planet.getUnitsNumber () < from.getUnitsNumber ()) {

                return planet;
            }
        }

        return null;
    }

    private Planet getNearAlliedSmallerPlanet(Planet from) {
//        for(Planet planet : mGameClient.getCore().getPlanets()) {
        boolean isAllAllied = true;
        for (Planet neighbor : from.getNeighbors ()) {
            if (neighbor.getOwnedBy () != from.getOwnedBy ()) {
                isAllAllied = false;
            }
        }
        if (isAllAllied) {
            return from.getNeighbors ().get ((int) (Math.random () * from.getNeighbors ().size ()));
        }
//        }

        return null;
    }

    @Override
    public void connectionReady () {
        mConnection.enterRoom (mRoomId);
        mUserId = mConnection.getConnectionId ();
    }

    @Override
    public void connectionDropped () {
        stopAI ();
    }

    @Override
    public void listRooms (JSONArray roomData) {

    }

    @Override
    public void setIsInRoom (boolean isInRoom) {

    }

    @Override
    public void startGame (String mapName) {
        mGameClient = mConnection.getGameClient ();
        start ();
    }

    @Override
    public void stopGame () {
        mIsRunning = false;
    }

    @Override
    public void receiveMessage(int uid, String uname, int roomIndex, String message) {
//        if(message.equals(""));# TODO AI message commands mb?
    }

    public void stopAI () {
        stopGame ();

        if (mGameClient != null)
            mGameClient.leaveRoom ();
        if (mConnection != null)
            mConnection.exitServer ();
    }

    public int getUserId () {
        return mUserId;
    }


}
