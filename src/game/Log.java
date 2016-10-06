package game;

public class Log {
	public static void e (Object msg) {
		System.err.println ("ERROR: " + msg);
	}

	public static void i (Object msg) {
		System.out.println ("INFO: " + msg);


	}
	public static void w (Object msg) {
		System.out.println ("WARNING: " + msg);
	}
}
