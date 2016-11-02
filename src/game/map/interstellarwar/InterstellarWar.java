package game.map.interstellarwar;

import game.Textures;
import game.Util;
import game.connection.Command;
import game.connection.UserConnection;
import game.geom.Arrow;
import game.geom.Color;
import game.geom.Point;
import game.geom.Rect;
import game.map.GameMap;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.io.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class InterstellarWar extends GameMap {
	public static final String GAME_NAME = "Interstellar War";
	private static final int EDGE_MOVE_DISTANCE = 20;
	private static final int EDGE_MOVE_UNIT = 2;
	private Rect mBackground;
	private Point mViewPort;
	private int mPlanetNumber;
	private int mConnectionNumber;
	private ArrayList <Planet> mPlanets = new ArrayList <> ();
	private ArrayList <SpaceShip> mSpaceShips = new ArrayList <> ();
	private ArrayList <Road> mConnections = new ArrayList <> ();
	private boolean mWasMouseDown = false;
	private Planet mSelectedPlanetFrom = null;
	private Planet mSelectedPlanetTo = null;
	private int mTickNumber = 0;

	@Override
	public void init () {
		mBackground = new Rect (0, 0, Display.getWidth (), Display.getHeight ());
		mBackground.setTexture (Textures.InterstellarWar.background[(int) (Math.random () * Textures.InterstellarWar.background.length)]);
		for (Planet planet : mPlanets) {
			planet.setTexture (Textures.InterstellarWar.planet[(int) (Math.random () * Textures.InterstellarWar.planet.length)]);
		}

		mViewPort.setPosition (Display.getWidth () / 2 - mViewPort.getX (), Display.getHeight () / 2 - mViewPort.getY ());
	}

	@Override
	public void mouseEvent () {
		mouseOnEdge ();

		if (Mouse.isButtonDown (0) && !mWasMouseDown) {
			mWasMouseDown = true;
			Point point = new Point (Mouse.getX () - mViewPort.getX (), Mouse.getY () - mViewPort.getY ());

			for (Planet planet : mPlanets) {
				if (planet.isInside (point) && ((UserConnection) getConnection ()).getRoomIndex () == planet.getOwnedBy ()) {
					mSelectedPlanetFrom = planet;
					break;
				}
			}
		} else if (Mouse.isButtonDown (0) && mWasMouseDown) {
			Point point = new Point (Mouse.getX () - mViewPort.getX (), Mouse.getY () - mViewPort.getY ());

			boolean isThere = false;
			for (Planet planet : mPlanets) {
				if (planet.isInside (point) && planet.isNeighbor (mSelectedPlanetFrom)) {
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
					Point point = new Point (Mouse.getX () - mViewPort.getX (), Mouse.getY () - mViewPort.getY ());

					for (Planet planet : mPlanets) {
						if (planet.isInside (point) && planet.isNeighbor (mSelectedPlanetFrom)) {
							mSelectedPlanetTo = planet;
							int index = ((UserConnection) getConnection ()).getRoomIndex ();
							if (mSelectedPlanetFrom.getOwnedBy () == index) {
								getConnection ().send (Command.Type.GAME_COMMAND, GameCommand.START_MOVE_UNITS, mPlanets.indexOf (mSelectedPlanetFrom), mPlanets.indexOf (mSelectedPlanetTo), mTickNumber, mSelectedPlanetFrom.getUnitNumber ());
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


	@Override
	public void keyboardEvent () {
		if (Keyboard.isKeyDown (Keyboard.KEY_LEFT)) {
			mViewPort.move (EDGE_MOVE_UNIT, 0);
		} else if (Keyboard.isKeyDown (Keyboard.KEY_RIGHT)) {
			mViewPort.move (-EDGE_MOVE_UNIT, 0);
		}
		if (Keyboard.isKeyDown (Keyboard.KEY_DOWN)) {
			mViewPort.move (0, EDGE_MOVE_UNIT);
		} else if (Keyboard.isKeyDown (Keyboard.KEY_UP)) {
			mViewPort.move (0, -EDGE_MOVE_UNIT);
		}
	}

	@Override
	public void draw () {
		mBackground.draw ();

		GL11.glPushMatrix ();
		GL11.glTranslated (mViewPort.getX (), mViewPort.getY (), 0);

		if (mSelectedPlanetFrom != null) {
			Util.drawCircle (mSelectedPlanetFrom.getX (), mSelectedPlanetFrom.getY (), mSelectedPlanetFrom.getRadius (), Color.values ()[mSelectedPlanetFrom.getOwnedBy ()]);
		}
		if (mSelectedPlanetTo != null) {
			Util.drawCircle (mSelectedPlanetTo.getX (), mSelectedPlanetTo.getY (), mSelectedPlanetTo.getRadius (), Color.values ()[mSelectedPlanetFrom.getOwnedBy ()]);
		}
		mConnections.forEach (Road::draw);

		mPlanets.forEach (Planet::draw);

		try {
			mSpaceShips.forEach (SpaceShip::draw);
		} catch (ConcurrentModificationException ex) {
		}
		//#TODO ConcurrentModificationException toDel list delete here??

		if (mSelectedPlanetTo != null) {
			Arrow arrow = new Arrow (mSelectedPlanetFrom, mSelectedPlanetTo, 40);
			arrow.setColor (Color.values ()[mSelectedPlanetFrom.getOwnedBy ()]);
			arrow.draw ();
		}
		GL11.glPopMatrix ();
	}

	@Override
	public void loadMap (String fileName) throws NotValidMapException {
		mPlanets = new ArrayList <> ();
		mConnections = new ArrayList <> ();
		try {
			File mapFile = new File (System.getProperty ("user.dir") + "/res/interstellarwar/" + fileName);

			FileReader fileReader = new FileReader (mapFile);
			BufferedReader bufferedReader = new BufferedReader (fileReader);

			setMapName (bufferedReader.readLine ());
			setMaxUsers (Integer.parseInt (bufferedReader.readLine ()));

			mPlanetNumber = Integer.parseInt (bufferedReader.readLine ());
			mConnectionNumber = Integer.parseInt (bufferedReader.readLine ());

			for (int i = 0; i < mPlanetNumber; i++) {
				String[] params = bufferedReader.readLine ().split (" ");

				mPlanets.add (new Planet (Double.parseDouble (params[0]), Double.parseDouble (params[1]), Double.parseDouble (params[2]), Integer.parseInt (params[3]), Integer.parseInt (params[4])));
			}
			for (int i = 0; i < mConnectionNumber; i++) {
				String[] params = bufferedReader.readLine ().split (" ");
				int fromIndex = Integer.parseInt (params[0]);
				int toIndex = Integer.parseInt (params[1]);

				Planet from = mPlanets.get (fromIndex);
				Planet to = mPlanets.get (toIndex);

				from.linkTo (to);
				mConnections.add (new Road (from, to, fromIndex, toIndex));
			}

		} catch (FileNotFoundException exception) {
			throw new NotValidMapException ("Map is not found! : " + exception.toString ());
		} catch (IOException exception) {
			throw new NotValidMapException ("Map file cannot be read! : " + exception.toString ());
		} catch (IndexOutOfBoundsException | NullPointerException exception) {
			exception.printStackTrace ();
			throw new NotValidMapException ("Map format is not valid : " + exception.toString ());
		}
	}

	@Override
	public void loadData (Serializable[] data) {
		mViewPort = new Point (0, 0);
		mPlanets = new ArrayList <> ();
		mConnections = new ArrayList <> ();
		int i = 0;

		setMapName ((String) data[i++]);
		setMaxUsers ((int) data[i++]);

		mPlanetNumber = (int) data[i++];
		mConnectionNumber = (int) data[i++];

		for (int j = 0; j < mPlanetNumber; j++) {
			Planet newPlanet = new Planet ((double) data[i++], (double) data[i++], (double) data[i++], (int) data[i++], (int) data[i++]);
			mPlanets.add (newPlanet);
			if (newPlanet.getOwnedBy () == ((UserConnection) getConnection ()).getRoomIndex ()) {
				mViewPort.setPosition (newPlanet.getX (), newPlanet.getY ());
			}
		}

		for (int j = 0; j < mConnectionNumber; j++) {
			int fromIndex = (int) data[i++];
			int toIndex = (int) data[i++];
			Planet from = mPlanets.get (fromIndex);
			Planet to = mPlanets.get (toIndex);
			from.linkTo (to);
			mConnections.add (new Road (from, to, fromIndex, toIndex));
		}
	}

	@Override
	public Serializable[] toData () {
		ArrayList <Serializable> list = new ArrayList <> ();

		list.add (getMapName ());
		list.add (getMaxUsers ());
		list.add (mPlanetNumber);
		list.add (mConnectionNumber);

		for (int j = 0; j < mPlanetNumber; j++) {
			Planet planet = mPlanets.get (j);
			Point center = planet;
			list.add (center.getX ());
			list.add (center.getY ());
			list.add (planet.getRadius ());
			list.add (planet.getOwnedBy ());
			list.add (planet.getUnitNumber ());
		}

		for (int j = 0; j < mConnectionNumber; j++) {
			Road road = mConnections.get (j);
			list.add (road.getFromIndex ());
			list.add (road.getToIndex ());
		}
		Serializable[] data = new Serializable[list.size ()];
		return list.toArray (data);
	}

	@Override
	public void onGameThread () {
		try {
			Thread.sleep (50);
			mTickNumber++;

			moveUnits ();

			if (mTickNumber % 32 == 0) {
				spawnUnits ();
			}

		} catch (InterruptedException e) {
			e.printStackTrace ();
		}
	}

	@Override
	public void receiveClient (Command command) {
		switch ((GameCommand) command.data[0]) {
			case START_MOVE_UNITS:
				addSpaceShip (mPlanets.get ((int) command.data[1]), mPlanets.get ((int) command.data[2]), (int) command.data[3], (int) command.data[4]);
				break;
		}
	}

	@Override
	public void receiveServer (Command command) {
		switch ((GameCommand) command.data[1]) {
			case START_MOVE_UNITS:
				if ((int) command.data[5] > 0)
					getConnection ().send (Command.Type.GAME_COMMAND, GameCommand.START_MOVE_UNITS, command.data[2], command.data[3], command.data[4], command.data[5]);
				break;
		}
	}

	private void addSpaceShip (Planet from, Planet to, int tickNumber, int unitNumber) {
		mSpaceShips.add (new SpaceShip (from, to, mTickNumber - tickNumber, unitNumber));
		from.setUnitNumber (from.getUnitNumber () - unitNumber);
	}

	private void moveUnits () {
		Iterator <SpaceShip> iterator = mSpaceShips.iterator ();
		while (iterator.hasNext ()) {
			SpaceShip spaceShip = iterator.next ();
			if (spaceShip.tick ()) {
				spaceShip.unitsArrived ();
				iterator.remove ();
			}
		}
	}

	private void spawnUnits () {
		for (Planet planet : mPlanets) {
			planet.addUnit ();
		}
	}

	private void mouseOnEdge () {
		if (Mouse.getX () < EDGE_MOVE_DISTANCE) {
			mViewPort.move (EDGE_MOVE_UNIT, 0);
		} else if (Mouse.getX () > Display.getWidth () - EDGE_MOVE_DISTANCE) {
			mViewPort.move (-EDGE_MOVE_UNIT, 0);
		}
		if (Mouse.getY () < EDGE_MOVE_DISTANCE) {
			mViewPort.move (0, EDGE_MOVE_UNIT);
		} else if (Mouse.getY () > Display.getHeight () - EDGE_MOVE_DISTANCE) {
			mViewPort.move (0, -EDGE_MOVE_UNIT);
		}
	}

	enum GameCommand {
		START_MOVE_UNITS
	}
}
