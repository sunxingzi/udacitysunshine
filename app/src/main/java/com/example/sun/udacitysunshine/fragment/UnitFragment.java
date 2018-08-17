package com.example.sun.udacitysunshine.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.sun.udacitysunshine.R;

/**
 * Created by su on 2018/8/16.
 */

public class UnitFragment extends BaseDialogFragment implements RadioGroup.OnCheckedChangeListener {
    private static final String TAG = UnitFragment.class.getSimpleName();
    public static final String UNIT_KEY = "unit";

    private UnitListener mUnitListener;

    private String chooseUnitString;

    public static UnitFragment newInstance(String unit) {
        UnitFragment fragment = new UnitFragment();
        Bundle bundle = new Bundle();
        bundle.putString(UNIT_KEY, unit);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UnitListener) {
            mUnitListener = (UnitListener) context;
        } else {
            throw new IllegalArgumentException("activity must implements listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mUnitListener = null;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_meteic:
                chooseUnitString = getString(R.string.pref_units_metric);
                Log.e(TAG, "onCheckedChanged: 0 "+chooseUnitString);
                break;
            case R.id.radio_imperial:
                chooseUnitString = getString(R.string.pref_units_imperial);
                Log.e(TAG, "onCheckedChanged: 1 "+chooseUnitString);
                break;
            default:
                break;

        }

    }

    public interface UnitListener {
        void chooseUnit(String unit);
    }


    @Override
    public int getLayout() {
        return R.layout.view_dialog_radio_button;
    }

    @Override
    public void findItemView(View rootView) {
        TextView msg = (TextView) rootView.findViewById(R.id.tv_msg);
        RadioGroup mRadioGroup = (RadioGroup) rootView.findViewById(R.id.radio_group);
        RadioButton mMetricRadioButton = (RadioButton) rootView.findViewById(R.id.radio_meteic);
        RadioButton mImperialRadioButton = (RadioButton) rootView.findViewById(R.id.radio_imperial);
        Button cancel = (Button) rootView.findViewById(R.id.btn_cancle);
        Button sure = (Button) rootView.findViewById(R.id.btn_sure);

        msg.setText(getString(R.string.pref_units_label));

        String unitString = getArguments().getString(UNIT_KEY);
        if (!TextUtils.isEmpty(unitString)) {
            String metricString = getString(R.string.pref_units_metric);
            String imperialString = getString(R.string.pref_units_imperial);
            if (metricString.equals(unitString)) {
                mMetricRadioButton.setChecked(true);
                chooseUnitString = metricString;
            } else if (imperialString.equals(unitString)) {
                mImperialRadioButton.setChecked(true);
                chooseUnitString = imperialString;
            }
        }

        cancel.setOnClickListener(this);
        sure.setOnClickListener(this);
        mRadioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancle:
                Log.e(TAG, "onItemClick: btn_cancel " );
                getDialog().dismiss();
                break;
            case R.id.btn_sure:
                Log.e(TAG, "onItemClick: btn_sure " );
                Log.e(TAG, "onItemClick: "+chooseUnitString );
                mUnitListener.chooseUnit(chooseUnitString);
                getDialog().dismiss();
            default:
                break;
        }

    }
}
