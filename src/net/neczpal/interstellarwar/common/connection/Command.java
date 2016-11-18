package net.neczpal.interstellarwar.common.connection;

import java.io.Serializable;

public class Command implements Serializable {
	static final long serialVersionUID = 42321L;
	public Type type;
	public Serializable[] data;

	/**
	 * Erstellt ein Befehl mit einer Typ
	 *
	 * @param type Die Typ der Befehl
	 */
	public Command (Type type) {
		this (type, 0);
	}

	/**
	 * Erstellt ein Befehl mit einer Typ, und Data
	 *
	 * @param type Die Typ der Befehl
	 * @param data Die Data der Befehl
	 */
	public Command (Type type, Serializable... data) {
		this.type = type;
		this.data = data;
	}

	/**
	 * Addiert ein Header(Amfangdata) zu dem Data
	 *
	 * @param header_data die Amfangdata
	 */
	public void addHeader (Serializable... header_data) {
		Serializable[] tempData = new Serializable[data.length + header_data.length];
		System.arraycopy (header_data, 0, tempData, 0, header_data.length);
		System.arraycopy (data, 0, tempData, header_data.length, data.length);
		this.data = tempData;
	}

	@Override
	public String toString () {
		return type.toString () + "[" + data.length + "]";
	}

	public enum Type {
		ENTER_SERVER, EXIT_SERVER, LIST_ROOMS, MAP_DATA, CONNECTION_READY, READY_TO_PLAY, ENTER_ROOM, LEAVE_ROOM, START_ROOM, GAME_COMMAND
	}

}
