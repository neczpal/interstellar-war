package game;

/**
 * Created by neczp on 2016. 10. 17..
 */
public class Textures {

	public static void loadTextures () {
		InterstellarWar.planet = new int[9];
		InterstellarWar.planet[0] = Loader.loadTexture ("res/interstellarwar/planet1.png");
		InterstellarWar.planet[1] = Loader.loadTexture ("res/interstellarwar/planet2.png");
		InterstellarWar.planet[2] = Loader.loadTexture ("res/interstellarwar/planet3.png");
		InterstellarWar.planet[3] = Loader.loadTexture ("res/interstellarwar/planet4.png");
		InterstellarWar.planet[4] = Loader.loadTexture ("res/interstellarwar/planet5.png");
		InterstellarWar.planet[5] = Loader.loadTexture ("res/interstellarwar/planet6.png");
		InterstellarWar.planet[6] = Loader.loadTexture ("res/interstellarwar/planet7.png");
		InterstellarWar.planet[7] = Loader.loadTexture ("res/interstellarwar/planet8.png");
		InterstellarWar.planet[8] = Loader.loadTexture ("res/interstellarwar/planet9.png");

		RockPaperScissors.rock = Loader.loadTexture ("res/rockpaperscissors/rock.png");
		RockPaperScissors.paper = Loader.loadTexture ("res/rockpaperscissors/paper.png");
		RockPaperScissors.scissors = Loader.loadTexture ("res/rockpaperscissors/scissors.png");
	}

	public static class InterstellarWar {
		public static int[] planet;
	}

	public static class RockPaperScissors {
		public static int rock;
		public static int paper;
		public static int scissors;
	}
}
