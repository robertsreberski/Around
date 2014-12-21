package com.StrapleGroup.around.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.ui.utils.AroundViewPager;
import com.StrapleGroup.around.ui.view.fragments.*;

import java.util.List;
import java.util.Vector;

/**
 * Created by Robert on 2014-08-31.
 */
public class MainActivity extends FragmentActivity implements Constants {
    private PagerAdapter pagerAdapter;
    private Context context;
    private AroundViewPager pager;
    private Fragment navDrawer;
    private ImageButton drawer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        context = getApplicationContext();
        this.initializePaging();
    }


    private void initializePaging() {
        drawer = (ImageButton) findViewById(R.id.drawer);
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


    public void menu(View view) {
        if (findViewById(R.id.nav_drawer) == null) {
            navDrawer = new NavDrawer();
            FragmentTransaction pTransaction = getSupportFragmentManager().beginTransaction();
            pTransaction.add(R.id.main_container, navDrawer).addToBackStack("NavDraw").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        } else {
            FragmentTransaction pTransaction = getSupportFragmentManager().beginTransaction();
            pTransaction.remove(navDrawer).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
        }
    }

    public void goFriendList(View view) {
        pager.setCurrentItem(4, true);
        FragmentTransaction pTransaction = getSupportFragmentManager().beginTransaction();
        pTransaction.remove(navDrawer).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
        drawer.setImageResource(R.drawable.friendlist_img);
    }

    public void goSettings(View view) {
        pager.setCurrentItem(0, true);
        FragmentTransaction pTransaction = getSupportFragmentManager().beginTransaction();
        pTransaction.remove(navDrawer).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
        drawer.setImageResource(R.drawable.settings_img);
    }

    public void goAround(View view) {
        pager.setCurrentItem(1, true);
        FragmentTransaction pTransaction = getSupportFragmentManager().beginTransaction();
        pTransaction.remove(navDrawer).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
        drawer.setImageResource(R.drawable.aroundyou_img);
    }

    public void goHome(View view) {
        pager.setCurrentItem(2, true);
        FragmentTransaction pTransaction = getSupportFragmentManager().beginTransaction();
        pTransaction.remove(navDrawer).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
        drawer.setImageResource(R.drawable.home_img);
    }

    public void goNews(View view) {
        pager.setCurrentItem(3, true);
        FragmentTransaction pTransaction = getSupportFragmentManager().beginTransaction();
        pTransaction.remove(navDrawer).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
        drawer.setImageResource(R.drawable.news_img);
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
