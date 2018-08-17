package com.example.sun.udacitysunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sun.udacitysunshine.utilities.SunshineDateUtils;
import com.example.sun.udacitysunshine.utilities.SunshineWeatherUtils;

/**
 * Created by su on 2018/8/15.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private static final int VIEW_TYPE_TODAY = 102;
    private static final int VIEW_TYPE_FUTURE_DAY = 101;

    private Context mContext;

    final private ForecastAdapterOnClickHandler mClickHandler;

    public interface ForecastAdapterOnClickHandler {
        void onClick(long date);
    }

    private boolean mUseTodayLayout;
    private Cursor mCursor;

    public ForecastAdapter(Context context, ForecastAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }

    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.forecast_list_item;
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        view.setFocusable(true);
        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder holder, int position) {

        mCursor.moveToPosition(position);
        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        int weatherImageId;
        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_TYPE_TODAY:
                weatherImageId = SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);
                break;
            case VIEW_TYPE_FUTURE_DAY:
                weatherImageId = SunshineWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
        holder.iconView.setImageResource(weatherImageId);

        //weather date
        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, true);
        Log.e("date", "onBindViewHolder:dateString "+dateString + "dateInMillis " +dateInMillis );
        holder.dateView.setText(dateString);

        //weather description
        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
        String descriptionAlly = mContext.getString(R.string.a11y_forecast, description);

        holder.descriptionView.setText(description);
        holder.descriptionView.setContentDescription(descriptionAlly);

        //high temperature
        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        String highString = SunshineWeatherUtils.formatTemperature(mContext, highInCelsius);
        String highAlly = mContext.getString(R.string.a11y_high_temp, highString);
        holder.highTempView.setText(highString);
        holder.highTempView.setContentDescription(highAlly);

        //low temperature
        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
        String lowString = SunshineWeatherUtils.formatTemperature(mContext, lowInCelsius);
        String lowAlly = mContext.getString(R.string.a11y_low_temp, lowString);
        holder.lowTempView.setText(lowString);
        holder.lowTempView.setContentDescription(lowAlly);
    }

    @Override
    public int getItemCount() {
        if(null == mCursor){

            return 0;
        }
        return mCursor.getCount();
    }

    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView iconView;
        final TextView dateView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;

        ForecastAdapterViewHolder(View view) {
            super(view);
            iconView = (ImageView) view.findViewById(R.id.weather_icon);
            dateView = (TextView) view.findViewById(R.id.date);
            descriptionView = (TextView) view.findViewById(R.id.weather_description);
            highTempView = (TextView) view.findViewById(R.id.high_temperature);
            lowTempView = (TextView) view.findViewById(R.id.low_temperature);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            Log.e("date", "onClick: "+dateInMillis);
            mClickHandler.onClick(dateInMillis);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mUseTodayLayout && position == 0){
            return VIEW_TYPE_TODAY;
        }else{
            return VIEW_TYPE_FUTURE_DAY;
        }
    }

    void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }
}
