package net.neczpal.interstellarwar.android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import net.neczpal.interstellarwar.clientcommon.ClientConnection;
import net.neczpal.interstellarwar.clientcommon.UserInterface;
import net.neczpal.interstellarwar.common.connection.RoomData;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class LoginActivity extends Activity implements UserInterface {

	public static ClientConnection mConnection;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_login);
		final EditText usernameEditText = (EditText) findViewById (R.id.usernameInputId);
		final EditText addressEditText = (EditText) findViewById (R.id.addressInputId);

		Button loginButton = (Button) findViewById (R.id.loginButtonId);
		loginButton.setOnClickListener (new View.OnClickListener () {
			@Override
			public void onClick (View v) {

				AsyncTask.execute (new Runnable () {
					@Override
					public void run () {
						try {
							mConnection = new ClientConnection (addressEditText.getText ().toString (), usernameEditText.getText ().toString ());
							mConnection.setUserInterface (LoginActivity.this);
						} catch (IOException e) {
							e.printStackTrace ();
						}
					}
				});

			}
		});

	}

	@Override
	public void connectionReady () {
		Intent intent = new Intent (this, LoungeActivity.class);
		startActivity (intent);
	}

	@Override
	public void connectionDropped () {
	}

	@Override
	public void listRooms (JSONArray arrayList) {

	}

	@Override
	public void setIsInRoom (boolean b) {
	}

	@Override
	public void startGame (String s) {
	}

	@Override
	public void stopGame () {
	}

	@Override
	public void receiveMessage(int uid, String uname, int roomIndex, String message) {

	}

	@Override
	public void onPointerCaptureChanged(boolean hasCapture) {

	}
}
