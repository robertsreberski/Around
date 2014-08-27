package com.StrapleGroup.around.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.database.OpenHelper;
import com.StrapleGroup.around.database.base.FriendsInfo;
import com.StrapleGroup.around.database.daos.FriendsInfoDao;
import com.StrapleGroup.around.ui.controler.FriendsAdapter;

public class FriendsListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_friends_list);
	        ListView list = (ListView) findViewById(R.id.list);
	        SQLiteOpenHelper openHelper = new OpenHelper(this.getApplicationContext());
			SQLiteDatabase db = openHelper.getWritableDatabase();
	        FriendsInfoDao pFriendsDao = new FriendsInfoDao(db);
	        
	        ArrayAdapter<FriendsInfo> adapter = new FriendsAdapter(this.getApplicationContext(), pFriendsDao.get(0));
	        list.setAdapter(adapter);


	}

	public void back(View v) {
		Intent intentBack = new Intent(getApplicationContext(), MapActivity.class);
		startActivity(intentBack);
	}
}