package com.StrapleGroup.around.ui.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.ui.controler.AroundListAdapter;

/**
 * Created by Robert on 2014-09-21.
 */
public class AroundFriendsFragment extends Fragment {
    private AroundListAdapter aroundListAdapter;
    private Context context;
    private DataManagerImpl dataManager;
    private ViewGroup viewGroup;
    private RefreshReceiver refreshReceiver = new RefreshReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        dataManager = new DataManagerImpl(context);
        getActivity().registerReceiver(refreshReceiver, new IntentFilter(Constants.REFRESH_FRIEND_LIST_LOCAL_ACTION));
        aroundListAdapter = new AroundListAdapter(context, dataManager.getAroundCursor(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_around_friends, container, false);
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

    private class RefreshReceiver extends WakefulBroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            aroundListAdapter.swapCursor(dataManager.getAroundCursor());
        }
    }
}