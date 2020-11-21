//
//  CommandType.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 01. 30..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import Foundation

public class CommandType {
    public static let CONNECTION_READY = "CONNECTION_READY";
    public static let ENTER_SERVER = "ENTER_SERVER";
    public static let EXIT_SERVER = "EXIT_SERVER";
    public static let LIST_ROOMS = "LIST_ROOMS";
    public static let READY_TO_PLAY = "READY_TO_PLAY";
    public static let FILL_ROOM_WITH_AI = "FILL_ROOM_WITH_AI";
    public static let ENTER_ROOM = "ENTER_ROOM";
    public static let LEAVE_ROOM = "LEAVE_ROOM";
    public static let START_ROOM = "START_ROOM";
    public static let GET_MAP_DATA = "GET_MAP_DATA";
    public static let GAME_COMMAND = "GAME_COMMAND";
}
