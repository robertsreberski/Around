package com.StrapleGroup.around.controler.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.ConnectionHelper;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.base.AroundInfo;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.ui.utils.ImageHelper;
import com.StrapleGroup.around.ui.view.dialogs.LocationDialog;
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
    float MIN_DISTANCE = 1 * 1000;
    private SendInfoTask sendInfo;
    private Timer looper;
    private Context context;
    private SharedPreferences prefs;
    private SharedPreferences settingsPrefs;
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
        settingsPrefs = getSharedPreferences(SETTINGS_PREFS, MODE_PRIVATE);
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
                            ConnectionHelper connectionHelper = new ConnectionHelper(context);
                            JSONObject pRefreshObject = connectionHelper.updateToApp(prefs.getString(KEY_LOGIN, ""), prefs.getString(KEY_PASS, ""),
                                    Double.parseDouble(prefs.getString(KEY_X, "")), Double.parseDouble(prefs.getString(KEY_Y, "")),
                                    prefs.getInt(KEY_ACTIVITY, 4), settingsPrefs.getString(KEY_STATUS, ""));
                            try {
                                if (pRefreshObject != null) {
                                    if (pRefreshObject.getBoolean(KEY_VALID)) {
                                        dataManager.deleteAroundList();
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
                                            if (pDistance <= Float.parseFloat(settingsPrefs.getString(getString(R.string.key_range), ""))) {
                                                AroundInfo pAround = new AroundInfo();
                                                pAround.setLogin(pName);
                                                pAround.setDistance(Float.toString(pDistance));
                                                dataManager.saveAroundFriend(pAround);
                                                Intent pNotifierIntent = new Intent(context, AroundNotifierService.class);
                                                pNotifierIntent.putExtra(KEY_LOGIN, pJsonFriend.getString(KEY_LOGIN));
                                                pNotifierIntent.putExtra(KEY_X, pFriendLat);
                                                pNotifierIntent.putExtra(KEY_Y, pFriendLng);
                                                startService(pNotifierIntent);
                                            }
                                        }
                                        for (int i = 0; i < pRequestArray.length(); i++) {
                                            JSONObject pJsonRequest = pRequestArray.getJSONObject(i);
                                            DataManagerImpl pDataManager = new DataManagerImpl(context);
                                            FriendsInfo pFriend = new FriendsInfo();
                                            ImageHelper pImageHelper = new ImageHelper();
                                            pFriend.setLoginFriend(pJsonRequest.getString(KEY_LOGIN));
                                            pFriend.setProfilePhoto(pImageHelper.encodeImageForDB(BitmapFactory.decodeResource(getResources(), R.drawable.facebook_example)));
                                            pFriend.setStatus(STATUS_INVITATION);
                                            if (pDataManager.findFriend(pJsonRequest.getString(KEY_LOGIN)) == -1)
                                                pDataManager.saveRequest(pFriend);
                                        }

                                    }
                                    sendBroadcast(new Intent(REFRESH_FRIEND_LIST_LOCAL_ACTION));
                                } else Log.e("SERVER", "SERVER DOESNT WORK");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute(null, null, null);
                } else Log.e("NOT_LOGGED_IN", "ERROR IN DATALOADSERVICE");
            } else {
                sendNotification();
                Log.e("NO_LOCATION_ENABLED", "ERROR IN DATALOADSERVICE");
            }
        }
    }

    private void sendNotification() {
        Intent pIntent = new Intent(this, LocationDialog.class);
        TaskStackBuilder pTaskStack = TaskStackBuilder.create(this).addNextIntent(pIntent);
        Notification.Builder pNotification = new Notification.Builder(this).setSmallIcon(R.drawable.home_img).setAutoCancel(true)
                .setContentTitle("Location settings turned off!").setContentText("Please turn on location settings")
                .setContentIntent(pTaskStack.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));
        NotificationManager pNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        pNotificationManager.notify(0, pNotification.build());
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