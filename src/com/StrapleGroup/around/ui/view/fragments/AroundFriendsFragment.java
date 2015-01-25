package com.StrapleGroup.around.ui.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.services.DataRefreshService;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.ui.controler.AroundListAdapter;
import com.StrapleGroup.around.ui.utils.UpdateHelper;

/**
 * Created by Robert on 2014-09-21.
 */
public class AroundFriendsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private AroundListAdapter aroundListAdapter;
    private Context context;
    private DataManagerImpl dataManager;
    private ViewGroup viewGroup;
    private RefreshReceiver refreshReceiver = new RefreshReceiver();
    private SwipeRefreshLayout swipeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        dataManager = DataManagerImpl.getInstance(context);
        getActivity().registerReceiver(refreshReceiver, new IntentFilter(Constants.REFRESH_FRIEND_LIST_LOCAL_ACTION));
        aroundListAdapter = new AroundListAdapter(context, dataManager.getAroundCursor(), 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_around_friends, container, false);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light
                , android.R.color.holo_orange_light, android.R.color.holo_red_light);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ListView listView = (ListView) getActivity().findViewById(R.id.listView_around);
        listView.setAdapter(aroundListAdapter);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(refreshReceiver);
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        UpdateHelper pUpdateHelper = new UpdateHelper(context);
                        pUpdateHelper.getUpdateOnDemand();
                        return null;
                    }
                }.execute(null, null, null);

                swipeLayout.setRefreshing(false);
            }
        }, 1000);
    }

    private class RefreshReceiver extends WakefulBroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            aroundListAdapter.swapCursor(dataManager.getAroundCursor());
        }
    }
}