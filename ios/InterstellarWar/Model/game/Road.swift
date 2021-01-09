//
//  Road.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 01. 30..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import SpriteKit

public class RoadKey : Hashable {
    public func hash(into hasher: inout Hasher) {
        hasher.combine((from + to) * ((from+1) * (to+1)))
    }
    
    public static func == (lhs: RoadKey, rhs: RoadKey) -> Bool {
        return (lhs.from == rhs.from && lhs.to == rhs.to) ||
            (lhs.to == rhs.from && lhs.from == rhs.to)
    }
    
    public var from : Int
    public var to : Int
    
    init (_ from: Int, _ to: Int) {
        self.from = from
        self.to = to
    }
}

public class Road {
    
    private var mKey : RoadKey
    
    private var mFrom : Planet, mTo : Planet
    
    private var mSpaceShips : [Int: SpaceShip]
    
    private var mNode : SKShapeNode

    private var lock = DispatchSemaphore(value: 1)
    
    init(key: RoadKey, from : Planet, to : Planet) {
        mKey = key
        mFrom = from
        mTo = to
        mSpaceShips = [Int: SpaceShip] ()
        
        
        let path = CGMutablePath()
        path.move(to: CGPoint(x: from.getX(), y: from.getY()))
        path.addLine(to: CGPoint(x: to.getX(), y: to.getY()))
        
        mNode = SKShapeNode(path: path)
    }

    // GETTERS

    public func getFrom () -> Planet {
        return mFrom
    }

    public func getTo () -> Planet {
        return mTo
    }
    
    public func addSpaceship (spaceShip: SpaceShip) {
        lock.wait()
        
        if (!mSpaceShips.keys.contains (spaceShip.getId())){
            mSpaceShips[spaceShip.getId()] = spaceShip;
        }
        
        lock.signal()
    }

    public func removeSpaceships (ids: Set<Int>) {
        lock.wait()
        
        mSpaceShips = mSpaceShips.filter { !ids.contains($0.key)}
        
        lock.signal()
    }
//
    public func removeSpaceship (id: Int) {
        lock.wait()
        
        mSpaceShips.removeValue(forKey: id)
        
        lock.signal()
    }

    public func getSpaceShips () -> [SpaceShip] {
        return mSpaceShips.values.map {$0}
    }
    
    public func getNode () -> SKShapeNode {
        return mNode
    }


}
