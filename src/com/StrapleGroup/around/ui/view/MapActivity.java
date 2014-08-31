package com.StrapleGroup.around.ui.view;


import android.content.*;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.services.DataLoadService;
import com.StrapleGroup.around.controler.services.LocationService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class MapActivity extends Fragment implements Constants {


    private GoogleMap mapPane = null;

    private Context context = null;
    private AtomicInteger msgId = new AtomicInteger();
    private SharedPreferences sharedUserInfo;
    private LocationReceiver locationReceiver;

    private Intent intentLocationService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationReceiver = new LocationReceiver();
        context = getActivity().getApplicationContext();
        sharedUserInfo = getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        // creating map

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_map, container, false);

    }

    @Override
    public void onStart() {
        mapPane = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map_create)).getMap();
        mapCustomer();
        initLocationService();
//        if (checkIfLogin() == false) {
//            Intent pLoginIntent = new Intent(this.context, LoginActivity.class);
//            startActivity(pLoginIntent);
//            finish();
//        }
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(locationReceiver);
        getActivity().stopService(intentLocationService);
        super.onStop();
    }

    public void logoff(View v) {
        sharedUserInfo.edit().clear().commit();
        Log.i("GREAT", "Successfully logged off");
        if (!sharedUserInfo.contains(KEY_LOGIN) && !sharedUserInfo.contains(KEY_PASS)) {
            Intent logoffSuccessfulIntent = new Intent(this.context, LoginActivity.class);
            startActivity(logoffSuccessfulIntent);
            getActivity().finish();
        }
    }

    public void send(View v) {

    }

    public void next(View v) throws IOException {
//        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        Intent pFriendListNext = new Intent(context, FriendsListActivity.class);
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
        Intent pDataLoadService = new Intent(context, DataLoadService.class);
        getActivity().startService(pDataLoadService);
        intentLocationService = new Intent(context, LocationService.class);
        getActivity().startService(intentLocationService);
        IntentFilter locationFilter = new IntentFilter(LOCATION_ACTION);
        getActivity().registerReceiver(locationReceiver, locationFilter);
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
