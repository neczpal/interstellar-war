//
//  Spaceship.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 01. 30..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import Foundation
import SpriteKit

public func == (lhs: SpaceShip, rhs: SpaceShip) -> Bool {
    return lhs.getId () == rhs.getId ()
}

public class SpaceShip : Equatable {
    public static let SPACESHIP_TYPES = 11
    private static let SPACE_SHIP_SPEED = 6.0
    private static let PADDING = CGFloat(30.0)
    
    private var mId : Int
    
    private var mUnitsNumber : Int
    private var mOwnedBy : Int
    private var mMaxTick : Int
    private var mCurrentTick : Int

    private var mFromPlanet : Planet
    private var mToPlanet : Planet
    private var mRoad : Road
 
    private var mVx : Double, mVy : Double
 
    private var mTextureIndex : Int
    
    private var mNode : SKSpriteNode
    private var mLabelNode : SKLabelNode

    convenience init (id: Int, road: Road, from fromPlanet : Planet, to toPlanet : Planet, numberOfUnits unitsNumber : Int) {
        
        let lx = Double(toPlanet.getX () - fromPlanet.getX ())
        let ly = Double(toPlanet.getY () - fromPlanet.getY ())
        let length = fromPlanet.distance (toPlanet)
        
        self.init(id: id,
                  road: road,
                  from: fromPlanet,
                  to: toPlanet,
                  vx: lx / length * SpaceShip.SPACE_SHIP_SPEED,
                  vy: ly / length * SpaceShip.SPACE_SHIP_SPEED,
                  ownedBy : fromPlanet.getOwnedBy(),
                  numberOfUnits: unitsNumber,
                  currentTick : 0,
                  maxTick : Int(length / SpaceShip.SPACE_SHIP_SPEED),
                  textureIndex : Int.random(in: 0..<SpaceShip.SPACESHIP_TYPES))
    }

    init (id: Int, road: Road, from fromPlanet : Planet, to toPlanet : Planet, vx : Double, vy: Double, ownedBy : Int, numberOfUnits unitsNumber : Int, currentTick : Int, maxTick : Int, textureIndex : Int) {
        mId = id
        mTextureIndex = textureIndex
        mUnitsNumber = unitsNumber
        mCurrentTick = currentTick
        mMaxTick = maxTick
        mFromPlanet = fromPlanet
        mToPlanet = toPlanet
        mRoad = road
        mVx = vx
        mVy = vy
        mOwnedBy = ownedBy
        
        //Node set
        mNode = SKSpriteNode(texture: TEXTURES.spaceship[mTextureIndex])
        mNode.position = CGPoint(x: fromPlanet.getX(), y: fromPlanet.getY())
        mNode.size = CGSize(width: TEXTURES.spaceship_dimens[mTextureIndex].width,
                            height: TEXTURES.spaceship_dimens[mTextureIndex].height)
        
        let angle = atan (mVy / mVx)
        mNode.colorBlendFactor = 1.0
        mNode.color = COLOR[mOwnedBy]
        mNode.zRotation = CGFloat(angle)
        
        mLabelNode = SKLabelNode(text: "\(mUnitsNumber)")
        mLabelNode.fontName = "Arial"
        let scalingFactor = min(
            CGFloat(TEXTURES.spaceship_dimens[mTextureIndex].width) / (mLabelNode.frame.width+SpaceShip.PADDING),
            CGFloat(TEXTURES.spaceship_dimens[mTextureIndex].height) / (mLabelNode.frame.height+SpaceShip.PADDING))

        // Change the fontSize.
        mLabelNode.fontSize *= scalingFactor

        // Optionally move the SKLabelNode to the center of the rectangle.
        mLabelNode.position = CGPoint(x: 0.0, y: 0.0 + Double(mLabelNode.frame.height / 2.0))
        
        mLabelNode.zRotation = CGFloat( -angle)
        mNode.addChild(mLabelNode)
        
        mRoad.addSpaceship(spaceShip: self)
    }

    /**
     * Incrementiert die Zeitvariable
     */
    func tick () {
        mCurrentTick += 1
        mNode.position.x += CGFloat(mVx)
        mNode.position.y += CGFloat(mVy)
    }

    /**
     * @return Entscheidet ob das Raumschiff angekommt ist.
     */
    func isArrived () -> Bool {
        return mCurrentTick >= mMaxTick
    }
    
    func isCollided (with other: SpaceShip) -> Bool {
        return mCurrentTick+other.mCurrentTick >= mMaxTick-2 &&
            mToPlanet == other.mFromPlanet &&
            mFromPlanet == other.mToPlanet
    }

    // GETTERS

    func getId () -> Int {
        return mId
    }
    
    func getUnitsNumber () -> Int{
        return mUnitsNumber
    }

    func getOwnedBy () -> Int {
        return mOwnedBy
    }

    func getCurrentTick () -> Int {
        return mCurrentTick
    }

    func getFromPlanet () -> Planet {
        return mFromPlanet
    }

    func getToPlanet () -> Planet {
        return mToPlanet
    }

    func getVx () -> Double {
        return mVx
    }

    func getVy () -> Double {
        return mVy
    }

    func getMaxTick () -> Int {
        return mMaxTick
    }

    func getTextureIndex () -> Int {
        return mTextureIndex
    }
    
    func getRoad () -> Road {
        return mRoad
    }
    
    //SETTERS
    
    func setUnitsNumber (unitsNumber: Int) {
        mUnitsNumber = unitsNumber
        mLabelNode.text = "\(unitsNumber)";
    }

    func setOwnedBy (ownedBy: Int) {
        mOwnedBy = ownedBy
        mNode.color = COLOR[ownedBy]
    }

    func setMaxTick (maxTick: Int) {
        mMaxTick = maxTick
    }

    func setCurrentTick (currentTick: Int) {
        mNode.position.x += CGFloat(Double(mCurrentTick - currentTick) * mVx)
        mNode.position.y += CGFloat(Double(mCurrentTick - currentTick) * mVy)
        
        mCurrentTick = currentTick
    }

    func setFromPlanet (fromPlanet: Planet) {
        mFromPlanet = fromPlanet
    }

    func setToPlanet (toPlanet: Planet) {
        mToPlanet = toPlanet
    }

    func setRoad (road: Road) {
        mRoad = road
    }

    func setVx (vx: Double) {
        mVx = vx
    }

    func setVy (vy: Double) {
        mVy = vy
    }

    func setTextureIndex (textureIndex: Int) {
        mTextureIndex = textureIndex
        mNode.texture = TEXTURES.spaceship[textureIndex]
        mNode.size = CGSize(width: TEXTURES.spaceship_dimens[textureIndex].width,
                            height: TEXTURES.spaceship_dimens[textureIndex].height)
    }
    
    func getNode () -> SKSpriteNode{
        return mNode
    }

}
