package com.StrapleGroup.around.ui.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;

/**
 * Created by Robert on 2014-09-21.
 */
public class StartActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if(checkIfLogin() == true){
        Intent pIntent = new Intent(this, MainActivity.class);
        startActivity(pIntent);
//        } else {
//            Intent pIntent = new Intent(this, LoginActivity.class);
//            startActivity(pIntent);
//        }

    }

    private boolean checkIfLogin() {
        SharedPreferences sharedUserInfo = getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE);
        boolean pCheck = false;
        if (sharedUserInfo.contains(Constants.KEY_LOGIN) && sharedUserInfo.contains(Constants.KEY_PASS)) {
            pCheck = true;
        }
        return pCheck;
    }
}