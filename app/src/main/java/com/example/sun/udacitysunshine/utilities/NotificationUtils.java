package com.example.sun.udacitysunshine.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.example.sun.udacitysunshine.DetailActivity;
import com.example.sun.udacitysunshine.R;
import com.example.sun.udacitysunshine.data.SunshinePreferences;
import com.example.sun.udacitysunshine.data.WeatherContract;

import static android.support.v4.app.NotificationCompat.*;

/**
 * Created by su on 2018/8/14.
 */

public class NotificationUtils {

    public static final String[] WEATHER_NOTIFICATION_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
    };

    public static final int INDEX_WEATHER_ID = 0;
    public static final int INDEX_MAX_TEMP = 1;
    public static final int INDEX_MIN_TEMP = 2;

    private static final int WEATHER_NOTIFICATION_ID = 3004;
    private static final String WEATHER_NOTIFICATION_CHANNEL_ID = "weather_notification_channel";

    private static final int WEATHER_PENDING_INTENT_ID = 1110;

    public static void notifyUserOfNewWeather(Context context) {
        Uri todayWeatherUri = WeatherContract.WeatherEntry.
                buildWeatherUriWithDate(SunshineDateUtils.normalizeDate(System.currentTimeMillis()));

        Cursor todayWeatherCursor = context.getContentResolver().query(
                todayWeatherUri,
                WEATHER_NOTIFICATION_PROJECTION,
                null,
                null,
                null
        );
        if (todayWeatherCursor.moveToFirst()) {
            int weatherId = todayWeatherCursor.getInt(INDEX_WEATHER_ID);
            double high = todayWeatherCursor.getDouble(INDEX_MAX_TEMP);
            double low = todayWeatherCursor.getDouble(INDEX_MIN_TEMP);

            Resources resources = context.getResources();
            int largeArtResourceId = SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);

            Bitmap largeIcon = BitmapFactory.decodeResource(resources, largeArtResourceId);
            String notificationTitle = context.getString(R.string.app_name);
            String notificationText = getNotificationText(context, weatherId, high, low);

            int smallArtResourceId = SunshineWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        WEATHER_NOTIFICATION_CHANNEL_ID,
                        "Primary",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationManager.createNotificationChannel(channel);
            }
            Builder notificationBuilder = new Builder(context, WEATHER_NOTIFICATION_CHANNEL_ID)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(smallArtResourceId)
                    .setLargeIcon(largeIcon)
                    .setContentIntent(contentIntent(context,todayWeatherUri))
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setAutoCancel(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                notificationBuilder.setPriority(PRIORITY_HIGH);
            }

          /*   Intent detailIntentForToday = new Intent(context,DetailActivity.class);
            detailIntentForToday.setData(todayWeatherUri);

         TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(detailIntentForToday);
            PendingIntent resultPendingIntent = taskStackBuilder.getPendingIntent(
                    0,PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.setContentIntent(resultPendingIntent);*/

            notificationManager.notify(WEATHER_NOTIFICATION_ID, notificationBuilder.build());

            SunshinePreferences.saveLastNotificationTime(context, System.currentTimeMillis());
        }


        todayWeatherCursor.close();
    }

    private static PendingIntent contentIntent(Context context,Uri todayWeatherUri) {
        Intent detailIntentForToday = new Intent(context, DetailActivity.class);
        detailIntentForToday.setData(todayWeatherUri);
        return PendingIntent.getService(context,
                WEATHER_PENDING_INTENT_ID,
                detailIntentForToday,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static String getNotificationText(Context context, int weatherId, double high, double low) {
        String shortDescription = SunshineWeatherUtils.getStringForWeatherCondition(context, weatherId);
        String notificationFormat = context.getString(R.string.format_notification);
        String notificationText = String.format(notificationFormat,
                shortDescription,
                SunshineWeatherUtils.formatTemperature(context, high),
                SunshineWeatherUtils.formatTemperature(context, low));
        return notificationText;
    }
}
