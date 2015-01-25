package com.StrapleGroup.around.ui.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.ConnectionHelper;
import com.StrapleGroup.around.ui.utils.AroundViewPager;
import com.StrapleGroup.around.ui.utils.ConnectionUtils;
import com.StrapleGroup.around.ui.utils.ImageHelper;
import com.StrapleGroup.around.ui.view.fragments.AroundFriendsFragment;
import com.StrapleGroup.around.ui.view.fragments.AroundMapFragment;
import com.StrapleGroup.around.ui.view.fragments.FriendsListFragment;
import com.StrapleGroup.around.ui.view.fragments.UserFragment;
import com.StrapleGroup.around.ui.view.fragments.registerFragments.EmailAndPassFragment;
import com.StrapleGroup.around.ui.view.fragments.registerFragments.ProfileInfoFragment;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;
import java.util.Vector;

/**
 * Created by Robert on 2014-08-27.
 */
public class RegisterActivity extends FragmentActivity implements Constants {
    private String login;
    private String pass;
    private float width;
    private float height;
    private AroundViewPager pager;
    private PagerAdapter pagerAdapter;
    ChangePageReceiver changePageReceiver = new ChangePageReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        adjustOnCreate();
        initializePaging();
    }

    private void initializePaging() {
        Vector<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, EmailAndPassFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, ProfileInfoFragment.class.getName()));

        this.pagerAdapter = new PagerAdapter(getSupportFragmentManager(), fragments);
        pager = (AroundViewPager) findViewById(R.id.pager);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
            }
        });
        pager.setAdapter(this.pagerAdapter);
        pager.setCurrentItem(0);
    }

    private void adjustOnCreate() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        pager = (AroundViewPager) findViewById(R.id.pager);
        RelativeLayout.LayoutParams pParams = (RelativeLayout.LayoutParams) pager.getLayoutParams();
        pParams.bottomMargin = (int) height / 3;
        pager.setLayoutParams(pParams);
    }
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(changePageReceiver, new IntentFilter(CHANGE_PAGE_LOCAL_ACTION));
    }

//    public void register(View v) {
//        boolean done = true;
//        loginField.setError(null);
//        passField.setError(null);
//        login = loginField.getText().toString();
//        pass = passField.getText().toString();
//        if (TextUtils.isEmpty(login)) {
//            done = false;
//        }
//        if (TextUtils.isEmpty(pass)) {
//            done = false;
//        }
//        if (done) {
//            if (ConnectionUtils.hasActiveInternetConnection(context)) {
//                registerButton.setText("");
//                registerProgress.setVisibility(View.VISIBLE);
//                new AsyncTask<Void, Void, Boolean>() {
//                    private String photoString;
//                    private SharedPreferences prefs;
//
//                    @Override
//                    protected Boolean doInBackground(Void... params) {
//                        prefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
//                        ImageHelper pImageHelper = new ImageHelper();
//                        photoString = pImageHelper.encodeImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.facebook_example));
//                        ConnectionHelper pConnectionHelper = new ConnectionHelper(context);
//                        Double pLat = 0.000;
//                        Double pLng = 0.000;
//                        int pActivity = DetectedActivity.UNKNOWN;
//                        if (prefs.contains(KEY_X) && prefs.contains(KEY_Y)) {
//                            pLat = Double.parseDouble(prefs.getString(KEY_X, ""));
//                            pLng = Double.parseDouble(prefs.getString(KEY_Y, ""));
//                        }
//                        if (prefs.contains(KEY_ACTIVITY)) pActivity = prefs.getInt(KEY_ACTIVITY, 4);
//                        return pConnectionHelper.registerToApp(login, pass, pLat, pLng, photoString, pActivity);
//                    }
//
//                    @Override
//                    protected void onPostExecute(Boolean aBoolean) {
//                        super.onPostExecute(aBoolean);
//                        if (aBoolean) {
//                            SharedPreferences.Editor pPrefsEditor = prefs.edit();
//                            pPrefsEditor.putString(KEY_LOGIN, login);
//                            pPrefsEditor.putString(KEY_PASS, pass);
//                            pPrefsEditor.putString(KEY_STATUS, STATUS_ONLINE);
//                            pPrefsEditor.putString(KEY_PHOTO, photoString);
//                            pPrefsEditor.commit();
//                            registrationSuccessful();
//                        } else registrationUnsuccessful();
//
//                    }
//                }.execute(null, null, null);
//            } else {
//                noConnection();
//            }
//        }
//
//
//    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(changePageReceiver);
        super.onStop();
    }

    private void noConnection() {
        Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragments;

        public PagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }

    private class ChangePageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            pager.setCurrentItem(intent.getIntExtra("PAGE", intent.getIntExtra("PAGE", 0)), true);
        }
    }

}