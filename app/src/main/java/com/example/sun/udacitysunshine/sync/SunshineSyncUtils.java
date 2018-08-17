package com.example.sun.udacitysunshine.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import com.example.sun.udacitysunshine.data.WeatherContract;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by su on 2018/8/15.
 */

public class SunshineSyncUtils {
    /**
     * 同步天气信息的时间间隔
     */
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3;

    private static boolean sInitialized;

    private static final String SUNSHINE_SYNC_TAG = "sunshine_sync";

    static void scheduleFirecaseJobDispatcherSync(Context context){
        Driver driver  = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        //创建一个任务周期性的同步天气信息
        Job syncSunshineJob = dispatcher.newJobBuilder()
                //为job添加同步数据的服务
                .setService(SunshineFirebaseJobService.class)
                //设置tag用于标识这个job
                .setTag(SUNSHINE_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                //天气信息每3-4小时更新一次
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        dispatcher.schedule(syncSunshineJob);
    }

    synchronized public static void initialize(final Context context){
        if(sInitialized){
            return;
        }
        sInitialized = true;
        scheduleFirecaseJobDispatcherSync(context);
        /**
         * 在子线程中查看contentprovider中是否有数据
         */
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
                String[] projectionColumns = {WeatherContract.WeatherEntry._ID};
                String selectionStatement = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();
                Cursor cursor = context.getContentResolver().query(
                        forecastQueryUri,
                        projectionColumns,
                        selectionStatement,
                        null,
                        null);
                //如果cursor为null，需要立即更新数据
                if(null == cursor || cursor.getCount() == 0){
                    startImmediateSync(context);
                }
                cursor.close();
            }
        });
        checkForEmpty.start();
    }

    /**
     * 使用SunshineSyncIntentService立即同步天气信息
     * @param context
     */
    public static void startImmediateSync(Context context){
        Intent intentToSyncImmediately = new Intent(context,SunshineSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }

}
