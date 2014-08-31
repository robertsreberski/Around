package com.StrapleGroup.around.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;

import java.util.List;
import java.util.Vector;

/**
 * Created by Robert on 2014-08-31.
 */
public class SwipeActivities extends FragmentActivity implements Constants {
    private PagerAdapter pagerAdapter;
    private SharedPreferences sharedUserInfo;
    private Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.swipe_activities);
        context = getApplicationContext();
        this.initializePaging();

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

        ViewPager pager = (ViewPager) super.findViewById(R.id.viewpager);
        pager.setAdapter(this.pagerAdapter);
    }

    public void logoff(View v) {
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
        Intent pAddFriendIntent = new Intent(context, AddFriendActivity.class);
        startActivity(pAddFriendIntent);
    }

    private class PagerAdapter extends FragmentPagerAdapter {
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