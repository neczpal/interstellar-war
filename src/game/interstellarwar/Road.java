package game.interstellarwar;

import java.io.Serializable;

public class Road implements Serializable {
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
