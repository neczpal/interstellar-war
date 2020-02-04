//
//  LoginViewController.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 02. 02..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import UIKit


var sharedConnection : ClientConnection?;

class LoginViewController: UITableViewController, UserInterface{

    @IBOutlet weak var nameField: UITextField!
    
    @IBOutlet weak var serverField: UITextField!
    
    var sections: [MainSection] = [.login, .rooms, .disconnect]
    
    var shouldHide: [Bool] = [false, true, true]
    
    var mRooms : [RoomData] = [RoomData]();
    
    
    
    enum MainSection {
        case login
        case rooms
        case disconnect
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        nameField.text = "iOS"
        serverField.text = "localhost"
        
        
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "joinRoomCell")

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
    }

    // MARK: - Table view data source

    override func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return shouldHide[section] ? CGFloat.leastNonzeroMagnitude : super.tableView(tableView, heightForHeaderInSection: section)
    }
    
    override func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return shouldHide[section] ? CGFloat.leastNonzeroMagnitude : super.tableView(tableView, heightForFooterInSection: section)
    }
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        
        return sections.count;
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if(shouldHide[section]) {
            return 0;
        }
        
        let currentSection = sections[section]
        
        switch currentSection {
        case .login:
            return 3
        case .disconnect:
            return 1
        case .rooms:
            return max(mRooms.count, 1)
        }
        
    }
    
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        if(shouldHide[section]) {
            return nil
        }
        
        let currentSection = sections[section]
        switch currentSection {
        case .login:
            return "Login details"
        case .disconnect:
            return "Disconnect"
        case .rooms:
            return "Rooms"
        }
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let currentSection = sections[indexPath.section]
        if currentSection == .rooms {
            let cell = tableView.dequeueReusableCell(withIdentifier: "joinRoomCell") ?? UITableViewCell(style: .default, reuseIdentifier: "joinRoomCell")
            
//             Display the results that we've found, if any. Otherwise, show "searching..."
            
            
            if mRooms.isEmpty {
                cell.textLabel?.text = "Searching for rooms..."
            } else {
                let row = indexPath.row;
                if row < mRooms.count {
                    let data = mRooms[indexPath.row]
                    cell.textLabel?.text = "\(data.mMapName) (\(data.mUsers.count)/\(data.mMaxUserCount))"
                } else {
                    cell.textLabel?.text = "[MISSING INFO]"
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
        case .login:
            if indexPath.row == 2 {
                loginServerButton()
            }
        case .rooms:
            if !mRooms.isEmpty {
                // Handle the user tapping on a discovered game
                let data = mRooms[indexPath.row]
                performSegue (withIdentifier: "ShowRoom", sender: data)
                
            }
        case .disconnect:
            if indexPath.row == 0 {
                disconnectServerButton()
            }
        }

        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    func loginServerButton () {
        // Dismiss the keyboard when the user starts hosting.
        view.endEditing(true)

        if (serverField.text == nil || serverField.text!.isEmpty) {
            showToast(controller: self, message: "Name is empty!", seconds: 1.5)
        } else
        if (nameField.text == nil || nameField.text!.isEmpty) {
            showToast(controller: self, message: "Server is empty!", seconds: 1.5)
        } else {
            do {
                sharedConnection = try ClientConnection(adresse: serverField.text!, userName: nameField.text!, ui: self)
            } catch let error {
                showToast(controller: self, message: "Cannot connect : \(error)", seconds: 1.5)
            }
        }
    }
    
    func disconnectServerButton() {
        if let con = sharedConnection {
            con.exitServer()
        }
    }
    
    
    func showToast(controller: UIViewController, message : String, seconds: Double) {
        let alert = UIAlertController(title: nil, message: message, preferredStyle: .alert)
        
        alert.view.backgroundColor = .black
        alert.view.alpha = 0.8
        alert.view.layer.cornerRadius = 15
        controller.present(alert, animated: true)
        
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + seconds) {
            alert.dismiss(animated: true)
        }
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

    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if (segue.identifier == "ShowRoom") {
            let roomViewController = segue.destination as! RoomViewController
            let data = sender as! RoomData
            
            roomViewController.data = data
            
            if let con = sharedConnection {
                con.setUserInterface(roomViewController)
                con.enterRoom(data.mRoomId)
            }
        }
    }
    

    func connectionReady () {
        DispatchQueue.main.async {
            self.shouldHide = [true, false, false]
            self.navigationItem.title = "Lobby"
            self.tableView.reloadData()
        }
    }

    func connectionDropped () {
        DispatchQueue.main.async {
            self.shouldHide = [false, true, true]
            self.navigationItem.title = "Connect"
            self.tableView.reloadData()
        }
    }

    func listRooms (_ allRoomData : [JSON]) {
        mRooms = [RoomData] ()
        
        for json in allRoomData {
            mRooms.append(RoomData(json: json))
        }
        
        DispatchQueue.main.async {
            self.tableView.reloadData()
        }
        
    }

    func setIsInRoom (_ isInRoom : Bool) {
        
    }

    func startGame (_ mapName : String) {
        
    }

    func stopGame () {
        
    }
    
}
