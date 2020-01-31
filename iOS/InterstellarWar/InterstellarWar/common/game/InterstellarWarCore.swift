//
//  InterstellarWarCore.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 01. 30..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import Foundation

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
    
    private typealias PK = InterstellarWarCommandParamKey

    /**
     * @Only server
     */
//    init (fileName : String)  {
//        loadMap (fileName);
//    }


    init (jsonData : JSON) {
        mMapName = ""
        mMaxUsers = -1
        mPlanets = [Planet]()
        mRoads = [Road]()
        mSpaceShips = [SpaceShip]()
        mBackgroundTextureIndex = -1
        
        mTickNumber = 0
        mIsRunning = false
        
        setData(data: jsonData)
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
    public func getData () -> JSON {
        var data = JSON()
        
        data[PK.BG_TEXTURE_INDEX_KEY].int = mBackgroundTextureIndex
        data[PK.MAP_NAME_KEY].string = mMapName
        data[PK.MAP_MAX_USER_COUNT_KEY].int = mMaxUsers
        
        
        var jsonPlanets = [JSON]()
        
        for planet in mPlanets {
            var planetJson = JSON()
            
            planetJson[PK.POSITION_X_KEY].int = planet.getX()
            planetJson[PK.POSITION_Y_KEY].int = planet.getY()
            planetJson[PK.RADIUS_KEY].int = planet.getRadius()
            planetJson[PK.OWNER_KEY].int = planet.getOwnedBy()
            planetJson[PK.UNIT_NUMBER_KEY].int = planet.getUnitsNumber()
            planetJson[PK.TEXTURE_INDEX_KEY].int = planet.getTextureIndex()
            
            jsonPlanets.append(planetJson)
        }
        data[PK.PLANETS_KEY].arrayObject = jsonPlanets
        
        
        
        var jsonRoads = [JSON]()
        
        for road in mRoads {
            var roadJson = JSON()
            
            roadJson[PK.FROM_INDEX_KEY].int = mPlanets.firstIndex(of: road.getFrom())
            roadJson[PK.TO_INDEX_KEY].int = mPlanets.firstIndex(of: road.getTo())
            
            jsonRoads.append(roadJson)
        }
        data[PK.ROADS_KEY].arrayObject = jsonRoads
        
        
        
        
        var jsonSpaceships = [JSON]()
        
        for spaceShip in mSpaceShips {
            var spaceShipJson = JSON()
            
            spaceShipJson[PK.FROM_INDEX_KEY].int = mPlanets.firstIndex(of: spaceShip.getFromPlanet())
            spaceShipJson[PK.TO_INDEX_KEY].int = mPlanets.firstIndex(of: spaceShip.getToPlanet())
            
            spaceShipJson[PK.VELOCITY_X_KEY].double = spaceShip.getVx()
            spaceShipJson[PK.VELOCITY_Y_KEY].double = spaceShip.getVy()
            
            spaceShipJson[PK.OWNER_KEY].int = spaceShip.getOwnedBy()
            spaceShipJson[PK.UNIT_NUMBER_KEY].int = spaceShip.getUnitsNumber()
            
            spaceShipJson[PK.CURRENT_TICK_NUMBER_KEY].int = spaceShip.getCurrentTick()
            spaceShipJson[PK.MAXIMUM_TICK_NUMBER_KEY].int = spaceShip.getMaxTick()
            
            spaceShipJson[PK.TEXTURE_INDEX_KEY].int = spaceShip.getTextureIndex()
            
            jsonSpaceships.append(spaceShipJson)
        }
        
        data[PK.SPACESHIPS_KEY].arrayObject = jsonSpaceships
        
        return data
    }

    /**
     * Einstellt die Spieldata durch der List
     *
     * @param data Der List die enthält die Spieldata
     */
    public func setData (data : JSON) {
        
        mBackgroundTextureIndex = data[PK.BG_TEXTURE_INDEX_KEY].int!
        mMapName = data[PK.MAP_NAME_KEY].string!
        mMaxUsers = data[PK.MAP_MAX_USER_COUNT_KEY].int!
        
        mPlanets = [Planet]()
        
        for planetJson in data[PK.PLANETS_KEY].array! {
            let x = planetJson[PK.POSITION_X_KEY].int!
            let y = planetJson[PK.POSITION_Y_KEY].int!
            let r = planetJson[PK.RADIUS_KEY].int!
            let owner = planetJson[PK.OWNER_KEY].int!
            let units = planetJson[PK.UNIT_NUMBER_KEY].int!
            let tex = planetJson[PK.TEXTURE_INDEX_KEY].int!
            
            mPlanets.append(Planet(x: x, y: y, radius: r, ownedBy: owner, unitsNumber: units, tex: tex))
        }
        
        mRoads = [Road]()
        
        for roadJson in data[PK.ROADS_KEY].array! {
            let fromIndex = roadJson[PK.FROM_INDEX_KEY].int!
            let toIndex = roadJson[PK.TO_INDEX_KEY].int!
            
            let fromPlanet = mPlanets[fromIndex];
            let toPlanet = mPlanets[toIndex];
            
            fromPlanet.linkTo(other: toPlanet)
            mRoads.append(Road(from: fromPlanet, to: toPlanet))
        }
        mSpaceShips = [SpaceShip]()
        
        for spaceShipJson in data[PK.SPACESHIPS_KEY].array! {
            let fromIndex = spaceShipJson[PK.FROM_INDEX_KEY].int!
            let toIndex = spaceShipJson[PK.TO_INDEX_KEY].int!
            
            let fromPlanet = mPlanets[fromIndex];
            let toPlanet = mPlanets[toIndex];
            
            let vx = spaceShipJson[PK.VELOCITY_X_KEY].double!
            let vy = spaceShipJson[PK.VELOCITY_Y_KEY].double!
            
            let owner = spaceShipJson[PK.OWNER_KEY].int!
            let units = spaceShipJson[PK.UNIT_NUMBER_KEY].int!
            
            let currentTick = spaceShipJson[PK.CURRENT_TICK_NUMBER_KEY].int!
            let maxTick = spaceShipJson[PK.MAXIMUM_TICK_NUMBER_KEY].int!
            
            let tex = spaceShipJson[PK.TEXTURE_INDEX_KEY].int!
            
            mSpaceShips.append(SpaceShip(from: fromPlanet, to: toPlanet, vx: vx, vy: vy, ownedBy: owner, numberOfUnits: units, currentTick: currentTick, maxTick: maxTick, textureIndex: tex))
        }
        
    }

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
                Thread.sleep (forTimeInterval: 50);
                mTickNumber += 1;

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
