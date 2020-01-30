//
//  InterstellarWarCore.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 01. 30..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import Foundation
//### TODO TODO TODO
public class InterstellarWarCore {
    public static let BACKGROUND_TYPES = 5;

    private var mMapName : String;
    private var mMaxUsers : Int;

    private var mPlanets : [Planet];
    private var mRoads : [Road];
    private var mSpaceShips : [SpaceShip];

    private var mBackgroundTextureIndex : Int;

    private var mTickNumber : Int;
    private var mIsRunning : Bool = false;

    /**
     * @Only server
     */
//    init (fileName : String)  {
//        loadMap (fileName);
//    }


    init (jsonArray : [JSON]) {
        setData(jsonArray)
    }


    /**
     * @Only server
     */
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
    /**
     * @return Der List die enthält die Spieldata
     */
//    public func getData () -> [JSON] {#TODODODODODO
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
//    public func setData (data : [JSON]) {
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

    //GAME FUNCTION

    /**
     * @param from Der Planet woher das Raumschiff abfahrt
     * @param to   Der Planet wohin das Raumschiff ankommt
     */
    public func startMoveSpaceShip (fromIndex : Int, toIndex : Int) {
        let fromPlanet = mPlanets[fromIndex];
        let toPlanet = mPlanets[toIndex];
        let unitNumber = fromPlanet.getUnitsNumber ();
        if (unitNumber > 0) {
            fromPlanet.setUnitsNumber (0);
            mSpaceShips.append(SpaceShip(from: fromPlanet, to: toPlanet, numberOfUnits: unitNumber))
        }
    }

    /**
     * Incrementiert die Zeitvariable von allem Raumschiff, und entfernt, falls es angekommt ist.
     */
    private func moveSpaceShips () {
        for spaceShip in mSpaceShips {
            spaceShip.tick ();
            if(spaceShip.isArrived ()) {
                spaceShip.getToPlanet ().spaceShipArrived (spaceShip);
            }
        }
        
        mSpaceShips = mSpaceShips.filter {!$0.isArrived()}
    }

    /**
     * Schafft Einheiten auf alle Planeten
     */
    private func spawnUnits () {
        for planet in self.mPlanets {
            planet.spawnUnit ()
        }
    }


    /**
     * Das Spiel-Thread
     */
    public func run () {
        mIsRunning = true;
        mTickNumber = 1;
        while (mIsRunning) {
//            try {
                Thread.sleep (50);
                mTickNumber++;

                moveSpaceShips ();

                if (mTickNumber % 32 == 0) {
                    spawnUnits ();
                }

//            } catch (InterruptedException e) {
//                e.printStackTrace ();
//            }
        }
    }

    /**
     * Beendet das Spiel
     */
    public func stopGame () {
        mIsRunning = false;
    }


    //GETTERS
    public func getMapName () -> String{
        return mMapName;
    }

    public func getMaxUsers () -> Int{
        return mMaxUsers;
    }

    public func getPlanets () -> [Planet]{
        return mPlanets;
    }

    public func getRoads ()  -> [Road]{
        return mRoads;
    }

    public func getSpaceShips () -> [SpaceShip]{
        return mSpaceShips;
    }

    public func getBackgroundTextureIndex () -> Int {
        return mBackgroundTextureIndex;
    }

    public func getTickNumber () -> Int {
        return mTickNumber;
    }

    public func isRunning () -> Bool{
        return mIsRunning;
    }


}
