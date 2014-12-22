package com.StrapleGroup.around.controler.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.StrapleGroup.around.base.Constants;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Robert on 2014-09-27.
 */
public class ActivityRecognitionService extends IntentService implements Constants {
    public ActivityRecognitionService() {
        super("ActivityRecognitionService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result =
                    ActivityRecognitionResult.extractResult(intent);
            DetectedActivity mostProbableActivity =
                    result.getMostProbableActivity();
            int confidence = mostProbableActivity.getConfidence();
            int activityType = mostProbableActivity.getType();
            if (confidence >= 60) {
                getSharedPreferences(USER_PREFS, MODE_PRIVATE).edit().putInt(KEY_ACTIVITY, activityType).commit();
                Intent pReturnIntent = new Intent(ACTIVITY_RECOGNITION_LOCAL_ACTION);
                pReturnIntent.putExtra(KEY_ACTIVITY, activityType);
                sendBroadcast(pReturnIntent);
            }
        } else {
            Log.e("ACTIVITY_RECOGNITION", "ACTIVITY CANNOT BE RECOGNIZED");
        }
    }
}
