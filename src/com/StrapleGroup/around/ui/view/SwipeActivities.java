package com.StrapleGroup.around.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by Robert on 2014-08-31.
 */
public class SwipeActivities extends FragmentActivity implements Constants {
    private PagerAdapter pagerAdapter;
    private SharedPreferences sharedUserInfo;
    private SharedPreferences sharedLatLng;
    private Context context;
    private GoogleCloudMessaging googleCloudMessaging;
    private static EditText friendLogin;
    private LinearLayout friendBar;
    private LinearLayout container;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.swipe_activities);
        context = getApplicationContext();
        this.initializePaging();
    }

    public static EditText getFriendLogin() {
        return friendLogin;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater pInflater = getMenuInflater();
        pInflater.inflate(R.menu.friends_list_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initializePaging() {
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, AroundMapFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, FriendsListFragment.class.getName()));
        this.pagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                invalidateOptionsMenu();
            }
        });
        pager.setAdapter(this.pagerAdapter);
    }

    public void logoff(View v) {
//        SQLiteOpenHelper openHelper = new OpenHelper(this.context);
//        SQLiteDatabase db = openHelper.getWritableDatabase();
//        //dataManager initialization
//        FriendsInfoDao dao = new FriendsInfoDao(db);
//        FriendsInfo friend = new FriendsInfo();
//        friend.setXFriend(0.00);
//        friend.setYFriend(0.00);
//        dao.updateCoordinates(friend);
//        Toast.makeText(context,"DATABASE WORKS! =)",Toast.LENGTH_SHORT).show();
        sharedUserInfo = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        sharedUserInfo.edit().clear().commit();
        Log.i("GREAT", "Successfully logged off");
        if (!sharedUserInfo.contains(KEY_LOGIN) && !sharedUserInfo.contains(KEY_PASS)) {
            Intent logoffSuccessfulIntent = new Intent(this.context, LoginActivity.class);
            startActivity(logoffSuccessfulIntent);
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_friend:
                addFriend();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addFriend() {
        if (findViewById(R.id.friend_bar) == null) {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.friend_add_bar, null);
            friendBar = (LinearLayout) viewGroup.findViewById(R.id.friend_bar);
            container = (LinearLayout) findViewById(R.id.friend_list_layout);
            container.addView(friendBar, 0);
            friendLogin = (EditText) friendBar.findViewById(R.id.friend_field);
        }
//        Intent pAddFriendIntent = new Intent(context, AddFriendActivity.class);
//        startActivity(pAddFriendIntent);
    }

    public void add(View view) {
        sharedUserInfo = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        sharedLatLng = getSharedPreferences(LATLNG_PREFS, MODE_PRIVATE);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (googleCloudMessaging == null) {
                    googleCloudMessaging = GoogleCloudMessaging
                            .getInstance(context);
                }
                Bundle pFriendDataBundle = new Bundle();
                sharedUserInfo = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
                pFriendDataBundle.putString("action", ADD_ACTION);
                pFriendDataBundle.putString("login", sharedUserInfo.getString(KEY_LOGIN, ""));
                pFriendDataBundle.putString("friend_login", friendLogin.getText().toString());
                pFriendDataBundle.putString("x", sharedLatLng.getString("LAT", ""));
                pFriendDataBundle.putString("y", sharedLatLng.getString("LNG", ""));
                try {
                    googleCloudMessaging.send(SERVER_ID, "m-" + UUID.randomUUID().toString(), pFriendDataBundle);
                    Log.e("SENDED", "ADD_REQUEST_SENDED");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);
        Toast.makeText(context, "Request sended successufuly!", Toast.LENGTH_SHORT);
        container.removeView(friendBar);
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
}