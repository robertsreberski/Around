package com.StrapleGroup.around.controler.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.OpenHelper;
import com.StrapleGroup.around.database.base.FriendsInfo;

import java.util.List;

/**
 * Created by Robert on 2014-08-31.
 */
public class NotificatorAroundFriendsService extends IntentService implements Constants {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NotificatorAroundFriendsService() {
        super("NotificatorAroundFriendsService");
    }

    private DataManagerImpl dataManager;
    private Context context;
    private List<FriendsInfo> friendsList;
    private SharedPreferences userPrefs;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        SQLiteOpenHelper openHelper = new OpenHelper(context);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        dataManager = new DataManagerImpl(context);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getBooleanExtra(MESSAGE, false) == true) {
            friendsList = dataManager.getAllFriendsInfo();
            for (int pCount = 0; pCount < friendsList.size(); pCount++) {
                int pInteger = pCount + 1;
                FriendsInfo pFriend = dataManager.getFriendInfo(pInteger);
//                userPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
                Intent pAddMarkerIntent = new Intent(MARKER_LOCAL_ACTION);
                pAddMarkerIntent.putExtra("LAT", pFriend.getXFriend());
                pAddMarkerIntent.putExtra("LNG", pFriend.getYFriend());
                sendBroadcast(pAddMarkerIntent);
            }
        }
    }

}
