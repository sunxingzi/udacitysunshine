package com.example.sun.udacitysunshine.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sun.udacitysunshine.R;

/**
 * Created by su on 2018/8/16.
 */

public class LocationFragment extends BaseDialogFragment {
    private static final String TAG = LocationFragment.class.getSimpleName();
    public static final String LOCATION_KEY = "location";

    private LocationListener mLocationListener;

    public interface LocationListener {
        void setNewLocation(String location);
    }

    private EditText inputLocation;

    public static LocationFragment newInstance(String location) {
        LocationFragment fragment = new LocationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(LOCATION_KEY, location);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LocationListener) {
            mLocationListener = (LocationListener) context;
        } else {
            throw new IllegalArgumentException("activity must implements LocationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLocationListener = null;
    }

    @Override
    public int getLayout() {
        return R.layout.view_dialog_input;
    }

    @Override
    public void findItemView(View rootView) {
        TextView msg = (TextView) rootView.findViewById(R.id.tv_msg);
        Button cancel = (Button) rootView.findViewById(R.id.btn_cancle);
        Button sure = (Button) rootView.findViewById(R.id.btn_sure);
        inputLocation = (EditText) rootView.findViewById(R.id.input);

        msg.setText(getString(R.string.pref_location_label));
        String locationString = getArguments().getString(LOCATION_KEY);
        Log.e(TAG, "findItemView: "+locationString );
        inputLocation.setText(locationString);

        cancel.setOnClickListener(this);
        sure.setOnClickListener(this);

    }

    @Override
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancle:
                getDialog().dismiss();
                break;
            case R.id.btn_sure:
                String val = inputLocation.getText().toString();
                if (!TextUtils.isEmpty(val) && val.length() > 0) {
                    mLocationListener.setNewLocation(val);
                } else {
                    Toast.makeText(getActivity(), "Please input location!", Toast.LENGTH_SHORT).show();
                }
                getDialog().dismiss();
                break;
            default:
                break;
        }

    }
}
