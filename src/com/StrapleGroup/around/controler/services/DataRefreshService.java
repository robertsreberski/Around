package com.StrapleGroup.around.controler.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.OpenHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.LocationClient;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Robert on 2014-08-30.
 */
public class DataRefreshService extends Service implements Constants, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    private SendInfoTask sendInfo;
    private Timer looper;
    private Context context;
    private GoogleCloudMessaging googleCloudMessaging;
    private LocationClient locationClient;
    private SharedPreferences sharedUserInfo;
    private SharedPreferences sharedLocationInfo;
    private Handler serviceHandler;
    private DataManagerImpl dataManager;
    private Location lastLocation;
    private ActivityRecognitionClient activityRecognitionClient;
    private PendingIntent mActivityRecognitionPendingIntent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sendInfo = new SendInfoTask();
        looper = new Timer();
        activityRecognitionClient = new ActivityRecognitionClient(this, this, this);
        locationClient = new LocationClient(this, this, this);
        Log.i("SERVICE_WORKING", "SENDING_DATA");
        context = getApplicationContext();
        SQLiteOpenHelper openHelper = new OpenHelper(this.context);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        //dataManager initialization
        dataManager = new DataManagerImpl(this.context);
        looper.scheduleAtFixedRate(sendInfo, 2 * 1000, 60 * 1000);

        serviceHandler = new Handler();
        Intent activityRecognitionIntent = new Intent(
                context, ActivityRecognitionService.class);
        mActivityRecognitionPendingIntent =
                PendingIntent.getService(this, 0, activityRecognitionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (!activityRecognitionClient.isConnected()) {
            activityRecognitionClient.connect();
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
        if (locationClient.isConnected()) {
            lastLocation = locationClient.getLastLocation();
            setLastLocation(lastLocation);
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
//                    Bundle pLoginData = new Bundle();
//                    int pNumberFriends = dataManager.getAllFriendsInfo().size();
//                    sharedUserInfo = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
//                    pLoginData.putString(KEY_ACTION, REFRESH_ACTION);
//                    pLoginData.putString(KEY_LOGIN, sharedUserInfo.getString(KEY_LOGIN, ""));
//                    if (checkIfLogin() && lastLocation != null) {
//                        pLoginData.putString("x", Double.toString(lastLocation.getLatitude()));
//                        pLoginData.putString("y", Double.toString(lastLocation.getLongitude()));
//                        pLoginData.putString("number", Integer.toString(pNumberFriends));
//                        String id = "m-" + UUID.randomUUID().toString();
//                        googleCloudMessaging = GoogleCloudMessaging
//                                .getInstance(context);
//                        try {
//                            googleCloudMessaging.send(SERVER_ID, id, pLoginData);
//                            Log.i("REQUESTED SUCCESSFUL", "*************************************************");

//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            Log.e("PROBLEM WITH LOGIN REQUEST",
//                                    "*******************************************************");
//                        }
//                    }
                    if (lastLocation != null) {
                        sharedLocationInfo = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor pEditor = sharedLocationInfo.edit();
                        Log.e("LATITUDE", Double.toString(lastLocation.getLatitude()));
                        Log.e("LONGTITUDE", Double.toString(lastLocation.getLongitude()));
                        pEditor.putString(Constants.KEY_X, Double.toString(lastLocation.getLatitude()));
                        pEditor.putString(Constants.KEY_Y, Double.toString(lastLocation.getLongitude()));
                        pEditor.commit();
                    }
                    return null;
                }
            }.execute(null, null, null);
        } else {
            Log.e("NOT_CONNECTED", "ERROR IN DATALOADSERVICE");
        }
    }

    private boolean checkIfLogin() {
        boolean pCheck = false;
        sharedUserInfo = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        if (sharedUserInfo.contains(KEY_LOGIN) && sharedUserInfo.contains(KEY_PASS)) {
            pCheck = true;
        }
        return pCheck;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (!locationClient.isConnected()) {
            activityRecognitionClient.requestActivityUpdates(1000, mActivityRecognitionPendingIntent);
            locationClient.connect();
            Log.e("CONNECTION", "JUST_CONNECTED");
        }

    }

    @Override
    public void onDisconnected() {
        activityRecognitionClient = null;
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