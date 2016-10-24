package game.ui;

import game.connection.ClientConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by neczp on 2016. 10. 24..
 */
public class LoginFrame extends JFrame implements ActionListener {

	private ClientConnection mConnection;

	private JTextField userNameTextField;
	private JTextField ipAddressTextField;
	private JButton loginButton;

	public LoginFrame () {
		super ("Login");

		setLayout (new FlowLayout ());
		setSize (250, 300);
		setResizable (false);
		setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
		setLocationByPlatform (true);

		JLabel userNameLabel = new JLabel ("USERNAME:");
		userNameTextField = new JTextField ("neczpal", 20);// #TODO remember last one
		JLabel ipAddressLabel = new JLabel ("IP-ADDRESS:");
		ipAddressTextField = new JTextField ("localhost", 20);

		loginButton = new JButton ("Login");
		loginButton.addActionListener (this);
		getRootPane ().setDefaultButton (loginButton);

		add (userNameLabel);
		add (userNameTextField);
		add (ipAddressLabel);
		add (ipAddressTextField);
		add (loginButton);

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
}
