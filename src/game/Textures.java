package game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Textures {

	public static int[] characters;

	public static void loadTextures () throws IOException {
		//GENERAL
		characters = Loader.loadTextures ("res/font64.png", 64, 64);

		//INTERSTELLAR WAR
		InterstellarWar.planet = new int[18];
		for (int i = 0; i < InterstellarWar.planet.length; i++) {
			InterstellarWar.planet[i] = Loader.loadTexture ("res/interstellarwar/planet" + (i + 1) + ".png");
		}
		InterstellarWar.spaceship = new int[11];
		InterstellarWar.spaceshipDimens = new double[11][2];
		for (int i = 0; i < InterstellarWar.spaceship.length; i++) {
			InterstellarWar.spaceship[i] = Loader.loadTexture ("res/interstellarwar/spaceship" + (i + 1) + ".png");
			BufferedImage image = ImageIO.read (new File ("res/interstellarwar/spaceship" + (i + 1) + ".png"));
			InterstellarWar.spaceshipDimens[i][0] = image.getWidth () / 4.0;
			InterstellarWar.spaceshipDimens[i][1] = image.getHeight () / 4.0;
		}
		InterstellarWar.background = Loader.loadTexture ("res/interstellarwar/background.png");
	}

	public static class InterstellarWar {
		public static int[] planet;
		public static int[] spaceship;
		public static double[][] spaceshipDimens;
		public static int background;
	}
}
