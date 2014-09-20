package com.StrapleGroup.around.ui.view;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Robert on 2014-09-19.
 */
public class UserInfoActivity extends Activity {
    Context context;
    private GoogleCloudMessaging googleCloudMessaging;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        context = getApplicationContext();

    }

    public void photo(View view) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                googleCloudMessaging = GoogleCloudMessaging.getInstance(context);
                Bundle pPhotoBundle = new Bundle();
                pPhotoBundle.putString(Constants.KEY_ACTION, Constants.PHOTO_ACTION);
                pPhotoBundle.putString("photo", getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE).getString("photo", ""));
                try {
                    googleCloudMessaging.send(Constants.SERVER_ID, UUID.randomUUID().toString(), pPhotoBundle);
                    Log.i("PHOTO", "SENDED");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);
    }
}