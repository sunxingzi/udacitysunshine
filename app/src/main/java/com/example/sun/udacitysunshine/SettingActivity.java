package com.example.sun.udacitysunshine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sun.udacitysunshine.data.SunshinePreferences;
import com.example.sun.udacitysunshine.data.WeatherContract;
import com.example.sun.udacitysunshine.fragment.LocationFragment;
import com.example.sun.udacitysunshine.fragment.UnitFragment;
import com.example.sun.udacitysunshine.sync.SunshineSyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity
        implements LocationFragment.LocationListener,
        UnitFragment.UnitListener {
    private static final String TAG = SettingActivity.class.getSimpleName();
    public static final String LOCATION_DIALOG = "location_dialog";
    public static final String UNIT_DIALOG = "unit_dialog";

    @BindView(R.id.rl_location)
    RelativeLayout rl_location;
    @BindView(R.id.location_desc)
    TextView tv_location;
    @BindView(R.id.rl_unit)
    RelativeLayout rl_unit;
    @BindView(R.id.unit_desc)
    TextView tv_unit;

    @BindView(R.id.rl_notification)
    RelativeLayout rl_notification;
    @BindView(R.id.notification_desc)
    TextView tv_notification;
    @BindView(R.id.cb_notification)
    CheckBox cb_notification;

    String location;
    String unit;
    String notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //location init
        location = SunshinePreferences.getPreferredWeatherLocation(this);
        tv_location.setText(location);

        //unit init
        boolean isMetric = SunshinePreferences.isMetric(this);
        Log.e(TAG, "onCreate: " + isMetric);
        if (isMetric) {
            unit = getString(R.string.pref_units_metric);
        } else {
            unit = getString(R.string.pref_units_imperial);
        }
        tv_unit.setText(unit);

        //notification
        boolean areNotificationEnabled = SunshinePreferences.areNotificationEnabled(this);
        Log.e(TAG, "onCreate: areNotificationEnabled "+areNotificationEnabled );
        if(areNotificationEnabled){
            tv_notification.setText(getString(R.string.pref_enable_notifications_true));
            cb_notification.setChecked(true);
        }else{
            tv_notification.setText(getString(R.string.pref_enable_notifications_false));
            cb_notification.setChecked(false);
        }
        Log.e(TAG, "onCreate: "+tv_notification.getText() );
        cb_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tv_notification.setText(getString(R.string.pref_enable_notifications_true));
                    SunshinePreferences.setNotificationEnabledStatus(SettingActivity.this,true);
                    Log.e(TAG, "onCheckedChanged: checked "+tv_notification.getText() );
                }else{
                    tv_notification.setText(getString(R.string.pref_enable_notifications_false));
                    SunshinePreferences.setNotificationEnabledStatus(SettingActivity.this,false);
                    Log.e(TAG, "onCheckedChanged: not checked "+tv_notification.getText() );
                }
            }
        });
    }

    @OnClick(R.id.rl_location)
    public void openLoationSettingDialog() {
        Log.e(TAG, "openLoationSettingDialog: " + location);
        LocationFragment locationDialog = LocationFragment.newInstance(location);
        locationDialog.show(getSupportFragmentManager(), LOCATION_DIALOG);
    }

    @OnClick(R.id.rl_unit)
    public void openUnitSettingDialog() {
        Log.e(TAG, "openUnitSettingDialog: " + unit);
        UnitFragment unitDialog = UnitFragment.newInstance(unit);
        unitDialog.show(getSupportFragmentManager(), UNIT_DIALOG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setNewLocation(String location) {
        Log.e(TAG, "setNewLocation: " + location);
        SunshinePreferences.setWeatherLocation(this, location);
        tv_location.setText(location);
        SunshinePreferences.resetLocationCoordinates(this);
        SunshineSyncUtils.startImmediateSync(this);

    }

    @Override
    public void chooseUnit(String unit) {
        Log.e(TAG, "chooseUnit: " + unit);
        if (!TextUtils.isEmpty(unit)) {
            tv_unit.setText(unit);
            SunshinePreferences.setUnit(this, unit);
            getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
        }
    }
}
