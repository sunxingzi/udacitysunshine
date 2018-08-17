package com.example.sun.udacitysunshine.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;

import com.example.sun.udacitysunshine.data.SunshinePreferences;
import com.example.sun.udacitysunshine.data.WeatherContract;
import com.example.sun.udacitysunshine.utilities.NetworkUtils;
import com.example.sun.udacitysunshine.utilities.NotificationUtils;
import com.example.sun.udacitysunshine.utilities.OpenWeatherJsonUtils;

import java.io.IOException;
import java.net.URL;

/**
 * Created by su on 2018/8/15.
 */

public class SunshineSyncTask {
    /**
     * 执行网络请求数据解析和更新数据，并且在用户没有关闭通知功能和上次通知时间超过一天，有新的数据到来时通知用户。
     * @param context
     */

    synchronized public static void syncWeather(Context context) {
        try {
            URL weatherRequestUrl = NetworkUtils.getUrl(context);
            String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);

            ContentValues[] weatherValues = OpenWeatherJsonUtils.getWeatherContentValuesFromJson(context, jsonWeatherResponse);

            if(weatherValues != null && weatherValues.length != 0){
                ContentResolver sunshineContentResolver = context.getContentResolver();
              //删除旧的数据
                sunshineContentResolver.delete(WeatherContract.WeatherEntry.CONTENT_URI,
                        null,null);
                //保存新的数据
                sunshineContentResolver.bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI,weatherValues);

                boolean notificationsEnable = SunshinePreferences.areNotificationEnabled(context);
                long timeSinceLastNotification = SunshinePreferences.getEllapsedTimeSinceLastNotification(context);
                boolean oneDayPassedSinceLastNotification = false;
                if(timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS){
                    oneDayPassedSinceLastNotification = true;
                }

                if(notificationsEnable && oneDayPassedSinceLastNotification){
                    NotificationUtils.notifyUserOfNewWeather(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
