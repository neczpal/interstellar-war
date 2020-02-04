//
//  Planet.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 01. 30..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import Foundation

public func == (lhs: Planet, rhs: Planet) -> Bool {
    return lhs.getId () == rhs.getId ()
}

public class Planet : Equatable {
    public static let PLANET_TYPES = 18
    
    private var mId : Int

    private var mX : Double
    private var mY : Double
    private var mRadius : Double

    private var mTextureIndex : Int

    private var mOwnedBy : Int
    private var mUnitsNumber : Int
    private var mNeighbors : [Planet]

    convenience init (id : Int, x  : Double, y : Double, radius : Double, ownedBy : Int, unitsNumber : Int) {
        self.init (id: id, x: x, y: y, radius: radius, ownedBy: ownedBy, unitsNumber: unitsNumber, tex: Int.random(in: 0..<Planet.PLANET_TYPES))
    }

    init (id: Int, x  : Double, y : Double, radius : Double, ownedBy : Int, unitsNumber : Int, tex : Int) {
        mTextureIndex = tex
        mId = id
        mX = x
        mY = y
        mRadius = radius
        mUnitsNumber = unitsNumber
        mOwnedBy = ownedBy
        mNeighbors = [Planet]()
    }

    func linkTo (other : Planet) {
        mNeighbors.append (other)
        other.mNeighbors.append (self)
    }

    func spawnUnit () {
        if (mOwnedBy > 0) {
            mUnitsNumber += 1
        }
    }

    func spaceShipArrived (_ spaceShip : SpaceShip) {
        if (spaceShip.getOwnedBy () == mOwnedBy) {
            mUnitsNumber += spaceShip.getUnitsNumber ()
        } else {
            mUnitsNumber -= spaceShip.getUnitsNumber ()
            if (mUnitsNumber < 0) {
                mOwnedBy = spaceShip.getOwnedBy ()
                mUnitsNumber = abs (mUnitsNumber)
            }
        }
    }

    public func distance (_ p : Planet) -> Double {
        return sqrt(pow(Double(p.getX () - getX ()), 2) + pow(Double(p.getY () - getY ()), 2))
    }

    public func isInside (_ px : Double, _ py : Double) -> Bool {
        return pow (px - Double(mX), 2) + pow ((py - Double(mY)), 2) <= Double(mRadius * mRadius)
    }

    public func isNeighbor (_ planet : Planet) -> Bool {
        return mNeighbors.contains (planet)
    }

    //SETTER, GETTERS

    public func getId () -> Int {
        return mId
    }
    
    public func getX () -> Double {
        return mX
    }

    public func getY () -> Double {
        return mY
    }

    public func getRadius () -> Double {
        return mRadius
    }

    public func getOwnedBy () -> Int {
        return mOwnedBy
    }

    public func getUnitsNumber () -> Int {
        return mUnitsNumber
    }

    

    public func getTextureIndex () -> Int {
        return mTextureIndex
    }
    
    // Setters
    
    public func setUnitsNumber (_ unitsNumber : Int) {
        self.mUnitsNumber = unitsNumber
    }

    public func setTextureIndex (textureIndex : Int) {
        mTextureIndex = textureIndex
    }

    public func getNeighbors () -> [Planet] {
        return mNeighbors
    }

    public func setX (x: Double) {
        mX = x
    }

    public func setY (y: Double) {
        mY = y
    }

    public func setRadius (radius: Double) {
        mRadius = radius
    }

    public func setOwnedBy (ownedBy: Int) {
        mOwnedBy = ownedBy
    }

}
