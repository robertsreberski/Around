package com.StrapleGroup.around.ui.view.fragments;


import android.content.*;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.google.android.gms.maps.model.*;

import java.util.concurrent.atomic.AtomicInteger;

public class AroundMapFragment extends Fragment implements Constants {


    private GoogleMap mapPane = null;

    private Context context = null;
    private AtomicInteger msgId = new AtomicInteger();
    private SharedPreferences sharedUserInfo;
    private LocationReceiver locationReceiver;
    private SupportMapFragment mapFragment;
    private Intent intentLocationService;
    private RefreshReceiver refreshReceiver;
    private Marker locMarker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationReceiver = new LocationReceiver();
        refreshReceiver = new RefreshReceiver();
        context = getActivity().getApplicationContext();
        sharedUserInfo = getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        // creating map

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onStart() {
        if (mapPane == null) {
            mapPane = mapFragment.getMap();
            mapCustomer();
        }
        initLocationService();
        IntentFilter pIntentFilter = new IntentFilter(MARKER_LOCAL_ACTION);
        getActivity().registerReceiver(refreshReceiver, pIntentFilter);
        super.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_create);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_create, mapFragment).commit();
        }


    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(locationReceiver);
        getActivity().stopService(intentLocationService);
        getActivity().unregisterReceiver(refreshReceiver);
        super.onStop();
    }


    public void mapCustomer() {
        mapPane.setMyLocationEnabled(false);
        mapPane.setBuildingsEnabled(true);
        mapPane.getUiSettings().setAllGesturesEnabled(false);
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
        BitmapDescriptor pMyLocIcon = BitmapDescriptorFactory.fromResource(R.drawable.my_loc_marker);
        if (locMarker != null) {
            locMarker.remove();
        }
        locMarker = mapPane.addMarker(new MarkerOptions().flat(true).icon(pMyLocIcon).position(pLatLng));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(pLatLng).zoom(15).build();
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

    public void addMarker(LatLng latLng) {
        MarkerOptions pMarkerOptions = new MarkerOptions();
        pMarkerOptions.position(latLng);
        mapPane.addMarker(pMarkerOptions);
    }

    private class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            serviceUsing(intent);
        }
    }

    private class RefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            double pLat = intent.getDoubleExtra("LAT", 0.00);
            double pLng = intent.getDoubleExtra("LNG", 0.00);
            LatLng pLatLng = new LatLng(pLat, pLng);
            addMarker(pLatLng);
            Log.e("MARKER_ADD", "****************************************MARKER_ADDED");
        }
    }
}
