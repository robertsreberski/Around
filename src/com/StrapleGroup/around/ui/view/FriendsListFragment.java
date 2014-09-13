package com.StrapleGroup.around.ui.view;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class FriendsListFragment extends Fragment implements Constants {
    private Context context;
    private DataManager dataManager;
    private SharedPreferences userInfoPrefs;
    private ListView requestList;
    private GoogleCloudMessaging googleCloudMessaging;
    private FriendAddResultReceiver friendAddResultReceiver = new FriendAddResultReceiver();
    private RequestReceiver requestReceiver = new RequestReceiver();
    private ViewGroup requestContainer;
    private LinearLayout requestListLayout;
    private ViewGroup viewGroup;
    private ViewGroup friendContainer;
    private Cursor friendCursor;
    private LinearLayout friendListLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        SQLiteOpenHelper openHelper = new OpenHelper(this.context);
        SQLiteDatabase db = openHelper.getReadableDatabase();
        //dataManager initialization
        dataManager = new DataManagerImpl(this.context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        doFriendList();
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        viewGroup = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.request_list, null);
        IntentFilter addFilter = new IntentFilter(ADD_LOCAL_ACTION);
        IntentFilter requestFilter = new IntentFilter(ADD_REQUEST_LOCAL_ACTION);
        getActivity().registerReceiver(requestReceiver, requestFilter);
        getActivity().registerReceiver(friendAddResultReceiver, addFilter);
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

    public void doFriendList() {
        List<FriendsInfo> friendsList = dataManager.getAllFriendsInfo();
        for (int pCount = 0; pCount < friendsList.size(); pCount++) {
            int pInteger = pCount + 1;
            final FriendsInfo pFriend = friendsList.get(pCount);
            addFriend(pFriend);
        }
    }

    public void addFriend(final FriendsInfo aFriend) {
        friendListLayout = (LinearLayout) getActivity().findViewById(R.id.friend_list_layout);
        friendContainer = (ViewGroup) getActivity().findViewById(R.id.friend_container);
        final ViewGroup newFriend = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.friend_item, null);
        ((TextView) newFriend.findViewById(R.id.friend_name)).setText(aFriend.getLoginFriend());
        ((TextView) newFriend.findViewById(R.id.latView)).setText(Double.toString(aFriend.getXFriend()));
        ((TextView) newFriend.findViewById(R.id.lngView)).setText(Double.toString(aFriend.getYFriend()));
        newFriend.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataManager.deleteFriend(aFriend.getId());
                friendContainer.removeView(newFriend);
            }
        });
        friendContainer.addView(newFriend, 0);
    }

    public void addRequest(final String aFriendName, String aLat, String aLng) {
        final ViewGroup newRequest = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.request_item, null);
        ((TextView) newRequest.findViewById(R.id.friend_requesting)).setText(aFriendName);
        userInfoPrefs = getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
        googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
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
                        googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
                        Bundle pResponse = new Bundle();
                        pResponse.putString(KEY_ACTION, ADD_RESPONSE);
                        pResponse.putString(KEY_MESSAGE, "accepted");
                        pResponse.putString("friend_login", userInfoPrefs.getString(KEY_LOGIN, ""));
                        pResponse.putString("login", aFriendName);
                        try {
                            googleCloudMessaging.send(SERVER_ID, "m-" + UUID.randomUUID().toString(), pResponse);
                            Log.i("RESPONSE_SEND", "SSUCCESSFULY");
                            dataManager.saveFriendInfo(pRequestingFriend);

                        } catch (IOException e) {
                            Log.i("RESPONSE_SEND", "UNSSUCCESSFULY");
                            e.printStackTrace();
                        }

                        return null;
                    }
                }.execute(null, null, null);
                requestContainer.removeView(newRequest);
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
                        pResponse.putString(KEY_ACTION, ADD_RESPONSE);
                        pResponse.putString(KEY_MESSAGE, "unaccepted");
                        pResponse.putString("friend_login", userInfoPrefs.getString(KEY_LOGIN, ""));
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
                requestContainer.removeView(newRequest);
            }
        });
        requestContainer.addView(newRequest, 0);
    }

    private class FriendAddResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            if (intent.getBooleanExtra(MESSAGE, true)) {
            final FriendsInfo pFriend = new FriendsInfo();
            userInfoPrefs = getActivity().getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE);
            pFriend.setId(dataManager.getAllFriendsInfo().size());
            pFriend.setLoginFriend(MainActivity.getFriendLogin().getText().toString());
            pFriend.setXFriend(Double.parseDouble(intent.getStringExtra("LAT")));
            pFriend.setYFriend(Double.parseDouble(intent.getStringExtra("LNG")));
            dataManager.saveFriendInfo(pFriend);
            addFriend(pFriend);
        }
    }

    private class RequestReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            friendListLayout = (LinearLayout) getActivity().findViewById(R.id.friend_list_layout);
            requestListLayout = (LinearLayout) viewGroup.findViewById(R.id.request_list);
            requestContainer = (ViewGroup) viewGroup.findViewById(R.id.request_container);
            friendListLayout.addView(requestListLayout, 0);
            addRequest(intent.getStringExtra("friend_name"), intent.getStringExtra("LAT"), intent.getStringExtra("LNG"));
        }
    }
}