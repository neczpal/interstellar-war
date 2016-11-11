package net.neczpal.interstellarwar.desktop;

import net.neczpal.interstellarwar.common.game.InterstellarWarCore;
import net.neczpal.interstellarwar.common.game.Planet;
import net.neczpal.interstellarwar.common.game.SpaceShip;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Textures {

	public static int[] characters;

	public static void loadTextures () throws IOException {
		//GENERAL
		characters = Loader.loadTextures ("res/textures/font64.png", 64, 64);

		//INTERSTELLAR WAR
		InterstellarWar.planet = new int[Planet.PLANET_TYPES];
		for (int i = 0; i < InterstellarWar.planet.length; i++) {
			InterstellarWar.planet[i] = Loader.loadTexture ("res/textures/planet" + (i + 1) + ".png");
		}
		InterstellarWar.spaceship = new int[SpaceShip.SPACESHIP_TYPES];
		InterstellarWar.spaceshipDimens = new float[SpaceShip.SPACESHIP_TYPES][2];
		for (int i = 0; i < InterstellarWar.spaceship.length; i++) {
			InterstellarWar.spaceship[i] = Loader.loadTexture ("res/textures/spaceship" + (i + 1) + ".png");
			BufferedImage image = ImageIO.read (new File ("res/textures/spaceship" + (i + 1) + ".png"));
			InterstellarWar.spaceshipDimens[i][0] = image.getWidth () / 4.0f;
			InterstellarWar.spaceshipDimens[i][1] = image.getHeight () / 4.0f;
		}
		InterstellarWar.background = new int[InterstellarWarCore.BACKGROUND_TYPES];
		for (int i = 0; i < InterstellarWar.background.length; i++) {
			InterstellarWar.background[i] = Loader.loadTexture ("res/textures/background" + (i + 1) + ".png");
		}
	}

	public static class InterstellarWar {
		public static int[] planet;
		public static int[] spaceship;
		public static float[][] spaceshipDimens;
		public static int[] background;
	}
}
