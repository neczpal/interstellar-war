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
	public Type type;
	public Serializable[] data;

	public Command (Type type) {
		this (type, null);
	}

	public Command (Type type, Serializable... data) {
		this.type = type;
		this.data = data;
	}

	public enum Type {
		ENTER_SERVER, EXIT_SERVER, LIST_ROOMS, MAP_DATA, ACCEPT_CONNECTION, DECLINE_CONNECTION, READY_TO_PLAY, IS_READY, ENTER_ROOM, LEAVE_ROOM, GAME_DATA
	}


}
