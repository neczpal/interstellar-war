package net.neczpal.interstellarwar.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceView;
import net.neczpal.interstellarwar.client.InterstellarWarClient;
import net.neczpal.interstellarwar.common.InterstellarWarCore;
import net.neczpal.interstellarwar.common.Planet;
import net.neczpal.interstellarwar.common.Road;
import net.neczpal.interstellarwar.common.SpaceShip;

import java.util.ArrayList;

public class InterstellarWarView extends SurfaceView {

	private InterstellarWarClient mInterstellarWarClient;
	private InterstellarWarCore mCore;

	private Paint paint = new Paint ();
	private int[] colors = new int[] {Color.WHITE, Color.RED, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.BLUE, Color.BLACK};

	public InterstellarWarView (Context context, InterstellarWarClient client) {
		super (context);
		mInterstellarWarClient = client;
		mCore = client.getCore ();
		setWillNotDraw (false);
	}

	@Override
	protected void onDraw (Canvas canvas) {
		super.onDraw (canvas);
		paint.setARGB (255, 255, 255, 255);
		canvas.drawRect (0, 0, getWidth (), getHeight (), paint);

		ArrayList <Planet> planets = mCore.getPlanets ();
		ArrayList <Road> roads = mCore.getRoads ();
		ArrayList <SpaceShip> spaceShips = mCore.getSpaceShips ();

		paint.setARGB (255, 0, 0, 255);
		for (Road road : roads) {
			Planet from = road.getFrom ();
			Planet to = road.getTo ();
			canvas.drawLine ((float) from.getX (), (float) from.getY (), (float) to.getX (), (float) to.getY (), paint);
		}

		for (Planet planet : planets) {
			paint.setColor (colors[planet.getOwnedBy ()]);
			canvas.drawCircle ((float) planet.getX (), (float) planet.getY (), (float) planet.getRadius (), paint);//#TODO
		}

		for (SpaceShip spaceShip : spaceShips) {
			paint.setColor (colors[spaceShip.getOwnedBy ()]);
			Planet from = spaceShip.getFromPlanet ();
			canvas.drawCircle ((float) (from.getX () + spaceShip.getCurrentTick () * spaceShip.getVx ()),
					(float) (from.getY () + spaceShip.getCurrentTick () * spaceShip.getVy ()), 5, paint);
		}

	}

	@Override
	protected void onDetachedFromWindow () {
		super.onDetachedFromWindow ();
	}


}
