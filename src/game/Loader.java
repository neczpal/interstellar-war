package game;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Properties;

import static org.lwjgl.opengl.GL11.*;

public final class Loader {

	private static String mRootDirectory = new File ("").getAbsolutePath ();
	private static HashMap <String, Integer> mTextureCache = new HashMap <> ();
	private static boolean useCache = false;

	private Loader () {
	}

	public static void setRootDirectory (String rootDirectory) {
		mRootDirectory = rootDirectory;
	}

	public static void setUseCache (boolean useCache) {
		Loader.useCache = useCache;
	}

	public static int[] loadTextures (String[] names) {
		int[] textures = new int[names.length];
		for (int i = 0; i < names.length; i++) {
			textures[i] = loadTexture (names[i]);
		}
		return textures;
	}

	public static int[] loadTextures (BufferedImage[] imgs) {
		int[] textures = new int[imgs.length];
		for (int i = 0; i < imgs.length; i++) {
			textures[i] = loadTexture (imgs[i]);
		}
		return textures;
	}

	public static int[] loadTextures (String name, int width, int height) {
		return loadTextures (loadImage (name), width, height);
	}

	public static int[] loadTextures (BufferedImage img, int width, int height) {
		if (img.getWidth () % width > 0 || img.getHeight () % height > 0) {
			System.out.println ("Nem sikerült feldarabolni a képet!");
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

	public static int loadTexture (String name) {
		Integer textureId;
		if ((textureId = mTextureCache.get (name)) != null) {
			return textureId;
		}
		return loadTexture (loadImage (name), name);
	}

	public static int loadTexture (BufferedImage image) {
		if (image == null) {
			return -1;
		}
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

	private static int loadTexture (BufferedImage image, String name) {
		int textureID = loadTexture (image);
		if (useCache) {
			mTextureCache.put (name, textureID);
		}
		return textureID;
	}

	public static BufferedImage loadImage (String filepath) {
		try {
			return ImageIO.read (getInputStream (filepath));
		} catch (IOException ex) {
			return null;
		}
	}

	public static Properties loadProperties (String propFile) {
		Properties p = new Properties ();
		try {
			p.load (getInputStream (propFile));
			return p;
		} catch (IOException ex) {
			return p;
		}
	}

	public static InputStream getInputStream (String filename) throws FileNotFoundException {
		return new FileInputStream (mRootDirectory + "/" + filename);
	}

}
