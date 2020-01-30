//
//  InterstellarWarCore.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 01. 30..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import Foundation
//### TODO TODO TODO
//public class InterstellarWarCore extends Thread {
//    public static final int BACKGROUND_TYPES = 5;
//
//    private String mMapName;
//    private int mMaxUsers;
//
//    private ArrayList <Planet> mPlanets;
//    private ArrayList <Road> mRoads;
//    private ArrayList <SpaceShip> mSpaceShips;
//
//    private int mBackgroundTextureIndex;
//
//    private int mTickNumber;
//    private volatile boolean mIsRunning = false;
//
//    /**
//     * Erstellt ein Spiel-Core durch einen File
//     *
//     * @param fileName der Name von der File
//     * @throws IOException falls die File nicht erreichbar ist
//     * @see InterstellarWarCore#loadMap(String)
//     */
//    public InterstellarWarCore (String fileName) throws IOException {
//        loadMap (fileName);
//    }
//
//
//    public InterstellarWarCore (JSONArray jsonArray) {
//        setData (jsonArray.toList ());
//    }
//
//    /**
//     * Erstellt ein Spiel-Core durch einen List
//     *
//     * @param data der List
//     * @see InterstellarWarCore#setData(List)
//     */
//    public InterstellarWarCore (List<Object> data) {
//        setData (data);
//    }
//
//    /**
//     * Einladet ein Spiel von einem File
//     *
//     * @param fileName der Name von der File
//     * @throws IOException falls die File nicht erreichbar ist
//     */
//    private void loadMap (String fileName) throws IOException {
//        mBackgroundTextureIndex = (int) (Math.random () * BACKGROUND_TYPES);
//
//        mPlanets = new ArrayList <> ();
//        mRoads = new ArrayList <> ();
//        mSpaceShips = new ArrayList <> ();
//
//        File mapFile = new File (System.getProperty ("user.dir") + "/res/maps/" + fileName);
//
//        FileReader fileReader = new FileReader (mapFile);
//        BufferedReader bufferedReader = new BufferedReader (fileReader);
//
//        mMapName = bufferedReader.readLine ();
//        mMaxUsers = Integer.parseInt (bufferedReader.readLine ());
//
//        int planetNumber = Integer.parseInt (bufferedReader.readLine ());
//        int connectionNumber = Integer.parseInt (bufferedReader.readLine ());
//
//        for (int i = 0; i < planetNumber; i++) {
//            String[] params = bufferedReader.readLine ().split (" ");
//
//            mPlanets.add (new Planet (Integer.parseInt (params[0]), Integer.parseInt (params[1]), Integer.parseInt (params[2]), Integer.parseInt (params[3]), Integer.parseInt(params[4])));
//        }
//        for (int i = 0; i < connectionNumber; i++) {
//            String[] params = bufferedReader.readLine ().split (" ");
//            int fromIndex = Integer.parseInt (params[0]);
//            int toIndex = Integer.parseInt (params[1]);
//
//            Planet from = mPlanets.get (fromIndex);
//            Planet to = mPlanets.get (toIndex);
//
//            from.linkTo (to);
//            mRoads.add (new Road (from, to));
//        }
//    }
//
//    /**
//     * @return Der List die enthält die Spieldata
//     */
//    public List<Object> getData () {
//        List<Object> list = new ArrayList<> ();
//
//        list.add (mBackgroundTextureIndex);
//
//        list.add (mMapName);
//        list.add (mMaxUsers);
//
//        list.add (mPlanets.size ());
//        list.add (mRoads.size ());
//        list.add (mSpaceShips.size ());
//
//        for (Planet planet : mPlanets) {
//            list.add (planet.getX ());
//            list.add (planet.getY ());
//            list.add (planet.getRadius ());
//            list.add (planet.getOwnedBy ());
//            list.add (planet.getUnitsNumber ());
//            list.add (planet.getTextureIndex ());
//        }
//        for (Road road : mRoads) {
//            list.add (mPlanets.indexOf (road.getFrom ()));
//            list.add (mPlanets.indexOf (road.getTo ()));
//        }
//        for (SpaceShip spaceShip : mSpaceShips) {
//            list.add (mPlanets.indexOf (spaceShip.getFromPlanet ()));
//            list.add (mPlanets.indexOf (spaceShip.getToPlanet ()));
//            list.add ((Double) spaceShip.getVx ());
//            list.add ((Double) spaceShip.getVy ());
//            list.add (spaceShip.getOwnedBy ());
//            list.add (spaceShip.getUnitsNumber ());
//            list.add (spaceShip.getCurrentTick ());
//            list.add (spaceShip.getMaxTick ());
//            list.add (spaceShip.getTextureIndex ());
//        }
//        return list;
//    }
//
//    /**
//     * Einstellt die Spieldata durch der List
//     *
//     * @param data Der List die enthält die Spieldata
//     */
//    public void setData (List<Object> data) {
//        int i = 0;
//
//        mBackgroundTextureIndex = (int) data.get (i++);
//
//        mPlanets = new ArrayList<> ();
//        mRoads = new ArrayList<> ();
//        mSpaceShips = new ArrayList<> ();
//
//
//        mMapName = (String) data.get (i++);
//        mMaxUsers = (int) data.get (i++);
//
//        int planetNumber = (int) data.get (i++);
//        int connectionNumber = (int) data.get (i++);
//        int spaceShipNumber = (int) data.get (i++);
//
//        for (int j = 0; j < planetNumber; j++) {
//            int x = (int) data.get (i++);
//            int y = (int) data.get (i++);
//            int r = (int) data.get (i++);
//            int ownedBy = (int) data.get (i++);
//            int unitNum = (int) data.get (i++);
//            int tex = (int) data.get (i++);
//
//            mPlanets.add (new Planet(x, y, r, ownedBy, unitNum, tex));
//        }
//
//        for (int j = 0; j < connectionNumber; j++) {
//            int fromIndex = (int) data.get (i++);
//            int toIndex = (int) data.get (i++);
//
//            Planet from = mPlanets.get (fromIndex);
//            Planet to = mPlanets.get (toIndex);
//            from.linkTo (to);
//
//            Road road = new Road (from, to);
//
//            mRoads.add (road);
//        }
//        for (int j = 0; j < spaceShipNumber; j++) {
//            int fromIndex = (int) data.get (i++);
//            int toIndex = (int) data.get (i++);
//
//            Planet from = mPlanets.get (fromIndex);
//            Planet to = mPlanets.get (toIndex);
//            //Not nice but hopefully works #TODO nicer solution with JSONObjects
//            double vx, vy;
//            Object vxObj = data.get (i++);
//            if (vxObj instanceof Integer) {
//                vx = ((Integer) vxObj).doubleValue ();
//            } else {
//                vx = (Double) vxObj;
//            }
//            Object vyObj = data.get (i++);
//            if (vxObj instanceof Integer) {
//                vy = ((Integer) vyObj).doubleValue ();
//            } else {
//                vy = (Double) vyObj;
//            }
//            int ownedBy = (int) data.get (i++);
//            int unitsNum = (int) data.get (i++);
//            int curTick = (int) data.get (i++);
//            int maxTick = (int) data.get (i++);
//            int tex = (int) data.get (i++);
//
//            mSpaceShips.add (new SpaceShip (from, to, vx, vy, ownedBy, unitsNum, curTick, maxTick, tex));
//        }
//    }
//
//    //GAME FUNCTION
//
//    /**
//     * @param from Der Planet woher das Raumschiff abfahrt
//     * @param to   Der Planet wohin das Raumschiff ankommt
//     */
//    public void startMoveSpaceShip (int from, int to) {
//        Planet fromPlanet = mPlanets.get (from);
//        Planet toPlanet = mPlanets.get (to);
//        int unitNumber = fromPlanet.getUnitsNumber ();
//        if (unitNumber > 0) {
//            fromPlanet.setUnitsNumber (0);
//            mSpaceShips.add (new SpaceShip (fromPlanet, toPlanet, unitNumber));
//        }
//    }
//
//    /**
//     * Incrementiert die Zeitvariable von allem Raumschiff, und entfernt, falls es angekommt ist.
//     */
//    private void moveSpaceShips () {
//        for (SpaceShip spaceShip : mSpaceShips) {
//            spaceShip.tick ();
//        }
//
//        Iterator <SpaceShip> iterator = mSpaceShips.iterator ();
//        while (iterator.hasNext ()) {
//            SpaceShip spaceShip = iterator.next ();
//            if (spaceShip.isArrived ()) {
//                spaceShip.getToPlanet ().spaceShipArrived (spaceShip);
//                iterator.remove ();
//            }
//        }
//    }
//
//    /**
//     * Schafft Einheiten auf alle Planeten
//     */
//    private void spawnUnits () {
//        for (Planet planet : mPlanets) {
//            planet.spawnUnit ();
//        }
//    }
//
//
//    /**
//     * Das Spiel-Thread
//     */
//    @Override
//    public void run () {
//        mIsRunning = true;
//        mTickNumber = 1;
//        while (mIsRunning) {
//            try {
//                Thread.sleep (50);
//                mTickNumber++;
//
//                moveSpaceShips ();
//
//                if (mTickNumber % 32 == 0) {
//                    spawnUnits ();
//                }
//
//            } catch (InterruptedException e) {
//                e.printStackTrace ();
//            }
//        }
//    }
//
//    /**
//     * Beendet das Spiel
//     */
//    public void stopGame () {
//        mIsRunning = false;
//    }
//
//
//    //GETTERS
//    public String getMapName () {
//        return mMapName;
//    }
//
//    public int getMaxUsers () {
//        return mMaxUsers;
//    }
//
//    public ArrayList <Planet> getPlanets () {
//        return mPlanets;
//    }
//
//    public ArrayList <Road> getRoads () {
//        return mRoads;
//    }
//
//    public ArrayList <SpaceShip> getSpaceShips () {
//        return mSpaceShips;
//    }
//
//    public int getBackgroundTextureIndex () {
//        return mBackgroundTextureIndex;
//    }
//
//    public int getTickNumber () {
//        return mTickNumber;
//    }
//
//    public boolean isRunning () {
//        return mIsRunning;
//    }
//
//
//}
