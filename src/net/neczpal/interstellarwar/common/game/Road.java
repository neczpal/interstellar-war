package net.neczpal.interstellarwar.common.game;

import java.io.Serializable;

public class Road implements Serializable {
	private static final long serialVersionUID = 1683452581122892189L;
	private Planet mFrom, mTo;

	/**
	 * Erstellt ein Weg zwischen zwei Planet
	 *
	 * @param from der eine Planet
	 * @param to   der andere Planet
	 */
	public Road (Planet from, Planet to) {
		this.mFrom = from;
		this.mTo = to;
	}

	// GETTERS

	public Planet getFrom () {
		return mFrom;
	}

	public Planet getTo () {
		return mTo;
	}

}
