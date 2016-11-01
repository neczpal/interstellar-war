package game;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Properties;

import static org.lwjgl.opengl.GL11.*;

public final class Loader {

	private static String mRootDirectory = new File ("").getAbsolutePath ();

	private Loader () {
	}

	public static void setRootDirectory (String rootDirectory) {
		mRootDirectory = rootDirectory;
	}

	public static int[] loadTextures (String name, int width, int height) throws IOException {
		return loadTextures (loadImage (name), width, height);
	}

	public static int[] loadTextures (BufferedImage img, int width, int height) {
		if (img.getWidth () % width > 0 || img.getHeight () % height > 0) {
			System.err.println ("Nem sikerült feldarabolni a képet!");
			return null;
		}
		int texw = img.getWidth () / width;
		int texh = img.getHeight () / height;
		int[] r = new int[texw * texh];
		for (int i = 0; i < texw; i++) {
			for (int k = 0; k < texh; k++) {
				r[k * texw + i] = loadTexture (img.getSubimage (i * width, k * height, width, height));
			}
		}
		return r;
	}

	public static int loadTexture (String name) throws IOException {
		return loadTexture (loadImage (name));
	}

	public static int loadTexture (BufferedImage image) {
		glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		int[] pixels = new int[image.getWidth () * image.getHeight ()];
		image.getRGB (0, 0, image.getWidth (), image.getHeight (), pixels, 0, image.getWidth ());

		ByteBuffer buffer = BufferUtils.createByteBuffer (image.getWidth () * image.getHeight () * 4);

		for (int y = 0; y < image.getHeight (); y++) {
			for (int x = 0; x < image.getWidth (); x++) {
				int pixel = pixels[y * image.getWidth () + x];
				buffer.put ((byte) ((pixel >> 16) & 0xFF));
				buffer.put ((byte) ((pixel >> 8) & 0xFF));
				buffer.put ((byte) (pixel & 0xFF));
				buffer.put ((byte) ((pixel >> 24) & 0xFF));
			}
		}

		buffer.flip ();

		glTexImage2D (GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth (), image.getHeight (), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		int textureID = glGenTextures ();
		glBindTexture (GL_TEXTURE_2D, textureID);


		glTexImage2D (GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth (), image.getHeight (), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		return textureID - 1;

	}

	public static BufferedImage loadImage (String filepath) throws IOException {
		return ImageIO.read (getInputStream (filepath));
	}

	public static Properties loadProperties (String propFile) throws IOException {
		Properties p = new Properties ();
		p.load (getInputStream (propFile));
		return p;
	}

	public static InputStream getInputStream (String filename) throws FileNotFoundException {
		return new FileInputStream (mRootDirectory + "/" + filename);
	}

}
