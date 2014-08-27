package com.StrapleGroup.around.ui.view;

import android.app.Activity;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginActivity extends Activity implements Constants {
	public static final String USER_PREFS = "userLoginData";
	private Context context;
	private EditText loginField;
	private EditText passField;
	private SharedPreferences sharedUserInfo;
	public GoogleCloudMessaging googleCloudMessaging;
	private AtomicInteger msgId = new AtomicInteger();
	private LoginResultReceiver loginResultReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		loginResultReceiver = new LoginResultReceiver();
		IntentFilter loginResultFilter = new IntentFilter(LOGIN_ACTION);
		registerReceiver(loginResultReceiver, loginResultFilter);
		setContentView(R.layout.activity_login);
		context = getApplicationContext();
		sharedUserInfo = getSharedPreferences(USER_PREFS, 0);
		loginField = (EditText) findViewById(R.id.loginField);
		passField = (EditText) findViewById(R.id.passField);
	}

	public void login(View v) {
		boolean done = true;
		loginField.setError(null);
		passField.setError(null);
		String pLogin = loginField.getText().toString();
		String pPass = passField.getText().toString();
		if(TextUtils.isEmpty(pLogin)){
			loginField.setError("Field required");
			done = false;
		}
		else if(TextUtils.isEmpty(pPass)){
			passField.setError("Field required");
			done = false;
		}
		if(done){
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				googleCloudMessaging = GoogleCloudMessaging
						.getInstance(context);
				Bundle pLoginData = new Bundle();
				pLoginData.putString("action", "com.StrapleGroup.gcm.LOGIN");
				pLoginData.putString(LOGIN, loginField.getText().toString());
				pLoginData.putString(PASS, passField.getText().toString());
				pLoginData.putString("x", "69.3004");
				pLoginData.putString("y", "30.0049");
				String id = Integer.toString(msgId.incrementAndGet());
				try {
					
					googleCloudMessaging.send(SERVER_ID, id, pLoginData);
					Log.i("REQUESTED SUCCESSFUL",
							"*************************************************");
				} catch (IOException e) {
					Log.e("PROBLEM WITH LOGIN REQUEST",
							"*******************************************************");
				}
				return null;
			}

		}.execute(null, null, null);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(loginResultReceiver);
		super.onStop();
		
	}
	protected void nextActivity(){
		Intent pNextActivityIntent = new Intent(this, MapActivity.class);
		startActivity(pNextActivityIntent);
	}
	protected void badRequest(){
		Toast.makeText(this, "Unauthorized", Toast.LENGTH_SHORT).show();
	}

	private class LoginResultReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getBooleanExtra(MESSAGE, true)){
				nextActivity();
			}else {
                badRequest();
            }
		}

	}
}
