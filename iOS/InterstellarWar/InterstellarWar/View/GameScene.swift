//
//  GameScene.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 02. 04..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//


import SpriteKit
import GameplayKit

class GameScene: SKScene {
    private static let EDGE_MOVE_DISTANCE = 20
    private static let EDGE_MOVE_UNIT = 2
    
    let screenHeight : Double = Double(UIScreen.main.bounds.size.height)
    let screenWidth  : Double = Double(UIScreen.main.bounds.size.width)
    
    //#TODO write setter
    var mInterstellarWarClient : InterstellarWarClient?
    var mCore : InterstellarWarCore?;
    
    private var worldNode : SKNode?
    private var bgNode : SKNode?
    
    
//    private var mBackground : Any?;//#TODO
    private var mViewPort = CGPoint (x: 0, y: 0);
    private var mZoom = 1.0

    private var mWasMouseDown = false;

    private var mSelectedPlanetFromIndex = -1;
    private var mSelectedPlanetToIndex = -1;
    
    
    public func buildUp() {
        worldNode = mCore!.getWorldNode()
        bgNode = mCore!.getBackgroundNode()
        
        
        addChild(worldNode!)
        addChild(bgNode!)
    }
    
    public func destroy() {
        removeChildren(in: [worldNode!, bgNode!])
    }
    
    func touchDown(atPoint pos : CGPoint) {
//        addChild(mCore!.getWorldNode())
    }
    
    func touchMoved(toPoint pos : CGPoint) {
        
    }
    
    func touchUp(atPoint pos : CGPoint) {
        
    }
    
    var fingers = [UITouch?](repeating: nil, count:5)

    var x: CGFloat = 0.0
    var y: CGFloat = 0.0
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        print("Touches began!")
        super.touchesBegan(touches, with: event)
        let planets = mCore!.getPlanets ();
        
        for touch in touches{
            let point = touch.location(in: self)
            for (index,finger)  in fingers.enumerated() {
                if finger == nil {
                    fingers[index] = touch
                    let realX = point.x - worldNode!.position.x
                    let realY = point.y - worldNode!.position.y
                    for (_, planet) in planets {
                        if (planet.isInside (Double(realX), Double(realY)) && mInterstellarWarClient!.getRoomIndex () == planet.getOwnedBy ()) {
                            mSelectedPlanetFromIndex = planet.getId ();
                            break;
                        }
                    }
                    
                    x = point.x
                    y = point.y
                    print("Real \(index+1): x=\(realX) , y=\(realY)")
                    print("Pos \(index+1): x=\(point.x) , y=\(point.y)")
                    
                    break
                }
            }
        }
        
    }

    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
//        print("Touches moved!")
        
        super.touchesMoved(touches, with: event)
        for touch in touches {
            let point = touch.location(in: self)
            for (_,finger) in fingers.enumerated() {
                if let finger = finger, finger == touch {
                    if(mSelectedPlanetFromIndex == -1) {
                        worldNode!.position.x += point.x - x
                        worldNode!.position.y += point.y - y
                        x = point.x
                        y = point.y
                    }
//                    print("finger \(index+1): x=\(point.x) , y=\(point.y)")
                    break
                }
            }
        }
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
//        print("Touches ended!")
        super.touchesEnded(touches, with: event)
        
        let planets = mCore!.getPlanets ();
        
        for touch in touches {
            let point = touch.location(in: self)
            for (index,finger) in fingers.enumerated() {
                if let finger = finger, finger == touch {
                    if (mSelectedPlanetFromIndex == -1) {
                        x = 0.0
                        y = 0.0
                    } else {
                        let realX = point.x - worldNode!.position.x
                        let realY = point.y - worldNode!.position.y
                        
                        for (_, planet) in planets {
                            if (planet.isInside (Double(realX), Double(realY)) &&
                                planet.isNeighbor (
                                    planets[mSelectedPlanetFromIndex]!)
                                ) {
                                if (planets[mSelectedPlanetFromIndex]!.getOwnedBy () == mInterstellarWarClient!.getRoomIndex ()) {
                                    
                                    mInterstellarWarClient!.startMoveSpaceShip (mSelectedPlanetFromIndex, planet.getId(), mCore!.getTickNumber (), planets[mSelectedPlanetFromIndex]!.getUnitsNumber ());
                                }
                                break;
                            }
                        }
                        mSelectedPlanetFromIndex = -1
                    }
                    
                    fingers[index] = nil
                    break
                }
            }
        }
        
        
    }

    override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
        print("Touches canceled!")
        super.touchesCancelled(touches, with: event)
//        guard let touches = touches else {
//            return
//        }
        
        touchesEnded(touches, with: event)
    }
    
    
//    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
//
//        for t in touches {
//            t.
//            self.touchDown(atPoint: t.location(in: self))
//
//
//        }
//    }

//    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
//        for t in touches { self.touchMoved(toPoint: t.location(in: self)) }
//    }

//    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
//        for t in touches { self.touchUp(atPoint: t.location(in: self)) }
//    }

//    override func touchesCancelled(_ touches: Set<UITouch>, with event: UIEvent?) {
//        for t in touches { self.touchUp(atPoint: t.location(in: self)) }
//    }
//
//
//    override func update(_ currentTime: TimeInterval) {
//
//    }
    
    
    
}
