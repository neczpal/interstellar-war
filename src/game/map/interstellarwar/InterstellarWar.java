package game.map.interstellarwar;

import game.Textures;
import game.Util;
import game.connection.Command;
import game.connection.UserConnection;
import game.geom.*;
import game.map.GameMap;
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
	private Point2D mViewPort;
	private int mPlanetNumber;
	private int mConnectionNumber;
	private ArrayList <Planet> mPlanets = new ArrayList <> ();
	private ArrayList <SpaceShip> mSpaceShips = new ArrayList <> ();
	private ArrayList <Integer[]> mConnections = new ArrayList <> ();
	private boolean mWasMouseDown = false;
	private Planet mSelectedPlanetFrom = null;
	private Planet mSelectedPlanetTo = null;
	private int tickNumber = 0;

	@Override
	public void init () {
		mBackground = new Rect (0, 0, Display.getWidth (), Display.getHeight ());
		mBackground.setTexture (Textures.InterstellarWar.background);
		for (Planet planet : mPlanets) {
			planet.setTexture (Textures.InterstellarWar.planet[(int) (planet.getX () + planet.getY ()) % Textures.InterstellarWar.planet.length]);
		}

		mViewPort.setPosition (Display.getWidth () / 2 - mViewPort.getX (), Display.getHeight () / 2 - mViewPort.getY ());
	}

	@Override
	public void mouseEvent () {
		mouseOnEdge ();

		if (Mouse.isButtonDown (0) && !mWasMouseDown) {
			mWasMouseDown = true;
			Point2D point = new Point2D (Mouse.getX () - mViewPort.getX (), Mouse.getY () - mViewPort.getY ());

			for (Planet planet : mPlanets) {
				if (planet.isInside (point) && ((UserConnection) getConnection ()).getRoomIndex () == planet.getOwnedBy ()) {
					mSelectedPlanetFrom = planet;
					break;
				}
			}
		} else if (Mouse.isButtonDown (0) && mWasMouseDown) {
			Point2D point = new Point2D (Mouse.getX () - mViewPort.getX (), Mouse.getY () - mViewPort.getY ());

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
					Point2D point = new Point2D (Mouse.getX () - mViewPort.getX (), Mouse.getY () - mViewPort.getY ());

					for (Planet planet : mPlanets) {
						if (planet.isInside (point) && planet.isNeighbor (mSelectedPlanetFrom)) {
							mSelectedPlanetTo = planet;
							int index = ((UserConnection) getConnection ()).getRoomIndex ();
							if (mSelectedPlanetFrom.getOwnedBy () == index) {
								getConnection ().send (Command.Type.GAME_COMMAND, GameCommand.START_MOVE_UNITS, mPlanets.indexOf (mSelectedPlanetFrom), mPlanets.indexOf (mSelectedPlanetTo));
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
		mBackground.draw ();

		GL11.glPushMatrix ();
		GL11.glTranslated (mViewPort.getX (), mViewPort.getY (), 0);
		if (mSelectedPlanetFrom != null) {
			Point2D center1 = mSelectedPlanetFrom;
			Util.drawCircle (center1.getX (), center1.getY (), mSelectedPlanetFrom.getRadius (), Color.values ()[mSelectedPlanetFrom.getOwnedBy ()]);
		}
		if (mSelectedPlanetTo != null) {
			Point2D center2 = mSelectedPlanetTo;
			Util.drawCircle (center2.getX (), center2.getY (), mSelectedPlanetTo.getRadius (), Color.values ()[mSelectedPlanetFrom.getOwnedBy ()]);
		}
		for (Integer[] integers : mConnections) {
			Planet planet1 = mPlanets.get (integers[0]);
			Planet planet2 = mPlanets.get (integers[1]);
			Line line = new Line (planet1, planet2);
			Color.WHITE.setGLColor ();
			line.draw ();
		}

		mPlanets.forEach (Planet::draw);

		try {
			for (SpaceShip spaceShip : mSpaceShips) {
				spaceShip.draw ();
			}
		} catch (ConcurrentModificationException ex) {
		}
		//#TODO ConcurrentModificationException toDel list delete here??

		if (mSelectedPlanetTo != null) {
			Arrow arrow = new Arrow (mSelectedPlanetFrom, mSelectedPlanetTo, 40);
			Color.values ()[mSelectedPlanetFrom.getOwnedBy ()].setGLColor ();
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
		mViewPort = new Point2D (0, 0);
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
			Point2D center = planet;
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
				getConnection ().send (Command.Type.GAME_COMMAND, GameCommand.START_MOVE_UNITS, command.data[2], command.data[3]);
				break;
			case END_MOVE_UNITS:
				mSpaceShips.remove (command.data[2]);
				getConnection ().send (Command.Type.GAME_COMMAND, GameCommand.END_MOVE_UNITS, command.data[2]);
		}
	}

	private void addSpaceShip (Planet from, Planet to) {
		if (from.getUnitNumber () == 0)
			return;

		double length = from.distance (to);
		double lx = to.getX () - from.getX ();
		double ly = to.getY () - from.getY ();
		double hx = lx / length * SpaceShip.SPACE_SHIP_SIZE;
		double hy = ly / length * SpaceShip.SPACE_SHIP_SIZE;

		Point2D a = new Point2D (from.getX () + hx, from.getY () + hy);
		Point2D b = new Point2D (from.getX () + hx / 2, from.getY () + hy / 2);
		Point2D c = new Point2D (from.getX () + hx / 2, from.getY () + hy / 2);
		b.rotate (from, -90);
		c.rotate (from, 90);

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
		START_MOVE_UNITS, END_MOVE_UNITS// #TODO end move units
	}
}
