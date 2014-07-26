package com.StrapleGroup.nearby.controler.services;

import com.StrapleGroup.nearby.view.MapActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {
	public static final String BROADCAST_ACTION = "NearBy";
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	public LocationManager locationManager;
	public NearbyLocationListener listener;
	public Location previousBestLocation = null;
	Intent intent;
	int counter = 0;

	
	public Location getPrevLocation(){
		return previousBestLocation;
	}
	@Override
	public void onCreate() {
	    super.onCreate();
	    intent = new Intent(BROADCAST_ACTION);      
	}

	@Override
	public void onStart(Intent intent, int startId) {      
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    listener = new NearbyLocationListener();        
	    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
	}

	@Override
	public IBinder onBind(Intent intent) {
	    return null;
	}

	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }
	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 500;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}

	@Override
	public void onDestroy() {       
	   // handler.removeCallbacks(sendUpdatesToUI);     
	    super.onDestroy();
	    Log.v("STOP_SERVICE", "DONE");
	    locationManager.removeUpdates(listener);        
	}   

	public static Thread performOnBackgroundThread(final Runnable runnable) {
	    final Thread t = new Thread() {
	        @Override
	        public void run() {
	            try {
	                runnable.run();
	            } finally {

	            }
	        }
	    };
	    t.start();
	    return t;
	}

	public class NearbyLocationListener implements LocationListener
	{

	    public void onLocationChanged(final Location loc)
	    {
	        Log.i("**************************************", "Location changed");
	        if(isBetterLocation(loc, previousBestLocation)) {
	            loc.getLatitude();
	            loc.getLongitude();
//	            intent.putExtra("")
	            intent.putExtra("Latitude", loc.getLatitude());
	            intent.putExtra("Longitude", loc.getLongitude());     
//	            intent.putExtra("Provider", loc.getProvider());                 
	            sendBroadcast(intent);          
	        }                               
	    }

	    public void onProviderDisabled(String provider)
	    {
	        Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
	    }


	    public void onProviderEnabled(String provider)
	    {
	        Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
	    }


	    public void onStatusChanged(String provider, int status, Bundle extras)
	    {

	    }

	}
	}
