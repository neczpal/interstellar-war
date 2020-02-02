//
//  ClientConnection.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 01. 30..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import Foundation



public class ClientConnection {

    private var mUserInterface : UserInterface;
//    private InterstellarWarClient mGameClient; #TODO

    private var mConnectionId : Int = -1;
    private var mRoomIndex : Int = -1;

    private var mUserName : String;

    private var mIsRunning : Bool;
    
    var mIn : InputStream?
    var mOut : OutputStream?
    
    enum ConnectionError : Error {
        case wrongAddress
    }

    /**
     * Erstellt eine Verbindung mit dem Server
     *
     * @param adresse  IP-Adresse des Servers mit Port
     * @param userName Benutzername der Klient
     * @throws IOException falls die Verbindung kann nicht aufbauen
     */
    init (adresse : String, userName : String, ui : UserInterface) throws {
        mUserName = userName;
        mIsRunning = false;
        mUserInterface = ui
        let hostAndPort = adresse.split (separator: ":");
        let host = String(hostAndPort[0])
        let port = hostAndPort.count > 1 ? Int(String(hostAndPort[1]))! : 23233
        
        Stream.getStreamsToHost(withName: host, port: port, inputStream: &mIn, outputStream: &mOut)
        
        if (mIn == nil || mOut == nil) {
            throw ConnectionError.wrongAddress
        }
        
        
        mIn!.open()
        mOut!.open()
        
        enterServer ();

        let queue = DispatchQueue(label: "com.aneczpal.interstellar", qos: .background)
        
        queue.async {
            self.run()
        }
        
//        this.start ();
        
    }

    private  let bufferSize = 1024;
    /**
     * Lest die Befehle, die Server geschickt
     */
    public func run () {
        mIsRunning = true;

//        try {
            while (mIsRunning) {
//                try {
                
                var data : Data = Data()
                
                
                let buffer = UnsafeMutablePointer<UInt8>.allocate(capacity: bufferSize)
                while (mIn!.hasBytesAvailable) {
                    let numberOfBytes = mIn!.read(buffer, maxLength: bufferSize)
                    data.append(buffer, count: numberOfBytes);
                }
                
                let string = String(data: data, encoding: String.Encoding.utf8)!
                
                //Split strings to lines
                
                let lines = string.split(separator: "\n")
                
                for linesub in lines {
                    let line = String(linesub)
                    if(!line.isEmpty) {
//                        print("Read line: \(line)")
                        let jsonObject = JSON(parseJSON: line);
                        
                        receive (jsonObject);
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

    /**
     * Abbaut die Verbindung
     */
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

    /**
     * Die Verbindung ist aufgebaut
     *
     * @param newConnectionId die bekommte Verbindung-ID
     */
    private func connectionReady (_ newConnectionId : Int) {
        mConnectionId = newConnectionId;
        mUserInterface.connectionReady ();
        print("-> Connected to the server. ID (" + String(mConnectionId) + ")");
    }

    /**
     * Die Zimmerdata sind bekommen.
     *
     * @param allRoomData Die Zimmerdata
     */
    private func listRooms (_ allRoomData : [JSON]) {
        mUserInterface.listRooms (allRoomData);
        print("-> RoomDatas loaded. Size (" + String(allRoomData.count) + ")");
    }

    /**
     * Ladet die Mappe ein
     *
     * @param roomIndex Die Benutzerindex in dem Zimmer
     * @param mapData   Die Mapdata
     */
    private func loadMap (_ roomIndex : Int, _ mapData : [JSON]) {
//        mRoomIndex = roomIndex;
//        InterstellarWarCore core = new InterstellarWarCore (mapData);
//        mGameClient = new InterstellarWarClient (core, this);
        mUserInterface.setIsInRoom (true);
        print("-> MapData loaded. RoomIndex (" + String(mRoomIndex) + ")");
    }

    /**
     * Startet das Spiel
     *
     * @param mapName Der Name der Mappe
     */
    private func startGame (_ mapName: String) {
        mUserInterface.startGame (mapName);
//        mGameClient.getCore ().start (); ### TODO TODO TODO
        print("-> Started the game with Map (" + mapName + ")");
    }

    /**
     * Bekommt ein Spielbefehl
     *
     * @param command Der Spielbefehl
     */
    private func gameCommand (_ command : JSON) {
//        mGameClient.receive (command);//### TODO TODO TODO
        print("-> Received GameCommand (" + command.rawString()! + ")");
    }

    /**
     * Bekommt ein Befehl
     *
     * @param command Der Befehl
     */
    public func receive (_ command : JSON) {
        let type = command[CommandParamKey.COMMAND_TYPE_KEY].string!

        switch (type) {
            
        case CommandType.CONNECTION_READY:
            let userId = command[CommandParamKey.USER_ID_KEY].int!
            
            connectionReady (userId)
            
        case CommandType.LIST_ROOMS:
            let allRoomData = command[CommandParamKey.ALL_ROOM_DATA_KEY].array!
            
            listRooms (allRoomData);
            
        case CommandType.GET_MAP_DATA:
            let userId = command[CommandParamKey.USER_ID_KEY].int!
            let mapData = command[CommandParamKey.MAP_DATA_KEY].array!
            
            loadMap (userId, mapData);
            
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

    /**
     * Eintritt in dem Server
     */
    public func enterServer () {
        
        var command = JSON();
        command[CommandParamKey.COMMAND_TYPE_KEY].string = CommandType.ENTER_SERVER
        command[CommandParamKey.USER_NAME_KEY].string = mUserName
        
        send (command);

        print("<- Connecting to the server Username (" + mUserName + ")");
    }

    /**
     * Verlasst dem Server
     */
    public func exitServer () {
        send (CommandType.EXIT_SERVER);
        //#TODO mb check if rly succesfull
        mUserInterface.connectionDropped()
    }

    /**
     * Eintitt in das Zimmer
     *
     * @param roomId Die Zimmer-ID
     */
    public func enterRoom (_ roomId : Int) {
        var command = JSON();
        command[CommandParamKey.COMMAND_TYPE_KEY].string = CommandType.ENTER_ROOM
        command[CommandParamKey.ROOM_ID_KEY].int = roomId
        
        send (command);

        print("<- Entering the Room Id (" + String(roomId) + ")");
    }

    /**
     * Beginnt das Spiel in dem Zimmer
     */
    public func startRoom () {
        send (CommandType.START_ROOM);
        print("<- Starting the Room");
    }

    /**
     * Verlasst das Zimmer
     */
    public func leaveRoom () {
        mUserInterface.stopGame ();

        send (CommandType.LEAVE_ROOM);

        mUserInterface.setIsInRoom (false);
        print ("<- Leaving the Room");
    }

    /**
     * Sendet ein Befehl
     *
     * @param type Typ des Befehls
     */
    public func send (_ commandType : String) {
        let command : JSON = [CommandParamKey.COMMAND_TYPE_KEY : commandType]
        
        send (command);
    }
    

    /**
     * Sendet ein Befehl
     *
     * @param command Der Befehl
     */
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

    /**
     * Beendet das Spiel, falls es läuft
     */
    private func stopGame () {
//        if (mGameClient != null && mGameClient.getCore ().isRunning ()) {
//            mGameClient.getCore ().stopGame ();
//        }
    }

    //GETTERS, SETTERS

    public func getRoomIndex () -> Int{
        return mRoomIndex;
    }

//    public InterstellarWarClient getGameClient () {
//        return mGameClient;
//    }
//
    func setUserInterface (_ userInterface : UserInterface) {
        mUserInterface = userInterface;
    }
}
