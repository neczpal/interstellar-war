package game.map;

import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by neczp on 2016. 10. 05..
 */
public class Planet {

	private static final int STACKS = 16, SLICES = 16;

	private float mRadius;
	private int mIndex;
	private ArrayList <InterstellarRoad> mNeighbors;
	private Vector3f mPosition;
	private Vector3f mColor;

	public Planet (int mIndex) {
		this.mIndex = mIndex;
		mPosition = new Vector3f (1f, 1f, 1f);
		mColor = new Vector3f (1f, 0f, 0f);
	}

	public float getX () {
		return mPosition.x;
	}
	public float getY () {
		return mPosition.y;
	}
	public float getZ () {
		return mPosition.z;
	}
	public void draw () {
		glPushMatrix ();
		glColor3f (mColor.x, mColor.y, mColor.z);
		glTranslatef (mPosition.x, mPosition.y, mPosition.z);
		Sphere s = new Sphere ();
		s.draw (mRadius, STACKS, SLICES);
		glPopMatrix ();
	}
}
