package com.StrapleGroup.around.controler.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import com.StrapleGroup.around.database.DataManagerImpl;

/**
 * Created by Robert on 2014-08-31.
 */
public class RefreshFriendsService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RefreshFriendsService(String name) {
        super(name);
    }

    private DataManagerImpl dataManager;
    private Context context;
    @Override
    protected void onHandleIntent(Intent intent) {
//        context = getApplicationContext();
//        SQLiteOpenHelper openHelper = new OpenHelper(context);
//        SQLiteDatabase db = openHelper.getWritableDatabase();
//        dataManager = new DataManagerImpl(context);
//        dataManager.findFriend()
    }

}
