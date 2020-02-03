//
//  RoomData.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 02. 03..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import Foundation

struct RoomData {
    var mRoomId : Int
    var mMapName : String
    var mUsers : [String]
    var mMaxUserCount : Int
    var mIsRunning : Bool
    
    
    init (json : JSON) {
        
        self.mRoomId = json[CommandParamKey.ROOM_ID_KEY].int!
        self.mMapName = json[CommandParamKey.MAP_NAME_KEY].string!
        self.mMaxUserCount = json[CommandParamKey.MAX_USER_COUNT_KEY].int!
        self.mIsRunning = json[CommandParamKey.IS_ROOM_RUNNING_KEY].bool!
        
        let jsonUsers = json[CommandParamKey.USER_LIST_KEY].array!
        
        self.mUsers = [String] ()
        
        for (index, _) in jsonUsers.enumerated() {
            mUsers.append (jsonUsers[index].string!);
        }
        
    }
    
}
