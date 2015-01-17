package com.StrapleGroup.around.ui.view.fragments;


import android.content.*;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.base.AroundInfo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AroundMapFragment extends Fragment implements Constants, GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {


    private GoogleMap mapPane;
    private Context context = null;
    private LocationReceiver locationReceiver;
    private SupportMapFragment mapFragment;
    private RefreshReceiver refreshReceiver;
    private Marker locMarker;
    private List<Marker> markerList = new ArrayList<Marker>();
    private boolean isAround = false;
    private int width;
    private int height;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationReceiver = new LocationReceiver();
        refreshReceiver = new RefreshReceiver();
        context = getActivity().getApplicationContext();
        // creating map


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View pMapView = inflater.inflate(R.layout.fragment_map, container, false);
        ((TextView) pMapView.findViewById(R.id.range_of_view)).setText("<" + getActivity().getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE).getString("around_range", ""));
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        return pMapView;
    }

    @Override
    public void onStart() {
        mapCustomer();
        initLocationService();
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(locationReceiver);
        getActivity().unregisterReceiver(refreshReceiver);
        super.onStop();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_create);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_create, mapFragment).commit();
        }
    }

    public void setAllAround() {
        DataManagerImpl pDataManager = new DataManagerImpl(context);
        List<AroundInfo> pAroundList = pDataManager.getAllAroundInfo();
        if (pAroundList.size() == 0) {
            isAround = false;
        } else {
            isAround = true;
            for (int i = 0; i < pAroundList.size(); i++) {
                AroundInfo pAround = pAroundList.get(i);
                LatLng pLatLng = new LatLng(pAround.getX(), pAround.getY());
                MarkerOptions pMarkerOptions = new MarkerOptions();
                pMarkerOptions.position(pLatLng).title(pAround.getLogin()).flat(true);
                markerList.add(mapPane.addMarker(pMarkerOptions));
            }
            addMarkers();
        }
    }

    public void mapCustomer() {
        mapPane = mapFragment.getMap();
        SharedPreferences pLatLngPrefs = getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        if (pLatLngPrefs.contains(KEY_X) && pLatLngPrefs.contains(KEY_Y)) {
            LatLng pLatLng = new LatLng(Double.parseDouble(pLatLngPrefs.getString(KEY_X, "")), Double.parseDouble(pLatLngPrefs.getString(KEY_Y, "")));
            BitmapDescriptor pMyLocIcon = BitmapDescriptorFactory.fromResource(R.drawable.my_loc_marker);
            if (locMarker != null) {
                locMarker.remove();
            }
            locMarker = mapPane.addMarker(new MarkerOptions().flat(true).icon(pMyLocIcon).position(pLatLng));
            CameraPosition pCameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(Double.parseDouble(pLatLngPrefs.getString(KEY_X, "")), Double.parseDouble(pLatLngPrefs.getString(KEY_Y, "")))).zoom(16).build();
            mapPane.moveCamera(CameraUpdateFactory
                    .newCameraPosition(pCameraPosition));
        }
        mapPane.setMyLocationEnabled(false);
        mapPane.setBuildingsEnabled(true);

        mapPane.setOnMarkerClickListener(this);
        mapPane.setOnCameraChangeListener(this);
        mapPane.setOnInfoWindowClickListener(this);
        mapPane.getUiSettings().setAllGesturesEnabled(false);
        mapPane.getUiSettings().setCompassEnabled(false);
        mapPane.getUiSettings().setZoomControlsEnabled(false);
        mapPane.getUiSettings().setMyLocationButtonEnabled(false);
        mapPane.getUiSettings().setZoomGesturesEnabled(false);
        mapPane.getUiSettings().setScrollGesturesEnabled(false);
    }

    private void initLocationService() {
        IntentFilter locationFilter = new IntentFilter(LOCATION_ACTION);
        getActivity().registerReceiver(locationReceiver, locationFilter);
        IntentFilter pIntentFilter = new IntentFilter(MARKER_LOCAL_ACTION);
        getActivity().registerReceiver(refreshReceiver, pIntentFilter);
        setAllAround();
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
        if (isAround = false) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(pLatLng).zoom(15).build();
            mapPane.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }
    }

    public void addMarkers() {
        LatLngBounds.Builder pCameraBoundsBuilder = LatLngBounds.builder();
        for (int i = 0; i < markerList.size(); i++) {
            pCameraBoundsBuilder.include(markerList.get(i).getPosition());
        }
        LatLngBounds pCameraBounds = pCameraBoundsBuilder.build();

        mapPane.moveCamera(CameraUpdateFactory.newLatLngBounds(pCameraBounds, width, height, 250));

    }

    public void deleteAllMarkers() {
        for (int i = 0; i < markerList.size(); i++) {
            markerList.get(i).remove();
        }
        markerList.clear();
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (cameraPosition.zoom > 16) {
            mapPane.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent pIntent = new Intent(CHANGE_PAGE_LOCAL_ACTION);
        pIntent.putExtra("PAGE", 1);
        context.sendBroadcast(pIntent);
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
            ((TextView) getActivity().findViewById(R.id.range_of_view)).setText("<" + getActivity().getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE).getString("around_range", ""));
            deleteAllMarkers();
            setAllAround();
        }
    }
}
