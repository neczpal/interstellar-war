package net.neczpal.interstellarwar.ai;

import net.neczpal.interstellarwar.clientcommon.ClientConnection;
import net.neczpal.interstellarwar.clientcommon.InterstellarWarClient;
import net.neczpal.interstellarwar.clientcommon.UserInterface;
import net.neczpal.interstellarwar.common.connection.RoomData;
import net.neczpal.interstellarwar.common.game.Planet;
import net.neczpal.interstellarwar.desktop.ui.frames.GameFrame;
import net.neczpal.interstellarwar.desktop.ui.frames.LobbyFrame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InterstellarWarAI extends Thread implements UserInterface {

    private ClientConnection mConnection;
    private InterstellarWarClient mGameClient;
    private GameFrame mGameFrame;
    private LobbyFrame lobbyFrame;

    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);

        new InterstellarWarAI(1);
    }

    public InterstellarWarAI(int roomId) {
//        mUI = new DesktopUI();
        try {
            mConnection = new ClientConnection("localhost", "AI_USER");
            mConnection.setUserInterface(this);


            sleep (200); // WAIT UNTIL CONNECTION READY

            mConnection.enterRoom (roomId);

            sleep (200);//WAIT FOR COMMAND

            mConnection.startRoom ();

            while(mConnection.getGameClient() == null) {}

            mGameClient = mConnection.getGameClient();

            start ();

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void run() {
        while(true) {
            try {
                sleep (1000);//WAIT FOR COMMAND

                //Search OWN nodes that has more POINT then any near NON-OWN nodes
                //IF FOUND move all to there
                findOurPlanets();
                for(Planet planet : ourPlanets) {
                    Planet target = getNearNeutralSmallerPlanet(planet);
                    try {
                        if (target != null) {
                            if (planet.getOwnedBy() == mConnection.getConnectionId()) {
                                mGameClient.startMoveSpaceShip(mGameClient.getCore().getPlanets().indexOf(planet),
                                        mGameClient.getCore().getPlanets().indexOf(target),
                                        mGameClient.getCore().getTickNumber(),
                                        planet.getUnitsNumber());
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
                            if(planet.getOwnedBy() == mConnection.getConnectionId()) {
                                int fromIndex = mGameClient.getCore ().getPlanets ().indexOf (planet);
                                int toIndex = mGameClient.getCore ().getPlanets ().indexOf (target);
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
//                    sleep(100);//WAIT FOR COMMAND

                }
//                    sleep(100);//WAIT FOR COMMAND

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

                //Search OWN nodes that has only allied node
                    //Find the one allied node that has the most non-allied neutral node neighbour
                        //Send units there






        }
    }
    private List<Planet> ourPlanets = new ArrayList<>();

    private void findOurPlanets () {
        ourPlanets.clear();
        for(Planet planet : mGameClient.getCore().getPlanets()) {
            if (planet.getOwnedBy() == mConnection.getConnectionId()) {
                ourPlanets.add(planet);
            }
        }
    }

    private Planet getNearNeutralSmallerPlanet(Planet from) {
        for(Planet planet : mGameClient.getCore().getPlanets()) {
            if (planet.getOwnedBy() != mConnection.getConnectionId() &&
                planet.isNeighbor(from) &&
                planet.getUnitsNumber() < from.getUnitsNumber()) {

                return planet;
            }
        }

        return null;
    }
    private Planet getNearAlliedSmallerPlanet(Planet from) {
//        for(Planet planet : mGameClient.getCore().getPlanets()) {
            boolean isAllAllied = true;
            for(Planet neighbor : from.getNeighbors()) {
                if (neighbor.getOwnedBy() != from.getOwnedBy()) {
                    isAllAllied = false;
                }
            }
            if (isAllAllied) {
                return from.getNeighbors().get((int) (Math.random() * from.getNeighbors().size()));
            }
//        }

        return null;
    }




    @Override
    public void connectionReady() {
//        mUI.connectionReady();
    }

    @Override
    public void connectionDropped() {
//        mUI.connectionDropped();
    }

    @Override
    public void listRooms(ArrayList<RoomData> roomData) {
//        mUI.listRooms(roomData);
    }

    @Override
    public void setIsInRoom(boolean isInRoom) {
//        mUI.setIsInRoom(isInRoom);
    }

    @Override
    public void startGame(String mapName) {
//        mGameFrame = new GameFrame("Interstellar War : " + mapName, false, 4, mConnection.getGameClient ());
//        mGameFrame.start ();
//        start();
    }

    @Override
    public void stopGame() {
//        if (mGameFrame != null) {
//            mGameFrame.stopGameFrame ();
//            mGameFrame = null;
//        }
    }
}
