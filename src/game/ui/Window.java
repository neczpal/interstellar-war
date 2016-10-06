package game.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import game.Loader;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author neczpal
 */
public class Window extends Thread {

	private String name;
	private final int width, height;

	private Panel cPanel;

	public Window (String name, int width, int height) {
		super (name);
		this.name = name;
		this.width = width;
		this.height = height;
		this.cPanel = new Panel ();
	}

	@Override
	public void run () {
		initDisplay ();
		initGL ();


		while (!Display.isCloseRequested ()) {
			glClear (GL_COLOR_BUFFER_BIT);

			mouseEvent ();
			keyboardEvent ();

			draw ();

			Display.sync (80);
			Display.update ();
		}
		clean ();
	}


	private void initDisplay () {
		try {
			Display.setDisplayMode (new DisplayMode (width, height));
			Display.setTitle (name);
			Display.create ();
			Keyboard.create ();
			Mouse.create ();
			Loader.setUseCache (false);

		} catch (LWJGLException ex) {
			Logger.getLogger (Window.class.getName ()).log (Level.SEVERE, null, ex);
		}
	}

	private void initGL () {
		glMatrixMode (GL_PROJECTION);
		glLoadIdentity ();
		glOrtho (0, Display.getWidth (), 0, Display.getHeight (), -1, 1);
		glMatrixMode (GL_MODELVIEW);

		glClearColor (0, 0, 0, 1);

		glDisable (GL_DEPTH_TEST);
	}

	//    private double fok = 0.0;
	private void draw () {
		//        doboz.tukroz(tukor).draw();
		//        doboz.draw();
		//        c.draw();
		//        c.tukroz(tukor).draw();
		//        c.tukroz(tukor2).draw();
		//        c.tukroz(tukor).tukroz(tukor2).draw();
		//        c.tukroz(tukor2).tukroz(tukor).draw();
		cPanel.draw ();
		//        Line l1 = line.visszLine(tukor);
		//        if(l1 != null){
		////            Line l2 = l1.visszLine(tukor2);
		////            if(l2 != null){
		////                l2.draw();
		////            }
		//            l1.draw();
		//        }
		////
		//        glColor3f(1f, 1f, 1f);
		//        Circle c = new Circle(250, 250, 200);
		//        Circle c2 = new Circle(420,320, 50);
		//        Line l = new Line(p, c.metszet(c2)[1]);
		//        Line l2 = new Line(p, c.metszet(c2)[0]);
		//        c.draw();
		//        c2.draw();
		//        glColor3f(1f, 0f, 0f);
		//        l.draw();
		//        l2.draw();

		//        t.draw();
		//        if(c.isInside(p))
		//            glColor3f(1f, 0f, 0f);
		//        else
		//            glColor3f(1f, 1f, 1f);
		//        poly.draw();
		//          t.draw();
		//          glColor3f(1f, 0f, 0f);
		//          System.mOut.println(t.getTerulet() + ": " + t.getKerulet() + ": " + t.getInnerCircle().getCenter());
		//          t.getInnerCircle().draw();
		//          glColor3f(1f, 1f, 1f);
		//        t.draw();
		//        poly.draw();
		//        Triangle te = t.forgat(p,fok);
		//        te.draw();
		//        Line l3 = new Line (te.getSulyPoint(), te.getSulyPoint().tukroz(e));
		//        l3.draw();
		//        Line l4 = new Line (t.getA(), l3.toEgyenes().metszet(e));
		//        l4.draw();
		//        te.tukroz(e).tukroz(l4.toEgyenes()).draw();
		//        te.tukroz(e).draw();
		//        t.tukroz(e).draw();
		//        doboz.tukroz(p).draw();
		//        doboz.draw();
		//        line.draw();
		//        tukor.draw();
		//        tukor2.draw();
		//          Math.a
	}

	//    private boolean asd = false;
	private void mouseEvent () {
		if (Mouse.isInsideWindow ()) {
			////            c.setK(Mouse.getX(), Mouse.getY());
			////            doboz.setPosition(Mouse.getX(), Mouse.getY());
			////        }
			////            if(asd)
			////                line.setB(Mouse.getX(), Mouse.getY());
			////            else
			////                line.setA(Mouse.getX(), Mouse.getY());
			//            p.setPosition(Mouse.getX(), Mouse.getY());
		}
		//        if(Mouse.isButtonDown(0))
		//            asd = !asd;
		cPanel.mouseEvent ();
	}

	private void keyboardEvent () {
		cPanel.keyboardEvent ();
		//        if(Keyboard.isKeyDown(Keyboard.KEY_R)){
		//            poly = Polygon.randomize(640d, 480d, 3);
		//        }
		//            fok += 0.4;
		////            line = new Line((int)(Math.random()*width),(int)(Math.random()*height),(int)(Math.random()*width),(int)(Math.random()*height));
		////            tukor = new Line ((int)(Math.random()*width),(int)(Math.random()*height),(int)(Math.random()*width),(int)(Math.random()*height));
		////            tukor2 = new Line ((int)(Math.random()*width),(int)(Math.random()*height),(int)(Math.random()*width),(int)(Math.random()*height));
		//        }
		//        if(Keyboard.isKeyDown(Keyboard.KEY_E)){
		//            fok -= 0.05;
		//        }
	}

	private void clean () {
		cPanel.mConnection.close ();
		Display.destroy ();
		Keyboard.destroy ();
		Mouse.destroy ();
	}


	public static void main (String[] args) {
		new Window ("Interstellar War", 640, 480).start ();
	}

}
