package com.StrapleGroup.around.ui.view;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.StrapleGroup.around.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class LoginActivity extends Activity {
	public static final String USER_PREFS = "userLoginData";
	private Context context;
	private EditText loginField;
	private EditText passField;
	private SharedPreferences sharedUserInfo;
	public GoogleCloudMessaging googleCloudMessaging;
	private AtomicInteger msgId = new AtomicInteger();
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
		final SharedPreferences prefs = getSharedPreferences(MapActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
		String registrationId = prefs.getString(MapActivity.PROPERTY_REG_ID, "");
        new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... voids) {
				googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
				Bundle data = new Bundle();
				data.putString("login", "This kurwa works");
				data.putString("password", );
				 String id = Integer.toString(msgId.incrementAndGet());
				try {
					googleCloudMessaging.send(MapActivity.SENDER_ID + "@gcm.googleapis.com", id, data);
					Log.e("SENDED", "THIS KURWA WORKS");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		}.execute(null, null, null);
			
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
