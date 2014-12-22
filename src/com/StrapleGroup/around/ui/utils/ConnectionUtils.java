package com.StrapleGroup.around.ui.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Robert on 2014-09-13.
 */
public class ConnectionUtils {

    public static boolean hasActiveInternetConnection(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo pWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo pMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (pWifi.isConnected() || pMobile.isConnected()) {
            return true;
        } else return false;
    }
}
