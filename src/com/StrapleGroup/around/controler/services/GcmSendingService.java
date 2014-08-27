package com.StrapleGroup.around.controler.services;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmSendingService extends WakefulBroadcastReceiver {
	  @Override
	    public void onReceive(Context context, Intent intent) {
//	         Explicitly specify that GcmIntentService will handle the intent.
//	        ComponentName comp = new ComponentName(context.getPackageName(), LoginService.class.getName());
//	         Start the service, keeping the device awake while it is launching.
//	        Intent pIntent = new Intent(context, (intent.setComponent(comp)));
	        
	        setResultCode(Activity.RESULT_OK);
	    }

}
