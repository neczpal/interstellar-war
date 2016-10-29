package game.ui;

import game.Loader;
import game.connection.UserConnection;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class LoginFrame extends JFrame implements ActionListener {

	private UserConnection mConnection;

	private JTextField userNameTextField;
	private JTextField ipAddressTextField;
	private JButton loginButton;
	private JComboBox <String> availableResolutionsComboBox;

	private Properties mProperties;

	public LoginFrame () {
		super ("Login");
		mProperties = Loader.loadProperties ("res/config");

		try {
			setIconImage (ImageIO.read (new File ("res/interstellarwar/planet5.png")));// #TODO useful icon
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
		userNameTextField = new JTextField (mProperties.getProperty ("username", "Player"), 20);

		JLabel ipAddressLabel = new JLabel ("IP-ADDRESS:");
		ipAddressTextField = new JTextField (mProperties.getProperty ("ip_address", "152.66.180.66"), 20);

		panel.add (userNameLabel);
		panel.add (userNameTextField);
		panel.add (ipAddressLabel);
		panel.add (ipAddressTextField);

		try {
			JLabel resolutionLabel = new JLabel ("RESOLUTION:");
			DisplayMode[] availableDisplayModes = Display.getAvailableDisplayModes ();
			String[] availableResolutions = new String[availableDisplayModes.length];
			for (int i = 0; i < availableDisplayModes.length; i++) {
				availableResolutions[i] = availableDisplayModes[i].getWidth () + "x" + availableDisplayModes[i].getHeight ();
			}
			availableResolutionsComboBox = new JComboBox <> (availableResolutions);
			availableResolutionsComboBox.setSelectedIndex (Integer.parseInt (mProperties.getProperty ("resolution", "0")));

			panel.add (resolutionLabel);
			panel.add (availableResolutionsComboBox);
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

	}

	public static void main (String args[]) {
		LoginFrame loginFrame = new LoginFrame ();
		loginFrame.setVisible (true);
	}

	@Override
	public void actionPerformed (ActionEvent e) {
		if (ipAddressTextField.getText ().isEmpty ()) {
			JOptionPane.showMessageDialog (this, "Please enter an IP-Address", "IP-Address missing!", JOptionPane.WARNING_MESSAGE);
		} else if (userNameTextField.getText ().isEmpty ()) {
			JOptionPane.showMessageDialog (this, "Please enter a username", "Username missing!", JOptionPane.WARNING_MESSAGE);
		} else {
			try {
				mConnection = new UserConnection (ipAddressTextField.getText (), userNameTextField.getText ());
				mConnection.setLoginFrame (this);

				try {
					mProperties.setProperty ("username", userNameTextField.getText ());
					mProperties.setProperty ("ip_address", ipAddressTextField.getText ());
					mProperties.setProperty ("resolution", Integer.toString (availableResolutionsComboBox.getSelectedIndex ()));
					mProperties.store (new FileOutputStream ("res/config"), null);
				} catch (IOException prop_ex) {
					prop_ex.printStackTrace ();
				}

			} catch (IOException ex) {
				JOptionPane.showMessageDialog (this, "Cannot connect to the server ( " + ipAddressTextField.getText () + ")", "Connection Error!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void openRoomsFrame () {
		dispose ();
		RoomsFrame roomsFrame = new RoomsFrame (mConnection);
		roomsFrame.setVisible (true);
		mConnection.setRoomsFrame (roomsFrame);
	}

	public int getSelectedDisplayModeIndex () {
		return availableResolutionsComboBox.getSelectedIndex ();
	}
}
