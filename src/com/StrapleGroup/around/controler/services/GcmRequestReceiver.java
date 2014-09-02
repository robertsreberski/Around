package com.StrapleGroup.around.controler.services;

import android.content.Context;
import android.content.Intent;
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
    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging pGcm = GoogleCloudMessaging.getInstance(context);
        Bundle loginResult = intent.getExtras();
        String messageType = pGcm.getMessageType(intent);
        if (!loginResult.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
//				Log.i("GREATEST", "Received: " + loginResult.getString(MESSAGE));
                if (loginResult.getString(ACTION).equals("LOGIN")) {
                    Intent pLoginIntent = new Intent(LOGIN_LOCAL_ACTION);
                    if (loginResult.getString(MESSAGE).equals("valid")) {
                        pLoginIntent.putExtra(MESSAGE, true);
                    } else if (loginResult.getString(MESSAGE).equals("invalid")) {
                        Toast.makeText(context, "Invalid login or password",
                                Toast.LENGTH_LONG).show();
                        pLoginIntent.putExtra(MESSAGE, false);
                    }
                    context.sendBroadcast(pLoginIntent);
                }
                if (loginResult.getString(ACTION).equals("REGISTER")) {
                    Intent pRegisterIntent = new Intent(REGISTER_LOCAL_ACTION);
                    if (loginResult.getString(MESSAGE).equals("completed")) {
                        pRegisterIntent.putExtra(MESSAGE, true);
                    }
                    if (loginResult.getString(MESSAGE).equals("incompleted")) {
                        pRegisterIntent.putExtra(MESSAGE, false);
                    }
                    context.sendBroadcast(pRegisterIntent);
                }
                if (loginResult.getString(ACTION).equals("ADD")) {
                    Intent pAddIntent = new Intent(ADD_LOCAL_ACTION);
                    if (loginResult.getString(MESSAGE).equals("completed")) {
                        pAddIntent.putExtra("LAT", loginResult.getString("x"));
                        pAddIntent.putExtra("LNG", loginResult.getString("y"));
                        pAddIntent.putExtra(MESSAGE, true);
                    }
                    if (loginResult.getString(MESSAGE).equals("incompleted")) {
                        pAddIntent.putExtra(MESSAGE, false);
                    }
                    context.sendBroadcast(pAddIntent);
                }
                if (loginResult.getString(ACTION).equals("FRIENDS")) {
                    Intent pRefreshIntent = new Intent(REFRESH_LOCAL_ACTION);
                    SQLiteOpenHelper openHelper = new OpenHelper(context);
                    SQLiteDatabase db = openHelper.getWritableDatabase();
                    FriendsInfoDao dao = new FriendsInfoDao(db);
                    dataManager = new DataManagerImpl(context);
                    friendsList = dataManager.getAllFriendsInfo();
                    for (int pCount = 0; pCount < friendsList.size(); pCount++) {
                        FriendsInfo pFriend = dataManager.getFriendInfo(pCount);
                        String test1 = pFriend.getLoginFriend() + "x";
                        String test2 = pFriend.getLoginFriend() + "y";
                        pFriend.setXFriend(Double.parseDouble(loginResult.getString(pFriend.getLoginFriend() + "x")));
                        pFriend.setYFriend(Double.parseDouble(loginResult.getString(pFriend.getLoginFriend() + "y")));
                        dao.updateCoordinates(pFriend, Long.toString(pFriend.getId()));
                        Log.e("REFRESHED", "REFRESH SUCCESSFUL");
                    }
                }

            }
        }
    }
}
