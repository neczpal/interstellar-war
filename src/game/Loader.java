package game;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.WaveData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Properties;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author Neczpál Ábel
 */
public final class Loader {

	private static String mRootFile = System.getProperty ("user.dir");
	private static Textures textureCache = new Textures ();
	private static Sounds soundCache = new Sounds ();
	private static boolean useCache = true;
	private Loader () {
	}

	/**
	 * Beállitja honnan induljon az elérési út
	 *
	 * @param rootFile Gyökér konyvtar
	 */
	public static void setRootClass (String rootFile) {
		mRootFile = rootFile;
	}

	/**
	 * Beállitja hogy használja-e az osztály a gyorsitotárat
	 *
	 * @param useCache default:true
	 */
	public static void setUseCache (boolean useCache) {
		Loader.useCache = useCache;
	}

	/**
	 * Betölti a texturákat
	 *
	 * @param names A kép helye relativ vagy abszolut elérési hely
	 * @return Texturák tömbje
	 */
	public static int[] loadTextures (String[] names) {
		int[] textures = new int[names.length];
		for (int i = 0; i < names.length; i++) {
			textures[i] = loadTexture (names[i]);
		}
		return textures;
	}

	/**
	 * Betölti a texturákat
	 *
	 * @param imgs Képek
	 * @return Texturák tömbje
	 */
	public static int[] loadTextures (BufferedImage[] imgs) {
		int[] textures = new int[imgs.length];
		for (int i = 0; i < imgs.length; i++) {
			textures[i] = loadTexture (imgs[i]);
		}
		return textures;
	}

	/**
	 * Betölti a texturákat. Felszeleteli width *height -os képekre
	 *
	 * @param names  A kép helye relativ vagy abszolut elérési hely
	 * @param width  Szeletelt képek széllesége
	 * @param height Szeletelt képek magasága
	 * @return Texturák tömbje
	 */
	public static int[] loadTextures (String names, int width, int height) {
		return loadTextures (loadImage (names), width, height);
	}

	/**
	 * Betölti a texturákat. Felszeleteli width *height -os képekre
	 *
	 * @param img    A kép
	 * @param width  Szeletelt képek széllesége
	 * @param height Szeletelt képek magasága
	 * @return Texturák tömbje
	 */
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

	/**
	 * Betölti a texturát a képböl
	 *
	 * @param name A kép helye relativ vagy abszolut elérési hely
	 * @return textura idje
	 */
	public static int loadTexture (String name) {
		if (name != null && textureCache.contains (name)) {
			return textureCache.getTexture (name);
		}
		return loadTexture (loadImage (name), name);
	}

	/**
	 * Betölti a texturát a képböl
	 *
	 * @param image A kép
	 * @return textura idje
	 */
	public static int loadTexture (BufferedImage image) {
		if (image == null) {
			return -1;
		}
		//Setup wrap mode
		glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		//Setup texture scaling filtering
		glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		int[] pixels = new int[image.getWidth () * image.getHeight ()];
		image.getRGB (0, 0, image.getWidth (), image.getHeight (), pixels, 0, image.getWidth ());

		ByteBuffer buffer = BufferUtils.createByteBuffer (image.getWidth () * image.getHeight () * 4); //4 for RGBA, 3 for RGB

		for (int y = 0; y < image.getHeight (); y++) {
			for (int x = 0; x < image.getWidth (); x++) {
				int pixel = pixels[y * image.getWidth () + x];
				buffer.put ((byte) ((pixel >> 16) & 0xFF));  // Red
				buffer.put ((byte) ((pixel >> 8) & 0xFF));   // Green
				buffer.put ((byte) (pixel & 0xFF));          // Blue
				buffer.put ((byte) ((pixel >> 24) & 0xFF));  // Alpha
			}
		}

		buffer.flip (); //FOR THE LOVE OF GOD DO NOT FORGET THIS

		// You now have a ByteBuffer filled with the color data of each pixel.
		// Now just create a texture ID and bind it. Then you can load it using
		// whatever OpenGL method you want, for example:
		glTexImage2D (GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth (), image.getHeight (), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		int textureID = glGenTextures (); //Generate texture ID
		glBindTexture (GL_TEXTURE_2D, textureID); //Bind texture ID


		//Send texel data to OpenGL
		glTexImage2D (GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth (), image.getHeight (), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		//Return the texture ID so we can bind it later again
		return textureID - 1;

	}

	/**
	 * Betölti a texturát a képböl + gyorsitotárazza.
	 *
	 * @param image kép
	 * @param name  kép neve
	 * @return
	 */
	public static int loadTexture (BufferedImage image, String name) {
		if (image == null) {
			return -1;
		}
		//Setup wrap mode
		glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		//Setup texture scaling filtering
		glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		int[] pixels = new int[image.getWidth () * image.getHeight ()];
		image.getRGB (0, 0, image.getWidth (), image.getHeight (), pixels, 0, image.getWidth ());

		ByteBuffer buffer = BufferUtils.createByteBuffer (image.getWidth () * image.getHeight () * 4); //4 for RGBA, 3 for RGB

		for (int y = 0; y < image.getHeight (); y++) {
			for (int x = 0; x < image.getWidth (); x++) {
				int pixel = pixels[y * image.getWidth () + x];
				buffer.put ((byte) ((pixel >> 16) & 0xFF));  // Red
				buffer.put ((byte) ((pixel >> 8) & 0xFF));   // Green
				buffer.put ((byte) (pixel & 0xFF));          // Blue
				buffer.put ((byte) ((pixel >> 24) & 0xFF));  // Alpha
			}
		}

		buffer.flip (); //FOR THE LOVE OF GOD DO NOT FORGET THIS

		// You now have a ByteBuffer filled with the color data of each pixel.
		// Now just create a texture ID and bind it. Then you can load it using
		// whatever OpenGL method you want, for example:
		glTexImage2D (GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth (), image.getHeight (), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		int textureID = glGenTextures (); //Generate texture ID
		glBindTexture (GL_TEXTURE_2D, textureID); //Bind texture ID


		//Send texel data to OpenGL
		glTexImage2D (GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth (), image.getHeight (), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		if (useCache) {
			textureCache.add (name, textureID - 1);
		}
		//Return the texture ID so we can bind it later again
		return textureID - 1;

	}

	/**
	 * Betölti a képet egy fileból
	 *
	 * @param filepath a file elérési utja relativ vagy abszolut
	 * @return Képet
	 */
	public static BufferedImage loadImage (String filepath) {
		try {
			return ImageIO.read (getInputStream (filepath));
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * Betölti a hang source-t egy fileból
	 *
	 * @param name a Sound helye relativ vagy abszolut elérési hely
	 * @return sound source id
	 */
	public static int loadSoundSource (String name) {
		if (name != null && soundCache.contains (name)) {
			return soundCache.getSoundSource (name);
		}
		return loadSoundSource (loadSound (name), name);
	}

	/**
	 * Betölti a hang source-t egy fileból
	 *
	 * @param sound a Sound
	 * @return sound source id
	 */
	public static int loadSoundSource (WaveData sound) {
		int buffer = alGenBuffers ();
		alBufferData (buffer, sound.format, sound.data, sound.samplerate);
		int source = alGenSources ();
		alSourcei (source, AL_BUFFER, buffer);

		return source;
	}

	/**
	 * Betölti a hang source-t egy fileból + gyorsitotárazza.
	 *
	 * @param sound A Sound
	 * @param name  a Sound neve
	 * @return sound source id
	 */
	public static int loadSoundSource (WaveData sound, String name) {

		if (sound == null) {
			return -1;
		}

		int buffer = alGenBuffers ();
		alBufferData (buffer, sound.format, sound.data, sound.samplerate);
		int source = alGenSources ();
		alSourcei (source, AL_BUFFER, buffer);
		if (useCache) {
			soundCache.add (name, source);
		}
		return source;
	}

	/**
	 * Betölti a hangot egy fileból
	 *
	 * @param sound a Sound helye relativ vagy abszolut elérési hely
	 * @return Sound
	 */
	public static WaveData loadSound (String sound) {
		return WaveData.create (new BufferedInputStream (getInputStream (sound)));
	}

	public static Properties loadProperties (String propFile) {
		try {
			Properties p = new Properties ();
			p.load (getInputStream (propFile));
			return p;
		} catch (IOException ex) {
			return null;
		}
	}

	/**
	 * Megkapja az inputstream streamet egy filehoz.
	 *
	 * @param filename A file relativ elérési helye
	 * @return
	 */
	public static InputStream getInputStream (String filename) {

		try {
			return new FileInputStream (mRootFile + "/" + filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace ();
			return null;
		}


		//		InputStream in = null;
		//		try {
		//			in = new FileInputStream (filename);
		//		} catch (Exception ex) {
		//			if (in != null) {
		//				try {
		//					in.close ();
		//				} catch (Exception kiv) {
		//				}
		//				in = null;
		//			}
		//		}
		//
		//		if (in == null) {
		//			try {
		//				in = new FileInputStream (new File (rootClass.getResource (filename).toURI ()));
		//			} catch (Exception ex) {
		//				if (in != null) {
		//					try {
		//						in.close ();
		//					} catch (Exception kiv) {
		//					}
		//
		//					in = null;
		//				}
		//			}
		//		}
		//		return in;
	}

	private static class Textures {

		private ArrayList <String> names;
		private ArrayList <Integer> textureIDs;

		Textures () {
			names = new ArrayList <> ();
			textureIDs = new ArrayList <> ();
		}

		void add (String s, Integer i) {
			names.add (s);
			textureIDs.add (i);
		}

		boolean contains (String s) {
			for (int i = 0; i < names.size (); i++) {
				if (names.get (i).equals (s)) {
					return true;
				}
			}
			return false;
		}

		int getTexture (String s) {
			return textureIDs.get (names.indexOf (s));
		}
	}

	private static class Sounds {
		private ArrayList <String> names;
		private ArrayList <Integer> soundSources;

		Sounds () {
			names = new ArrayList <> ();
			soundSources = new ArrayList <> ();
		}

		void add (String s, Integer i) {
			names.add (s);
			soundSources.add (i);
		}

		boolean contains (String s) {
			for (int i = 0; i < names.size (); i++) {
				if (names.get (i).equals (s)) {
					return true;
				}
			}
			return false;
		}

		int getSoundSource (String s) {
			return soundSources.get (names.indexOf (s));
		}
	}

}
