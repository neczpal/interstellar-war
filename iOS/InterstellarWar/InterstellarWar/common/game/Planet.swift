//
//  Planet.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 01. 30..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import Foundation

public func == (lhs: Planet, rhs: Planet) -> Bool {
    return lhs.getX() == rhs.getX() &&
        lhs.getY() == rhs.getY()
}

public class Planet : Equatable {
    public static let PLANET_TYPES = 18;

    private var mX : Int;
    private var mY : Int;
    private var mRadius : Int;

    private var mTextureIndex : Int;

    private var mOwnedBy : Int;
    private var mUnitsNumber : Int;
    private var mNeighbors : [Planet];

    convenience init (x  : Int, y : Int, radius : Int, ownedBy : Int, unitsNumber : Int) {
        self.init (x: x, y: y, radius: radius, ownedBy: ownedBy, unitsNumber: unitsNumber, tex: Int.random(in: 0..<Planet.PLANET_TYPES));
    }

    init (x  : Int, y : Int, radius : Int, ownedBy : Int, unitsNumber : Int, tex : Int) {
        mTextureIndex = tex;

        mX = x;
        mY = y;
        mRadius = radius;
        mUnitsNumber = unitsNumber;
        mOwnedBy = ownedBy;
        mNeighbors = [Planet]();
    }

    func linkTo (other : Planet) {
        mNeighbors.append (other);
        other.mNeighbors.append (self);
    }

    func spawnUnit () {
        if (mOwnedBy > 0) {
            mUnitsNumber += 1;
        }
    }

    func spaceShipArrived (spaceShip : SpaceShip) {
        if (spaceShip.getOwnedBy () == mOwnedBy) {
            mUnitsNumber += spaceShip.getUnitsNumber ();
        } else {
            mUnitsNumber -= spaceShip.getUnitsNumber ();
            if (mUnitsNumber < 0) {
                mOwnedBy = spaceShip.getOwnedBy ();
                mUnitsNumber = abs (mUnitsNumber);
            }
        }
    }

    public func distance (_ p : Planet) -> Double {
        return sqrt(pow(Double(p.getX () - getX ()), 2) + pow(Double(p.getY () - getY ()), 2))
    }

    public func isInside (_ px : Double, _ py : Double) -> Bool {
        return pow (px - Double(mX), 2) + pow ((py - Double(mY)), 2) <= Double(mRadius * mRadius);
    }

    public func isNeighbor (_ planet : Planet) -> Bool {
        return mNeighbors.contains (planet);
    }

    //SETTER, GETTERS

    public func getX () -> Int {
        return mX;
    }

    public func getY () -> Int {
        return mY;
    }

    public func getRadius () -> Int {
        return mRadius;
    }

    public func getOwnedBy () -> Int {
        return mOwnedBy;
    }

    public func getUnitsNumber () -> Int {
        return mUnitsNumber;
    }

    public func setUnitsNumber (unitsNumber : Int) {
        self.mUnitsNumber = unitsNumber;
    }

    public func getTextureIndex () -> Int {
        return mTextureIndex;
    }

}
