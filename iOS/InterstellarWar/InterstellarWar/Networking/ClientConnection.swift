//
//  ClientConnection.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 01. 30..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import Foundation



public class ClientConnection {

    private var mUserInterface : UserInterface
    private var mGameClient : InterstellarWarClient?

    private var mConnectionId : Int = -1;
    private var mRoomIndex : Int = -1;

    private var mUserName : String;

    private var mIsRunning : Bool;
    
    var mIn : InputStream?
    var mOut : OutputStream?
    
    init (adresse : String, userName : String, ui : UserInterface) throws {
        mUserName = userName;
        mIsRunning = false;
        mUserInterface = ui
        let hostAndPort = adresse.split (separator: ":");
        let host = String(hostAndPort[0])
        let port = hostAndPort.count > 1 ? Int(String(hostAndPort[1]))! : 23233
        
        Stream.getStreamsToHost(withName: host, port: port, inputStream: &mIn, outputStream: &mOut)
        
        mIn!.open()
        mOut!.open()
        
        enterServer ();

        self.start()
        
    }

    public func start () {
        let queue = DispatchQueue(label: "com.aneczpal.interstellar.connection", qos: .default)
        queue.async {
            self.run()
        }
    }
    
    private  let bufferSize = 4048;

    private func run () {
        mIsRunning = true;

//        try {
            while (mIsRunning) {
//                try {
                
                var data : Data = Data()
                
                
                let buffer = UnsafeMutablePointer<UInt8>.allocate(capacity: bufferSize)
                defer { buffer.deallocate() }
                while (mIn!.hasBytesAvailable) {
                    let read = mIn!.read(buffer, maxLength: bufferSize)
                    if (read == 0) {
                        break  // EOF
                    } else if read < 0 {
                        print("ERROR IN INPUTSTREAM")
                        break
                    }
                    
                    data.append(buffer, count: read);
                }
//                buffer.deallocate()
                let string = String(data: data, encoding: String.Encoding.utf8)!
                
                //Split strings to lines
                
                let lines = string.split(separator: "\n")
                
                for linesub in lines {
                    let line = String(linesub)
                    if(!line.isEmpty) {
                        print("Read line: \(line)")
                        if "}" == line.last && "{" == line.first {
                            let jsonObject = JSON(parseJSON: line)
                            self.receive (jsonObject);
                        }
                    }
                }
                
//                    if (line != null) {
//                        mLogger.log (Level.INFO, "Read line: " + line);
//                        JSONObject jsonObject = new JSONObject (line);
//                        receive (jsonObject);
//                    }
//                } catch (JSONException ex) {
//                    mLogger.log (Level.SEVERE, "-> Couldn't read an object: " + ex.getMessage ());
//                }
            }
//        } catch (IOException ex2) {
//            mLogger.log (Level.WARNING, "Client stopped: " + ex2.getMessage ());
//
//            stopClientConnection ();
//            stopGame ();
//            mUserInterface.connectionDropped ();
//        }
    }

    public func stopClientConnection () {
        mIsRunning = false;
        exitServer ();
//        try {
//            mSocket.close ();
//        } catch (IOException ex) {
//            mLogger.log (Level.WARNING, "Coulnd't close socket: " + ex.getMessage ());
//        }
    }

    //RECEIVE

    
    private func connectionReady (_ newConnectionId : Int) {
        mConnectionId = newConnectionId;
        mUserInterface.connectionReady ();
        print("-> Connected to the server. ID (" + String(mConnectionId) + ")");
    }

    private func listRooms (_ allRoomData : [JSON]) {
        mUserInterface.listRooms (allRoomData);
        print("-> RoomDatas loaded. Size (" + String(allRoomData.count) + ")");
    }

    private func loadMap (_ roomIndex : Int, _ mapData : JSON) {
        mRoomIndex = roomIndex;
        let core = InterstellarWarCore (jsonData: mapData);
        mGameClient = InterstellarWarClient (interstellarWarCore: core, clientConnection: self);
        
        mUserInterface.setIsInRoom (true);
        print("-> MapData loaded. RoomIndex (" + String(mRoomIndex) + ")");
    }

    private func startGame (_ mapName: String) {
        mUserInterface.startGame (mapName);
//        mGameClient!.getCore ().start () #TODO
        print("-> Started the game with Map (" + mapName + ")");
    }

    private func gameCommand (_ command : JSON) {
        mGameClient!.receive (command)
//        print("-> Received GameCommand (" + command.rawString()! + ")");
    }

    public func receive (_ command : JSON) {
        if !command.exists() {
            return;
        }
        
        let type = command[CommandParamKey.COMMAND_TYPE_KEY].string!
        
        
        switch (type) {
            
        case CommandType.CONNECTION_READY:
            let userId = command[CommandParamKey.USER_ID_KEY].int!
            
            connectionReady (userId)
            
        case CommandType.LIST_ROOMS:
            let allRoomData = command[CommandParamKey.ALL_ROOM_DATA_KEY].array!
            
            listRooms (allRoomData);
            
        case CommandType.GET_MAP_DATA:
            let roomIndex = command[CommandParamKey.ROOM_INDEX_KEY].int!
            let mapData = command[CommandParamKey.MAP_DATA_KEY]//#TODO
            
            loadMap (roomIndex, mapData);
            
        case CommandType.READY_TO_PLAY:
            let mapName = command[CommandParamKey.MAP_NAME_KEY].string!
            
            startGame (mapName);
            
        case CommandType.GAME_COMMAND:
            gameCommand (command);
            
        default:
            break
        }
    }

    //SEND

    public func enterServer () {
        
        var command = JSON();
        command[CommandParamKey.COMMAND_TYPE_KEY].string = CommandType.ENTER_SERVER
        command[CommandParamKey.USER_NAME_KEY].string = mUserName
        
        send (command);

        print("<- Connecting to the server Username (" + mUserName + ")");
    }

    public func exitServer () {
        send (CommandType.EXIT_SERVER);
        //#TODO mb check if rly succesfull
        mUserInterface.connectionDropped()
        print("<- Exiting server");
    }
    
    public func fillRoomWithAI () {
        send (CommandType.FILL_ROOM_WITH_AI);
        print("<- Filling room with AI");
    }

    public func enterRoom (_ roomId : Int) {
        var command = JSON();
        command[CommandParamKey.COMMAND_TYPE_KEY].string = CommandType.ENTER_ROOM
        command[CommandParamKey.ROOM_ID_KEY].int = roomId
        
        send (command);

        print("<- Entering the Room Id (" + String(roomId) + ")");
    }

    public func startRoom () {
        send (CommandType.START_ROOM);
        print("<- Starting the Room");
    }

    public func leaveRoom () {
        mUserInterface.stopGame ();

        send (CommandType.LEAVE_ROOM);

        mUserInterface.setIsInRoom (false);
        print ("<- Leaving the Room");
    }

    public func send (_ commandType : String) {
        let command : JSON = [CommandParamKey.COMMAND_TYPE_KEY : commandType]
        
        send (command);
    }
    
    public func send (_ jsonCommand : JSON) {
        var command : JSON = jsonCommand
        
        command[CommandParamKey.USER_ID_KEY].int = mConnectionId
        let string = command.rawString()!
        let onLineString = string.replacingOccurrences(of: "\n", with: "");
        
        print(onLineString)
        
//        do {
        let encodedDataArray = [UInt8]((onLineString + "\n").utf8)
        
        mOut!.write(UnsafePointer<UInt8>(encodedDataArray), maxLength: encodedDataArray.count)
        
        
//            if (!mSocket.isClosed ()) {
//                mOut.write (command.toString () + "\n");
//                mOut.flush ();
//            } else {
//                mLogger.log (Level.WARNING, "<- Couldn't send Command (" + command + "), because socket was closed.");
//            }
//        } catch (IOException ex) {
//            mLogger.log (Level.WARNING, "<- Couldn't send Command (" + command + "): " + ex.getMessage ());
//        }
    }

    private func stopGame () {
//        if (mGameClient != null && mGameClient.getCore ().isRunning ()) {
//            mGameClient.getCore ().stopGame ();
//        }
    }

    //GETTERS, SETTERS
    
    public func getConnectionId () -> Int{
        return mConnectionId;
    }
    
    public func getRoomIndex () -> Int{
        return mRoomIndex;
    }

    public func getGameClient () -> InterstellarWarClient? {
        return mGameClient;
    }

    func setUserInterface (_ userInterface : UserInterface) {
        mUserInterface = userInterface;
    }
}
