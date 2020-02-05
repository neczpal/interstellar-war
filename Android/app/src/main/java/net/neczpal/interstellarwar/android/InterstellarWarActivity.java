package net.neczpal.interstellarwar.android;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import net.neczpal.interstellarwar.clientcommon.UserInterface;
import net.neczpal.interstellarwar.common.connection.RoomData;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class InterstellarWarActivity extends Activity implements UserInterface, Runnable {

	private InterstellarWarView mInterstellarWarView;
	private Thread drawThread = new Thread (this);
	private boolean isRunning = false;


	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		mInterstellarWarView = new InterstellarWarView (this, LoginActivity.mConnection.getGameClient ());
		setContentView (mInterstellarWarView);
		Intent intent = getIntent ();
		ActionBar actionBar = getActionBar ();
		if (actionBar != null)
			actionBar.setTitle (intent.getStringExtra ("MAP_NAME"));
		LoginActivity.mConnection.setUserInterface (this);
		drawThread.start ();
	}

	@Override
	protected void onStop () {
		super.onStop ();
		isRunning = false;
	}

	@Override
	public void onBackPressed () {
		super.onBackPressed ();
		try {
			LoginActivity.mConnection.leaveRoom ();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connectionReady () {
	}

	@Override
	public void connectionDropped () {
		finish ();
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
		finish ();
	}

	@Override
	public void run () {
		isRunning = true;
		while (isRunning) {
			try {
				runOnUiThread (new Runnable () {
					@Override
					public void run () {
						mInterstellarWarView.invalidate ();
					}
				});
				Thread.sleep (50);
			} catch (InterruptedException e) {
				e.printStackTrace ();
			}
		}
	}
}
