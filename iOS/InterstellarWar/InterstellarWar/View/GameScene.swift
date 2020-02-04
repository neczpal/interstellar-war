//
//  GameScene.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 02. 04..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//


import SpriteKit
import GameplayKit

public class GameScene: SKScene {
    private static let EDGE_MOVE_DISTANCE = 20
    private static let EDGE_MOVE_UNIT = 2
    
    let screenHeight : Double = Double(UIScreen.main.bounds.size.height)
    let screenWidth  : Double = Double(UIScreen.main.bounds.size.width)
    
    private var mInterstellarWarClient : InterstellarWarClient?
    private var mCore : InterstellarWarCore?;
//    private var mBackground : Any?;//#TODO
    private var mViewPort = CGPoint (x: 0, y: 0);

    private var mWasMouseDown = false;

    private var mSelectedPlanetFromIndex = -1;
    private var mSelectedPlanetToIndex = -1;

//    private Textures mTextures; prob wont use this

//    private boolean isDrawing = false;//#TODO
    
//    private var label : SKLabelNode?
//    private var spinnyNode : SKShapeNode?
//    private var textureNode : SKSpriteNode?
    
    
//    private let snapperTextures = [
////        SKTexture(imageNamed: ("legycsapo_00000")),
////        SKTexture(imageNamed: ("legycsapo_00001")),
////        SKTexture(imageNamed: ("legycsapo_00002")),
////        SKTexture(imageNamed: ("legycsapo_00003"))
//    ]
//    private let swatterSound = [
////       SKAction.playSoundFileNamed("swatter_1.wav", waitForCompletion: true),
////       SKAction.playSoundFileNamed("swatter_2.wav", waitForCompletion: true),
////       SKAction.playSoundFileNamed("swatter_3.wav", waitForCompletion: true)
//    ]

    
//    override func didMove(to view: SKView) {
//
//    }
    
    
    func touchDown(atPoint pos : CGPoint) {
        
    }
    
    func touchMoved(toPoint pos : CGPoint) {
    }
    
    func touchUp(atPoint pos : CGPoint) {
        
    }
    
//    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
//        for t in touches { self.touchDown(atPoint: t.location(in: self)) }
//    }
//
//    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
//        for t in touches { self.touchMoved(toPoint: t.location(in: self)) }
//    }
//
//    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
//        for t in touches { self.touchUp(atPoint: t.location(in: self)) }
//    }
//
//    override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
//        for t in touches { self.touchUp(atPoint: t.location(in: self)) }
//    }
//
//
//    override func update(_ currentTime: TimeInterval) {
//
//    }
}
