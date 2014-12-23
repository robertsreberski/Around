package com.StrapleGroup.around.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.ConnectionHelper;
import com.StrapleGroup.around.ui.utils.ConnectionUtils;
import com.StrapleGroup.around.ui.utils.ImageHelper;
import com.google.android.gms.location.DetectedActivity;

public class LoginActivity extends Activity implements Constants {
    private Context context;
    private EditText loginField;
    private EditText passField;
    private SharedPreferences prefs;
    private String login = null;
    private String pass = null;
    private Button loginButton = null;
    private ProgressBar loginProgress = null;

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
                prefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);

                new AsyncTask<Void, Void, Boolean>() {

                    @Override
                    protected Boolean doInBackground(Void... params) {
                        ConnectionHelper pConnectionHelper = new ConnectionHelper(context);
                        Double pLat = 0.000;
                        Double pLng = 0.000;
                        int pActivity = DetectedActivity.UNKNOWN;
                        if (prefs.contains(KEY_X) && prefs.contains(KEY_Y)) {
                            pLat = Double.parseDouble(prefs.getString(KEY_X, ""));
                            pLng = Double.parseDouble(prefs.getString(KEY_Y, ""));
                        }
                        if (prefs.contains(KEY_ACTIVITY)) pActivity = prefs.getInt(KEY_ACTIVITY, 4);
                        return pConnectionHelper.loginToApp(login, pass, pLat, pLng, pActivity);
                    }

                    @Override
                    protected void onPostExecute(Boolean aBool) {
                        super.onPostExecute(aBool);
                        if (aBool) {
                            SharedPreferences.Editor pEditor = prefs.edit();
                            pEditor.putString(KEY_LOGIN, login);
                            pEditor.putString(KEY_PASS, pass);
                            pEditor.putString(KEY_STATUS, STATUS_ONLINE);
//                            ImageHelper pImageHelper = new ImageHelper();
//                            String photoString = pImageHelper.encodeImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.facebook_example));
//                            pEditor.putString(KEY_PHOTO, photoString);
                            pEditor.commit();
                            nextActivity();
                        } else badRequest();
                    }
                }.execute(null, null, null);
            } else noConnection();
        }
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
