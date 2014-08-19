package com.StrapleGroup.around.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.StrapleGroup.around.R;

public class LoginActivity extends Activity {
	public static final String USER_PREFS = "userLoginData";
	private Context context;
	private EditText loginField;
	private EditText passField;
	private SharedPreferences sharedUserInfo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		context = getApplicationContext();
		sharedUserInfo = getSharedPreferences(USER_PREFS, 0);
		loginField = (EditText) findViewById(R.id.loginField);
		passField = (EditText) findViewById(R.id.passField);
	}
	
	public void login(View v){
		Editor pLoginEditor = sharedUserInfo.edit();
		pLoginEditor.putString(MapActivity.KEY_LOGIN, loginField.getText().toString());
		pLoginEditor.putString(MapActivity.KEY_PASS, passField.getText().toString());
		Log.i("USER_DATA_SAVED", loginField.getText().toString() + " , " + passField.getText().toString());
		pLoginEditor.commit();
		if(sharedUserInfo.contains(MapActivity.KEY_LOGIN) && sharedUserInfo.contains(MapActivity.KEY_PASS)){
			Intent loginSuccessfulIntent = new Intent(this.context, MapActivity.class);
			startActivity(loginSuccessfulIntent);
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
	}
}
