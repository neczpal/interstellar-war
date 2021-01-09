//
//  InterstellarWarClient.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 02. 04..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import Foundation

public class InterstellarWarClient {
    
    private var mInterstellarWarCore : InterstellarWarCore;
    private var mClientConnection : ClientConnection;

    private typealias PK = InterstellarWarCommandParamKey
    
    init (interstellarWarCore : InterstellarWarCore, clientConnection : ClientConnection) {
        mInterstellarWarCore = interstellarWarCore;
        mClientConnection = clientConnection;
    }

    // RECEIVE

    public func receive (_ command : JSON) {
        let type = command[PK.GAME_COMMAND_TYPE_KEY].string;
        
        switch (type) {
        case InterstellarWarCommandType.REFRESH_WHOLE_MAP:
            let mapData = command[CommandParamKey.MAP_DATA_KEY]
            mInterstellarWarCore.setData (data: mapData);
        default:
            break
        }
    }

    // SEND

    public func startMoveSpaceShip (_ fromIndex: Int, _ toIndex : Int, _ tickNumber : Int, _ unitNumber : Int) {
        var command = JSON();
        
        command[CommandParamKey.COMMAND_TYPE_KEY].string = CommandType.GAME_COMMAND
        command[PK.GAME_COMMAND_TYPE_KEY].string = InterstellarWarCommandType.START_MOVE_SPACESHIP
        command[PK.FROM_ID_KEY].int = fromIndex
        command[PK.TO_ID_KEY].int = toIndex
        command[PK.TICK_NUMBER_KEY].int = tickNumber
        command[PK.UNIT_NUMBER_KEY].int = unitNumber
        
        mClientConnection.send (command);
    }

    public func leaveRoom () {
        mClientConnection.leaveRoom ();
    }

    //GETTERS

    public func getRoomIndex () -> Int{
        return mClientConnection.getRoomIndex ();
    }

    public func getCore () -> InterstellarWarCore {
        return mInterstellarWarCore;
    }
    
}
