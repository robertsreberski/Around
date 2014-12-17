package com.StrapleGroup.around.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
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
import com.StrapleGroup.around.ui.utils.ImageHelper;

public class LoginActivity extends Activity implements Constants {
    private Context context;
    private EditText loginField;
    private EditText passField;
    private SharedPreferences prefs;
    private String login = null;
    private String pass = null;
    private Button loginButton = null;
    private ProgressBar loginProgress = null;
//    private ConnectivityManager connectivityManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();
        loginField = (EditText) findViewById(R.id.loginField);
        passField = (EditText) findViewById(R.id.passField);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginProgress = (ProgressBar) findViewById(R.id.loginProgress);
        loginProgress.setVisibility(View.INVISIBLE);
//        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    }

    public void goRegister(View v) {
        Intent pGoRegister = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(pGoRegister);
    }

    public void login(View v) {
        boolean done = true;
        loginField.setError(null);
        passField.setError(null);
        login = loginField.getText().toString();
        pass = passField.getText().toString();
        if (TextUtils.isEmpty(login)) {
            loginField.setError("Field required");
            done = false;
        } else if (TextUtils.isEmpty(pass)) {
            passField.setError("Field required");
            done = false;
        }
        if (done) {
            if (ConnectionUtils.hasActiveInternetConnection(context)) {
                loginButton.setText("");
                loginProgress.setVisibility(View.VISIBLE);
                ImageHelper pImageHelper = new ImageHelper();
                prefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor pPrefsEditor = prefs.edit();
                pPrefsEditor.putString(KEY_LOGIN, login);
                pPrefsEditor.putString(KEY_PASS, pass);
                pPrefsEditor.putString(KEY_STATUS, STATUS_ONLINE);
                pPrefsEditor.putString(KEY_PHOTO, pImageHelper.encodeImage(BitmapFactory.decodeResource(getResources(), R.drawable.facebook_example)));
                pPrefsEditor.apply();
                try {
                    int aSet = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
                    Log.i("LOC", String.valueOf(aSet));
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {

                        Log.e("DID IT", "Lets done");
                        return null;
                    }
                };
                /*
                Space for RESTful
                 */
                nextActivity();
            } else {
                noConnection();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void nextActivity() {
        Intent pNextActivityIntent = new Intent(this, MainActivity.class);
        startActivity(pNextActivityIntent);
        finish();
    }

    private void noConnection() {
        loginProgress.setVisibility(View.INVISIBLE);
        loginButton.setText("Log in");
        Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
    }

    protected void badRequest() {
        loginProgress.setVisibility(View.INVISIBLE);
        loginButton.setText("Log in");
        Toast.makeText(this, "Unauthorized", Toast.LENGTH_SHORT).show();
    }

}
