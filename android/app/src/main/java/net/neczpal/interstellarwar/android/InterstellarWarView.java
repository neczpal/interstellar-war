package net.neczpal.interstellarwar.android;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.view.SurfaceView;
import net.neczpal.interstellarwar.clientcommon.InterstellarWarClient;
import net.neczpal.interstellarwar.common.game.InterstellarWarCore;
import net.neczpal.interstellarwar.common.game.Planet;
import net.neczpal.interstellarwar.common.game.Road;
import net.neczpal.interstellarwar.common.game.SpaceShip;

import java.io.IOException;
import java.util.List;

public class InterstellarWarView extends SurfaceView {

	private InterstellarWarClient mInterstellarWarClient;
	private InterstellarWarCore mCore;

	private Paint paint = new Paint ();
	private int[] colors = new int[] {Color.WHITE, Color.RED, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.BLUE, Color.BLACK};

	private Bitmap[] planetTextures;
	private Bitmap[] spaceShipTextures;
	private Bitmap[] backgroundTextures;

	private PointF mViewPort;
	private RectF[] mPlanetsDimens;
	private RectF mBackgroundDimens;
	private float mZoom = 2f;//#TODO ZOOM

	private int mWidth = 400;
	private int mHeight = 400;

	public InterstellarWarView (Context context, InterstellarWarClient client) {
		super (context);
		mInterstellarWarClient = client;
		mCore = client.getCore ();

		try {
			loadTextures ();
		} catch (IOException e) {
			e.printStackTrace ();
		}
		init ();

		setWillNotDraw (false);
	}

	private void loadTextures () throws IOException {
		Resources res = getResources ();
		planetTextures = new Bitmap[Planet.PLANET_TYPES];
		for (int i = 0; i < planetTextures.length; i++) {
			planetTextures[i] = BitmapFactory.decodeResource (res, res.getIdentifier ("planet" + (i + 1), "drawable", getContext ().getPackageName ()));
		}
		spaceShipTextures = new Bitmap[SpaceShip.SPACESHIP_TYPES];
		for (int i = 0; i < spaceShipTextures.length; i++) {
			spaceShipTextures[i] = BitmapFactory.decodeResource (res, res.getIdentifier ("spaceship" + (i + 1), "drawable", getContext ().getPackageName ()));
		}
		backgroundTextures = new Bitmap[InterstellarWarCore.BACKGROUND_TYPES];
		for (int i = 0; i < backgroundTextures.length; i++) {
			backgroundTextures[i] = BitmapFactory.decodeResource (res, res.getIdentifier ("background" + (i + 1), "drawable", getContext ().getPackageName ()));
		}
	}

	private void destroyTextures () {
		for (int i = 0; i < planetTextures.length; i++) {
			planetTextures[i].recycle ();
		}
		for (int i = 0; i < spaceShipTextures.length; i++) {
			spaceShipTextures[i].recycle ();
		}
		for (int i = 0; i < backgroundTextures.length; i++) {
			backgroundTextures[i].recycle ();
		}
	}

	private void init () {
		initViewPort ();
		initBackground ();
		initPlanetsPositions ();
	}

	private void initViewPort () {
		for (Planet planet : mCore.getPlanets ()) {
			if (planet.getOwnedBy () == mInterstellarWarClient.getRoomIndex ()) {
				mViewPort = new PointF ((float) (getWidth () / 2 - planet.getX ()), (float) (getHeight () / 2 - planet.getY ()));
			}
		}
	}

	private void initBackground () {
		mBackgroundDimens = new RectF (0, 0, mWidth, mHeight);
	}

	private void initPlanetsPositions () {
		List<Planet> planets = mCore.getPlanets ();
		mPlanetsDimens = new RectF[planets.size ()];
		for (int i = 0; i < planets.size (); i++) {
			Planet planet = planets.get (i);
			mPlanetsDimens[i] = new RectF (
					(float) (planet.getX () - planet.getRadius () / 2) * mZoom,
					(float) (planet.getY () - planet.getRadius () / 2) * mZoom,
					(float) (planet.getX () + planet.getRadius () / 2) * mZoom,
					(float) (planet.getY () + planet.getRadius () / 2) * mZoom);
		}
	}

	@Override
	protected void onSizeChanged (int w, int h, int oldw, int oldh) {
		super.onSizeChanged (w, h, oldw, oldh);
		mWidth = w;
		mHeight = h;
		initBackground ();
	}

	@Override
	protected void onDraw (Canvas canvas) {
		super.onDraw (canvas);

		List <Planet> planets = mCore.getPlanets ();
		List <Road> roads = mCore.getRoads ();
		List <SpaceShip> spaceShips = mCore.getSpaceShips ();

		drawBackground (canvas);

		paint.setARGB (255, 0, 0, 255);
		for (Road road : roads) {
			drawRoad (canvas, road);
		}

		for (int i = 0; i < planets.size (); i++) {
			Planet planet = planets.get (i);
			drawPlanet (canvas, planet, i);
		}

		for (SpaceShip spaceShip : spaceShips) {
			drawSpaceShip (canvas, spaceShip);
		}

	}

	private void drawBackground (Canvas canvas) {
		paint.setARGB (255, 255, 255, 255);
		canvas.drawBitmap (backgroundTextures[mCore.getBackgroundTextureIndex ()],
				null,
				mBackgroundDimens,
				paint);
	}

	private void drawRoad (Canvas canvas, Road road) {
		Planet from = road.getFrom ();
		Planet to = road.getTo ();

		canvas.drawLine ((float) (from.getX () * mZoom), (float) (from.getY () * mZoom), (float) (to.getX () * mZoom), (float) (to.getY () * mZoom), paint);
	}

	private void drawPlanet (Canvas canvas, Planet planet, int index) {
		paint.setColor (colors[planet.getOwnedBy ()]);
		canvas.drawBitmap (planetTextures[planet.getTextureIndex ()], null, mPlanetsDimens[index], paint);
		canvas.drawText (Integer.toString (planet.getUnitsNumber ()), mPlanetsDimens[index].centerX (), mPlanetsDimens[index].centerY (), paint);
	}

	private void drawSpaceShip (Canvas canvas, SpaceShip spaceShip) {
		paint.setColor (colors[spaceShip.getOwnedBy ()]);
		Planet from = spaceShip.getFromPlanet ();
		canvas.drawCircle ((float) (from.getX () + spaceShip.getCurrentTick () * spaceShip.getVx ())  * mZoom ,
				(float) (from.getY () + spaceShip.getCurrentTick () * spaceShip.getVy ())  * mZoom, 5 * mZoom, paint);
	}

	@Override
	protected void onDetachedFromWindow () {
		super.onDetachedFromWindow ();
		destroyTextures ();
	}
}
