package com.StrapleGroup.around.controler.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.StrapleGroup.around.R;
import com.StrapleGroup.around.base.Constants;
import com.StrapleGroup.around.ui.view.MainActivity;
import com.StrapleGroup.around.ui.view.dialogs.LocationDialog;

/**
 * Created by Robert on 2014-12-24.
 */
public class AroundNotifierService extends IntentService implements Constants {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AroundNotifierService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String pLogin = intent.getStringExtra(KEY_LOGIN);
        double pLat = intent.getDoubleExtra(KEY_X, 0.00000);
        double pLng = intent.getDoubleExtra(KEY_Y, 0.0000);
        Intent pMarkerIntent = new Intent(MARKER_LOCAL_ACTION);
        pMarkerIntent.putExtra(KEY_LOGIN, pLogin);
        pMarkerIntent.putExtra(KEY_X, pLat);
        pMarkerIntent.putExtra(KEY_Y, pLng);
        sendBroadcast(pMarkerIntent);
        sendNotification(pLogin, pMarkerIntent);

    }

    private void sendNotification(String aLogin, Intent aMarkerIntent) {
        Intent pIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder pTaskStack = TaskStackBuilder.create(this).addNextIntent(pIntent).addNextIntent(aMarkerIntent);
        Notification.Builder pNotification = new Notification.Builder(this).setSmallIcon(R.drawable.home_img).setAutoCancel(true)
                .setContentTitle("Your friend is around you!").setContentText(aLogin + " is around you. Feel free to poke him :)").setContentIntent(pTaskStack.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));
        NotificationManager pNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        pNotificationManager.notify(0, pNotification.build());

    }
}
