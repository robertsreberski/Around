package com.StrapleGroup.around.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.StrapleGroup.around.R;

public class FriendsListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_list);
	}

	public void back(View v) {
		Intent intentBack = new Intent(getApplicationContext(), MapActivity.class);
		startActivity(intentBack);
	}
}