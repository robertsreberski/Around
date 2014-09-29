package com.StrapleGroup.around.ui.view.fragments;

import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.ui.view.LoginActivity;
import com.google.android.gms.location.DetectedActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;


/**
 * Created by Robert on 2014-09-19.
 */
public class UserFragment extends Fragment implements Constants {
    private ActivityReceiver activityReceiver = new ActivityReceiver();
    private LogoutRequestReceiver logoutRequestReceiver = new LogoutRequestReceiver();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View pView = inflater.inflate(R.layout.fragment_user, container, false);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(new ImageLoaderConfiguration.Builder(getActivity().getApplicationContext()).build());
        DisplayImageOptions pOptions = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(330)).build();
        ImageView pProfileView = (ImageView) pView.findViewById(R.id.profilePhoto);
        imageLoader.displayImage("drawable://" + R.drawable.facebook_example, pProfileView, pOptions);
//        ((TextView) pView.findViewById(R.id.login_view)).setText(getActivity().getSharedPreferences(Constants.USER_PREFS,Context.MODE_PRIVATE).getString(Constants.KEY_LOGIN,""));
        return pView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(logoutRequestReceiver, new IntentFilter(Constants.LOG_OUT_LOCAL_ACTION));
        getActivity().registerReceiver(activityReceiver, new IntentFilter(Constants.ACTIVITY_RECOGNITION_LOCAL_ACTION));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(logoutRequestReceiver);
        getActivity().unregisterReceiver(activityReceiver);
        super.onStop();
    }

    private class ActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getIconFromType(intent.getIntExtra(Constants.KEY_ACTIVITY, 0));
        }

        private void getIconFromType(int activityType) {
            ImageView pActivityView = (ImageView) getActivity().findViewById(R.id.activity_view);
            switch (activityType) {
                case DetectedActivity.IN_VEHICLE:
                    pActivityView.setImageResource(R.drawable.icon_car);
                    break;
                case DetectedActivity.ON_BICYCLE:
                    pActivityView.setImageResource(R.drawable.icon_bike);
                    break;
                case DetectedActivity.ON_FOOT:
                    pActivityView.setImageResource(R.drawable.icon_walk);
                    break;
                case DetectedActivity.STILL:
                    pActivityView.setImageResource(R.drawable.icon_still);
                    break;
                case DetectedActivity.UNKNOWN:
                    pActivityView.setImageResource(R.drawable.icon_unknown);
                    break;
            }
        }
    }

    public class LogoutRequestReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences sharedUserInfo = getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
            sharedUserInfo.edit().clear().commit();
            Log.i("GREAT", "Successfully logged off");
            if (!sharedUserInfo.contains(KEY_LOGIN) && !sharedUserInfo.contains(KEY_PASS)) {
                Intent logoffSuccessfulIntent = new Intent(context, LoginActivity.class);
                startActivity(logoffSuccessfulIntent);
            }
        }
    }
}