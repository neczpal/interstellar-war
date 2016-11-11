package net.neczpal.interstellarwar.desktop.ui.frames;

import net.neczpal.interstellarwar.clientcommon.InterstellarWarClient;
import net.neczpal.interstellarwar.common.game.InterstellarWarCore;
import net.neczpal.interstellarwar.common.game.Planet;
import net.neczpal.interstellarwar.common.game.Road;
import net.neczpal.interstellarwar.common.game.SpaceShip;
import net.neczpal.interstellarwar.desktop.Textures;
import net.neczpal.interstellarwar.desktop.Util;
import net.neczpal.interstellarwar.desktop.geom.Color;
import net.neczpal.interstellarwar.desktop.geom.Point;
import net.neczpal.interstellarwar.desktop.geom.Rect;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class InterstellarWarPanel {
	private static final int EDGE_MOVE_DISTANCE = 20;
	private static final int EDGE_MOVE_UNIT = 2;
	private InterstellarWarClient mInterstellarWarClient;
	private InterstellarWarCore mCore;
	private Rect mBackground;
	private Point mViewPort;

	private boolean mWasMouseDown = false;

	private Planet mSelectedPlanetFrom = null;
	private Planet mSelectedPlanetTo = null;

	public InterstellarWarPanel (InterstellarWarClient client) {
		mCore = client.getCore ();
		mInterstellarWarClient = client;
	}

	public void init () {
		initBackground ();
		initViewPort ();
	}

	private void initBackground () {
		mBackground = new Rect (0, 0, Display.getWidth (), Display.getHeight ());
		mBackground.setTexture (Textures.InterstellarWar.background[mCore.getBackgroundTextureIndex ()]);
	}

	private void initViewPort () {
		for (Planet planet : mCore.getPlanets ()) {
			if (planet.getOwnedBy () == getInterstellarWarClient ().getRoomIndex ()) {
				mViewPort = new Point (Display.getWidth () / 2 - planet.getX (), Display.getHeight () / 2 - planet.getY ());
			}
		}
	}

	public void inputEvents () {
		moveViewPort ();
		selectPlanets ();
	}

	private void selectPlanets () {

		if (Mouse.isButtonDown (0) && !mWasMouseDown) {
			mWasMouseDown = true;
			Point point = getMousePosition ();

			for (Planet planet : mCore.getPlanets ()) {
				if (planet.isInside (point.getX (), point.getY ()) && mInterstellarWarClient.getRoomIndex () == planet.getOwnedBy ()) {
					mSelectedPlanetFrom = planet;
					break;
				}
			}
		} else if (Mouse.isButtonDown (0) && mWasMouseDown) {
			Point point = getMousePosition ();

			boolean isThere = false;
			for (Planet planet : mCore.getPlanets ()) {
				if (planet.isInside (point.getX (), point.getY ()) && planet.isNeighbor (mSelectedPlanetFrom)) {
					mSelectedPlanetTo = planet;
					isThere = true;
					break;
				}
			}
			if (!isThere) {
				mSelectedPlanetTo = null;
			}
		} else if (!Mouse.isButtonDown (0)) {

			if (mWasMouseDown) {
				mWasMouseDown = false;

				if (mSelectedPlanetFrom != null) {
					Point point = getMousePosition ();

					for (Planet planet : mCore.getPlanets ()) {
						if (planet.isInside (point.getX (), point.getY ()) && planet.isNeighbor (mSelectedPlanetFrom)) {
							mSelectedPlanetTo = planet;
							if (mSelectedPlanetFrom.getOwnedBy () == mInterstellarWarClient.getRoomIndex ()) {
								ArrayList <Planet> planets = mCore.getPlanets ();
								mInterstellarWarClient.startMoveSpaceShip (planets.indexOf (mSelectedPlanetFrom), planets.indexOf (mSelectedPlanetTo), mCore.getTickNumber (), mSelectedPlanetFrom.getUnitsNumber ());
							}
							break;
						}
					}

				}
			} else {
				mSelectedPlanetFrom = null;
				mSelectedPlanetTo = null;
			}
		}
	}

	private void moveViewPort () {
		if (Mouse.getX () < EDGE_MOVE_DISTANCE || Keyboard.isKeyDown (Keyboard.KEY_LEFT)) {
			mViewPort.move (EDGE_MOVE_UNIT, 0);
		} else if (Mouse.getX () > Display.getWidth () - EDGE_MOVE_DISTANCE || Keyboard.isKeyDown (Keyboard.KEY_RIGHT)) {
			mViewPort.move (-EDGE_MOVE_UNIT, 0);
		}
		if (Mouse.getY () < EDGE_MOVE_DISTANCE || Keyboard.isKeyDown (Keyboard.KEY_DOWN)) {
			mViewPort.move (0, EDGE_MOVE_UNIT);
		} else if (Mouse.getY () > Display.getHeight () - EDGE_MOVE_DISTANCE || Keyboard.isKeyDown (Keyboard.KEY_UP)) {
			mViewPort.move (0, -EDGE_MOVE_UNIT);
		}
	}

	private Point getMousePosition () {
		return new Point (Mouse.getX () - mViewPort.getX (), Mouse.getY () - mViewPort.getY ());
	}

	public void draw () {
		mBackground.draw ();
		ArrayList <Road> roads = mCore.getRoads ();
		ArrayList <Planet> planets = mCore.getPlanets ();
		ArrayList <SpaceShip> spaceShips = mCore.getSpaceShips ();

		GL11.glPushMatrix ();
		GL11.glTranslated (mViewPort.getX (), mViewPort.getY (), 0);

		if (mSelectedPlanetFrom != null) {
			Util.drawCircle (mSelectedPlanetFrom.getX (), mSelectedPlanetFrom.getY (), mSelectedPlanetFrom.getRadius (), Color.values ()[mSelectedPlanetFrom.getOwnedBy ()]);
		}
		if (mSelectedPlanetTo != null) {
			Util.drawCircle (mSelectedPlanetTo.getX (), mSelectedPlanetTo.getY (), mSelectedPlanetTo.getRadius (), Color.values ()[mSelectedPlanetFrom.getOwnedBy ()]);
		}

		for (Road road : roads) {
			drawRoad (road);
		}

		for (int i = 0; i < planets.size (); i++) {
			Planet planet = planets.get (i);
			drawPlanet (planet);
		}

		try {
			for (SpaceShip spaceShip : spaceShips) {
				drawSpaceShip (spaceShip);
			}
		} catch (ConcurrentModificationException ex) {
		}
		//#TODO ConcurrentModificationException toDel list delete here??

		if (mSelectedPlanetTo != null) {
			Util.drawArrow (new Point (mSelectedPlanetFrom.getX (), mSelectedPlanetFrom.getY ()),
					new Point (mSelectedPlanetTo.getX (), mSelectedPlanetTo.getY ()),
					Color.values ()[mSelectedPlanetFrom.getOwnedBy ()]);
		}

		GL11.glPopMatrix ();
	}

	private void drawRoad (Road road) {
		Util.drawLine (new Point (road.getFrom ().getX (), road.getFrom ().getY ()),
				new Point (road.getTo ().getX (), road.getTo ().getY ()));
	}

	private void drawPlanet (Planet planet) {
		Util.drawCircle (planet.getX (), planet.getY (), planet.getRadius (), Color.values ()[planet.getOwnedBy ()], Textures.InterstellarWar.planet[planet.getTextureIndex ()]);
		Util.drawString (Integer.toString (planet.getUnitsNumber ()), planet.getX (), planet.getY (), Color.values ()[planet.getOwnedBy ()]);
	}

	private void drawSpaceShip (SpaceShip spaceShip) {//#TODO do it with hashmap
		if (spaceShip == null)
			return;

		int spaceshipType = spaceShip.getUnitsNumber () / 10;
		if (spaceshipType > 10) {
			spaceshipType = 10;
		}

		float w = Textures.InterstellarWar.spaceshipDimens[spaceshipType][0];
		float h = Textures.InterstellarWar.spaceshipDimens[spaceshipType][1];

		Planet from = spaceShip.getFromPlanet ();
		Planet to = spaceShip.getToPlanet ();

		float lx = to.getX () - from.getX ();
		float ly = to.getY () - from.getY ();
		float angle = (float) Math.atan (ly / lx);

		Point a = new Point (from.getX () - w / 2, from.getY () + h / 2);
		Point b = new Point (from.getX () + w / 2, from.getY () + h / 2);
		Point c = new Point (from.getX () + w / 2, from.getY () - h / 2);
		Point d = new Point (from.getX () - w / 2, from.getY () - h / 2);

		Point fromPoint = new Point (from.getX (), from.getY ());

		a.rotate (fromPoint, angle);
		b.rotate (fromPoint, angle);
		c.rotate (fromPoint, angle);
		d.rotate (fromPoint, angle);

		GL11.glPushMatrix ();
		GL11.glTranslated (spaceShip.getCurrentTick () * spaceShip.getVx (), spaceShip.getCurrentTick () * spaceShip.getVy (), 0);

		Util.drawQuad (a, b, c, d, Color.values ()[spaceShip.getOwnedBy ()], Textures.InterstellarWar.spaceship[spaceshipType]);

		GL11.glPopMatrix ();
	}

	public InterstellarWarClient getInterstellarWarClient () {
		return mInterstellarWarClient;
	}
}
