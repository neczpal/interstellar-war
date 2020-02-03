package net.neczpal.interstellarwar.desktop.ui.frames;

import net.neczpal.interstellarwar.clientcommon.InterstellarWarClient;
import net.neczpal.interstellarwar.common.game.InterstellarWarCore;
import net.neczpal.interstellarwar.common.game.Planet;
import net.neczpal.interstellarwar.common.game.Road;
import net.neczpal.interstellarwar.common.game.SpaceShip;
import net.neczpal.interstellarwar.desktop.Loader;
import net.neczpal.interstellarwar.desktop.geom.Color;
import net.neczpal.interstellarwar.desktop.geom.GLUtil;
import net.neczpal.interstellarwar.desktop.geom.Point;
import net.neczpal.interstellarwar.desktop.geom.Rect;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.List;

public class InterstellarWarPanel {
    private static final int EDGE_MOVE_DISTANCE = 20;
    private static final int EDGE_MOVE_UNIT = 2;
    private InterstellarWarClient mInterstellarWarClient;
    private InterstellarWarCore mCore;
    private Rect mBackground;
	private Point mViewPort = new Point (0, 0);

    private boolean mWasMouseDown = false;

    private int mSelectedPlanetFromIndex = -1;
    private int mSelectedPlanetToIndex = -1;

    private Textures mTextures;

    private boolean isDrawing = false;//#TODO

    /**
     * Erstellt das Spielpanel
     *
     * @param client Das Spiel-Klient
     */
	public InterstellarWarPanel (InterstellarWarClient client) {
		mCore = client.getCore ();
		mInterstellarWarClient = client;
	}

	/**
	 * Initialisiert die Texturen
	 *
	 * @throws IOException falls die Texturefiles kann nicht eingeladet werden
	 */
	public void initTextures () throws IOException {
		mTextures = new Textures ();
	}

	/**
	 * Initialisiert das Spiel
	 */
	public void initGame () {
		initBackground ();
		initViewPort ();
	}

	/**
	 * Initialisiert das Hintergrund
	 */
	private void initBackground () {
		mBackground = new Rect (0, 0, Display.getWidth (), Display.getHeight ());
		mBackground.setTexture (mTextures.background[mCore.getBackgroundTextureIndex ()]);
	}

	/**
	 * Initialisiert das Ansichtsfenster
	 */
	private void initViewPort () {
		for (Planet planet : mCore.getPlanets ()) {
			if (planet.getOwnedBy () == getInterstellarWarClient ().getRoomIndex ()) {
				mViewPort = new Point (Display.getWidth () / 2 - planet.getX (), Display.getHeight () / 2 - planet.getY ());
			}
		}
	}

	/**
	 * Eingang events
	 */
	public void inputEvents () {
		moveViewPort ();
		selectPlanets ();
    }

    /**
     * Wählt das Planet aus, und schikt ein Raumschiff zu dem ausgewählte Planet
     */
    private void selectPlanets () {//#TODO Refactor im sure it could be nicer
        if (Mouse.isButtonDown (0) && !mWasMouseDown) {
            List<Planet> planets = mCore.getPlanets ();
            mWasMouseDown = true;
            Point point = getMousePosition ();

            for (int i = 0; i < planets.size (); i++) {
                Planet planet = planets.get (i);
                if (planet.isInside (point.getX (), point.getY ()) && mInterstellarWarClient.getRoomIndex () == planet.getOwnedBy ()) {
                    mSelectedPlanetFromIndex = i;
                    break;
                }
            }
        } else if (Mouse.isButtonDown (0) && mWasMouseDown && mSelectedPlanetFromIndex != -1) {
            List<Planet> planets = mCore.getPlanets ();
            Point point = getMousePosition ();

            boolean isThere = false;
            for (int i = 0; i < planets.size (); i++) {
                Planet planet = planets.get (i);
                if (planet.isInside (point.getX (), point.getY ()) && planet.isNeighbor (planets.get (mSelectedPlanetFromIndex))) {
                    this.mSelectedPlanetToIndex = i;
                    isThere = true;
                    break;
                }
            }
            if (!isThere) {
                this.mSelectedPlanetToIndex = -1;
			}
		} else if (!Mouse.isButtonDown (0)) {

			if (mWasMouseDown) {
                mWasMouseDown = false;

                if (mSelectedPlanetFromIndex != -1) {
                    List<Planet> planets = mCore.getPlanets ();
                    Point point = getMousePosition ();

                    for (int i = 0; i < planets.size (); i++) {
                        Planet planet = planets.get (i);
                        if (planet.isInside (point.getX (), point.getY ()) && planet.isNeighbor (planets.get (mSelectedPlanetFromIndex))) {
                            this.mSelectedPlanetToIndex = i;
                            if (planets.get (mSelectedPlanetFromIndex).getOwnedBy () == mInterstellarWarClient.getRoomIndex ()) {
                                mInterstellarWarClient.startMoveSpaceShip (mSelectedPlanetFromIndex, mSelectedPlanetToIndex, mCore.getTickNumber (), planets.get (mSelectedPlanetFromIndex).getUnitsNumber ());
                            }
                            break;
                        }
					}

                }
            } else {
                this.mSelectedPlanetFromIndex = -1;
                this.mSelectedPlanetToIndex = -1;
			}
		}
	}

	/**
	 * Bewegt das Ansichtsfenster
	 */
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

	/**
	 * @return Die Position der Maus
	 */
	private Point getMousePosition () {
		return new Point (Mouse.getX () - mViewPort.getX (), Mouse.getY () - mViewPort.getY ());
	}

	/**
	 * Malt das Spiel aus
	 */
	public void draw () {
		mBackground.draw ();
		List<Road> roads = mCore.getRoads ();
		List<Planet> planets = mCore.getPlanets ();
		List<SpaceShip> spaceShips = mCore.getSpaceShips ();

		GL11.glPushMatrix ();
		GL11.glTranslated (mViewPort.getX (), mViewPort.getY (), 0);

		if (mSelectedPlanetFromIndex != -1) {
			Planet selectedPlanet = planets.get (mSelectedPlanetFromIndex);
			GLUtil.drawCircle (selectedPlanet.getX (), selectedPlanet.getY (), selectedPlanet.getRadius (), Color.values ()[selectedPlanet.getOwnedBy ()]);
		}
		if (mSelectedPlanetToIndex != -1) {
			Planet selectedPlanet = planets.get (mSelectedPlanetToIndex);
            GLUtil.drawCircle (selectedPlanet.getX (), selectedPlanet.getY (), selectedPlanet.getRadius (), Color.values ()[selectedPlanet.getOwnedBy ()]);
        }

        isDrawing = true;
        for (Road road : roads) {
            drawRoad (road);
        }

        for (int i = 0; i < planets.size (); i++) {
            Planet planet = planets.get (i);
            drawPlanet (planet);
        }

        for (SpaceShip spaceShip : spaceShips) {
            try {
                drawSpaceShip (spaceShip);
            } catch (ConcurrentModificationException ex) {
            }
        }

        if (mSelectedPlanetToIndex != -1) {
            Planet selectedFromPlanet = planets.get (mSelectedPlanetFromIndex);
            Planet selectedToPlanet = planets.get (mSelectedPlanetToIndex);

            GLUtil.drawArrow (new Point (selectedFromPlanet.getX (), selectedFromPlanet.getY ()), new Point (selectedToPlanet.getX (), selectedToPlanet.getY ()), Color.values ()[selectedFromPlanet.getOwnedBy ()]);
        }

        GL11.glPopMatrix ();

        isDrawing = false;
	}

	/**
	 * Malt ein Weg aus
	 *
	 * @param road Das Weg
	 */
	private void drawRoad (Road road) {
		GLUtil.drawLine (new Point (road.getFrom ().getX (), road.getFrom ().getY ()), new Point (road.getTo ().getX (), road.getTo ().getY ()));
	}

	/**
	 * Malt ein Planet aus
	 *
	 * @param planet Das Planet
	 */
	private void drawPlanet (Planet planet) {
		GLUtil.drawCircle (planet.getX (), planet.getY (), planet.getRadius (), Color.values ()[planet.getOwnedBy ()], mTextures.planet[planet.getTextureIndex ()]);
		GLUtil.drawString (Integer.toString (planet.getUnitsNumber ()), planet.getX (), planet.getY (), Color.values ()[planet.getOwnedBy ()]);
	}

	/**
	 * Malt ein Raumschiff aus
	 *
	 * @param spaceShip Das Raumschiff
	 */
	private void drawSpaceShip (SpaceShip spaceShip) {
		int spaceshipType = spaceShip.getTextureIndex ();

		float w = mTextures.spaceshipDimens[spaceshipType][0];
		float h = mTextures.spaceshipDimens[spaceshipType][1];

		Planet from = spaceShip.getFromPlanet ();
		Planet to = spaceShip.getToPlanet ();

		double lx = to.getX () - from.getX ();
		double ly = to.getY () - from.getY ();
		double angle = Math.atan (ly / lx);

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

		GLUtil.drawQuad (a, b, c, d, Color.values ()[spaceShip.getOwnedBy ()], mTextures.spaceship[spaceshipType]);
		GLUtil.drawString (Integer.toString (spaceShip.getUnitsNumber ()), fromPoint.getX (), fromPoint.getY () + 20);

		GL11.glPopMatrix ();
	}

	//GETTERS

	public InterstellarWarClient getInterstellarWarClient () {
		return mInterstellarWarClient;
	}

	private class Textures {
		public int[] planet;
		public int[] spaceship;
		public float[][] spaceshipDimens;
		public int[] background;

		/**
		 * Ladet die Texturen ein
		 *
		 * @throws IOException falls die Texturen kann nicht eingeladet werden
		 */
		public Textures () throws IOException {
			planet = new int[Planet.PLANET_TYPES];
			for (int i = 0; i < planet.length; i++) {
				planet[i] = Loader.loadTexture ("res/textures/planet" + (i + 1) + ".png");
			}
			spaceship = new int[SpaceShip.SPACESHIP_TYPES];
			spaceshipDimens = new float[SpaceShip.SPACESHIP_TYPES][2];
			for (int i = 0; i < spaceship.length; i++) {
				spaceship[i] = Loader.loadTexture ("res/textures/spaceship" + (i + 1) + ".png");
				BufferedImage image = Loader.loadImage ("res/textures/spaceship" + (i + 1) + ".png");
				spaceshipDimens[i][0] = image.getWidth () / 4.0f;
				spaceshipDimens[i][1] = image.getHeight () / 4.0f;
			}
			background = new int[InterstellarWarCore.BACKGROUND_TYPES];
			for (int i = 0; i < background.length; i++) {
				background[i] = Loader.loadTexture ("res/textures/background" + (i + 1) + ".png");
			}
		}
	}
}
