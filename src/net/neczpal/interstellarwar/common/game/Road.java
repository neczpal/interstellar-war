package net.neczpal.interstellarwar.common.game;

import java.io.Serializable;

public class Road implements Serializable {
	private static final long serialVersionUID = 1683452581122892189L;
	private Planet mFrom, mTo;

	public Road (Planet from, Planet to) {
		this.mFrom = from;
		this.mTo = to;
	}

	public Planet getFrom () {
		return mFrom;
	}

	public Planet getTo () {
		return mTo;
	}

}
