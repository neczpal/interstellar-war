/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package game.connection;

import java.io.Serializable;

/**
 * @author neczpal
 */
public class Command implements Serializable {
	static final long serialVersionUID = 42321L;
	public Type type;
	public Serializable[] data;

	public Command (Type type) {
		this (type, 0);
	}

	public Command (Type type, Serializable... data) {
		this.type = type;
		this.data = data;
	}

	public void addHeader (Serializable... header_data) {
		Serializable[] tempData = new Serializable[data.length + header_data.length];
		System.arraycopy (header_data, 0, tempData, 0, header_data.length);
		System.arraycopy (data, 0, tempData, header_data.length, data.length);
		this.data = tempData;
	}

	public enum Type {
		ENTER_SERVER, EXIT_SERVER, LIST_ROOMS, MAP_DATA, CONNECTION_READY, READY_TO_PLAY, ENTER_ROOM, LEAVE_ROOM, START_ROOM, GAME_COMMAND
	}


}
