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
    private  IntentFilter resultFilter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerResultReceiver = new RegisterResultReceiver();
        resultFilter = new IntentFilter(REGISTER_LOCAL_ACTION);

        loginField = (EditText) findViewById(R.id.loginField_register);
        passField = (EditText) findViewById(R.id.passField_register);
        context = getApplicationContext();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void register(View v){
        boolean done = true;
        loginField.setError(null);
        passField.setError(null);
        login = loginField.getText().toString();
        pass = passField.getText().toString();
        if(TextUtils.isEmpty(login)){
            done = false;
        }
        if(TextUtils.isEmpty(pass)){
            done = false;
        }
        if(done){
            new AsyncTask<Void, Void, Void>(){

                @Override
                protected Void doInBackground(Void... params) {

                    try {
                        googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
                        Bundle data = new Bundle();
                        String id = "m-"+ UUID.randomUUID().toString();
                        latLngPrefs = getSharedPreferences(LATLNG_PREFS,MODE_PRIVATE);
                        data.putString("action", REGISTER_ACTION);
                        data.putString("login", login);
                        data.putString("password", pass);
                        String pLat = "34.32123";
                        String pLng = "53.31331";
                        if(latLngPrefs.contains("x") && latLngPrefs.contains("y")){
                        pLat = latLngPrefs.getString("x","");
                        pLng = latLngPrefs.getString("y","");}
                        data.putString("x", latLngPrefs.getString("x",pLat));
                        data.putString("y", latLngPrefs.getString("y", pLng));
                        googleCloudMessaging.send(SERVER_ID, id, 0, data);
                        Log.e("GOOD", "sth");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute(null,null,null);
        }

    }

    @Override
    protected void onPause() {
        unregisterReceiver(registerResultReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(registerResultReceiver,resultFilter);
    }

    public void registrationSuccessful() {
        Toast.makeText(context,"Registration successful!",Toast.LENGTH_SHORT).show();
        Intent returnIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(returnIntent);
        finish();
    }
    public void registrationUnsuccessful(){
        Toast.makeText(context, "Something go wrong :c", Toast.LENGTH_SHORT).show();
    }
        private class RegisterResultReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra(MESSAGE,true)){
                registrationSuccessful();
            }else{
                registrationUnsuccessful();
            }
        }
    }

}