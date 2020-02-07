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
    
    private var mSpaceShips : [SpaceShip]
    
    private var mNode : SKShapeNode

    
    init(key: RoadKey, from : Planet, to : Planet) {
        mKey = key
        mFrom = from
        mTo = to
        mSpaceShips = [SpaceShip] ()
        
        
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
        if (!mSpaceShips.contains (spaceShip)){
            mSpaceShips.append (spaceShip);
        }
    }

//    public func removeSpaceship (spaceShip: SpaceShip) {
//        mSpaceShips.remove
//    }
//
//    public func removeSpaceships (spaceShips: SpaceShip) {
//        mSpaceShips.removeAl
//    }

    public func getSpaceShips () -> [SpaceShip] {
        return mSpaceShips;
    }
    
    public func getNode () -> SKShapeNode {
        return mNode
    }


}
