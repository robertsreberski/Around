package com.StrapleGroup.around.ui.view;

import android.app.Activity;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.services.DataLoadService;
import com.StrapleGroup.around.controler.services.LocationService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class MapActivity extends Activity implements Constants {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private GoogleMap mapPane = null;
    private GoogleCloudMessaging googleCloudMessaging = null;
    private Context context = null;
    private AtomicInteger msgId = new AtomicInteger();
    private String registrationId = null;
    private SharedPreferences sharedUserInfo;
    private LocationReceiver locationReceiver;
    private SharedPreferences gcmStorage;
    private Intent intentLocationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        context = getApplicationContext();
        locationReceiver = new LocationReceiver();
        sharedUserInfo = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        // creating map
        mapPane = ((MapFragment) getFragmentManager().findFragmentById(
                R.id.map_create)).getMap();
        mapCustomer();
        // GCM initialization
        googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
        registrationId = getRegistrationId();
        if (registrationId.isEmpty()) {
            registerInBackground();
        }

    }

    @Override
    protected void onStart() {
        initLocationService();
//        if (checkIfLogin() == false) {
//            Intent pLoginIntent = new Intent(this.context, LoginActivity.class);
//            startActivity(pLoginIntent);
//            finish();
//        }
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(locationReceiver);
        stopService(intentLocationService);
        super.onStop();
    }

    public void logoff(View v) {
        sharedUserInfo.edit().clear().commit();
        Log.i("GREAT", "Successfully logged off");
        if (!sharedUserInfo.contains(KEY_LOGIN) && !sharedUserInfo.contains(KEY_PASS)) {
            Intent logoffSuccessfulIntent = new Intent(this.context, LoginActivity.class);
            startActivity(logoffSuccessfulIntent);
            finish();
        }
    }

    public void send(View v) {
    }

    public void next(View v) throws IOException {
//        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        Intent pFriendListNext = new Intent(this, FriendsListActivity.class);
        startActivity(pFriendListNext);
    }

    public void mapCustomer() {
        mapPane.setMyLocationEnabled(true);
        mapPane.getUiSettings().setCompassEnabled(false);
        mapPane.getUiSettings().setZoomControlsEnabled(false);
        mapPane.getUiSettings().setMyLocationButtonEnabled(false);
        mapPane.getUiSettings().setZoomGesturesEnabled(false);
        mapPane.getUiSettings().setScrollGesturesEnabled(false);
    }

    private void initLocationService() {
        Intent pDataLoadService = new Intent(this, DataLoadService.class);
        startService(pDataLoadService);
        intentLocationService = new Intent(this, LocationService.class);
        startService(intentLocationService);
        IntentFilter locationFilter = new IntentFilter(LOCATION_ACTION);
        registerReceiver(locationReceiver, locationFilter);
    }

    public void serviceUsing(Intent intent) {
        final double lat = intent.getDoubleExtra("Latitude", 0.00);
        final double lng = intent.getDoubleExtra("Longitude", 0.00);
        LatLng pLatLng = new LatLng(lat, lng);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(pLatLng).zoom(15).tilt(30).build();
        mapPane.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    private String getRegistrationId() {
        gcmStorage = getSharedPreferences(GCM_PREFS, MODE_PRIVATE);
        String registrationId = gcmStorage.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("NO_REGID", "Registration not found.");
            return "";
        }
        return registrationId;
    }

    public int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object... params) {
                String msg = "";
                try {
                    if (googleCloudMessaging == null) {
                        googleCloudMessaging = GoogleCloudMessaging
                                .getInstance(context);
                    }
                    registrationId = googleCloudMessaging.register(SENDER_ID);
                    Log.e("HURRAY", "Registration Works");
                    sendRegistrationIdToBackend();
                    storeRegistrationId(context, registrationId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend() throws IOException {
        String msg = "";
        Bundle data = new Bundle();
        data.putString("my_action", "com.StrapleGroup.around.REGISTER");
        String id = Integer.toString(msgId.incrementAndGet());
        googleCloudMessaging.send(SENDER_ID + "@gcm.googleapis.com", id, data);
        msg = "Sent message";
        Log.e("HURRAY", "Registration Works");
    }

    private void storeRegistrationId(Context context, String regId) {
        gcmStorage = getSharedPreferences(GCM_PREFS, MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i("baaad", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = gcmStorage.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private boolean checkIfLogin() {
        boolean pCheck = false;
        if (sharedUserInfo.contains(KEY_LOGIN) && sharedUserInfo.contains(KEY_PASS)) {
            pCheck = true;
        }
        return pCheck;
    }

    private class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            serviceUsing(intent);
        }
    }
}
