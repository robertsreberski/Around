package com.StrapleGroup.around.controler.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.ConnectionHelper;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Robert on 2014-08-30.
 */
public class DataRefreshService extends Service implements Constants, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private SendInfoTask sendInfo;
    private Timer looper;
    private Context context;
    private SharedPreferences prefs;
    private Handler serviceHandler;
    private GoogleApiClient googleApiClient;
    private DataManagerImpl dataManager;
    private Location lastLocation;
    private PendingIntent mActivityRecognitionPendingIntent;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("SERVICE_WORKING", "SENDING_DATA");
        context = getApplicationContext();
        prefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        //dataManager initialization
        dataManager = new DataManagerImpl(this.context);
        googleApiClient = new GoogleApiClient.Builder(this).addApi(ActivityRecognition.API).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        Intent activityRecognitionIntent = new Intent(
                context, ActivityRecognitionService.class);
        mActivityRecognitionPendingIntent =
                PendingIntent.getService(this, 0, activityRecognitionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        looper.cancel();
        Log.e("SERVICE_STOPPED", "DONE");
        super.onDestroy();
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public void sendLocation() {
        if (googleApiClient.isConnected()) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            setLastLocation(lastLocation);
            if (lastLocation != null) {
                SharedPreferences.Editor pEditor = prefs.edit();
                Log.e("LATITUDE", Double.toString(lastLocation.getLatitude()));
                Log.e("LONGTITUDE", Double.toString(lastLocation.getLongitude()));
                pEditor.putString(Constants.KEY_X, Double.toString(lastLocation.getLatitude()));
                pEditor.putString(Constants.KEY_Y, Double.toString(lastLocation.getLongitude()));
                pEditor.commit();
                if (checkIfLogin()) {
                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... params) {
//                            ConnectionHelper connectionHelper = new ConnectionHelper(context);
//                            JSONArray pFriendArray = connectionHelper.updateToApp(prefs.getString(KEY_LOGIN, ""), prefs.getString(KEY_PASS, ""),
//                                    Double.parseDouble(prefs.getString(KEY_X, "")), Double.parseDouble(prefs.getString(KEY_Y, "")),
//                                    prefs.getInt(KEY_ACTIVITY, 4), prefs.getString(KEY_STATUS, ""));
//                            try {
//                                for (int i = 0; i < pFriendArray.length(); i++) {
//                                    JSONObject pJsonFriend = pFriendArray.getJSONObject(i);
//                                    DataManagerImpl pDataManager = new DataManagerImpl(context);
//                                    FriendsInfo pFriend = new FriendsInfo();
//                                    pFriend.setLoginFriend(pJsonFriend.getString(KEY_LOGIN));
//                                    pFriend.setXFriend(pJsonFriend.getDouble(KEY_X));
//                                    pFriend.setYFriend(pJsonFriend.getDouble(KEY_Y));
//                                    pFriend.setActivities(pJsonFriend.getInt(KEY_ACTIVITY));
//                                    pFriend.setStatus(pJsonFriend.getString(KEY_STATUS));
//                                    pFriend.setProfilePhoto(Base64.decode(pJsonFriend.getString(KEY_PHOTO), 0));
//                                    pDataManager.saveFriendInfo(pFriend);
//                                    /*
//                                    Will create notifications here :1
//                                     */
//                                }
//                                sendBroadcast(new Intent(REFRESH_FRIEND_LIST_LOCAL_ACTION));
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
                            return null;
                        }
                    }.execute(null, null, null);
                } else Log.e("NOT_LOGGED_IN", "ERROR IN DATALOADSERVICE");
            } else {
                Log.e("NO_LOCATION_ENABLED", "ERROR IN DATALOADSERVICE");
            }
        }
    }

    private boolean checkIfLogin() {
        boolean pCheck = false;
        if (prefs.contains(KEY_LOGIN) && prefs.contains(KEY_PASS)) pCheck = true;
        return pCheck;
    }

    @Override
    public void onConnected(Bundle bundle) {
        sendInfo = new SendInfoTask();
        looper = new Timer();
        looper.scheduleAtFixedRate(sendInfo, 2 * 1000, 60 * 1000);
        serviceHandler = new Handler();
        Log.e("CONNECTION", "JUST_CONNECTED");
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(googleApiClient, 1000, mActivityRecognitionPendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class SendInfoTask extends TimerTask {

        @Override
        public void run() {
            serviceHandler.post(new Runnable() {
                @Override
                public void run() {
                    sendLocation();
                }
            });
        }
    }
}