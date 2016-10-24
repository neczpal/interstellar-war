package game.map.interstellarwar;

import game.Textures;
import game.Util;
import game.connection.ClientConnection;
import game.connection.Command;
import game.geom.Color;
import game.geom.Line;
import game.geom.Point2D;
import game.map.GameMap;
import org.lwjgl.input.Mouse;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by neczp on 2016. 10. 06..
 */
public class InterstellarWar extends GameMap {
	public static final String GAME_NAME = "Interstellar War";

	private int mPlanetNumber;
	private int mConnectionNumber;

	private ArrayList <Planet> mPlanets = new ArrayList <> ();
	private ArrayList <SpaceShip> mSpaceShips = new ArrayList <> ();
	private ArrayList <Integer[]> mConnections = new ArrayList <> ();

	private boolean mWasMouseDown = false;

	private Planet mSelectedPlanetFrom = null;
	private Planet mSelectedPlanetTo = null;
	//ONLY SERVER
	private int tickNumber = 0;

	@Override
	public void initTextures () {
		for (Planet planet : mPlanets) {
			planet.setTexture (Textures.InterstellarWar.planet[(int) (planet.getCenter ().getX () + planet.getCenter ().getY ()) % 9]);
		}
	}

	@Override
	public void mouseEvent () {
		if (Mouse.isButtonDown (0) && !mWasMouseDown) {
			mWasMouseDown = true;
			Point2D point = new Point2D (Mouse.getX (), Mouse.getY ());

			for (Planet planet : mPlanets) {
				if (planet.isInside (point) && ((ClientConnection) getConnection ()).getRoomIndex () == planet.getOwnedBy ()) {
					mSelectedPlanetFrom = planet;
					break;
				}
			}
		} else if (Mouse.isButtonDown (0) && mWasMouseDown) {
			Point2D point = new Point2D (Mouse.getX (), Mouse.getY ());

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
					Point2D point = new Point2D (Mouse.getX (), Mouse.getY ());

					for (Planet planet : mPlanets) {
						if (planet.isInside (point) && planet.isNeighbor (mSelectedPlanetFrom)) {
							mSelectedPlanetTo = planet;
							int index = ((ClientConnection) getConnection ()).getRoomIndex ();
							if (mSelectedPlanetFrom.getOwnedBy () == index) {
								getConnection ().send (Command.Type.GAME_DATA, GameCommand.START_MOVE_UNITS, mPlanets.indexOf (mSelectedPlanetFrom), mPlanets.indexOf (mSelectedPlanetTo));
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

	}

	@Override
	public void draw () {
		if (mSelectedPlanetFrom != null) {
			Point2D center1 = mSelectedPlanetFrom.getCenter ();
			Util.drawCircle (center1.getX (), center1.getY (), mSelectedPlanetFrom.getRadius () + 3, Color.values ()[mSelectedPlanetFrom.getOwnedBy ()]);
		}
		if (mSelectedPlanetTo != null) {
			Point2D center2 = mSelectedPlanetTo.getCenter ();
			Util.drawCircle (center2.getX (), center2.getY (), mSelectedPlanetTo.getRadius () + 3, Color.values ()[mSelectedPlanetFrom.getOwnedBy ()]);
		}

		mPlanets.forEach (Planet::draw);
		for (Integer[] integers : mConnections) {
			Planet planet1 = mPlanets.get (integers[0]);
			Planet planet2 = mPlanets.get (integers[1]);
			Line line = new Line (planet1.getCenter (), planet2.getCenter ());
			Color.WHITE.setGLColor ();
			line.draw ();
		}
		for (SpaceShip spaceShip : mSpaceShips) {
			spaceShip.draw ();
		}//#TODO ConcurrentModificationException toDel list delete here??
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
				mConnections.add (new Integer[] {Integer.parseInt (params[0]), Integer.parseInt (params[1])});

				Planet from = mPlanets.get (Integer.parseInt (params[0]));
				Planet to = mPlanets.get (Integer.parseInt (params[1]));

				from.linkTo (to);
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
		mPlanets = new ArrayList <> ();
		mConnections = new ArrayList <> ();
		int i = 0;

		setMapName ((String) data[i++]);
		setMaxUsers ((int) data[i++]);

		mPlanetNumber = (int) data[i++];
		mConnectionNumber = (int) data[i++];

		for (int j = 0; j < mPlanetNumber; j++) {
			mPlanets.add (new Planet ((double) data[i++], (double) data[i++], (double) data[i++], (int) data[i++], (int) data[i++]));
		}

		for (int j = 0; j < mConnectionNumber; j++) {
			int fromIndex = (int) data[i++];
			int toIndex = (int) data[i++];
			Planet from = mPlanets.get (fromIndex);
			Planet to = mPlanets.get (toIndex);
			from.linkTo (to);
			mConnections.add (new Integer[] {fromIndex, toIndex});
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
			Point2D center = planet.getCenter ();
			list.add (center.getX ());
			list.add (center.getY ());
			list.add (planet.getRadius ());
			list.add (planet.getOwnedBy ());
			list.add (planet.getUnitNumber ());
		}

		for (int j = 0; j < mConnectionNumber; j++) {
			Integer[] pair = mConnections.get (j);
			list.add (pair[0]);
			list.add (pair[1]);
		}
		Serializable[] data = new Serializable[list.size ()];
		return list.toArray (data);
	}

	@Override
	public void onGameThread () {
		try {
			Thread.sleep (50);
			tickNumber++;

			moveUnits ();

			if (tickNumber % 32 == 0) {
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
				addSpaceShip (mPlanets.get ((int) command.data[1]), mPlanets.get ((int) command.data[2]));
				break;
			case END_MOVE_UNITS:
				mSpaceShips.remove (command.data[1]);
		}
	}

	@Override
	public void receiveServer (Command command) {
		switch ((GameCommand) command.data[1]) {
			case START_MOVE_UNITS:
				addSpaceShip (mPlanets.get ((int) command.data[2]), mPlanets.get ((int) command.data[3]));
				getConnection ().send (Command.Type.GAME_DATA, GameCommand.START_MOVE_UNITS, command.data[2], command.data[3]);
				break;
			case END_MOVE_UNITS:
				mSpaceShips.remove (command.data[2]);
				getConnection ().send (Command.Type.GAME_DATA, GameCommand.END_MOVE_UNITS, command.data[2]);
		}
	}

	private void addSpaceShip (Planet from, Planet to) {
		if (from.getUnitNumber () == 0)
			return;

		double length = from.distance (to);
		double lx = to.getCenter ().getX () - from.getCenter ().getX ();
		double ly = to.getCenter ().getY () - from.getCenter ().getY ();
		double hx = lx / length * SpaceShip.SPACE_SHIP_SIZE;
		double hy = ly / length * SpaceShip.SPACE_SHIP_SIZE;

		Point2D a = new Point2D (from.getCenter ().getX () + hx, from.getCenter ().getY () + hy);
		Point2D b = new Point2D (from.getCenter ().getX () + hx / 2, from.getCenter ().getY () + hy / 2);
		Point2D c = new Point2D (from.getCenter ().getX () + hx / 2, from.getCenter ().getY () + hy / 2);
		b.rotate (from.getCenter (), -90);
		c.rotate (from.getCenter (), 90);

		mSpaceShips.add (new SpaceShip (a, b, c, from.getOwnedBy (), from.getUnitNumber (), (int) (2 * length / SpaceShip.SPACE_SHIP_SIZE), hx / 2, hy / 2, mPlanets.indexOf (to)));
		from.setUnitNumber (0);
	}

	private void moveUnits () {
		Iterator <SpaceShip> iterator = mSpaceShips.iterator ();
		while (iterator.hasNext ()) {
			SpaceShip spaceShip = iterator.next ();
			if (spaceShip.tick ()) {
				spaceShip.moveUnitsTo (mPlanets.get (spaceShip.getToPlanet ()));
				iterator.remove ();
			}
		}
	}

	private void spawnUnits () {
		for (Planet planet : mPlanets) {
			planet.addUnit ();
		}
	}

	enum GameCommand {
		START_MOVE_UNITS, END_MOVE_UNITS// #TODO end move units
	}
}
