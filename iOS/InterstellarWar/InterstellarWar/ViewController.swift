//
//  ViewController.swift
//  InterstellarWar
//
//  Created by Neczpál Ábel on 2020. 01. 29..
//  Copyright © 2020. Neczpál Ábel. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    var clientConnection : ClientConnection?;
    
    @IBOutlet weak var hostTextField: UITextField!
    @IBOutlet weak var usernameTextField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }

    @IBAction func connectPressed(_ sender: UIButton) {
        if let connection = clientConnection {
            connection.exitServer()
        }
        
        clientConnection = ClientConnection(adresse: hostTextField.text!, userName: usernameTextField.text!)
        
        print("Connect Pressed!")
    }
    
    @IBAction func disconnectPressed(_ sender: UIButton) {
        if let connection = clientConnection {
            connection.exitServer()
            connection.send(CommandType.EXIT_SERVER)
        }
        print("DISconnect Pressed!")
    }
}

