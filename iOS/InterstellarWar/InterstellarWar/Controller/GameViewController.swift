//
//  GameViewController.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 02. 04..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import UIKit
import SpriteKit
import GameplayKit

class GameViewController: UIViewController, UserInterface{
    
    private var mGameScene : GameScene?
    
//    var mCore : InterstellarWarCore?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if let view = self.view as! SKView? {
            // Load the SKScene from 'GameScene.sks'
//            if let scene = GameScene(fileNamed: "GameScene")
            
            if let scene = GameScene(fileNamed: "GameScene") {
                // Set the scale mode to scale to fit the window
                scene.scaleMode = .aspectFill
                scene.size = self.view.frame.size
                
                scene.mInterstellarWarClient = sharedConnection!.getGameClient()
                scene.mCore = scene.mInterstellarWarClient!.getCore()
                scene.mCore!.start()
                scene.buildUp()
                
                mGameScene = scene
                // Present the scene
                view.presentScene(scene)
                
            }
            
            view.ignoresSiblingOrder = false
            view.showsFPS = true
            view.showsNodeCount = true
        }
    }

    override var shouldAutorotate: Bool {
        return true
    }

    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        if UIDevice.current.userInterfaceIdiom == .phone {
            return .allButUpsideDown
        } else {
            return .all
        }
    }

    override var prefersStatusBarHidden: Bool {
        return false
    }
    
//    override func viewWillDisappear(_ animated: Bool) {
//            super.viewWillDisappear(animated)
//            if isMovingFromParent {
//                if let viewControllers = self.navigationController?.viewControllers {
//                    if (viewControllers.count >= 1) {
//                        let previousViewController = viewControllers[viewControllers.count-2] as! LoginViewController
//                        // whatever you want to do
//                        if let con = sharedConnection {
//                            con.leaveRoom()
//                            con.setUserInterface(previousViewController)
//                        }
//                    }
//                }
//            }
//        }
    
    func connectionReady() {
//        <#code#>
    }
    
    func connectionDropped() {
//        <#code#>
    }
    
    func listRooms(_ roomData: [JSON]) {
//        <#code#>
    }
    
    func setIsInRoom(_ isInRoom: Bool) {
//        <#code#>
    }
    
    func startGame(_ mapName: String) {
//        <#code#>
    }
    
    func stopGame() {
//        <#code#>
    }
}
