package com.StrapleGroup.around.ui.utils;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by Robert on 2014-12-29.
 */
public class FastLocationObtainer {
    private LocationManager locationManager;
    private double latitude;
    private double longtitude;

    public FastLocationObtainer(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria pCriteria = new Criteria();
        pCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        Location pLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(pCriteria, true));
        setLatitude(pLocation.getLatitude());
        setLongtitude(pLocation.getLongitude());
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }
}

