package com.StrapleGroup.around.ui.view.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import com.StrapleGroup.around.R;

import java.util.Set;

/**
 * Created by Robert on 2014-08-31.
 */
public class LocationDialog extends Activity {
    private LocationManager locationManager;
    private Context context;
    private ToggleButton internetAllow;
    private ToggleButton gpsAllow;
    private Button doneButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_location);
        internetAllow = (ToggleButton) findViewById(R.id.internet_toggle);
        gpsAllow = (ToggleButton) findViewById(R.id.gps_toggle);
        doneButton = (Button) findViewById(R.id.done_button);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLocationProviders();
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                finish();
            }
        });
    }

    private void checkLocationProviders() {
        try {
            switch (Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE)) {
                case (Settings.Secure.LOCATION_MODE_OFF):
                    internetAllow.setChecked(false);
                    gpsAllow.setChecked(false);
                    break;
                case (Settings.Secure.LOCATION_MODE_BATTERY_SAVING):
                    internetAllow.setChecked(true);
                    gpsAllow.setChecked(false);
                    break;
                case (Settings.Secure.LOCATION_MODE_SENSORS_ONLY):
                    internetAllow.setChecked(false);
                    gpsAllow.setChecked(true);
                    break;
                case (Settings.Secure.LOCATION_MODE_HIGH_ACCURACY):
                    internetAllow.setChecked(true);
                    gpsAllow.setChecked(true);
                    break;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

}

