package com.StrapleGroup.around.ui.view;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.ui.controler.MapControler;
import com.StrapleGroup.around.controler.services.LocationService;
import com.StrapleGroup.around.database.OpenHelper;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends Activity {
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    static final String TAG = "GCMDemo";
	private Button mPrev;
	private GoogleMap mapPane=null;
	private Location polledLocation = null;
	private LocationReceiver broadcast= new LocationReceiver();
	String SENDER_ID = "960206351442";
    GoogleCloudMessaging googleCloudMessaging;
//    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String registrationId;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_map);
		//tworzenie mapy
				mapPane = ((MapControler) getFragmentManager().findFragmentById(R.id.map_create)).getMap();
				mapCustomer();
		//inicjacja zmiennych GCM
				context = getApplicationContext();
				googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
				registrationId = getRegistrationId(context);
		 if (registrationId.isEmpty()) {
	            registerInBackground();
	        }
		 SQLiteOpenHelper openHelper = new OpenHelper(this.context);
		 SQLiteDatabase db = openHelper.getWritableDatabase();
		 
		 //tworzenie GUI
		mPrev = (Button) findViewById(R.id.location);
	}
	@Override
	protected void onResume() {
		super.onResume();
		initLocationService();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(broadcast);
		super.onPause();
	}
	
	public void next(View v){
		Intent intentNext = new Intent(this,FriendsListActivity.class);
		startActivity(intentNext);
	}

	public void mapCustomer(){
		mapPane.setMyLocationEnabled(true);
		mapPane.getUiSettings().setCompassEnabled(false);
		mapPane.getUiSettings().setZoomControlsEnabled(false);
		mapPane.getUiSettings().setMyLocationButtonEnabled(false);
		mapPane.getUiSettings().setZoomGesturesEnabled(false);
		mapPane.getUiSettings().setScrollGesturesEnabled(false);
	}

	private void initLocationService() {
		Intent pIntentLocationService = new Intent(this, LocationService.class);
		startService(pIntentLocationService);
		registerReceiver(broadcast, new IntentFilter(LocationService.BROADCAST_ACTION));
	}
	
	public void serviceUsing(Intent intent) {
		final double lat = intent.getDoubleExtra("Latitude", 0.00);
		final double lng = intent.getDoubleExtra("Longitude", 0.00);
		final String provider = intent.getStringExtra("Provider");
		polledLocation = new Location(provider);
		polledLocation.setLatitude(lat);
		polledLocation.setLongitude(lng);
		LatLng pLatLng = new LatLng(polledLocation.getLatitude(), polledLocation.getLongitude());
		Toast.makeText(this,Double.toString(polledLocation.getLatitude()) + " , "+ Double.toString(polledLocation.getLongitude()), Toast.LENGTH_LONG).show();
		CameraPosition cameraPosition = new CameraPosition.Builder().target(pLatLng).zoom(15).tilt(30).build();
		mapPane.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}
	
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    return registrationId;
	}
	// Do zmiany na bardziej bezpieczne przechowywanie registerID
	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(MapActivity.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
	
	public int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	@SuppressWarnings("unchecked")
	private void registerInBackground() {
		new AsyncTask() {
			@Override
			protected String doInBackground(Object... params) {
				String msg = "";
	            try {
	                if (googleCloudMessaging == null) {
	                    googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
	                }
	                registrationId = googleCloudMessaging.register(SENDER_ID);
	                msg = "Device registered, registration ID=" + registrationId;
	                sendRegistrationIdToBackend();
	                storeRegistrationId(context, registrationId);
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	            }
	            return msg;
			}
	    }.execute(null, null, null);
	}
	
	private void sendRegistrationIdToBackend() {
		//wysylanie registrationId na serwer
	}
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
	public class LocationReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			MapActivity.this.serviceUsing(intent);		
		}
		
	}
}
