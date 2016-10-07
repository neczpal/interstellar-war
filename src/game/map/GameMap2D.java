package game.map;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by neczp on 2016. 10. 06..
 */
public class GameMap2D extends GameMap {

	private ArrayList <Planet2D> mPlanets;

	public GameMap2D () {
		mPlanets = new ArrayList <> ();
	}

	@Override
	public void draw () {
		mPlanets.forEach (Planet2D::draw);
	}

	@Override
	public void loadMap (String fileName) throws NotValidMapException {
		try {
			File mapFile = new File (System.getProperty ("user.dir") + "/res/maps2d/" + fileName);

			setMapFile (mapFile);

			FileReader fileReader = new FileReader (mapFile);
			BufferedReader bufferedReader = new BufferedReader (fileReader);

			setName (bufferedReader.readLine ());
			setMaxUsers (Integer.parseInt (bufferedReader.readLine ()));

			String line;
			while (!((line = bufferedReader.readLine ()).equals (""))) {
				String[] params = line.split (" ");
				mPlanets.add (new Planet2D (Integer.parseInt (params[0]), Integer.parseInt (params[1]), Integer.parseInt (params[2]), Integer.parseInt (params[3]), Integer.parseInt (params[4])));
			}

			while (!((line = bufferedReader.readLine ()) != null)) {
				String[] params = line.split (" ");

				Planet2D from = mPlanets.get (Integer.parseInt (params[0]));
				Planet2D to = mPlanets.get (Integer.parseInt (params[1]));

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
}
