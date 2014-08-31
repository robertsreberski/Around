package com.StrapleGroup.around.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.OpenHelper;
import com.StrapleGroup.around.database.base.FriendsInfo;

import java.util.Random;

/**
 * Created by Robert on 2014-08-30.
 */
public class AddFriendActivity extends Activity {
    private DataManagerImpl dataManager;
    private SQLiteDatabase db;
    private Context context;
    private EditText friendLogin;

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
    }

    public void add(View view) {
        Random random = new Random();
        FriendsInfo pFriend = new FriendsInfo();
        pFriend.setId(random.nextLong());
        pFriend.setLoginFriend(friendLogin.getText().toString());
        pFriend.setYFriend(0.00);
        pFriend.setXFriend(0.00);
        dataManager.saveFriendInfo(pFriend);
        Intent intent = new Intent(context, FriendsListActivity.class);
        startActivity(intent);
        finish();
    }
}