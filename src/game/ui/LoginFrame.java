package game.ui;

import game.connection.ClientConnection;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by neczp on 2016. 10. 24..
 */
public class LoginFrame extends JFrame implements ActionListener {

	private ClientConnection mConnection;

	private JTextField userNameTextField;
	private JTextField ipAddressTextField;
	private JButton loginButton;
	private JComboBox <String> availableResolutionsComboBox;

	public LoginFrame () {
		super ("Login");
		try {
			setIconImage (ImageIO.read (new File ("res/rockpaperscissors/rock.png")));// #TODO useful icon
		} catch (IOException e) {
			e.printStackTrace ();
		}
		setSize (290, 320);
		setResizable (false);
		setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
		setLocationByPlatform (true);

		JPanel panel = new JPanel ();
		panel.setLayout (new GridLayout (8, 1));
		panel.setBorder (BorderFactory.createEmptyBorder (10, 10, 10, 10));

		JLabel userNameLabel = new JLabel ("USERNAME:");
		userNameTextField = new JTextField ("neczpal", 20);// #TODO remember last one

		JLabel ipAddressLabel = new JLabel ("IP-ADDRESS:");
		ipAddressTextField = new JTextField ("localhost", 20);

		panel.add (userNameLabel);
		panel.add (userNameTextField);
		panel.add (ipAddressLabel);
		panel.add (ipAddressTextField);

		try {
			JLabel resolutionLabel = new JLabel ("RESOLUTION:"); //#TODO in settings?
			DisplayMode[] availableDisplayModes = Display.getAvailableDisplayModes ();
			String[] availableResolutions = new String[availableDisplayModes.length];
			for (int i = 0; i < availableDisplayModes.length; i++) {
				availableResolutions[i] = availableDisplayModes[i].getWidth () + "x" + availableDisplayModes[i].getHeight ();
			}
			availableResolutionsComboBox = new JComboBox <> (availableResolutions);
			availableResolutionsComboBox.setSelectedIndex (10);//# TODO ez igy nem lesz jo

			add (resolutionLabel);
			add (availableResolutionsComboBox);
		} catch (LWJGLException e) {
			e.printStackTrace ();
		}


		loginButton = new JButton ("Login");
		loginButton.addActionListener (this);
		getRootPane ().setDefaultButton (loginButton);

		JLabel gap = new JLabel ("");
		panel.add (gap);

		panel.add (loginButton);

		add (panel);
		setVisible (true);
	}

	public static void main (String args[]) {
		LoginFrame loginFrame = new LoginFrame ();
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		mConnection = new ClientConnection (ipAddressTextField.getText (), userNameTextField.getText ());
		mConnection.setLoginFrame (this);
	}

	public void openRoomsFrame () {
		dispose ();
		RoomsFrame roomsFrame = new RoomsFrame (mConnection);
		mConnection.setRoomsFrame (roomsFrame);
	}

	public int getSelectedDisplayModeIndex () {
		return availableResolutionsComboBox.getSelectedIndex ();
	}
}
