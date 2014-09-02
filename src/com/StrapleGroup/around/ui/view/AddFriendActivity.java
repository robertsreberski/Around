package com.StrapleGroup.around.ui.view;

import android.app.Activity;
import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.OpenHelper;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Robert on 2014-08-30.
 */
public class AddFriendActivity extends Activity implements Constants {
    private DataManagerImpl dataManager;
    private SQLiteDatabase db;
    private Context context;
    private EditText friendLogin;
    private GoogleCloudMessaging googleCloudMessaging;
    private SharedPreferences userInfoPrefs;
    private FriendAddResultReceiver friendAddResultReceiver;
    private AtomicInteger atomicInt;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend_activity);
        context = getApplicationContext();
        SQLiteOpenHelper openHelper = new OpenHelper(this.context);
        db = openHelper.getWritableDatabase();
        //dataManager initialization
        dataManager = new DataManagerImpl(this.context);
        friendLogin = (EditText) findViewById(R.id.friend_field);
        friendAddResultReceiver = new FriendAddResultReceiver();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(friendAddResultReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter friendAddResultFilter = new IntentFilter(ADD_LOCAL_ACTION);
        registerReceiver(friendAddResultReceiver, friendAddResultFilter);
    }

    public void add(View view) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                if (googleCloudMessaging == null) {
                    googleCloudMessaging = GoogleCloudMessaging
                            .getInstance(context);
                }
                Bundle pFriendDataBundle = new Bundle();
                userInfoPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
                pFriendDataBundle.putString("action", ADD_ACTION);
                pFriendDataBundle.putString("login", userInfoPrefs.getString(KEY_LOGIN, ""));
                pFriendDataBundle.putString("friend_login", friendLogin.getText().toString());
                try {
                    googleCloudMessaging.send(SERVER_ID, "m-" + UUID.randomUUID().toString(), pFriendDataBundle);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);
    }

    private void badRequest() {
        Toast.makeText(context, "Can't add friend", Toast.LENGTH_SHORT).show();
    }

    private class FriendAddResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(MESSAGE, true)) {
                final FriendsInfo pFriend = new FriendsInfo();
                userInfoPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
                int pMsgId = 0;
                if (userInfoPrefs.getInt(MSG_ID, 0) != 0) {
                    pMsgId = userInfoPrefs.getInt(MSG_ID, 0);
                    userInfoPrefs.edit().putInt(MSG_ID, pMsgId + 1).commit();
                    pMsgId++;
                } else {
                    userInfoPrefs.edit().putInt(MSG_ID, 1).commit();
                    pMsgId = 1;
                }
                pFriend.setId(pMsgId);
                pFriend.setLoginFriend(friendLogin.getText().toString());
                pFriend.setYFriend(Double.parseDouble(intent.getStringExtra("LAT")));
                pFriend.setXFriend(Double.parseDouble(intent.getStringExtra("LNG")));
                dataManager.saveFriendInfo(pFriend);
                Intent successfulAddIntent = new Intent(context, SwipeActivities.class);
                startActivity(successfulAddIntent);
                finish();
            } else {
                badRequest();
            }
        }

    }
}