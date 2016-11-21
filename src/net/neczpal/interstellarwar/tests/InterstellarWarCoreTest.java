package net.neczpal.interstellarwar.tests;

import net.neczpal.interstellarwar.common.game.InterstellarWarCore;
import net.neczpal.interstellarwar.common.game.Planet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class InterstellarWarCoreTest {
	private InterstellarWarCore mCore;


	@Before
	public void setUp () throws Exception {
		mCore = new InterstellarWarCore ("testmap01");
		mCore.start ();
	}

	@After
	public void tearDown () throws Exception {
		mCore.stopGame ();
		mCore = null;
	}

	@Test
	public void testCapturedPlanetSpawnUnit () throws Exception {
		ArrayList <Planet> planets = mCore.getPlanets ();
		int selectedPlanetIndex = 0;

		Planet selectedPlanet = planets.get (selectedPlanetIndex);
		int lastUnitsNumber = selectedPlanet.getUnitsNumber ();

		Thread.sleep (1600);
		Assert.assertEquals ("Unit spawned", lastUnitsNumber + 1, selectedPlanet.getUnitsNumber ());
	}

	@Test
	public void testUncapturedPlanetNotSpawnUnit () throws Exception {
		ArrayList <Planet> planets = mCore.getPlanets ();
		int selectedPlanetIndex = 1;

		Planet selectedPlanet = planets.get (selectedPlanetIndex);
		int lastUnitsNumber = selectedPlanet.getUnitsNumber ();

		Thread.sleep (1600);
		Assert.assertEquals ("Unit not spawned", lastUnitsNumber, selectedPlanet.getUnitsNumber ());
	}

	@Test
	public void testSpaceShipMove () throws Exception {
		ArrayList <Planet> planets = mCore.getPlanets ();
		int selectedFromPlanetIndex = 0;
		int selectedToPlanetIndex = 1;
		int sentUnitNumber = 1;

		int lastUnitsNumber = planets.get (selectedToPlanetIndex).getUnitsNumber ();

		mCore.startMoveSpaceShip (selectedFromPlanetIndex, selectedToPlanetIndex, 0, sentUnitNumber);

		Assert.assertTrue ("SpaceShip starts moving", mCore.getSpaceShips ().size () > 0);

		int i = 0;
		while (mCore.getSpaceShips ().size () > 0 && i < 200) { //WAIT UNTIL ARRIVE
			Thread.sleep (10);
			i++;
		}
		Assert.assertFalse ("SpaceShip arrived", i == 200);

		Assert.assertEquals ("Neutral Planet unit number decreased", lastUnitsNumber - sentUnitNumber, planets.get (selectedToPlanetIndex).getUnitsNumber ());
	}

	@Test
	public void testSpaceShipMoveConquers () throws Exception {
		ArrayList <Planet> planets = mCore.getPlanets ();
		int selectedFromPlanetIndex = 0;
		int selectedToPlanetIndex = 1;
		int sentUnitNumber = 5;

		int lastUnitsNumber = planets.get (selectedToPlanetIndex).getUnitsNumber ();

		mCore.startMoveSpaceShip (selectedFromPlanetIndex, selectedToPlanetIndex, 0, sentUnitNumber);

		Assert.assertTrue ("SpaceShip starts moving", mCore.getSpaceShips ().size () > 0);

		int i = 0;
		while (mCore.getSpaceShips ().size () > 0 && i < 200) { //WAIT UNTIL ARRIVE
			Thread.sleep (10);
			i++;
		}
		Assert.assertFalse ("SpaceShip arrived", i == 200);

		Assert.assertEquals ("Neutral Planet conquered", Math.abs (lastUnitsNumber - sentUnitNumber), planets.get (selectedToPlanetIndex).getUnitsNumber ());
	}
}