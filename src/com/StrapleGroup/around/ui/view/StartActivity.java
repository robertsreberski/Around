package com.StrapleGroup.around.ui.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.services.DataRefreshService;
import com.StrapleGroup.around.ui.view.dialogs.LocationDialog;
import com.StrapleGroup.around.ui.view.dialogs.NetworkDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Robert on 2014-09-21.
 */
public class StartActivity extends FragmentActivity implements NetworkDialog.NoticeDialogListener {
    private static int SPLASH_TIME_OUT = 1250;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkInternetConn();
    }

    private void initiateApp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent pDataLoadService = new Intent(StartActivity.this, DataRefreshService.class);
                startService(pDataLoadService);
                if (checkIfLogin() == true) {
                    Intent pIntent = new Intent(StartActivity.this, MainActivity.class);
                    pIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(pIntent);
                } else {
                    Intent pIntent = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(pIntent);
                }
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private void checkInternetConn() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        LocationManager pLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        NetworkInfo pWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo pMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (pWifi.isConnected() || pMobile.isConnected()) {
            final int pServicesStatus = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
            if (pServicesStatus == ConnectionResult.SUCCESS) {
                if (pLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == false && pLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
                    startActivity(new Intent(StartActivity.this, LocationDialog.class));
                } else {
                    initiateApp();
//                    NotificationCompat.Builder pBuilder = new NotificationCompat.Builder(this).setCategory(Notification.CATEGORY_ERROR).setSmallIcon(R.drawable.home_img)
//                            .setContentTitle("Location Services").setContentText("Turn on your location feature!");
//                    NotificationManager mNotifyMgr =
//                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                    mNotifyMgr.notify(001,pBuilder.build());
                }

            } else {
                if (GooglePlayServicesUtil.isUserRecoverableError(pServicesStatus)) {
                    Dialog pDialog = GooglePlayServicesUtil.getErrorDialog(pServicesStatus, getParent(), GooglePlayServicesUtil.GOOGLE_PLAY_SERVICES_VERSION_CODE);
                    if (pDialog != null) {
                        pDialog.show();
                        pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            public void onDismiss(DialogInterface dialog) {
                                if (ConnectionResult.SERVICE_INVALID == pServicesStatus)
                                    getParent().finish();
                            }
                        });

                    }
                }
            }
        } else if (!pWifi.isConnected() && !pMobile.isConnected()) {
            DialogFragment pNetworkDialog = new NetworkDialog();
            pNetworkDialog.show(getFragmentManager(), "NetworkDialog");

        }
    }

    private boolean checkIfLogin() {
        SharedPreferences sharedUserInfo = getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE);
        boolean pCheck = false;
        if (sharedUserInfo.contains(Constants.KEY_LOGIN) && sharedUserInfo.contains(Constants.KEY_PASS)) {
            pCheck = true;
        }
        return pCheck;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        checkInternetConn();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        finish();
    }
}