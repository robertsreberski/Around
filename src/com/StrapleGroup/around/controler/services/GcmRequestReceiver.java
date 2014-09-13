package com.StrapleGroup.around.controler.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.OpenHelper;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.daos.FriendsInfoDao;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.List;

public class GcmRequestReceiver extends WakefulBroadcastReceiver implements
        Constants {
    private DataManagerImpl dataManager;
    private List<FriendsInfo> friendsList;
    private SharedPreferences userPrefs;

    @Override
    public void onReceive(Context context, Intent intent) {
        final GoogleCloudMessaging pGcm = GoogleCloudMessaging.getInstance(context);
        Bundle loginResult = intent.getExtras();
        String messageType = pGcm.getMessageType(intent);
        if (!loginResult.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
//				Log.i("GREATEST", "Received: " + loginResult.getString(MESSAGE));
                if (loginResult.getString(KEY_ACTION).equals("LOGIN")) {
                    Intent pLoginIntent = new Intent(LOGIN_LOCAL_ACTION);
                    if (loginResult.getString(KEY_MESSAGE).equals("valid")) {
                        pLoginIntent.putExtra(KEY_MESSAGE, true);
                    } else if (loginResult.getString(KEY_MESSAGE).equals("invalid")) {
                        Toast.makeText(context, "Invalid login or password",
                                Toast.LENGTH_LONG).show();
                        pLoginIntent.putExtra(KEY_MESSAGE, false);
                    }
                    context.sendBroadcast(pLoginIntent);
                }
                if (loginResult.getString(KEY_ACTION).equals("REGISTER")) {
                    Intent pRegisterIntent = new Intent(REGISTER_LOCAL_ACTION);
                    if (loginResult.getString(KEY_MESSAGE).equals(COMPLETED)) {
                        pRegisterIntent.putExtra(KEY_MESSAGE, true);
                    }
                    if (loginResult.getString(KEY_MESSAGE).equals(INCOMPLETED)) {
                        pRegisterIntent.putExtra(KEY_MESSAGE, false);
                    }
                    context.sendBroadcast(pRegisterIntent);
                }
                if (loginResult.getString(KEY_ACTION).equals("ADD")) {
                    Intent pAddIntent = new Intent(ADD_LOCAL_ACTION);
                    if (loginResult.getString(KEY_MESSAGE).equals(COMPLETED)) {
                        pAddIntent.putExtra("LAT", loginResult.getString(LAT_SERVER));
                        pAddIntent.putExtra("LNG", loginResult.getString(LNG_SERVER));
                        pAddIntent.putExtra(KEY_MESSAGE, true);
                        Log.e("TAKEN", "ADD_RESPONSE");
                    } else if (loginResult.getString(KEY_MESSAGE).equals(INCOMPLETED)) {
                        pAddIntent.putExtra(KEY_MESSAGE, false);
                    } else if (loginResult.getString(KEY_MESSAGE).equals("rejected")) {
                        Log.i("FRIEND", "ADD_REJECTED");
                    }
                    context.sendBroadcast(pAddIntent);
                }
                if (loginResult.getString(KEY_ACTION).equals("FRIENDS")) {
                    Intent pRefreshIntent = new Intent(context, NotificatorAroundFriendsService.class);
                    SQLiteOpenHelper openHelper = new OpenHelper(context);
                    SQLiteDatabase db = openHelper.getWritableDatabase();
                    FriendsInfoDao dao = new FriendsInfoDao(db);
                    dataManager = new DataManagerImpl(context);
                    if (loginResult.getString(KEY_MESSAGE).equals(COMPLETED)) {
                        for (int pCount = 0; pCount < dataManager.getAllFriendsInfo().size(); pCount++) {
                            int pInteger = pCount + 1;
                            FriendsInfo pFriend = dataManager.getFriendInfo(pInteger);
                            double pLat = Double.parseDouble(loginResult.getString(pFriend.getLoginFriend() + LAT_SERVER));
                            double pLng = Double.parseDouble(loginResult.getString(pFriend.getLoginFriend() + LNG_SERVER));
                            pFriend.setXFriend(pLat);
                            pFriend.setYFriend(pLng);
                            dao.updateCoordinates(pFriend, Integer.toString(pInteger));
                            Log.e("REFRESHED", "REFRESH SUCCESSFUL");
                            pRefreshIntent.putExtra(KEY_MESSAGE, true);
                        }
                        if (loginResult.get(KEY_MESSAGE).equals(INCOMPLETED)) {
                            pRefreshIntent.putExtra(KEY_MESSAGE, false);
                        }
                    }
                    context.startService(pRefreshIntent);
                }
                if (loginResult.getString(KEY_ACTION).equals("REQUEST")) {
                    Intent requestIntent = new Intent(ADD_REQUEST_LOCAL_ACTION);
                    userPrefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                    final String friendLogin = loginResult.getString(KEY_LOGIN);
                    String pLat = loginResult.getString(LAT_SERVER);
                    String pLng = loginResult.getString(LNG_SERVER);
                    requestIntent.putExtra("friend_name", friendLogin);
                    requestIntent.putExtra("LAT", pLat);
                    requestIntent.putExtra("LNG", pLng);
                    context.sendBroadcast(requestIntent);
                }
            }
        }
    }
}