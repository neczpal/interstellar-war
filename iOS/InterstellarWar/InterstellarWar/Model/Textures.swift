//
//  Textures.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 02. 06..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import SpriteKit

public class Textures {
    var planet : [SKTexture]
    var spaceship : [SKTexture]
    var spaceship_dimens : [(width:Double, height:Double)]
    var background : [SKTexture]

    init (){
        planet = [SKTexture] ()
        for index in 1...Planet.PLANET_TYPES {
            planet.append( SKTexture(imageNamed: "planet\(index)"))
        }
        spaceship = [SKTexture] ()
        spaceship_dimens = [(width:Double, height:Double)] ()

        for index in 0..<SpaceShip.SPACESHIP_TYPES {
            spaceship.append(SKTexture(imageNamed: "spaceship\(index+1)"))
            spaceship_dimens.append(
                (width: Double(spaceship[index].cgImage().width) / 4.0,
                 height: Double(spaceship[index].cgImage().height) / 4.0))
        }

        background = [SKTexture] ()
        for index in 1...InterstellarWarCore.BACKGROUND_TYPES {
            background.append( SKTexture (imageNamed: "background\(index)"))
        }
    }
}

let TEXTURES = Textures()

let COLOR = [
    UIColor(red: 236.0 / 255.0,
            green: 240.0 / 255.0,
            blue: 241.0 / 255.0,
            alpha: 1.0),
    UIColor(red: 231.0 / 255.0,
            green: 76.0 / 255.0,
            blue: 60.0 / 255.0,
            alpha: 1.0),
    UIColor(red: 46.0 / 255.0,
            green: 204.0 / 255.0,
            blue: 113.0 / 255.0,
            alpha: 1.0),
    UIColor(red: 155.0 / 255.0,
            green: 89.0 / 255.0,
            blue: 182.0 / 255.0,
            alpha: 1.0),
    UIColor(red: 241.0 / 255.0,
            green: 196.0 / 255.0,
            blue: 15.0 / 255.0,
            alpha: 1.0),
    UIColor(red: 26.0 / 255.0,
            green: 188.0 / 255.0,
            blue: 156.0 / 255.0,
            alpha: 1.0),
    UIColor(red: 41.0 / 255.0,
            green: 128.0 / 255.0,
            blue: 185.0 / 255.0,
            alpha: 1.0),
    UIColor(red: 33.0 / 255.0,
            green: 30.0 / 255.0,
            blue: 27.0 / 255.0,
            alpha: 1.0)
]

