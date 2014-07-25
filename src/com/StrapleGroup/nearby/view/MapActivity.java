package com.StrapleGroup.nearby.view;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.StrapleGroup.nearby.model.MapModel;
import com.StrapleGroup.nearby.R;
import com.StrapleGroup.nearby.R.id;
import com.StrapleGroup.nearby.R.layout;
import com.StrapleGroup.nearby.base.BaseActivity;
import com.StrapleGroup.nearby.controler.LocateUserControler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends BaseActivity  implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {
	public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	Button mPrev;
	private Location currentLocation;
	public double lat;
	public double lang;
	GoogleMap mapPane;
	private Location location;
	private LatLng latLng;
	LocationClient locationClient;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		locationClient = new LocationClient(this, this, this);
		mapPane = ((MapModel) getFragmentManager().findFragmentById(R.id.map_create)).getMap();
		mapPane.setMyLocationEnabled(true);
		mPrev = (Button) findViewById(R.id.location);
		
	}
	public void start(View v){
		this.currentLocation = locationClient.getLastLocation();
		latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
		CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).tilt(90).build();
		mapPane.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		cameraPosition = new CameraPosition.Builder().target(latLng).zoom(15).tilt(0).build();
		mapPane.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		lat =currentLocation.getLatitude();
		lang = currentLocation.getLongitude();
       Toast.makeText(this,"Latitude: "+String.valueOf(lat)+" ;Longitude: "+String.valueOf(lang), Toast.LENGTH_LONG).show();
    }

	@Override
	protected void onStart() {
		super.onStart();
		locationClient.connect();
		
	}

	@Override
	protected void onResume() {
		super.onResume();
//		if (mPrefs.contains("KEY_UPDATES_ON")) {
//			mUpdatesRequest = mPrefs.getBoolean("KEY_UPDATES_ON", false);
//
//		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
//	
	@Override
    protected void onStop() {
		locationClient.disconnect();
        super.onStop();
    }
	@Override
	public void onConnected(Bundle dataBundle) {
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
			try {
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (IntentSender.SendIntentException e) {
				e.printStackTrace();
			}
		} else {
			connectionResult.getErrorCode();
		}
	}
	

}
