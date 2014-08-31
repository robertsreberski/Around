package com.StrapleGroup.around.controler.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;
import com.StrapleGroup.around.base.Constants;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmRequestReceiver extends WakefulBroadcastReceiver implements
        Constants {

    @Override
    public void onReceive(Context context, Intent intent) {
        GoogleCloudMessaging pGcm = GoogleCloudMessaging.getInstance(context);
        Bundle loginResult = intent.getExtras();
        String messageType = pGcm.getMessageType(intent);
        if (!loginResult.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
//				Log.i("GREATEST", "Received: " + loginResult.getString(MESSAGE));
                if (loginResult.getString(ACTION).equals("LOGIN")) {
                    Intent pLoginIntent = new Intent(LOGIN_LOCAL_ACTION);
                    if (loginResult.getString(MESSAGE).equals("valid")) {
                        pLoginIntent.putExtra(MESSAGE, true);
                    } else if (loginResult.getString(MESSAGE).equals("invalid")) {
                        Toast.makeText(context, "Invalid login or password",
                                Toast.LENGTH_LONG).show();
                        pLoginIntent.putExtra(MESSAGE, false);
                    }
                    context.sendBroadcast(pLoginIntent);
                }
                if (loginResult.getString(ACTION).equals("REGISTER")) {
                    Intent pRegisterIntent = new Intent(REGISTER_LOCAL_ACTION);
                    if (loginResult.getString(MESSAGE).equals("completed")) {
                        pRegisterIntent.putExtra(MESSAGE, true);
                    }
                    if (loginResult.getString(MESSAGE).equals("incompleted")) {
                        pRegisterIntent.putExtra(MESSAGE, false);
                    }
                    context.sendBroadcast(pRegisterIntent);
                }
                if (loginResult.getString(ACTION).equals("ADD")) {
                    Intent pAddIntent = new Intent(ADD_LOCAL_ACTION);
                    if (loginResult.getString(MESSAGE).equals("completed")) {
                        pAddIntent.putExtra("LAT", loginResult.getString("x"));
                        pAddIntent.putExtra("LNG", loginResult.getString("y"));
                        pAddIntent.putExtra(MESSAGE, true);
                    }
                    if (loginResult.getString(MESSAGE).equals("incompleted")) {
                        pAddIntent.putExtra(MESSAGE, false);
                    }
                    context.sendBroadcast(pAddIntent);
                }

            }
        }
    }
}
