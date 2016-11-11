package net.neczpal.interstellarwar.android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import net.neczpal.interstellarwar.clientcommon.UserInterface;
import net.neczpal.interstellarwar.common.connection.RoomData;

import java.util.ArrayList;

public class LoungeActivity extends Activity implements UserInterface {

	ListView mRoomsListView;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_lounge);
		LoginActivity.mConnection.setUserInterface (this);
		mRoomsListView = (ListView) findViewById (R.id.room_list);
		mRoomsListView.setOnItemClickListener (new AdapterView.OnItemClickListener () {
			@Override
			public void onItemClick (AdapterView <?> parent, View view, int position, long id) {
				final RoomData roomData = (RoomData) parent.getAdapter ().getItem (position);
				AsyncTask.execute (new Runnable () {
					@Override
					public void run () {
						LoginActivity.mConnection.enterRoom (roomData.getRoomId ());
					}
				});
			}
		});
	}

	@Override
	public void onBackPressed () {
		super.onBackPressed ();
		LoginActivity.mConnection = null;
	}

	// UI interface
	@Override
	public void connectionReady () {
	}

	@Override
	public void connectionDropped () {
		finish ();
		LoginActivity.mConnection = null;
	}

	@Override
	public void listRooms (final ArrayList <RoomData> arrayList) {
		runOnUiThread (new Runnable () {
			@Override
			public void run () {
				mRoomsListView.setAdapter (new RoomDataArrayAdapter (LoungeActivity.this, R.layout.view_roomlist_element, arrayList));
			}
		});
	}

	@Override
	public void setIsInRoom (boolean b) {

	}

	@Override
	public void startGame (String mapName) {
		Intent intent = new Intent (this, InterstellarWarActivity.class);
		intent.putExtra ("MAP_NAME", mapName);
		startActivity (intent);
	}

	@Override
	public void stopGame () {

	}
}
