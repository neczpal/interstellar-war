package net.neczpal.interstellarwar.android;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import net.neczpal.interstellarwar.common.connection.RoomData;

import java.util.ArrayList;

/**
 * Created by neczp on 2016. 11. 06..
 */

public class RoomDataArrayAdapter extends ArrayAdapter <RoomData> {

	private Context mContext;
	private int mResourceId;
	private ArrayList <RoomData> mRoomDatas;

	public RoomDataArrayAdapter (Context context, int resource, ArrayList <RoomData> roomDatas) {
		super (context, resource, roomDatas);
		mContext = context;
		mResourceId = resource;
		mRoomDatas = roomDatas;
	}

	@NonNull
	@Override
	public View getView (int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater ();
			convertView = inflater.inflate (mResourceId, parent, false);
		}

		RoomData roomData = mRoomDatas.get (position);
		TextView mapNameTextView = (TextView) convertView.findViewById (R.id.mapNameId);
		mapNameTextView.setText (roomData.getMapName ());
		TextView userCountTextView = (TextView) convertView.findViewById (R.id.userCountId);
		mapNameTextView.setText (roomData.getMapName ());
		userCountTextView.setText (roomData.getUsers ().size () + "/" + roomData.getMaxUserCount ());

		return convertView;
	}
}
