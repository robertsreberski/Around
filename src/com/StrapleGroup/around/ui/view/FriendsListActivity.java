package com.StrapleGroup.around.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.database.OpenHelper;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.intefaces.DataManager;
import com.StrapleGroup.around.ui.controler.FriendsAdapter;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendsListActivity extends Activity {
    private Context context;
    private DataManager dataManager;
    private AtomicInteger atomicInteger = new AtomicInteger();
    private FriendsInfo friend;
    private List<FriendsInfo> friendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        context = getApplicationContext();
        SQLiteOpenHelper openHelper = new OpenHelper(this.context);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        //dataManager initialization
        dataManager = new DataManagerImpl(this.context);
        friend = new FriendsInfo();
//        for (int i = 0; i < 10; i++) {
//            friend.setId(i);
//            friend.setLoginFriend("robin" + i);
//            friend.setXFriend(42.42352);
//            friend.setYFriend(31.123123);
//            dataManager.saveFriendInfo(friend);
//        }
        friendsList = dataManager.getAllFriendsInfo();
        db.endTransaction();
        FriendsAdapter adapter = new FriendsAdapter(context, friendsList);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);


    }

    private void addFriend() {
        Intent pAddFriendIntent = new Intent(FriendsListActivity.this, AddFriendActivity.class);
        startActivity(pAddFriendIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater pInflater = getMenuInflater();
        pInflater.inflate(R.menu.friends_list_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_friend:
                addFriend();
        }

        return super.onOptionsItemSelected(item);
    }
}