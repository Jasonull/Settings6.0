package com.hct.fuelgauge;

import android.content.Context;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

import com.android.settings.R;
import com.mediatek.settings.ext.IBatteryExt;
import com.mediatek.settings.FeatureOption;
import com.mediatek.settings.UtilsExt;
import android.content.Intent;

public class BatteryPrecentDisplayExt {

    private static final String TAG = "BatteryPrecentDisplayExt";

    private static final String KEY_BATTERY_PERCENT_DISPLAY = "battery_percent_display";
    private static final String KEY_BATTERY_PERCENT_DISPLAY_ENABLE = "battery_percent_display_enable";


    private Context mContext;
    private PreferenceGroup mAppListGroup;
    private SwitchPreference BatteryPrecentDisplayPre;


    public BatteryPrecentDisplayExt(Context context, PreferenceGroup appListGroup) {
        mContext = context;
        mAppListGroup = appListGroup;
        // Battery plugin initialization
    }


    public void initBatteryPrecentExtItems() {

        // background power saving
	{
            BatteryPrecentDisplayPre = new SwitchPreference(mContext);
            BatteryPrecentDisplayPre.setKey(KEY_BATTERY_PERCENT_DISPLAY);
            BatteryPrecentDisplayPre.setTitle(R.string.str_battery_percentage);
            BatteryPrecentDisplayPre.setOrder(-4);
            BatteryPrecentDisplayPre.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                    KEY_BATTERY_PERCENT_DISPLAY_ENABLE, mContext.getResources().getInteger(com.android.internal.R.integer.battery_percent_display)) != 0);
            mAppListGroup.addPreference(BatteryPrecentDisplayPre);
        }
    }

    // on click
    public boolean onPowerUsageExtItemsClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference instanceof SwitchPreference && preference.getKey().equals(KEY_BATTERY_PERCENT_DISPLAY)) {
            SwitchPreference pref = (SwitchPreference) preference;
           if (KEY_BATTERY_PERCENT_DISPLAY.equals(preference.getKey())) {
                int bgState = pref.isChecked() ? 1 : 0;
                Log.d(TAG, "KEY_BATTERY_PERCENT_DISPLAY: " + bgState);
                Settings.System.putInt(mContext.getContentResolver(),
                        KEY_BATTERY_PERCENT_DISPLAY_ENABLE, bgState);
                if (BatteryPrecentDisplayPre != null) {
                    BatteryPrecentDisplayPre.setChecked(pref.isChecked());
                }
		Intent intent  = new Intent("action.battery.precent.show");
		intent.putExtra("show", pref.isChecked());
		mContext.sendBroadcast(intent); 
	      
            }
            return true;
            // If user click on PowerSaving preference just return here
        } 
        return false;
    }
}
