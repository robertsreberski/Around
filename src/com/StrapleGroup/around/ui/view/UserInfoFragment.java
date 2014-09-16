package com.StrapleGroup.around.ui.view;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;

/**
 * Created by Robert on 2014-09-14.
 */
public class UserInfoFragment extends Fragment {
    private View inflatedView = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.user_info_fragment, container, false);
        SharedPreferences pUserPrefs = getActivity().getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        if (pUserPrefs.contains(Constants.KEY_LOGIN)) {
            String test = pUserPrefs.getString(Constants.KEY_LOGIN, "");
            TextView text = (TextView) inflatedView.findViewById(R.id.login_view);
            text.setText(pUserPrefs.getString(Constants.KEY_LOGIN, ""));
        }
        return inflatedView;
    }
}