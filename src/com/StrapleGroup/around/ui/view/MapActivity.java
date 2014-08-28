package com.StrapleGroup.around.ui.view;

import android.app.Activity;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.services.LocationService;
import com.StrapleGroup.around.database.DataManagerImpl;
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
	private Location polledLocation = null;

	private GoogleCloudMessaging googleCloudMessaging = null;
	private Context context = null;
	private AtomicInteger msgId = new AtomicInteger();
	private String registrationId = null;
	private DataManagerImpl dataManager = null;
	private SharedPreferences sharedUserInfo;
    private SharedPreferences sharedLatLng;
	private String userLoginData = null;
	private LocationReceiver locationReceiver;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		context = getApplicationContext();
        locationReceiver = new LocationReceiver();
		// creating database
//		SQLiteOpenHelper openHelper = new OpenHelper(this.context);
//		SQLiteDatabase db = openHelper.getWritableDatabase();
		//dataManager initialization
//		dataManager = new DataManagerImpl(this.context);
		sharedUserInfo = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
		sharedLatLng = getSharedPreferences(LATLNG_PREFS, MODE_PRIVATE);
		// creating map
		mapPane = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map_create)).getMap();
		mapCustomer();
		// GCM initialization
		googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
		registrationId = getRegistrationId(context);
		if (registrationId.isEmpty()) {
			registerInBackground();
		}
		
	}
	@Override
	protected void onStart() {
//		if(checkIfLogin() == false){
//
//			Intent pLoginIntent = new Intent(this.context, LoginActivity.class);
//			startActivity(pLoginIntent);
//			finish();
//		}
        initLocationService();
		super.onStart();
	}

    @Override
    protected void onStop() {
        unregisterReceiver(locationReceiver);
        super.onStop();
    }

    public void logoff(View v){
		sharedUserInfo.edit().clear().commit();
		Log.i("GREAT", "Successfully logged off");
		if(!sharedUserInfo.contains(KEY_LOGIN) && !sharedUserInfo.contains(KEY_PASS)){
			Intent logoffSuccessfulIntent = new Intent(this.context, LoginActivity.class);
			startActivity(logoffSuccessfulIntent);
		}
	}
	public void send(View v){
//		new AsyncTask<Void, Void, String>(){
//			@Override
//			protected String doInBackground(Void... params) {
//				googleCloudMessaging = GoogleCloudMessaging
//						.getInstance(context);
//				try {
//				Bundle data = new Bundle();
//				String id = "m-"+ UUID.randomUUID().toString();
//                    data.putString("action", REGISTER_ACTION);
//                    data.putString("login", "robert_super");
//                    data.putString("password", "gosc");
//                    data.putString("x", "42.134124");
//                    data.putString("y", "42.621123");
//					googleCloudMessaging.send(SERVER_ID, id, 0, data);
//					Log.e("GOOD", "sth");
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				return null;
//			}
//
//		}.execute(null, null, null);
	}
	public void next(View v) throws IOException {
		googleCloudMessaging = GoogleCloudMessaging
				.getInstance(context);
		 Intent pFriendListNext = new Intent(this,FriendsListActivity.class);
		 sendRegistrationIdToBackend();
			Log.i("WORK", "Good Work Nicolas");
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
		Intent pIntentLocationService = new Intent(this, LocationService.class);
		startService(pIntentLocationService);
        IntentFilter locationFilter = new IntentFilter(LOCATION_ACTION);
		registerReceiver(locationReceiver, locationFilter);
	}

	public void serviceUsing(Intent intent) {
		final double lat = intent.getDoubleExtra("Latitude", 0.00);
		final double lng = intent.getDoubleExtra("Longitude", 0.00);
		final String provider = intent.getStringExtra("Provider");
		polledLocation = new Location(provider);
		polledLocation.setLatitude(lat);
		polledLocation.setLongitude(lng);
		LatLng pLatLng = new LatLng(polledLocation.getLatitude(),
				polledLocation.getLongitude());
		Toast.makeText(
				this,
				Double.toString(polledLocation.getLatitude()) + " , "
						+ Double.toString(polledLocation.getLongitude()),
				Toast.LENGTH_LONG).show();
        SharedPreferences.Editor pLatLngEditor = sharedLatLng.edit();
        pLatLngEditor.putString("x",Double.toString(polledLocation.getLatitude()));
        pLatLngEditor.putString("y",Double.toString(polledLocation.getLongitude()));
        pLatLngEditor.commit();
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(pLatLng).zoom(15).tilt(30).build();
		mapPane.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i("baaad", "Registration not found.");
			return "";
		}
		Log.e("REG_ID", registrationId);
		return registrationId;
	}

	// Do zmiany na bardziej bezpieczne przechowywanie registerID
	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(MapActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
					msg = "Device registered, registration ID="
							+ registrationId;
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
//        try {
            Bundle data = new Bundle();
                data.putString("my_action", "com.StrapleGroup.around.REGISTER");
                String id = Integer.toString(msgId.incrementAndGet());
                googleCloudMessaging.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                msg = "Sent message";
                Log.e("HURRAY", "Registration Works");
//        }
//        catch (IOException ex) {
//            msg = "Error :" + ex.getMessage();
//        }
		
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i("baaad", "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	private boolean checkIfLogin(){
		boolean pCheck = false;
		if(sharedUserInfo.contains(KEY_LOGIN) && sharedUserInfo.contains(KEY_PASS)){
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
