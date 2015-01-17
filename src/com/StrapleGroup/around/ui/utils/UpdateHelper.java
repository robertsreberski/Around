package com.StrapleGroup.around.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.ConnectionHelper;
import com.StrapleGroup.around.controler.services.AroundNotifierService;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.base.AroundInfo;
import com.StrapleGroup.around.database.base.FriendsInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Robert on 2015-01-08.
 */
public class UpdateHelper implements Constants {
    Random random = new Random();
    Context context;
    DataManagerImpl dataManager;

    public UpdateHelper(Context context) {
        this.context = context;
        dataManager = new DataManagerImpl(context);
    }

    public void getUpdateOnDemand() {
        SharedPreferences settingsPrefs = context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE);
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        ConnectionHelper connectionHelper = new ConnectionHelper(context);
        JSONObject pRefreshObject = connectionHelper.updateToApp(prefs.getString(KEY_LOGIN, ""), prefs.getString(KEY_PASS, ""),
                Double.parseDouble(prefs.getString(KEY_X, "")), Double.parseDouble(prefs.getString(KEY_Y, "")),
                prefs.getInt(KEY_ACTIVITY, 4), settingsPrefs.getString(KEY_STATUS, ""));
        try {
            if (pRefreshObject != null) {
                if (pRefreshObject.getBoolean(KEY_VALID)) {
                    JSONArray pFriendArray = pRefreshObject.getJSONArray(KEY_FRIEND_LIST);
                    JSONArray pRequestArray = pRefreshObject.getJSONArray(KEY_REQUEST_LIST);
                    if (pRefreshObject.getBoolean(KEY_PHOTO_LIST)) {
                        connectionHelper.updatePhotoRequest(prefs.getString(KEY_LOGIN, ""), prefs.getString(KEY_PASS, ""));
                    }
                    for (int i = 0; i < pFriendArray.length(); i++) {
                        JSONObject pJsonFriend = pFriendArray.getJSONObject(i);
                        DataManagerImpl pDataManager = new DataManagerImpl(context);
                        FriendsInfo pFriend = new FriendsInfo();
                        final double pFriendLat = pJsonFriend.getDouble(KEY_X);
                        final double pFriendLng = pJsonFriend.getDouble(KEY_Y);
                        String pName = pJsonFriend.getString(KEY_LOGIN);
                        pFriend.setLoginFriend(pName);
                        pFriend.setXFriend(pFriendLat);
                        pFriend.setYFriend(pFriendLng);
                        pFriend.setActivities(pJsonFriend.getInt(KEY_ACTIVITY));
                        pFriend.setStatus(pJsonFriend.getString(KEY_STATUS));
                        if (pDataManager.findFriend(pJsonFriend.getString(KEY_LOGIN)) == -1) {
                            pDataManager.saveFriendInfo(pFriend);
                        } else {
                            pDataManager.updateFriendInfo(pFriend);
                        }
                        Location pFriendLocation = new Location("Friend Location");
                        pFriendLocation.setLatitude(pFriendLat);
                        pFriendLocation.setLongitude(pFriendLng);
                        Location pMyLocation = new Location("My Location");
                        pMyLocation.setLatitude(Double.parseDouble(prefs.getString(KEY_X, "")));
                        pMyLocation.setLongitude(Double.parseDouble(prefs.getString(KEY_Y, "")));
                        float pDistance = pMyLocation.distanceTo(pFriendLocation);
                        long l = -1;
                        long pAroundL = dataManager.findAround(pName);
                        if (pDistance <= Float.parseFloat(settingsPrefs.getString(context.getString(R.string.key_range), "")) && pAroundL == l) {
                            AroundInfo pAround = new AroundInfo();
                            pAround.setLogin(pName);
                            pAround.setX(pFriendLat);
                            pAround.setY(pFriendLng);
                            pAround.setDistance(Float.toString(pDistance));
                            dataManager.saveAroundFriend(pAround);
                            Intent pNotifierIntent = new Intent(context, AroundNotifierService.class);
                            pNotifierIntent.putExtra(KEY_LOGIN, pJsonFriend.getString(KEY_LOGIN));
                            pNotifierIntent.putExtra(KEY_X, pFriendLat);
                            pNotifierIntent.putExtra(KEY_Y, pFriendLng);
                            pNotifierIntent.putExtra("atomic", random.nextInt());
                            context.startService(pNotifierIntent);
                        } else if (pDistance <= Float.parseFloat(settingsPrefs.getString(context.getString(R.string.key_range), "")) && pAroundL != l) {
                            AroundInfo pAround = new AroundInfo();
                            pAround.setLogin(pName);
                            pAround.setX(pFriendLat);
                            pAround.setY(pFriendLng);
                            pAround.setDistance(Float.toString(pDistance));
                            dataManager.updateAroundFriend(pAround);
                        } else if (pAroundL != l) {
                            dataManager.deleteAround(dataManager.findAround(pName));
                        }
                    }
                    context.sendBroadcast(new Intent(MARKER_LOCAL_ACTION));

                    for (int i = 0; i < pRequestArray.length(); i++) {
                        JSONObject pJsonRequest = pRequestArray.getJSONObject(i);
                        DataManagerImpl pDataManager = new DataManagerImpl(context);
                        FriendsInfo pFriend = new FriendsInfo();
                        ImageHelper pImageHelper = new ImageHelper();
                        pFriend.setLoginFriend(pJsonRequest.getString(KEY_LOGIN));
                        pFriend.setProfilePhoto(pImageHelper.encodeImageForDB(BitmapFactory.decodeResource(context.getResources(), R.drawable.facebook_example)));
                        pFriend.setStatus(STATUS_REQUEST);
                        if (pDataManager.findFriend(pJsonRequest.getString(KEY_LOGIN)) == -1)
                            pDataManager.saveRequest(pFriend);
                    }

                }
                context.sendBroadcast(new Intent(REFRESH_FRIEND_LIST_LOCAL_ACTION));
            } else Log.e("SERVER", "SERVER DOESNT WORK");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
