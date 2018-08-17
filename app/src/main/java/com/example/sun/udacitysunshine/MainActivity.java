package com.example.sun.udacitysunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.sun.udacitysunshine.data.SunshinePreferences;
import com.example.sun.udacitysunshine.data.WeatherContract;
import com.example.sun.udacitysunshine.sync.SunshineSyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ForecastAdapter.ForecastAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.recyclerview_forecast)
    RecyclerView mRecyclerview;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    public static final String[] MAIN_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };

    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;

    private static final int ID_FORECAST_LOADER = 44;

    private ForecastAdapter mForecastAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getSupportActionBar().setElevation(0f);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.setHasFixedSize(true);
        mForecastAdapter = new ForecastAdapter(this, this);
        mRecyclerview.setAdapter(mForecastAdapter);

        showLoading();

        getSupportLoaderManager().initLoader(ID_FORECAST_LOADER, null, this);
        Log.e(TAG, "onCreate: getSupportLoaderManager" );

        SunshineSyncUtils.initialize(this);
        Log.e(TAG, "onCreate:  SunshineSyncUtils.initialize");
    }

    private void showLoading() {
        mRecyclerview.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showWeatherDataView() {
        mRecyclerview.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    private void openPreferredLocationInMap() {
        double[] coords = SunshinePreferences.getLocationCoordinates(this);
        String posLat = Double.toString(coords[0]);
        String posLong = Double.toString(coords[1]);
        Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.e(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e(TAG, "onCreateLoader: " );
        switch (id) {
            case ID_FORECAST_LOADER:
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
                Log.e("555", "onCreateLoader: " + forecastQueryUri);
                String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
                String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                return new CursorLoader(
                        this,
                        forecastQueryUri,
                        MAIN_FORECAST_PROJECTION,
                        selection,
                        null,
                        sortOrder
                );
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(TAG, "onLoadFinished: " );
        Log.e(TAG, "onLoadFinished:  0 " + data);
        mForecastAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
        }
        mRecyclerview.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) {
            Log.e(TAG, "onLoadFinished: no 0 " + data);
            showWeatherDataView();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);

    }

    @Override
    public void onClick(long date) {
        Intent weatherDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        Uri uriForDateClicked = WeatherContract.WeatherEntry.buildWeatherUriWithDate(date);
        weatherDetailIntent.setData(uriForDateClicked);
        startActivity(weatherDetailIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }
        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
