package com.StrapleGroup.around.ui.view;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.services.DataRefreshService;
import com.StrapleGroup.around.controler.services.LocationService;
import com.StrapleGroup.around.ui.utils.NetworkDialog;

/**
 * Created by Robert on 2014-09-21.
 */
public class StartActivity extends FragmentActivity implements NetworkDialog.NoticeDialogListener {
    private static int SPLASH_TIME_OUT = 2500;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        checkInternetConn();
    }

    private void initiateApp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent pDataLoadService = new Intent(StartActivity.this, DataRefreshService.class);
                startService(pDataLoadService);
                Intent intentLocationService = new Intent(StartActivity.this, LocationService.class);
                startService(intentLocationService);
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
        NetworkInfo pWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo pMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (pWifi.isConnected() || pMobile.isConnected()) {
            initiateApp();
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