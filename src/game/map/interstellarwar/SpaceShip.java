package game.map.interstellarwar;

import game.Textures;
import game.Util;
import game.geom.Color;
import game.geom.Point;
import game.geom.Quad;
import org.lwjgl.opengl.GL11;

public class SpaceShip extends Quad {
	private static final int SPACE_SHIP_SPEED = 6;

	private int mUnitsNum;
	private int mOwnedBy;
	private int mMaxTick;
	private int mCurrentTick;
	private Planet mToPlanet;
	private double vx, vy;

	public SpaceShip (Planet from, Planet to, int currentTick) {
		super (new Point[4]);

		int spaceshipType = (int) (Math.random () * Textures.InterstellarWar.spaceship.length);

		double w = Textures.InterstellarWar.spaceshipDimens[spaceshipType][0];
		double h = Textures.InterstellarWar.spaceshipDimens[spaceshipType][1];
		double lx = to.getX () - from.getX ();
		double ly = to.getY () - from.getY ();
		double angle = Math.atan (ly / lx);

		//SET POINTS
		Point[] arr = new Point[] {
				new Point (from.getX () - w / 2, from.getY () + h / 2),
				new Point (from.getX () + w / 2, from.getY () + h / 2),
				new Point (from.getX () + w / 2, from.getY () - h / 2),
				new Point (from.getX () - w / 2, from.getY () - h / 2)
		};
		for (Point point : arr) {
			point.rotate (from, angle);
		}

		setA (arr[0]);
		setB (arr[1]);
		setC (arr[2]);
		setD (arr[3]);

		//SET SPEED
		double length = from.distance (to);
		vx = lx / length * SpaceShip.SPACE_SHIP_SPEED;
		vy = ly / length * SpaceShip.SPACE_SHIP_SPEED;

		//SET TICK
		mMaxTick = (int) (length / SpaceShip.SPACE_SHIP_SPEED);
		mCurrentTick = currentTick;

		mOwnedBy = from.getOwnedBy ();

		mUnitsNum = from.getUnitNumber ();
		mToPlanet = to;

		//SET GRAPHICHS
		setColor (Color.values ()[mOwnedBy]);
		setTexture (Textures.InterstellarWar.spaceship[spaceshipType]);

	}

	public boolean tick () {
		mCurrentTick++;
		return mCurrentTick >= mMaxTick;
	}

	@Override
	public void draw () {
		GL11.glPushMatrix ();
		GL11.glTranslated (mCurrentTick * vx, mCurrentTick * vy, 0);

		super.draw ();
		Point center = getCenter ();
		Util.drawString (Integer.toString (mUnitsNum), center.getX (), center.getY () + 20, 12, Color.values ()[mOwnedBy]);

		GL11.glPopMatrix ();
	}

	public void unitsArrived () {
		if (mToPlanet.getOwnedBy () == mOwnedBy) {
			mToPlanet.addUnit (mUnitsNum);
		} else {
			mToPlanet.addUnit (-mUnitsNum);
			if (mToPlanet.getUnitNumber () < 0) {
				mToPlanet.setOwnedBy (mOwnedBy);
				mToPlanet.setUnitNumber (Math.abs (mToPlanet.getUnitNumber ()));
			}
		}
	}
}
