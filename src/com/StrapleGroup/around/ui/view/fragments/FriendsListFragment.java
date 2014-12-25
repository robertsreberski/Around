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
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.ui.controler.SmartListAdapter;
import com.StrapleGroup.around.ui.view.dialogs.FriendDialog;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class FriendsListFragment extends Fragment implements Constants, AdapterView.OnItemClickListener {
    private Context context;
    private DataManagerImpl dataManager;
    private RefreshReceiver refreshReceiver = new RefreshReceiver();
    private DeleteReceiver deleteReceiver;
    private SmartListAdapter smartListAdapter;

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
        ListView listView = (ListView) getActivity().findViewById(R.id.listViewTest);
        listView.setAdapter(smartListAdapter);
        listView.setOnItemClickListener(this);
        startComps();
    }

    @Override
    public void onResume() {
        super.onResume();
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
        Intent pIntent = new Intent(getActivity().getBaseContext(), FriendDialog.class);
        startActivity(pIntent);
    }

    private class RefreshReceiver extends WakefulBroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            smartListAdapter.swapCursor(dataManager.getCompleteCursor());
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