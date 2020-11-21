//
//  Planet.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 01. 30..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import Foundation
import SpriteKit

public func == (lhs: Planet, rhs: Planet) -> Bool {
    return lhs.getId () == rhs.getId ()
}

public class Planet : Equatable {
    public static let PLANET_TYPES = 18
    
    private static let PADDING = CGFloat(30.0)
    
    private var mId : Int

    private var mX : Double
    private var mY : Double
    private var mRadius : Double

    private var mTextureIndex : Int

    private var mOwnedBy : Int
    private var mUnitsNumber : Int
    private var mNeighbors : [Planet]
    
    private var mNode : SKSpriteNode
    private var mLabelNode : SKLabelNode

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
        
        mNode = SKSpriteNode(texture: TEXTURES.planet[tex])
        mNode.position = CGPoint(x: mX, y: mY)
        mNode.size = CGSize(width: radius*2, height: radius*2)
        mNode.colorBlendFactor = 1.0
        mNode.color = COLOR[mOwnedBy]
        
        mLabelNode = SKLabelNode(text: "\(mUnitsNumber)")
        mLabelNode.fontName = "Arial"
        let scalingFactor = min(
            CGFloat(radius * 2) / (mLabelNode.frame.width+Planet.PADDING),
            CGFloat(radius * 2) / (mLabelNode.frame.height+Planet.PADDING))

        // Change the fontSize.
        mLabelNode.fontSize *= scalingFactor

        // Optionally move the SKLabelNode to the center of the rectangle.
        mLabelNode.position = CGPoint(x: 0.0, y: 0.0 - Double(mLabelNode.frame.height / 2.0))
        
        mNode.addChild(mLabelNode)
    }

    func linkTo (other : Planet) {
        mNeighbors.append (other)
        other.mNeighbors.append (self)
    }

    func spawnUnit () {
        if (mOwnedBy > 0) {
            setUnitsNumber (mUnitsNumber+1)
        }
    }

    func spaceShipArrived (_ spaceShip : SpaceShip) {
        if (spaceShip.getOwnedBy () == mOwnedBy) {
            setUnitsNumber(mUnitsNumber + spaceShip.getUnitsNumber ())
        } else {
            let newUnitsNumber = mUnitsNumber - spaceShip.getUnitsNumber ()
            
            if (newUnitsNumber < 0) {
                setOwnedBy(ownedBy: spaceShip.getOwnedBy ())
                setUnitsNumber(abs(newUnitsNumber))
            } else {
                setUnitsNumber(newUnitsNumber)
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
        self.mLabelNode.text = "\(unitsNumber)";
    }

    public func setTextureIndex (textureIndex : Int) {
        mTextureIndex = textureIndex
        mNode.texture = TEXTURES.planet[textureIndex]
    }

    public func getNeighbors () -> [Planet] {
        return mNeighbors
    }

    public func setX (x: Double) {
        mX = x
        mNode.position.x = CGFloat(x)
    }

    public func setY (y: Double) {
        mY = y
        mNode.position.y = CGFloat(y)
    }

    public func setRadius (radius: Double) {
        mRadius = radius
        mNode.size = CGSize(width: 2 * radius, height: 2 * radius)
    }

    public func setOwnedBy (ownedBy: Int) {
        mOwnedBy = ownedBy
        mNode.color = COLOR[ownedBy]
    }
    
    public func getNode () -> SKSpriteNode {
        return mNode
    }

}
