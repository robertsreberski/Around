package com.StrapleGroup.around.ui.utils;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.StrapleGroup.around.R;

/**
 * Created by Robert on 2014-08-31.
 */
public class LocationEnabledDialog extends DialogFragment {
    private LocationManager locationManager;
    private Context context = getActivity().getApplicationContext();

    static LocationEnabledDialog newInstance() {
        return new LocationEnabledDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location_enabled, container, false);

    }

    public int checkProviders() {
        int answer = 0;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

        }

        return answer;
    }
}


