package com.StrapleGroup.nearby.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.StrapleGroup.nearby.R;
import com.StrapleGroup.nearby.base.BaseActivity;
import com.StrapleGroup.nearby.controler.services.LocationService;
import com.StrapleGroup.nearby.model.MapModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends BaseActivity {

	private Button mPrev;
	private GoogleMap mapPane;
	private UiSettings settings;
	private LatLng latLng;
	public static final String BROADCAST_ACTION = "Hello World";
	private Location polledLocation = null;
	private BroadcastReceiver broadcast = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			MapActivity.this.serviceUsing(intent);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		mapPane = ((MapModel) getFragmentManager().findFragmentById(
				R.id.map_create)).getMap();
		mapPane.setMyLocationEnabled(true);
		mapPane.getUiSettings().setCompassEnabled(false);
		mapPane.getUiSettings().setZoomControlsEnabled(false);
		mapPane.getUiSettings().setMyLocationButtonEnabled(false);
		mapPane.getUiSettings().setZoomGesturesEnabled(false);
		mapPane.getUiSettings().setScrollGesturesEnabled(false);
		mPrev = (Button) findViewById(R.id.location);
	}

	public void serviceUsing(Intent intent) {
		final double lat = intent.getDoubleExtra("Latitude", 0.00);
		final double lng = intent.getDoubleExtra("Longitude", 0.00);
		final String provider = intent.getStringExtra("Provider");
		polledLocation = new Location(provider);
		polledLocation.setLatitude(lat);
		polledLocation.setLongitude(lng);
		latLng = new LatLng(polledLocation.getLatitude(),
				polledLocation.getLongitude());
		Toast.makeText(
				this,
				Double.toString(polledLocation.getLatitude()) + " , "
						+ Double.toString(polledLocation.getLongitude())
				// polledLocation.getProvider()
				, Toast.LENGTH_LONG).show();
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(latLng).zoom(15).tilt(30).build();
		mapPane.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
	}

	private void initService() {
		Intent intent = new Intent(this, LocationService.class);
		startService(intent);
		registerReceiver(broadcast, new IntentFilter(
				LocationService.BROADCAST_ACTION));
	}

	public void start(View v) {
		// intent = new Intent(BROADCAST_ACTION);
		// intent.setAction("com.example.broadcastsample.SHOW_TOAST");
		// sendBroadcast(intent);
		// this.currentLocation = locationClient.getLastLocation();
		// latLng = new LatLng(currentLocation.getLatitude(),
		// currentLocation.getLongitude());
		// CameraPosition cameraPosition = new
		// CameraPosition.Builder().target(latLng).zoom(15).tilt(90).build();
		// mapPane.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		// cameraPosition = new
		// CameraPosition.Builder().target(latLng).zoom(15).tilt(0).build();
		// mapPane.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		// lat =currentLocation.getLatitude();
		// lang = currentLocation.getLongitude();
		// Toast.makeText(this,"Latitude: "+String.valueOf(lat)+" ;Longitude: "+String.valueOf(lang),
		// Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// locationClient.connect();

	}

	@Override
	protected void onResume() {
		super.onResume();
		initService();
		// if (mPrefs.contains("KEY_UPDATES_ON")) {
		// mUpdatesRequest = mPrefs.getBoolean("KEY_UPDATES_ON", false);
		//
		// }
	}

	@Override
	protected void onPause() {
		unregisterReceiver(broadcast);
		super.onPause();
	}

	//
	@Override
	protected void onStop() {
		// locationClient.disconnect();
		super.onStop();
	}

}
