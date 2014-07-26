//package com.StrapleGroup.nearby.controler.services;
//
//import com.StrapleGroup.nearby.view.MapActivity;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.location.Location;
//import android.location.LocationListener;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//public class NearbyLocationListener implements
//		LocationListener {
//MapActivity activity;
//	Intent intent;
////	Location previousBestLocation;
//	LocationService location;
//	public void onLocationChanged(final Location loc) {
//		Log.i("**************************************", "Location changed");
//		if ( location.isBetterLocation(loc, location.getPrevLocation())) {
//			loc.getLatitude();
//			loc.getLongitude();
//			intent.putExtra("Latitude", loc.getLatitude());
//			intent.putExtra("Longitude", loc.getLongitude());
//			intent.putExtra("Provider", loc.getProvider());
////			sendBroadcast(intent);
//
//		}
//	}
//	public void onProviderDisabled(String provider)
//    {
////        Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
//    }
//
//
//    public void onProviderEnabled(String provider)
//    {
////        Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
//    }
//	@Override
//	public void onStatusChanged(String provider, int status, Bundle extras) {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//
//}
