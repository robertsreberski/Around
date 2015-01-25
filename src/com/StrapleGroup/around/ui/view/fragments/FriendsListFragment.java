package com.StrapleGroup.around.ui.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.tables.FriendsInfoTable;
import com.StrapleGroup.around.ui.controler.SmartListAdapter;
import com.StrapleGroup.around.ui.controler.SmartListBetterAdapter;
import com.StrapleGroup.around.ui.utils.UpdateHelper;
import com.StrapleGroup.around.ui.view.dialogs.FriendDialog;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.List;

public class FriendsListFragment extends Fragment implements Constants, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String INVITATION_HEADER = "REQUESTED";
    private static final String REQUEST_HEADER = "INVITATIONS";
    private static final String FRIEND_HEADER = "FRIENDS";

    private Context context;
    private DataManagerImpl dataManager;
    private RefreshReceiver refreshReceiver = new RefreshReceiver();
    private DeleteReceiver deleteReceiver;
    private SmartListBetterAdapter smartListAdapter;
    private SwipeRefreshLayout swipeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        deleteReceiver = new DeleteReceiver();
        dataManager = DataManagerImpl.getInstance(context);
        smartListAdapter = new SmartListBetterAdapter(context);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
//        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
//        swipeLayout.setOnRefreshListener(this);
//        swipeLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ListView listView = (ListView) getActivity().findViewById(R.id.list);
        listView.setAdapter(smartListAdapter);
        listView.setOnItemClickListener(this);
        startComps();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshFriendList();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(refreshReceiver);
        getActivity().unregisterReceiver(deleteReceiver);
        super.onDestroy();
    }

    private void startComps() {
        IntentFilter addFilter = new IntentFilter(REFRESH_FRIEND_LIST_LOCAL_ACTION);
        IntentFilter deleteFilter = new IntentFilter(DELETE_LOCAL_ACTION);
        getActivity().registerReceiver(deleteReceiver, deleteFilter);
        getActivity().registerReceiver(refreshReceiver, addFilter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FriendsInfo pFriend = (FriendsInfo) smartListAdapter.getItem(position);
        if (!pFriend.getStatus().equals(STATUS_INVITATION) && !pFriend.getStatus().equals(STATUS_REQUEST)) {
            Intent pIntent = new Intent(getActivity().getBaseContext(), FriendDialog.class);
            Bundle pBundle = new Bundle();
            pBundle.putString(KEY_LOGIN, pFriend.getLoginFriend());
            pBundle.putDouble(KEY_X, pFriend.getXFriend());
            pBundle.putDouble(KEY_Y, pFriend.getYFriend());
            pBundle.putString(KEY_STATUS, pFriend.getStatus());
            pBundle.putString(KEY_ACTIVITY, Integer.toString(pFriend.getActivities()));
            pBundle.putByteArray(KEY_PHOTO, pFriend.getProfilePhoto());
            pIntent.putExtra("friend_bundle", pBundle);
            startActivity(pIntent);
        } else return;
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


    public void refreshFriendList() {
        smartListAdapter.clearList();
        List<FriendsInfo> pList = dataManager.getAllFriendsInfo();
        for (int i = 0; i < pList.size(); i++) {
            if (pList.get(i).getStatus().equals(STATUS_INVITATION)) {
                smartListAdapter.addHeader(INVITATION_HEADER);
                for (int a = 0; a < pList.size(); a++) {
                    if (pList.get(a).getStatus().equals(STATUS_INVITATION)) {
                        smartListAdapter.addItem(pList.get(a));
                    }
                }
                break;
            }
        }
        for (int i = 0; i < pList.size(); i++) {
            if (pList.get(i).getStatus().equals(STATUS_REQUEST)) {
                smartListAdapter.addHeader(REQUEST_HEADER);
                for (int a = 0; a < pList.size(); a++) {
                    if (pList.get(a).getStatus().equals(STATUS_REQUEST)) {
                        smartListAdapter.addItem(pList.get(a));
                    }
                }
                break;
            }
        }
        for (int i = 0; i < pList.size(); i++) {
            if (!pList.get(i).getStatus().equals(STATUS_INVITATION) && !pList.get(i).getStatus().equals(STATUS_REQUEST)) {
                smartListAdapter.addHeader(FRIEND_HEADER);
                for (int a = 0; a < pList.size(); a++) {
                    if (!pList.get(a).getStatus().equals(STATUS_INVITATION) && !pList.get(a).getStatus().equals(STATUS_REQUEST)) {
                        smartListAdapter.addItem(pList.get(a));
                    }
                }
                break;
            }
        }
        smartListAdapter.notifyDataSetChanged();
    }


    private class RefreshReceiver extends WakefulBroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            refreshFriendList();
        }
    }

    private class DeleteReceiver extends WakefulBroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String pLoginToDelete = intent.getStringExtra(KEY_LOGIN);
            dataManager.deleteFriend(dataManager.findFriend(pLoginToDelete));
        }
    }
}