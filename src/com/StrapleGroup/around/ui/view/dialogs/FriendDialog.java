package com.StrapleGroup.around.ui.view.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.controler.ConnectionHelper;
import com.StrapleGroup.around.database.DataManagerImpl;
import com.StrapleGroup.around.ui.utils.ImageHelper;

/**
 * Created by Robert on 2014-12-24.
 */
public class FriendDialog extends Activity implements Constants {
    TextView loginView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_friend);
        getWindow().setLayout(1300, 1500);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        friendCustomizer(intent);
        ImageButton pClose = (ImageButton) findViewById(R.id.close);
        pClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button pDeleteAction = (Button) findViewById(R.id.delete_button);
        pDeleteAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Boolean>() {

                    @Override
                    protected Boolean doInBackground(Void... params) {
                        SharedPreferences pPrefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
                        ConnectionHelper pConnectionHelper = new ConnectionHelper(getApplicationContext());
                        return pConnectionHelper.sendDeleteRequest(pPrefs.getString(KEY_LOGIN, ""), pPrefs.getString(KEY_PASS, ""), loginView.getText().toString());
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        if (aBoolean == true) {
                            DataManagerImpl dataManager = new DataManagerImpl(getApplicationContext());
                            dataManager.deleteFriend(dataManager.findFriend(loginView.getText().toString()));
                            dataManager.deleteAround(dataManager.findAround(loginView.getText().toString()));
                            sendBroadcast(new Intent(REFRESH_FRIEND_LIST_LOCAL_ACTION));
                            finish();
                        }
                    }
                }.execute(null, null, null);
            }
        });
    }

    public void friendCustomizer(Intent aIntent) {
        Bundle pResult = aIntent.getBundleExtra("friend_bundle");
        loginView = (TextView) findViewById(R.id.login);
        TextView pDistance = (TextView) findViewById(R.id.distance);
        ImageView pPhotoView = (ImageView) findViewById(R.id.profilePhoto);
        ImageView pStatusView = (ImageView) findViewById(R.id.status_view);
        loginView.setText(pResult.getString(KEY_LOGIN));
        ImageHelper pImageHelper = new ImageHelper();
        pImageHelper.setImg(getApplicationContext(), pPhotoView, pImageHelper.decodeImageFromBytes(pResult.getByteArray(KEY_PHOTO)));
    }
}