package com.StrapleGroup.around.ui.view.fragments;

import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.ui.utils.ImageHelper;
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
    private ImageHelper imageHelper = new ImageHelper();
    private SharedPreferences prefs;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);
        TextView pLoginView = (TextView) view.findViewById(R.id.login_view);
        prefs = getActivity().getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        changePhoto();
        pLoginView.setText(prefs.getString(KEY_LOGIN, ""));
        return view;
    }

    public void changePhoto() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(new ImageLoaderConfiguration.Builder(getActivity().getApplicationContext()).build());
        DisplayImageOptions pOptions = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(330)).build();
        ImageView pProfileView = (ImageView) view.findViewById(R.id.profilePhoto);
        imageLoader.displayImage(imageHelper.getImageUri(getActivity().getApplicationContext(), imageHelper.getCroppedBitmap(imageHelper.decodeImage(prefs.getString(KEY_PHOTO, "")))).toString(), pProfileView, pOptions);
    }

    @Override
    public void onStart() {
        super.onStart();
        changePhoto();
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