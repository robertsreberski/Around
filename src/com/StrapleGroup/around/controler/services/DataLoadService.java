package com.StrapleGroup.around.controler.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.StrapleGroup.around.base.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationClient;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by Robert on 2014-08-30.
 */
public class DataLoadService extends Service implements Constants, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    private SendInfoTask sendInfo;
    private Timer looper;
    private Context context;
    private GoogleCloudMessaging googleCloudMessaging;
    private LocationClient locationClient;
    private SharedPreferences sharedUserInfo;
    private SharedPreferences sharedLocationInfo;
    private Handler serviceHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sendInfo = new SendInfoTask();
        looper = new Timer();
        locationClient = new LocationClient(this, this, this);
        Log.i("SERVICE_WORKING", "SENDING_DATA");
        context = getApplicationContext();
        looper.scheduleAtFixedRate(sendInfo, 2 * 1000, 60 * 1000);

        serviceHandler = new Handler();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (!locationClient.isConnected()) {
            locationClient.connect();
            Log.e("CONNECTION", "JUST_CONNECTED");
        }
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        looper.cancel();
        Log.e("SERVICE_STOPPED", "DONE");
        super.onDestroy();
    }

    public void sendLocation() {
        if (locationClient.isConnected()) {
            Bundle pLoginData = new Bundle();
            pLoginData.putString("action", LOGIN_ACTION);
            pLoginData.putString(LOGIN, "null");
            Location pLastLocation = locationClient.getLastLocation();
            pLoginData.putString("x", Double.toString(pLastLocation.getLatitude()));
            pLoginData.putString("y", Double.toString(pLastLocation.getLongitude()));
            String id = "m-" + UUID.randomUUID().toString();
            googleCloudMessaging = GoogleCloudMessaging
                    .getInstance(context);
            if (checkIfLogin() && pLastLocation != null) {
                try {
                    googleCloudMessaging.send(SERVER_ID, id, pLoginData);
                    Log.i("REQUESTED SUCCESSFUL",
                            "*************************************************");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("PROBLEM WITH LOGIN REQUEST",
                            "*******************************************************");
                }
            } else if (pLastLocation != null) {
                sharedLocationInfo = getSharedPreferences(LATLNG_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor pEditor = sharedLocationInfo.edit();
                Log.e("LATITUDE", Double.toString(pLastLocation.getLatitude()));
                Log.e("LONGTITUDE", Double.toString(pLastLocation.getLongitude()));
                pEditor.putString("LAT", Double.toString(pLastLocation.getLatitude()));
                pEditor.putString("LNG", Double.toString(pLastLocation.getLongitude()));
                pEditor.commit();
            }
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

    }

    @Override
    public void onDisconnected() {

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