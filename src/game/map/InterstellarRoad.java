package game.map;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by neczp on 2016. 10. 05..
 */
public class InterstellarRoad {
	private Planet from;
	private Planet to;
	//#TODO TYPE SLOW, FAST, DANGEROUS, NORMAL
	//also color

	public InterstellarRoad (Planet from, Planet to) {
		this.from = from;
		this.to = to;
	}

	public void draw () {
		glBegin (GL_LINES);
		glVertex3f (from.getX (), from.getY (), from.getZ ());
		glVertex3f (to.getX (), to.getY (), to.getZ ());
		glEnd ();
	}
}
