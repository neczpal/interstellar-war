//
//  InterstellarWarCore.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 01. 30..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import Foundation
import SpriteKit

public class InterstellarWarCore {
    public static let BACKGROUND_TYPES = 5;

    private var mMapName : String;
    private var mMaxUsers : Int;

    private var mPlanets : [Int : Planet];
    private var mRoads : [RoadKey : Road];
    private var mSpaceShips : [Int : SpaceShip];

    private var mBackgroundTextureIndex : Int;

    private var mTickNumber : Int;
    private var mIsRunning : Bool = false;
    private var mTimer : Timer?
    
    private var mSpaceshipIdCounter : Int = 0
    
    private typealias PK = InterstellarWarCommandParamKey
    
    private var mWorldNode : SKNode
    private var mBackgroundNode : SKSpriteNode
    
    var lock = DispatchSemaphore(value: 1)

    init (jsonData : JSON) {
        mMapName = ""
        mMaxUsers = -1
        mPlanets = [Int : Planet]()
        mRoads = [RoadKey : Road]()
        mSpaceShips = [Int :SpaceShip]()
        mBackgroundTextureIndex = -1
        mTickNumber = 0
        mIsRunning = false
        mWorldNode = SKNode()
        mBackgroundNode = SKSpriteNode()
        
        initData(data: jsonData)
    }

    public func getData () -> JSON {
        var data = JSON()
        
        data[PK.BG_TEXTURE_INDEX_KEY].int = mBackgroundTextureIndex
        data[PK.MAP_NAME_KEY].string = mMapName
        data[PK.MAP_MAX_USER_COUNT_KEY].int = mMaxUsers
        
        
        var jsonPlanets = [JSON]()
        
        for planet in mPlanets.values {
            var planetJson = JSON()
            
            planetJson[PK.POSITION_X_KEY].double = planet.getX()
            planetJson[PK.POSITION_Y_KEY].double = planet.getY()
            planetJson[PK.RADIUS_KEY].double = planet.getRadius()
            planetJson[PK.OWNER_KEY].int = planet.getOwnedBy()
            planetJson[PK.UNIT_NUMBER_KEY].int = planet.getUnitsNumber()
            planetJson[PK.TEXTURE_INDEX_KEY].int = planet.getTextureIndex()
            
            jsonPlanets.append(planetJson)
        }
        data[PK.PLANETS_KEY].arrayObject = jsonPlanets
        
        
        
        var jsonRoads = [JSON]()
        
        for road in mRoads.values {
            var roadJson = JSON()
            
            roadJson[PK.FROM_ID_KEY].int = road.getFrom().getId()
            roadJson[PK.TO_ID_KEY].int = road.getTo().getId()
            
            jsonRoads.append(roadJson)
        }
        data[PK.ROADS_KEY].arrayObject = jsonRoads
        
        
        
        
        var jsonSpaceships = [JSON]()
        
        for spaceShip in mSpaceShips.values {
            var spaceShipJson = JSON()
            
            spaceShipJson[PK.FROM_ID_KEY].int = spaceShip.getFromPlanet().getId()
            spaceShipJson[PK.TO_ID_KEY].int = spaceShip.getToPlanet().getId()
            
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
    
    public func setData (data : JSON) {
        lock.wait()
        
        mTickNumber = data[PK.MAP_TICK_NUMBER_KEY].int!
        mBackgroundTextureIndex = data[PK.BG_TEXTURE_INDEX_KEY].int!
        mMapName = data[PK.MAP_NAME_KEY].string!
        mMaxUsers = data[PK.MAP_MAX_USER_COUNT_KEY].int!
        
        
        for planetJson in data[PK.PLANETS_KEY].array! {
            let id = planetJson[PK.GAME_OBJECT_ID_KEY].int!
            let planet = mPlanets[id]!

//            planet.setX(x: planetJson[PK.POSITION_X_KEY].double!)
//            planet.setY(y: planetJson[PK.POSITION_Y_KEY].double!)
//            planet.setRadius(radius: planetJson[PK.RADIUS_KEY].double!)
            planet.setOwnedBy(ownedBy: planetJson[PK.OWNER_KEY].int!)
            planet.setUnitsNumber(planetJson[PK.UNIT_NUMBER_KEY].int!)
//            planet.setTextureIndex(textureIndex: planetJson[PK.TEXTURE_INDEX_KEY].int!)
            
        }
        
        // # roads doesnt change - but just in case -
//        for roadJson in data[PK.ROADS_KEY].array! {
//            let fromIndex = roadJson[PK.FROM_ID_KEY].int!
//            let toIndex = roadJson[PK.TO_ID_KEY].int!
//
//            let fromPlanet = mPlanets[fromIndex];
//            let toPlanet = mPlanets[toIndex];
            
//            fromPlanet.linkTo(other: toPlanet)
//            mRoads.append(Road(from: fromPlanet, to: toPlanet))
//        }
        for spaceShipJson in data[PK.SPACESHIPS_KEY].array! {
            let id = spaceShipJson[PK.GAME_OBJECT_ID_KEY].int!
            
            let fromId = spaceShipJson[PK.FROM_ID_KEY].int!
            let toId = spaceShipJson[PK.TO_ID_KEY].int!
            
            let road = mRoads[RoadKey(fromId, toId)]!
            let fromPlanet = mPlanets[fromId]!;
            let toPlanet = mPlanets[toId]!;
            
            let vx = spaceShipJson[PK.VELOCITY_X_KEY].double!
            let vy = spaceShipJson[PK.VELOCITY_Y_KEY].double!
            
            let owner = spaceShipJson[PK.OWNER_KEY].int!
            let units = spaceShipJson[PK.UNIT_NUMBER_KEY].int!
            
            let currentTick = spaceShipJson[PK.CURRENT_TICK_NUMBER_KEY].int!
            let maxTick = spaceShipJson[PK.MAXIMUM_TICK_NUMBER_KEY].int!
            
            let tex = spaceShipJson[PK.TEXTURE_INDEX_KEY].int!
            
            //Is spaceship already created?
            if(mSpaceShips.keys.contains(id)) {
                let spaceShip = mSpaceShips[id]!
//                spaceShip.setVx (vx: vx);
//                spaceShip.setVy (vy: vy);
//                spaceShip.setRoad (road: road);
                spaceShip.setUnitsNumber (unitsNumber: units);
                spaceShip.setCurrentTick (currentTick: currentTick);
//                spaceShip.setMaxTick (maxTick: maxTick);
//                spaceShip.setTextureIndex (textureIndex: tex);
                
            } else {
                let spaceShip = SpaceShip(id: id, road: road, from: fromPlanet, to: toPlanet, vx: vx, vy: vy, ownedBy: owner, numberOfUnits: units, currentTick: currentTick, maxTick: maxTick, textureIndex: tex)
                mWorldNode.addChild(spaceShip.getNode())
                mSpaceShips[id] = spaceShip
            }
        
        }
        
        lock.signal()
    }
    
    private func initData (data : JSON) {
        lock.wait()
        mBackgroundTextureIndex = data[PK.BG_TEXTURE_INDEX_KEY].int!
        
        mBackgroundNode = SKSpriteNode(texture: TEXTURES.background[mBackgroundTextureIndex])
        mBackgroundNode.zPosition = -1
        
        mMapName = data[PK.MAP_NAME_KEY].string!
        mMaxUsers = data[PK.MAP_MAX_USER_COUNT_KEY].int!
        
        mPlanets = [Int : Planet]()
        
        for planetJson in data[PK.PLANETS_KEY].array! {
            
            let id = planetJson[PK.GAME_OBJECT_ID_KEY].int!
            let x = planetJson[PK.POSITION_X_KEY].double!
            let y = planetJson[PK.POSITION_Y_KEY].double!
            let r = planetJson[PK.RADIUS_KEY].double!
            let owner = planetJson[PK.OWNER_KEY].int!
            let units = planetJson[PK.UNIT_NUMBER_KEY].int!
            let tex = planetJson[PK.TEXTURE_INDEX_KEY].int!
            
            let planet = Planet(id: id, x: x, y: y, radius: r, ownedBy: owner, unitsNumber: units, tex: tex)
            
            mPlanets[id] = planet
            mWorldNode.addChild(planet.getNode())
        }
        
        mRoads = [RoadKey : Road]()
        
        for roadJson in data[PK.ROADS_KEY].array! {
            let fromId = roadJson[PK.FROM_ID_KEY].int!
            let toId = roadJson[PK.TO_ID_KEY].int!
            
            let key = RoadKey(fromId, toId)
            
            let fromPlanet = mPlanets[fromId]!;
            let toPlanet = mPlanets[toId]!;
            
            fromPlanet.linkTo(other: toPlanet)
            
            let road = Road(key: key, from: fromPlanet, to: toPlanet)
            
            mRoads[key] = road
            mWorldNode.addChild(road.getNode())
        }
        mSpaceShips = [Int : SpaceShip]()
        
        // # initialized maps doesn't have spaceships # - just in case -
        for spaceShipJson in data[PK.SPACESHIPS_KEY].array! {
            let id = spaceShipJson[PK.GAME_OBJECT_ID_KEY].int!
            
            let fromId = spaceShipJson[PK.FROM_ID_KEY].int!
            let toId = spaceShipJson[PK.TO_ID_KEY].int!
            
            let road = mRoads[RoadKey(fromId, toId)]!
            let fromPlanet = mPlanets[fromId]!;
            let toPlanet = mPlanets[toId]!;
            
            let vx = spaceShipJson[PK.VELOCITY_X_KEY].double!
            let vy = spaceShipJson[PK.VELOCITY_Y_KEY].double!
            
            let owner = spaceShipJson[PK.OWNER_KEY].int!
            let units = spaceShipJson[PK.UNIT_NUMBER_KEY].int!
            
            let currentTick = spaceShipJson[PK.CURRENT_TICK_NUMBER_KEY].int!
            let maxTick = spaceShipJson[PK.MAXIMUM_TICK_NUMBER_KEY].int!
            
            let tex = spaceShipJson[PK.TEXTURE_INDEX_KEY].int!
            
            mSpaceShips[id] = SpaceShip(id: id, road: road, from: fromPlanet, to: toPlanet, vx: vx, vy: vy, ownedBy: owner, numberOfUnits: units, currentTick: currentTick, maxTick: maxTick, textureIndex: tex)
        }
        
        lock.signal()
    }


    //GAME FUNCTION

    //# prob only server function as well
//    public func startMoveSpaceShip (fromId : Int, toId : Int) {
//        let fromPlanet = mPlanets[fromId]!;
//        let toPlanet = mPlanets[toId]!;
//        let unitNumber = fromPlanet.getUnitsNumber ();
//        if (unitNumber > 0) {
//            fromPlanet.setUnitsNumber (0);
//            let id = mSpaceshipIdCounter
//            mSpaceshipIdCounter += 1
//
//            mSpaceShips[id] = SpaceShip(id : id, from: fromPlanet, to: toPlanet, numberOfUnits: unitNumber)
//
//        }
//    }

    private func moveSpaceShips () {
        for spaceShip in mSpaceShips.values {
            spaceShip.tick ();
            if(spaceShip.isArrived ()) {
                let road = spaceShip.getRoad()
                road.removeSpaceship(id: spaceShip.getId())
                spaceShip.getToPlanet ().spaceShipArrived (spaceShip);
                
                spaceShip.getNode().removeAllChildren()
                spaceShip.getNode().removeFromParent()
//                mWorldNode.removeChildren(in: [spaceShip.getNode()])
            }
        }
        
        mSpaceShips = mSpaceShips.filter {!$0.value.isArrived()}
        
        var deleteCoreSpaceShips = [Int] ()

        for road in mRoads.values {
            let spaceShips = road.getSpaceShips()
            var deleteRoadSpaceShips : Set<Int> = Set<Int>()

            if (spaceShips.count > 1) {
                for i in 0..<spaceShips.count {
                    let spaceShip1 = spaceShips[i]
                    for j in (i+1)..<spaceShips.count {
                        let spaceShip2 = spaceShips[j]
                        if (spaceShip1.isCollided (with: spaceShip2) && spaceShip1.getOwnedBy () != spaceShip2.getOwnedBy ()) {
                            if (spaceShip1.getUnitsNumber () > spaceShip2.getUnitsNumber ()) {
                                spaceShip1.setUnitsNumber (
                                    unitsNumber: spaceShip1.getUnitsNumber () - spaceShip2.getUnitsNumber ());
                                deleteCoreSpaceShips.append (spaceShip2.getId ());
                                deleteRoadSpaceShips.insert (spaceShip2.getId ());
                            } else if (spaceShip2.getUnitsNumber () > spaceShip1.getUnitsNumber ()) {
                                spaceShip2.setUnitsNumber(
                                    unitsNumber: spaceShip2.getUnitsNumber () - spaceShip1.getUnitsNumber ())
                                deleteCoreSpaceShips.append (spaceShip1.getId ());
                                deleteRoadSpaceShips.insert (spaceShip1.getId ());
                            } else {
                                spaceShip2.setUnitsNumber (unitsNumber: 0);
                                spaceShip1.setUnitsNumber (unitsNumber: 0);
                                deleteCoreSpaceShips.append (spaceShip1.getId ())
                                deleteCoreSpaceShips.append (spaceShip2.getId ())
                                deleteRoadSpaceShips.insert (spaceShip1.getId ())
                                deleteRoadSpaceShips.insert (spaceShip2.getId ())
                            }
                        }
                    }
                }
                road.removeSpaceships(ids: deleteRoadSpaceShips)
//                for index in deleteRoadSpaceShips.sorted().reversed() {
//                    road.removeSpaceship(index: index)
//                }
            }
        }
        for key in deleteCoreSpaceShips {
            if ( mSpaceShips.keys.contains(key) ) {
                mSpaceShips[key]!.getNode().removeAllChildren()
                mSpaceShips[key]!.getNode().removeFromParent()
            }
        }
        
        
        
        mSpaceShips = mSpaceShips.filter({
            !deleteCoreSpaceShips.contains($0.key)})
////                mWorldNode.removeChildren(in: [mSpaceShips[key]!.getNode()])
//                mSpaceShips.removeValue(forKey: key)
//            }
//        }
    }

    private func spawnUnits () {
        for planet in mPlanets.values {
            planet.spawnUnit ()
        }
    }

    public func start () {
//        let queue = DispatchQueue(label: "com.aneczpal.interstellar.core", qos: .default)

//        queue.async {
//            self.run()

            self.mTimer = Timer.scheduledTimer(timeInterval: 0.05, target: self, selector: #selector(self.run), userInfo: nil, repeats: true)
            
//        }
    }
    
    @objc func run () {
//        mIsRunning = true;
//        mTickNumber = 1;
//        while (mIsRunning) {
//            try {
//            Thread.sleep (forTimeInterval: 0.05);
            
            lock.wait()
            
            mTickNumber += 1;
        
            moveSpaceShips ();

            if (mTickNumber % 32 == 0) {
                spawnUnits ();
            }
            
            lock.signal()
//            } catch (InterruptedException e) {
//                e.printStackTrace ();
//            }
//        }
    }

    public func stopGame () {
        mIsRunning = false;
        mTimer?.invalidate()
    }


    //GETTERS
    public func getMapName () -> String{
        return mMapName;
    }

    public func getMaxUsers () -> Int{
        return mMaxUsers;
    }

    public func getPlanet(id : Int) -> Planet? {
        return mPlanets[id]
    }
    
    public func getPlanets () -> [Int: Planet]{
        return mPlanets;
    }

    public func getRoad (key : RoadKey) -> Road? {
        return mRoads[key]
    }
    
    public func getRoads ()  -> [RoadKey:Road]{
        return mRoads;
    }

    public func getSpaceShip (id : Int) -> SpaceShip? {
        return mSpaceShips[id]
    }
    
    public func getSpaceShips () -> [Int : SpaceShip]{
        return mSpaceShips;
    }

    public func getBackgroundTextureIndex () -> Int {
        return mBackgroundTextureIndex;
    }

    public func getTickNumber () -> Int {
        lock.wait()
        defer {
            lock.signal()
        }
        return mTickNumber;
    }

    public func isRunning () -> Bool{
        return mIsRunning;
    }
    
    public func getWorldNode () -> SKNode {
        return mWorldNode
    }
    public func getBackgroundNode () -> SKNode {
        return mBackgroundNode
    }


}
