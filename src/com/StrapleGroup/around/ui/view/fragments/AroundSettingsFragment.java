package com.StrapleGroup.around.ui.view.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.StrapleGroup.around.R;

/**
 * Created by Robert on 2014-09-27.
 */
public class AroundSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.around_settings);
    }
}
