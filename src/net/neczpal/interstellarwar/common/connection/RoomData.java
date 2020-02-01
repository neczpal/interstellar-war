package net.neczpal.interstellarwar.common.connection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoomData implements Serializable {
    static final long serialVersionUID = 42123L;

    private int mRoomId;
    private String mMapName;
    private List<String> mUsers;
    private int mMaxUserCount;
    private volatile boolean mIsRunning;

    /**
     * Erstellt ein Zimmerdatei
     *
     * @param mRoomId       Die ID von Zimmer
     * @param mMapName      Der Name von der Mappe
     * @param mUsers        Die Namen der Benutzer
     * @param mMaxUserCount Die maximum Anzahl der Benutzer
     * @param mIsRunning    Ob dieses Zimmer schon beginnt hat
     */
    public RoomData (int mRoomId, String mMapName, List<String> mUsers, int mMaxUserCount, boolean mIsRunning) {
        this.mRoomId = mRoomId;
        this.mMapName = mMapName;
        this.mUsers = mUsers;
        this.mMaxUserCount = mMaxUserCount;
        this.mIsRunning = mIsRunning;
    }

    public RoomData (JSONObject jsonObject) {
        this.mRoomId = jsonObject.getInt (CommandParamKey.ROOM_ID_KEY);
        this.mMapName = jsonObject.getString (CommandParamKey.MAP_NAME_KEY);
        JSONArray jsonUsers = jsonObject.getJSONArray (CommandParamKey.USER_LIST_KEY);
        this.mUsers = new ArrayList<> ();
        for (int i = 0; i < jsonUsers.length (); i++) {
            mUsers.add (jsonUsers.getString (i));
        }
        this.mMaxUserCount = jsonObject.getInt (CommandParamKey.MAX_USER_COUNT_KEY);
        this.mIsRunning = jsonObject.getBoolean (CommandParamKey.IS_ROOM_RUNNING_KEY);
    }

    //GETTERS

    public int getRoomId () {
        return mRoomId;
    }

    public String getMapName () {
        return mMapName;
    }

    public List<String> getUsers () {
        return mUsers;
    }

    public int getMaxUserCount () {
        return mMaxUserCount;
    }

    public boolean isRunning () {
        return mIsRunning;
    }
}
