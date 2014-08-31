package com.StrapleGroup.around.ui.view;

import android.app.Activity;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
	private LoginResultReceiver loginResultReceiver;
    private SharedPreferences userInfoPreferences;
    private SharedPreferences locationPreferences;
    private IntentFilter loginResultFilter;
    private String registrationId = null;
    private String login = null;
    private SharedPreferences gcmStorage;
    private GoogleCloudMessaging googleCloudMessaging = null;
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

        googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
        registrationId = getRegistrationId();
        if (registrationId.isEmpty()) {
            registerInBackground();
        }
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
        Intent pNextActivityIntent = new Intent(this, AroundMapFragment.class);
        startActivity(pNextActivityIntent);
        finish();
    }
	protected void badRequest(){
		Toast.makeText(this, "Unauthorized", Toast.LENGTH_SHORT).show();
	}

    public int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object... params) {
                String msg = "";
                try {
                    if (googleCloudMessaging == null) {
                        googleCloudMessaging = GoogleCloudMessaging
                                .getInstance(context);
                    }
                    registrationId = googleCloudMessaging.register(SENDER_ID);
                    Log.e("HURRAY", "Registration Works");
                    sendRegistrationIdToBackend();
                    storeRegistrationId(context, registrationId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend() throws IOException {
        String msg = "";
        Bundle data = new Bundle();
        data.putString("my_action", "com.StrapleGroup.around.REGISTER");
        String id = UUID.randomUUID().toString();
        googleCloudMessaging.send(SENDER_ID + "@gcm.googleapis.com", id, data);
        Log.e("HURRAY", "Registration Works");
    }

    private void storeRegistrationId(Context context, String regId) {
        gcmStorage = getSharedPreferences(GCM_PREFS, MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.i("REGID_SAVED", "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = gcmStorage.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private String getRegistrationId() {
        gcmStorage = getSharedPreferences(GCM_PREFS, MODE_PRIVATE);
        String registrationId = gcmStorage.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("NO_REGID", "Registration not found.");
            return "";
        }
        return registrationId;
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
