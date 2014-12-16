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
import com.StrapleGroup.around.ui.utils.ConnectionUtils;
import com.StrapleGroup.around.ui.utils.ImageCompressor;

/**
 * Created by Robert on 2014-08-27.
 */
public class RegisterActivity extends Activity implements Constants {
    private EditText loginField;
    private EditText passField;
    private Context context;
    private String login;
    private String pass;
    private ProgressBar registerProgress;
    private Button registerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
                        ImageCompressor pImageCompressor = new ImageCompressor();
                        SharedPreferences.Editor pPrefsEditor = getSharedPreferences(USER_PREFS, MODE_PRIVATE).edit();
                        pPrefsEditor.putString(KEY_LOGIN, login);
                        pPrefsEditor.putString(KEY_PASS, pass);
                        pPrefsEditor.putString(KEY_STATUS, STATUS_ONLINE);
                        pPrefsEditor.putString(KEY_PHOTO, pImageCompressor.encodeImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.facebook_example)));
                        return null;
                    }
                }.execute(null, null, null);
                registrationSuccessful();
            } else {
                noConnection();
            }
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void noConnection() {
        Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void registrationSuccessful() {
        Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show();
        Intent returnIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(returnIntent);
        finish();
    }

    public void registrationUnsuccessful() {
        Toast.makeText(context, "Something went wrong :c", Toast.LENGTH_SHORT).show();
    }


}