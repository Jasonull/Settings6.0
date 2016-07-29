package com.android.settings.gesture;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.content.Context;
import android.preference.Preference;
import android.provider.Settings;
import com.android.settings.R;
import android.util.Log;

public class HctTpGestureSwitchPreference extends Preference implements
        CompoundButton.OnCheckedChangeListener {
    private Switch mSwitch;
    private String mProperties = null;
    private boolean mCurrentStatus = true;

    public HctTpGestureSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onBindView(View view) {
        // TODO Auto-generated method stub
        Log.d("gestureunlock", "onBindView");
        super.onBindView(view);
        mSwitch = (Switch) view.findViewById(R.id.hct_switchWidget);
        mSwitch.setOnCheckedChangeListener(this);
        mSwitch.setChecked(mCurrentStatus);
    }

    public boolean getCurrentStatus() {
        return mSwitch.isChecked();
    }

    public void setCurrentStatus(boolean ischeck) {
        mCurrentStatus = ischeck;
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        // TODO Auto-generated method stub
        return super.onCreateView(parent);
    }

    public void setPropertiesString(final String Properties) {
        mProperties = Properties;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        Log.d("gestureunlock", "onCheckedChanged");
        mCurrentStatus = isChecked;
        GestureListSettings.setPropertiesValue(mProperties, isChecked);

    }

}
