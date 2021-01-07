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
    private var selectedFromPlanetNode : SKShapeNode?
    private var selectedToPlanetNode : SKShapeNode?
    private var selectedToArrowNode : SKShapeNode?
    
    
    
//    private var mBackground : Any?;//#TODO
    private var mViewPort = CGPoint (x: 0, y: 0);
    private var mZoom = 1.0


    private var mSelectedPlanetId = -1;
    
    
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
                            mSelectedPlanetId = planet.getId ();
                            
                            //FROM - PLANET NODE
                            selectedFromPlanetNode = SKShapeNode(circleOfRadius: CGFloat(planet.getRadius()))
                            selectedFromPlanetNode!.strokeColor = COLOR[planet.getOwnedBy()]
                            selectedFromPlanetNode!.lineWidth = 1.75
                            selectedFromPlanetNode!.zPosition = 10
                            selectedFromPlanetNode!.position = planet.getNode().position
                            
                            worldNode!.addChild(selectedFromPlanetNode!)
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
        super.touchesMoved(touches, with: event)
        for touch in touches {
            let point = touch.location(in: self)
            for (_,finger) in fingers.enumerated() {
                if let finger = finger, finger == touch {
                    if(selectedToPlanetNode != nil) {
                        worldNode!.removeChildren(in: [selectedToArrowNode!])
                        worldNode!.removeChildren(in: [selectedToPlanetNode!])
                        selectedToArrowNode = nil
                        selectedToPlanetNode = nil
                    }
                    
                    if(mSelectedPlanetId == -1) {
                        worldNode!.position.x += point.x - x
                        worldNode!.position.y += point.y - y
                        x = point.x
                        y = point.y
                        
                    } else {
                        //#TODO
//                        let planets = mCore!.getPlanets ();
//                        let realX = point.x - worldNode!.position.x
//                        let realY = point.y - worldNode!.position.y
//
//                        for (_, planet) in planets {
//                            if (planet.isInside (Double(realX), Double(realY)) &&
//                                planet.isNeighbor (
//                                    planets[mSelectedPlanetId]!)
//                                ) {
//                                if (planets[mSelectedPlanetId]!.getOwnedBy () == mInterstellarWarClient!.getRoomIndex ()) {
//
//                                    if ( selectedToPlanetNode == nil) {
//
//                                    selectedToPlanetNode = SKShapeNode(circleOfRadius: CGFloat(planet.getRadius()))
//                                    selectedToPlanetNode!.strokeColor = COLOR[planets[mSelectedPlanetId]!.getOwnedBy()]
//                                    selectedToPlanetNode!.lineWidth = 1.25
//                                    selectedToPlanetNode!.zPosition = 10
//                                    selectedToPlanetNode!.position = planet.getNode().position
//
//
//                                        //Line NODE
//                                        let path = CGMutablePath()
//                                        path.move(to: CGPoint(x: planets[mSelectedPlanetId]!.getX(), y: planets[mSelectedPlanetId]!.getY()))
//                                        path.addLine(to: CGPoint(x: planet.getX(), y: planet.getY()))
//
//
//                                        selectedToArrowNode = SKShapeNode(path: path)
//                                        selectedToArrowNode!.lineWidth = 1.65
//                                        selectedToArrowNode!.strokeColor = COLOR[planets[mSelectedPlanetId]!.getOwnedBy()]
//
//
//                                        worldNode!.addChild(selectedToArrowNode!)
//                                        worldNode!.addChild(selectedToPlanetNode!)
//
//                                    }
//                                }
//                                break;
//                            }
//                        }
                    }
//                    print("finger \(index+1): x=\(point.x) , y=\(point.y)")
                    break
                }
            }
        }
    }

    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesEnded(touches, with: event)
        
        
        for touch in touches {
            let point = touch.location(in: self)
            for (index,finger) in fingers.enumerated() {
                if let finger = finger, finger == touch {
                    if (mSelectedPlanetId == -1) {
                        x = 0.0
                        y = 0.0
                    } else {
                        let planets = mCore!.getPlanets ();
                        let realX = point.x - worldNode!.position.x
                        let realY = point.y - worldNode!.position.y
                        
                        for (_, planet) in planets {
                            if (planet.isInside (Double(realX), Double(realY)) &&
                                planet.isNeighbor (
                                    planets[mSelectedPlanetId]!)
                                ) {
                                if (planets[mSelectedPlanetId]!.getOwnedBy () == mInterstellarWarClient!.getRoomIndex ()) {
                                    
                                    mInterstellarWarClient!.startMoveSpaceShip (mSelectedPlanetId, planet.getId(), mCore!.getTickNumber (), planets[mSelectedPlanetId]!.getUnitsNumber ());
                                }
                                break;
                            }
                        }
                        mSelectedPlanetId = -1

                        worldNode!.removeChildren(in: [selectedFromPlanetNode!])
                        if(selectedToPlanetNode != nil) {
                            worldNode!.removeChildren(in: [selectedToArrowNode!])
                            worldNode!.removeChildren(in: [selectedToPlanetNode!])
                            selectedToArrowNode = nil
                            selectedToPlanetNode = nil
                        }
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
