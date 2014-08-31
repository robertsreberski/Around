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
import java.util.UUID;

public class LoginActivity extends Activity implements Constants {
	public static final String USER_PREFS = "userLoginData";
	private Context context;
	private EditText loginField;
	private EditText passField;
	public GoogleCloudMessaging googleCloudMessaging;
	private LoginResultReceiver loginResultReceiver;
    private SharedPreferences userInfoPreferences;
    private SharedPreferences locationPreferences;
    private IntentFilter loginResultFilter;
    private String login = null;
    private  String pass = null;
    @Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
        loginResultReceiver = new LoginResultReceiver();
        loginResultFilter = new IntentFilter(LOGIN_LOCAL_ACTION);

		context = getApplicationContext();
		loginField = (EditText) findViewById(R.id.loginField);
		passField = (EditText) findViewById(R.id.passField);
	}
    public void goRegister(View v){
        Intent pGoRegister = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(pGoRegister);
    }
	public void login(View v) {
		boolean done = true;
		loginField.setError(null);
		passField.setError(null);
		login = loginField.getText().toString();
		pass = passField.getText().toString();
		if(TextUtils.isEmpty(login)){
			loginField.setError("Field required");
			done = false;
		}
		else if(TextUtils.isEmpty(pass)){
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
				pLoginData.putString("action", LOGIN_ACTION);

                pLoginData.putString(LOGIN, loginField.getText().toString());
				pLoginData.putString(PASS, passField.getText().toString());
				pLoginData.putString("x", "69.3004");
				pLoginData.putString("y", "30.0049");
				String id = "m-"+UUID.randomUUID().toString();
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
    protected void onPause() {
        unregisterReceiver(loginResultReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(loginResultReceiver, loginResultFilter);
    }

	protected void nextActivity(){
		Intent pNextActivityIntent = new Intent(this, MapActivity.class);
        startActivity(pNextActivityIntent);
        finish();
    }
	protected void badRequest(){
		Toast.makeText(this, "Unauthorized", Toast.LENGTH_SHORT).show();
	}

	private class LoginResultReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getBooleanExtra(MESSAGE, true)){
                userInfoPreferences = getSharedPreferences(USER_PREFS,MODE_PRIVATE);
                SharedPreferences.Editor pEditor = userInfoPreferences.edit();
                pEditor.putString(KEY_LOGIN,login);
                pEditor.putString(KEY_PASS,pass);
                pEditor.commit();
				nextActivity();
			}else {
                badRequest();
            }
		}

	}
}
