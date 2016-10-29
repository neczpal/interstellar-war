package game;

/**
 * Created by neczp on 2016. 10. 17..
 */
public class Textures {

	public static void loadTextures () {
		InterstellarWar.planet = new int[18];
		for (int i = 0; i < InterstellarWar.planet.length; i++) {
			InterstellarWar.planet[i] = Loader.loadTexture ("res/interstellarwar/planet" + (i + 1) + ".png");
		}
		InterstellarWar.background = Loader.loadTexture ("res/interstellarwar/background.png");
	}

	public static class InterstellarWar {
		public static int[] planet;
		public static int background;
	}
}
