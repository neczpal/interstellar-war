//
//  RoomViewController.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 02. 02..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import UIKit

class RoomViewController: UITableViewController, UserInterface {
    
    
    var data : RoomData = RoomData (mRoomId: -1, mMapName: "", mUsers: [], mMaxUserCount: 0, mIsRunning: false)
    
    enum RoomSection {
        case users, buttons
    }
    var sections : [RoomSection] = [.users, .buttons]

    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "userCell")
        
        
        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
    }
    
    
    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return sections.count
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch sections[section] {
        case .users:
            return max(1, data.mMaxUserCount)
        case .buttons:
            return 3
        }
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let currentSection = sections[indexPath.section]
        
//        if currentSection == .buttons { #TODO Hide fillAI button if room is full
//            if (indexPath.row == 1 && data.mUsers.count == data.mMaxUserCount) {
//                let cell = tableView.dequeueReusableCell(withIdentifier: "fillAICell") ?? UITableViewCell(style: .default, reuseIdentifier: "fillAICell")
//
//                cell.isUserInteractionEnabled = false
//                cell.textLabel!.isEnabled = false
////                cell.detailTextLabel!.isEnabled = false
//            }
//        } else
        if currentSection == .users {
            //#TODO how to avoid creating cells in storyboard (more dynamic layout)
            let cell = tableView.dequeueReusableCell(withIdentifier: "userCell") ?? UITableViewCell(style: .default, reuseIdentifier: "userCell")
//             Display the results that we've found, if any. Otherwise, show "searching..."
            
            
            if data.mUsers.isEmpty {
                cell.textLabel?.text = "Loading..."
            } else {
                let row = indexPath.row
                
                if(row >= data.mUsers.count) {
                    cell.textLabel?.textColor = #colorLiteral(red: 0.8039215803, green: 0.8039215803, blue: 0.8039215803, alpha: 1)
                    cell.textLabel?.text = "Empty"
                } else {
                    cell.textLabel?.textColor = nil
                    let name = data.mUsers[row];
                    cell.textLabel?.text = name
                }
                
//                let peerEndpoint = results[indexPath.row].endpoint
//                if case let NWEndpoint.service(name: name, type: _, domain: _, interface: _) = peerEndpoint {
//                    cell.textLabel?.text = name
//                } else {
//                    cell.textLabel?.text = "Unknown Endpoint"
//                }
            }
            return cell
        }
        return super.tableView(tableView, cellForRowAt: indexPath)
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let currentSection = sections[indexPath.section]
        switch currentSection {
        case .users:
            break;
        case .buttons:
            switch indexPath.row {
                case 0:
                    startGameButton()
                case 1:
                    fillWithAIButton()
                case 2:
                    exitRoomButton()
                default:
                    break
            }
        }

        tableView.deselectRow(at: indexPath, animated: true)
    }

    /*
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "reuseIdentifier", for: indexPath)

        // Configure the cell...

        return cell
    }
    */

    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    
    func startGameButton () {
        if let con = sharedConnection {
            con.startRoom()
//            con.setUserInterface(roomViewController)
        }
    }
    
    func fillWithAIButton () {
        if let con = sharedConnection {
            con.fillRoomWithAI()
        }
    }
    
    func exitRoomButton () {
        self.navigationController?.popViewController(animated: true)
        
        if let con = sharedConnection {
            con.leaveRoom()
//            con.setUserInterface(roomViewController)
        }
    }
    
    // MARK: - Navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "ShowGame") {
            let gameViewController = segue.destination as! GameViewController
            let mapName = sender as! String
            
            gameViewController.navigationItem.title = mapName
            
            if let con = sharedConnection {
                con.setUserInterface(gameViewController)
            }
        }
    }

    
    func connectionReady() {
        //unused in this scope
    }
    
    func connectionDropped() {
        //IDK if this works prob buggy
        self.navigationController?.popViewController(animated: true);
        if let con = sharedConnection {
            con.exitServer()
        }
    }
    
    func listRooms(_ roomData: [JSON]) {
        for room in roomData {
            if (room[CommandParamKey.ROOM_ID_KEY].int! == data.mRoomId) {
                data = RoomData(json: room)
                DispatchQueue.main.async {
                    self.navigationItem.title = self.data.mMapName
                    self.tableView.reloadData()
                }
                
                break
            }
        }
        
        //unused in this scope
    }
    
    func setIsInRoom(_ isInRoom: Bool) {
        if (isInRoom == false) {
            
        }
        //
    }
    
    func startGame(_ mapName: String) {
        
        DispatchQueue.main.async {
            
            self.performSegue (withIdentifier: "ShowGame", sender: mapName)
        }
    }
    
    func stopGame() {
        //
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        if isMovingFromParent {
            if let viewControllers = self.navigationController?.viewControllers {
                if (viewControllers.count >= 1) {
                    let previousViewController = viewControllers[viewControllers.count-1] as! LoginViewController
                    // whatever you want to do
                    if let con = sharedConnection {
                        con.leaveRoom()
                        con.setUserInterface(previousViewController)
                    }
//                    previousViewController.callOrModifySomething()
                }
            }
        }
    }

}
