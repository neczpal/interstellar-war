package net.neczpal.interstellarwar.desktop;

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

	/**
	 * Kann nicht Instantiieren
	 */
	private Loader () {
	}

	/**
	 * Stellt das Root-Mappe ein
	 *
	 * @param rootDirectory Der Name des Mappes
	 */
	public static void setRootDirectory (String rootDirectory) {
		mRootDirectory = rootDirectory;
	}

	/**
	 * Ladet ein Texture-Map
	 *
	 * @param name   Der Name des Files
	 * @param width  Die Breite der Tile
	 * @param height Die Höhe der Tile
	 * @return OpenGL Indexe der Texturen
	 * @throws IOException falls das File kann nicht geöffnet werden
	 */
	public static int[] loadTextures (String name, int width, int height) throws IOException {
		return loadTextures (loadImage (name), width, height);
	}

	/**
	 * Ladet ein Texture-Map
	 *
	 * @param img    Das Bild
	 * @param width  Die Breite der Tile
	 * @param height Die Höhe der Tile
	 * @return OpenGL Indexe der Texturen
	 */
	public static int[] loadTextures (BufferedImage img, int width, int height) {
		if (img.getWidth () % width > 0 || img.getHeight () % height > 0) {
			System.err.println ("Nem sikerült feldarabolni a képet!");
			return null;
		}
		int texw = img.getWidth () / width;
		int texh = img.getHeight () / height;
		int[] ret = new int[texw * texh];
		for (int i = 0; i < texw; i++) {
			for (int k = 0; k < texh; k++) {
				ret[k * texw + i] = loadTexture (img.getSubimage (i * width, k * height, width, height));
			}
		}
		return ret;
	}

	/**
	 * Ladet ein Texture ein
	 *
	 * @param name Der Name des Files
	 * @return OpenGL Index der Texture
	 * @throws IOException falls das File kann nicht geöffnet werden
	 */
	public static int loadTexture (String name) throws IOException {
		return loadTexture (loadImage (name));
	}

	/**
	 * Ladet ein Texture ein
	 *
	 * @param image Der Bild
	 * @return OpenGL Index der Texture
	 */
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

	/**
	 * Ladet ein Bild von einem File ein
	 *
	 * @param filepath Der File
	 * @return Das Bild
	 * @throws IOException falls das File kann nicht geöffnet werden
	 */
	public static BufferedImage loadImage (String filepath) throws IOException {
		return ImageIO.read (getInputStream (filepath));
	}

	/**
	 * Ladet ein Property File ein
	 *
	 * @param propFile Der Name der File
	 * @return Das Properties Objekt
	 * @throws IOException falls das File kann nicht geöffnet werden
	 */
	public static Properties loadProperties (String propFile) throws IOException {
		Properties p = new Properties ();
		p.load (getInputStream (propFile));
		return p;
	}

	/**
	 * Macht ein InputStrom aus dem Name eines Files
	 *
	 * @param filename Der Name der File
	 * @return Das InputStrom
	 * @throws FileNotFoundException falls das File nicht existiert
	 */
	public static InputStream getInputStream (String filename) throws FileNotFoundException {
		return new FileInputStream (mRootDirectory + "/" + filename);
	}

}
