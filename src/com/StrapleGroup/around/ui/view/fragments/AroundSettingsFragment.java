package com.StrapleGroup.around.ui.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;

/**
 * Created by Robert on 2014-09-27.
 */
public class AroundSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.around_settings);
        ((Preference) findPreference("log_out")).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent pLogOutIntent = new Intent(Constants.LOG_OUT_LOCAL_ACTION);
                getActivity().sendBroadcast(pLogOutIntent);
                return true;
            }
        });
    }
}
