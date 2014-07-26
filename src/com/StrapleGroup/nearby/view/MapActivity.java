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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends BaseActivity {

	Button mPrev;
	private Location currentLocation;
	public double lat;
	public double lang;
	GoogleMap mapPane;
	private Location location;
	private LatLng latLng;
	private Intent intent;
	public static final String BROADCAST_ACTION = "Hello World";
	private Location polledLocation = null;
	private BroadcastReceiver broadcast = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			MapActivity.this.serviceUsing(intent);
		}
	};
	// LocationClient locationClient;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		// locationClient = new LocationClient(this, this, this);
		mapPane = ((MapModel) getFragmentManager().findFragmentById(
				R.id.map_create)).getMap();
		mapPane.setMyLocationEnabled(true);
		mPrev = (Button) findViewById(R.id.location);

	}
	
	public void serviceUsing(Intent intent) {
		final double lat = intent.getDoubleExtra("Latitude", 0.00);
		final double lng = intent.getDoubleExtra("Longitude", 0.00);
		final String provider = intent.getStringExtra("Provider");
		polledLocation = new Location(provider);
		polledLocation.setLatitude(lat);
		polledLocation.setLongitude(lng);
		Toast.makeText(this, Double.toString(polledLocation.getLatitude())+" , "+Double.toString(polledLocation.getLongitude()), Toast.LENGTH_LONG).show();
	}
	private void startService(){}
		private void initService() {
			Intent intent = new Intent(this, LocationService.class); 
			startService(intent);
			registerReceiver(broadcast, new IntentFilter(LocationService.BROADCAST_ACTION)); }
		
	
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
		super.onPause();
	}

	//
	@Override
	protected void onStop() {
		// locationClient.disconnect();
		super.onStop();
	}

}
