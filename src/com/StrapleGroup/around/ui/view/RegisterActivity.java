package com.StrapleGroup.around.ui.view;

import android.app.Activity;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.ui.utils.ConnectionUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Robert on 2014-08-27.
 */
public class RegisterActivity extends Activity implements Constants {
    private EditText loginField;
    private EditText passField;
    private Context context;
    private GoogleCloudMessaging googleCloudMessaging;
    private SharedPreferences latLngPrefs;
    private RegisterResultReceiver registerResultReceiver;
    private String login;
    private String pass;
    private IntentFilter resultFilter;
    private ProgressBar registerProgress;
    private Button registerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerResultReceiver = new RegisterResultReceiver();
        resultFilter = new IntentFilter(REGISTER_LOCAL_ACTION);
        registerButton = (Button) findViewById(R.id.registerButton);
        loginField = (EditText) findViewById(R.id.loginField_register);
        passField = (EditText) findViewById(R.id.passField_register);
        registerProgress = (ProgressBar) findViewById(R.id.registerProgress);
        registerProgress.setVisibility(View.INVISIBLE);
        context = getApplicationContext();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void register(View v) {
        boolean done = true;
        loginField.setError(null);
        passField.setError(null);
        login = loginField.getText().toString();
        pass = passField.getText().toString();
        if (TextUtils.isEmpty(login)) {
            done = false;
        }
        if (TextUtils.isEmpty(pass)) {
            done = false;
        }
        if (done) {
            if (ConnectionUtils.hasActiveInternetConnection(context)) {
                registerButton.setText("");
                registerProgress.setVisibility(View.VISIBLE);
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {

                        try {
                            googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
                            Bundle data = new Bundle();
                            String id = "m-" + UUID.randomUUID().toString();
                            latLngPrefs = getSharedPreferences(LATLNG_PREFS, MODE_PRIVATE);
                            data.putString(KEY_ACTION, REGISTER_ACTION);
                            data.putString(KEY_LOGIN, login);
                            data.putString(KEY_SERVER_PASS, pass);
                            String pLat = "00.00000";
                            String pLng = "00.00000";
                            if (latLngPrefs.contains("LAT") && latLngPrefs.contains("LNG")) {
                                pLat = latLngPrefs.getString("LAT", "");
                                pLng = latLngPrefs.getString("LNG", "");
                            }
                            data.putString("x", latLngPrefs.getString("x", pLat));
                            data.putString("y", latLngPrefs.getString("y", pLng));
                            googleCloudMessaging.send(SERVER_ID, id, 0, data);
                            Log.e("GOOD", "sth");
                        } catch (IOException e) {
                            registerProgress.setVisibility(View.INVISIBLE);
                            registerButton.setText("Register");
                            Log.e("PROBLEM WITH LOGIN REQUEST",
                                    "*******************************************************");
                        }
                        return null;
                    }
                }.execute(null, null, null);
            } else {
                noConnection();
                noConnection();
            }
        }


    }

    @Override
    protected void onPause() {
        unregisterReceiver(registerResultReceiver);
        super.onPause();
    }

    private void noConnection() {
        Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(registerResultReceiver, resultFilter);
    }

    public void registrationSuccessful() {
        Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show();
        Intent returnIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(returnIntent);
        finish();
    }

    public void registrationUnsuccessful() {
        Toast.makeText(context, "Something go wrong :c", Toast.LENGTH_SHORT).show();
    }

    private class RegisterResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(KEY_MESSAGE, true)) {
                registrationSuccessful();
            } else {
                registrationUnsuccessful();
            }
        }
    }

}