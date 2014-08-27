package com.StrapleGroup.around.controler.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;
import com.StrapleGroup.around.base.Constants;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class LoginRequestReceiver extends WakefulBroadcastReceiver implements
		Constants {

	@Override
	public void onReceive(Context context, Intent intent) {
		GoogleCloudMessaging pGcm = GoogleCloudMessaging.getInstance(context);
		Bundle loginResult = intent.getExtras();
		String messageType = pGcm.getMessageType(intent);
		Intent pIntent = new Intent(LOGIN_ACTION);
		if (!loginResult.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				Log.i("GREATEST", "Received: " + loginResult.getString(MESSAGE));
				if (loginResult.getString(MESSAGE).equals("valid")) {
					pIntent.putExtra(MESSAGE, true);
				} else if (loginResult.getString(MESSAGE).equals("invalid")) {
					Toast.makeText(context, "Invalid login or password",
							Toast.LENGTH_LONG).show();
					pIntent.putExtra(MESSAGE, false);
				}
				context.sendBroadcast(pIntent);
			}
		}
	}
}
