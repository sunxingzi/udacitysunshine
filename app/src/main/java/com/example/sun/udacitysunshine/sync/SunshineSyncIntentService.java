package com.example.sun.udacitysunshine.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by su on 2018/8/15.
 */

public class SunshineSyncIntentService extends IntentService {
    public SunshineSyncIntentService(){
        super("SunshineSyncIntentService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SunshineSyncTask.syncWeather(this);
    }
}
