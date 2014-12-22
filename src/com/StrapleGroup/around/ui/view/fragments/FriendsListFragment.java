package com.StrapleGroup.around.ui.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.ui.controler.SmartListAdapter;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class FriendsListFragment extends Fragment implements Constants {
    private Context context;
    private DataManagerImpl dataManager;
    private SharedPreferences userInfoPrefs;
    private ListView requestList;
    private GoogleCloudMessaging googleCloudMessaging;
    private RefreshReceiver refreshReceiver = new RefreshReceiver();
    private RequestReceiver requestReceiver = new RequestReceiver();
    private ViewGroup requestContainer;
    private LinearLayout requestListLayout;
    private ViewGroup viewGroup;
    private ViewGroup friendContainer;
    private LinearLayout friendListLayout;
    private SharedPreferences sharedLocationInfo;
    private DeleteReceiver deleteReceiver;
    private SmartListAdapter smartListAdapter;
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        deleteReceiver = new DeleteReceiver();
        dataManager = new DataManagerImpl(this.context);
        smartListAdapter = new SmartListAdapter(context, dataManager.getCompleteCursor(), 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.request_list, null);
        ListView listView = (ListView) getActivity().findViewById(R.id.listViewTest);
        listView.setAdapter(smartListAdapter);
        startComps();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(requestReceiver);
        getActivity().unregisterReceiver(refreshReceiver);
        getActivity().unregisterReceiver(deleteReceiver);
        super.onDestroy();
    }

    private void badRequest() {
        Toast.makeText(context, "Can't add friend", Toast.LENGTH_SHORT).show();
    }

    public void addFriend(final FriendsInfo aFriend) {
        FriendsInfo pFriend = new FriendsInfo();
        /*
        Space for RESTful
         */
        pFriend.setLoginFriend("test_friend");
        dataManager.saveFriendInfo(aFriend);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                smartListAdapter.swapCursor(dataManager.getCompleteCursor());
                return null;
            }
        }.execute(null, null, null);
    }

    public void addRequest(final String aFriendName, String aLat, String aLng) {
        final ViewGroup newRequest = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.request_item, null);
        ((TextView) newRequest.findViewById(R.id.friend_requesting)).setText(aFriendName);
        userInfoPrefs = getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        final FriendsInfo pRequestingFriend = new FriendsInfo();
        pRequestingFriend.setId(dataManager.getAllFriendsInfo().size());
        pRequestingFriend.setLoginFriend(aFriendName);
        pRequestingFriend.setXFriend(Double.parseDouble(aLat));
        pRequestingFriend.setYFriend(Double.parseDouble(aLng));
        newRequest.findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        userInfoPrefs = getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                         /*
                        Space for RESTful
                            */
                        dataManager.saveFriendInfo(pRequestingFriend);
                        smartListAdapter.swapCursor(dataManager.getCompleteCursor());
                        Log.i("RESPONSE_SEND", "SUCCESSFULY");
                        Log.i("RESPONSE_SEND", "UNSSUCCESSFULY");
                        return null;
                    }
                }.execute(null, null, null);
                requestContainer.removeView(newRequest);
            }
        });
        newRequest.findViewById(R.id.decline_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestContainer.removeView(newRequest);
            }
        });
        requestContainer.addView(newRequest, 0);
    }


    private void startComps() {
        IntentFilter addFilter = new IntentFilter(REFRESH_FRIEND_LIST_LOCAL_ACTION);
        IntentFilter requestFilter = new IntentFilter(ADD_REQUEST_LOCAL_ACTION);
        IntentFilter deleteFilter = new IntentFilter(DELETE_LOCAL_ACTION);
        getActivity().registerReceiver(requestReceiver, requestFilter);
        getActivity().registerReceiver(deleteReceiver, deleteFilter);
        getActivity().registerReceiver(refreshReceiver, addFilter);
    }

    private class RefreshReceiver extends WakefulBroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            smartListAdapter.swapCursor(dataManager.getCompleteCursor());
        }
    }

    private class RequestReceiver extends WakefulBroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            friendListLayout = (LinearLayout) getActivity().findViewById(R.id.friend_list_layout);
            requestListLayout = (LinearLayout) viewGroup.findViewById(R.id.request_list);
            requestContainer = (ViewGroup) viewGroup.findViewById(R.id.request_container);
            friendListLayout.addView(requestListLayout, 1);
            addRequest(intent.getStringExtra("friend_name"), intent.getStringExtra("LAT"), intent.getStringExtra("LNG"));
        }
    }

    private class DeleteReceiver extends WakefulBroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String pLoginToDelete = intent.getStringExtra(KEY_LOGIN);
            dataManager.deleteFriend(dataManager.findFriend(pLoginToDelete));
            smartListAdapter.swapCursor(dataManager.getCompleteCursor());
        }
    }
}