package game.map.interstellarwar;

import game.geom.Line;
import game.geom.Point;

public class Road extends Line {
	private int mFromIndex, mToIndex;

	public Road (Point a, Point b, int fromIndex, int toIndex) {
		super (a, b);
		this.mFromIndex = fromIndex;
		this.mToIndex = toIndex;
	}

	public int getFromIndex () {
		return mFromIndex;
	}

	public int getToIndex () {
		return mToIndex;
	}
}
