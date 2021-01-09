//
//  UserInterface.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 02. 02..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import Foundation

protocol UserInterface {
    
    func connectionReady ();

    func connectionDropped ();

    func listRooms (_ roomData : [JSON]);

    func setIsInRoom (_ isInRoom : Bool);

    func startGame (_ mapName : String);

    func stopGame ();
}
