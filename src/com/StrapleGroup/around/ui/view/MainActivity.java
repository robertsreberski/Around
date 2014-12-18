package com.StrapleGroup.around.ui.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.ui.utils.AroundViewPager;
import com.StrapleGroup.around.ui.utils.ImageHelper;
import com.StrapleGroup.around.ui.view.fragments.*;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.List;
import java.util.Vector;

/**
 * Created by Robert on 2014-08-31.
 */
public class MainActivity extends FragmentActivity implements Constants {
    private PagerAdapter pagerAdapter;
    private SharedPreferences sharedUserInfo;
    private SharedPreferences sharedLatLng;
    private Context context;
    private GoogleCloudMessaging googleCloudMessaging;
    private static EditText friendLogin;
    private RelativeLayout friendBar;
    private RelativeLayout container;
    private AroundViewPager pager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        context = getApplicationContext();
        this.initializePaging();
        setButtons();
    }

    public static EditText getFriendLogin() {
        return friendLogin;
    }

    private void initializePaging() {
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, UserFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, AroundFriendsFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, AroundMapFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, NewsFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, FriendsListFragment.class.getName()));
        this.pagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);

        pager = (AroundViewPager) findViewById(R.id.pager);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
            }
        });
        pager.setAdapter(this.pagerAdapter);
        pager.setOffscreenPageLimit(4);
        pager.setCurrentItem(2);
    }

    public void setButtons() {
        final ImageButton pAroundButton = (ImageButton) findViewById(R.id.goToAround);
        final ImageButton pHomeButton = (ImageButton) findViewById(R.id.goToUser);
        final ImageButton pLogButton = (ImageButton) findViewById(R.id.goToNews);
        final ImageButton pFriendListButton = (ImageButton) findViewById(R.id.goToFriendList);
        final ImageButton pMapButton = (ImageButton) findViewById(R.id.goToMap);
        pMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pAroundButton.setSelected(false);
                pHomeButton.setSelected(false);
                pLogButton.setSelected(false);
                pFriendListButton.setSelected(false);
                pager.setCurrentItem(2);
            }
        });
        pAroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pAroundButton.isSelected()) {
                    pAroundButton.setSelected(true);
                    pHomeButton.setSelected(false);
                    pLogButton.setSelected(false);
                    pFriendListButton.setSelected(false);
                    pager.setCurrentItem(1);
                }
            }
        });
        pHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pHomeButton.isSelected()) {
                    pAroundButton.setSelected(false);
                    pHomeButton.setSelected(true);
                    pLogButton.setSelected(false);
                    pFriendListButton.setSelected(false);
                    pager.setCurrentItem(0);
                }
            }
        });
        pLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pLogButton.isSelected()) {
                    pAroundButton.setSelected(false);
                    pHomeButton.setSelected(false);
                    pLogButton.setSelected(true);
                    pFriendListButton.setSelected(false);
                    pager.setCurrentItem(3);
                }
            }
        });
        pFriendListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pFriendListButton.isSelected()) {
                    pAroundButton.setSelected(false);
                    pHomeButton.setSelected(false);
                    pLogButton.setSelected(false);
                    pFriendListButton.setSelected(true);
                    pager.setCurrentItem(4);
                }
            }
        });
    }


    public void addFriend(View view) {
        if (findViewById(R.id.friend_bar) == null) {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.friend_add_bar, null);
            friendBar = (RelativeLayout) viewGroup.findViewById(R.id.friend_bar);
            container = (RelativeLayout) findViewById(R.id.friend_list_layout);
            container.addView(friendBar, 1);
            friendLogin = (EditText) friendBar.findViewById(R.id.friend_field);
        }
    }
//
//
public void add(View view) {
    sharedUserInfo = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
    container.removeView(friendBar);
    ImageHelper pImageHelper = new ImageHelper();
    DataManagerImpl pDataManager = new DataManagerImpl(context);
    FriendsInfo pFriend = new FriendsInfo();
    pFriend.setId(0);
    pFriend.setLoginFriend(friendLogin.getText().toString());
    pFriend.setActivities(Integer.toString(1));
    pFriend.setProfilePhoto(pImageHelper.encodeImage(BitmapFactory.decodeResource(getResources(), R.drawable.facebook_example)));
    pFriend.setStatus(STATUS_ONLINE);
    pFriend.setXFriend(32.1231);
    pFriend.setYFriend(32.5345);
    pDataManager.saveFriendInfo(pFriend);
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