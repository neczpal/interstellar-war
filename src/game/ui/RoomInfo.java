package game.ui;

/**
 * Created by neczp on 2016. 10. 12..
 */
public class RoomInfo {
	int mRoomId;
	String mGameName;
	String mMapName;
	int mUserCount;
	int mMaxUserCount;

	public RoomInfo (int mRoomId, String mGameName, String mMapName, int mUserCount, int mMaxUserCount) {
		this.mRoomId = mRoomId;
		this.mGameName = mGameName;
		this.mMapName = mMapName;
		this.mMaxUserCount = mMaxUserCount;
		this.mUserCount = mUserCount;
	}
}
