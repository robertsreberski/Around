package com.StrapleGroup.around.ui.view;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.services.LocationService;
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
    List<Fragment> fragments;
    private ObjectAnimator animateIn;
    private ObjectAnimator animateOut;
    private BroadcastReceiver changePageReceiver = new ChangePageReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        context = getApplicationContext();
        this.initializePaging();
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(0);
        }
    }

    private void initializePaging() {
        drawer = (ImageButton) findViewById(R.id.drawer);
        fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, AroundMapFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, AroundFriendsFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, FriendsListFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, UserFragment.class.getName()));

        this.pagerAdapter = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        pager = (AroundViewPager) findViewById(R.id.pager);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
            }
        });
        pager.setAdapter(this.pagerAdapter);
        pager.setPageTransformer(true, new AroundPageTransformer());
        pager.setOffscreenPageLimit(3);
        pager.setCurrentItem(0);
    }


    public void menu(View view) {
        if (findViewById(R.id.nav_drawer) == null) {
            navDrawer = new NavDrawer();
            FragmentTransaction pTransaction = getSupportFragmentManager().beginTransaction();
            pTransaction.add(R.id.main_container, navDrawer).addToBackStack("NavDraw").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
        } else {
            FragmentTransaction pTransaction = getSupportFragmentManager().beginTransaction();
            pTransaction.remove(navDrawer).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Location service", "RESUME");
        startService(new Intent(MainActivity.this, LocationService.class));
        registerReceiver(changePageReceiver, new IntentFilter(CHANGE_PAGE_LOCAL_ACTION));
    }

    @Override
    protected void onPause() {
        stopService(new Intent(MainActivity.this, LocationService.class));
        unregisterReceiver(changePageReceiver);
        super.onPause();
    }

    public void goFriendList(View view) {
        pager.setCurrentItem(2, true);
        FragmentTransaction pTransaction = getSupportFragmentManager().beginTransaction();
        pTransaction.remove(navDrawer).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
        drawer.setImageResource(R.drawable.friendlist_img);
    }

    public void goSettings(View view) {
        pager.setCurrentItem(3, true);
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
        pager.setCurrentItem(0, true);
        FragmentTransaction pTransaction = getSupportFragmentManager().beginTransaction();
        pTransaction.remove(navDrawer).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).commit();
        drawer.setImageResource(R.drawable.home_img);
    }

    private class AroundPageTransformer implements ViewPager.PageTransformer {
        @Override
        public void transformPage(View view, float position) {
            int pageHeight = view.getHeight();
            int pageWidth = view.getWidth();
            if (position < -1) {
                view.setAlpha(1);
            } else if (position <= 1) {
                int pPagerPosition = pager.getCurrentItem();
                view.setTranslationX(pageWidth * -position);
                view.setTranslationY(position * (pageHeight));
//                switch (pPagerPosition){
//                    case 0:
//                        View pZeroView = fragments.get(pPagerPosition).getView();
//                        pZeroView.setTranslationY(position*(pageHeight));
//                    break;
//                    case 1:
//                        View pFirstView = fragments.get(pPagerPosition).getView();
//                        pFirstView.setTranslationY(position*(pageHeight));
//                        pFirstView.findViewById(R.id.around_bar).setTranslationY(position*(pageHeight/2));
//                        break;
//                    case 2:
//                        View pSecondView = fragments.get(pPagerPosition).getView();
//                        pSecondView.setTranslationY(position*(pageHeight));
//                        pSecondView.findViewById(R.id.list_bar).setTranslationY(position*(pageHeight/2));
//                        break;
//                    case 3:
//                        View pThirdView = fragments.get(pPagerPosition).getView();
//                        pThirdView.setTranslationY(position*(pageHeight));
//                        pThirdView.findViewById(R.id.news_bar).setTranslationY(position*(pageHeight/2));
//                        break;
//                    case 4:
//                        View pFourthView = fragments.get(pPagerPosition).getView();
//                        pFourthView.setTranslationY(position*(pageHeight));
//                        pFourthView.findViewById(R.id.user_bar).setTranslationY(position*(pageHeight/2));
//                        break;
//                }
//                drawer.setTranslationX(position*(pageWidth/3));
//                drawer.setTranslationY(position*(pageHeight/3));
//                drawer.setAlpha(1);
            } else {
                view.setAlpha(1);
            }

        }
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
            pager.setCurrentItem(intent.getIntExtra("PAGE", 0), true);
            drawer.setImageResource(R.drawable.aroundyou_img);
        }
    }
}
