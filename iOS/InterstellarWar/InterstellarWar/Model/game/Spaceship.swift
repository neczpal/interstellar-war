//
//  Spaceship.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 01. 30..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import Foundation

public class SpaceShip {
    public static let SPACESHIP_TYPES = 11
    private static let SPACE_SHIP_SPEED = 6.0
    
    private var mUnitsNumber : Int
    private var mOwnedBy : Int
    private var mMaxTick : Int
    private var mCurrentTick : Int

    private var mFromPlanet : Planet
    private var mToPlanet : Planet
 
    private var mVx : Double, mVy : Double
 
    private var mTextureIndex : Int

    init (from fromPlanet : Planet, to toPlanet : Planet, numberOfUnits unitsNumber : Int) {
        
        mTextureIndex = Int.random(in: 0..<SpaceShip.SPACESHIP_TYPES)
        mUnitsNumber = unitsNumber;
        mCurrentTick = 0;
        mFromPlanet = fromPlanet;
        mToPlanet = toPlanet;

        mOwnedBy = mFromPlanet.getOwnedBy ();

        let lx = Double(toPlanet.getX () - fromPlanet.getX ());
        let ly = Double(toPlanet.getY () - fromPlanet.getY ());
        let length = fromPlanet.distance (toPlanet);

        mMaxTick = Int(length / SpaceShip.SPACE_SHIP_SPEED);

        mVx = lx / length * SpaceShip.SPACE_SHIP_SPEED;
        mVy = ly / length * SpaceShip.SPACE_SHIP_SPEED;
    }

    init (from fromPlanet : Planet, to toPlanet : Planet, vx : Double, vy: Double, ownedBy : Int, numberOfUnits unitsNumber : Int, currentTick : Int, maxTick : Int, textureIndex : Int) {
        mTextureIndex = textureIndex;
        mUnitsNumber = unitsNumber;
        mCurrentTick = currentTick;
        mMaxTick = maxTick;
        mFromPlanet = fromPlanet;
        mToPlanet = toPlanet;
        mVx = vx;
        mVy = vy;
        mOwnedBy = ownedBy;
    }

    /**
     * Incrementiert die Zeitvariable
     */
    func tick () {
        mCurrentTick += 1;
    }

    /**
     * @return Entscheidet ob das Raumschiff angekommt ist.
     */
    func isArrived () -> Bool{
        return mCurrentTick >= mMaxTick;
    }

    // GETTERS

    func getUnitsNumber () -> Int{
        return mUnitsNumber;
    }

    func getOwnedBy () -> Int {
        return mOwnedBy;
    }

    func getCurrentTick () -> Int {
        return mCurrentTick;
    }

    func getFromPlanet () -> Planet {
        return mFromPlanet;
    }

    func getToPlanet () -> Planet {
        return mToPlanet;
    }

    func getVx () -> Double {
        return mVx;
    }

    func getVy () -> Double {
        return mVy;
    }

    func getMaxTick () -> Int {
        return mMaxTick;
    }

    func getTextureIndex () -> Int {
        return mTextureIndex;
    }

}
