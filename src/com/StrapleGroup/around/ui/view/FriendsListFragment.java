package com.StrapleGroup.around.ui.view;

import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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
import com.StrapleGroup.around.database.OpenHelper;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.intefaces.DataManager;
import com.StrapleGroup.around.ui.controler.FriendsAdapter;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class FriendsListFragment extends ListFragment implements Constants {
    private Context context;
    private DataManager dataManager;
    private List<FriendsInfo> friendsList;
    private SharedPreferences userInfoPrefs;
    private ListView requestList;
    private GoogleCloudMessaging googleCloudMessaging;
    private FriendAddResultReceiver friendAddResultReceiver = new FriendAddResultReceiver();
    private RequestReceiver requestReceiver = new RequestReceiver();
    FriendsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        SQLiteOpenHelper openHelper = new OpenHelper(this.context);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        //dataManager initialization
        dataManager = new DataManagerImpl(this.context);
        friendsList = dataManager.getAllFriendsInfo();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter addFilter = new IntentFilter(ADD_LOCAL_ACTION);
        IntentFilter requestFilter = new IntentFilter(ADD_REQUEST_LOCAL_ACTION);
        getActivity().registerReceiver(requestReceiver, requestFilter);
        getActivity().registerReceiver(friendAddResultReceiver, addFilter);
        adapter = new FriendsAdapter(context, friendsList);
        this.setListAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(requestReceiver);
        getActivity().unregisterReceiver(friendAddResultReceiver);
        super.onDestroy();
    }

    private void badRequest() {
        Toast.makeText(context, "Can't add friend", Toast.LENGTH_SHORT).show();
    }

    public void addRequest(final String aFriendName) {
        final ViewGroup newRequest = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.request_item, null);
        ((TextView) newRequest.findViewById(R.id.friend_requesting)).setText(aFriendName);
        userInfoPrefs = getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
        newRequest.findViewById(R.id.accept_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        userInfoPrefs = getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                        googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
                        Bundle pResponse = new Bundle();
                        pResponse.putString(ACTION, ADD_RESPONSE);
                        pResponse.putString(MESSAGE, "accepted");
                        // Lub "unaccepted" ;
                        pResponse.putString("friend_login", userInfoPrefs.getString(LOGIN, ""));
                        pResponse.putString("login", aFriendName);
                        try {
                            googleCloudMessaging.send(SERVER_ID, "m-" + UUID.randomUUID().toString(), pResponse);
                            Log.i("RESPONSE_SEND", "SSUCCESSFULY");
                        } catch (IOException e) {
                            Log.i("RESPONSE_SEND", "UNSSUCCESSFULY");
                            e.printStackTrace();
                        }

                        return null;
                    }
                }.execute(null, null, null);
                requestList.removeView(newRequest);
            }
        });
        newRequest.findViewById(R.id.reject_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        userInfoPrefs = getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                        googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
                        Bundle pResponse = new Bundle();
                        pResponse.putString(ACTION, ADD_RESPONSE);
                        pResponse.putString(MESSAGE, "unaccepted");
                        pResponse.putString("friend_login", userInfoPrefs.getString(LOGIN, ""));
                        pResponse.putString("login", aFriendName);
                        try {
                            googleCloudMessaging.send(SERVER_ID, "m-" + UUID.randomUUID().toString(), pResponse);
                            Log.i("RESPONSE_SEND", "SSUCCESSFULY");
                        } catch (IOException e) {
                            Log.i("RESPONSE_SEND", "UNSSUCCESSFULY");
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute(null, null, null);
                requestList.removeView(newRequest);
            }
        });
        requestList.addView(newRequest, 0);
    }


    private class FriendAddResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(MESSAGE, true)) {
                final FriendsInfo pFriend = new FriendsInfo();
                userInfoPrefs = getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
                pFriend.setId(friendsList.size());
                pFriend.setLoginFriend(SwipeActivities.getFriendLogin().getText().toString());
                pFriend.setYFriend(Double.parseDouble(intent.getStringExtra("LAT")));
                pFriend.setXFriend(Double.parseDouble(intent.getStringExtra("LNG")));
                dataManager.saveFriendInfo(pFriend);
                adapter.notifyDataSetChanged();
            } else {
                badRequest();
            }
        }
    }

    private class RequestReceiver extends BroadcastReceiver {
        private LinearLayout container;
        private LinearLayout requestListLayout;

        @Override
        public void onReceive(Context context, Intent intent) {
            ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.request_list, null);
            requestListLayout = (LinearLayout) viewGroup.findViewById(R.id.request_list);
            container = (LinearLayout) getActivity().findViewById(R.id.friend_list_layout);
            container.addView(requestList, 0);
            requestList = (ListView) getActivity().findViewById(R.id.request_list_view);
            addRequest(intent.getStringExtra("friend_name"));
        }
    }
}