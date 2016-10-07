/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.server;

import java.io.Serializable;

/**
 * @author neczpal
 */
public class Command implements Serializable {
	static final long serialVersionUID = 42L;
	Type type;
	Serializable[] data;
	public Command (Type type) {
		this (type, null);
	}

	public Command (Type type, Serializable... data) {
		this.type = type;
		this.data = data;
	}

	public enum Type {
		ENTER_SERVER, EXIT_SERVER, ACCEPT_CONNECTION, DECLINE_CONNECTION, NEED_MAP, SEND_MAP, RDY2PLAY, GAME_START
	}


}
