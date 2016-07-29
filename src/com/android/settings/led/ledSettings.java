package com.android.settings.led;

import android.os.Bundle;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import android.os.SystemProperties;
import android.preference.PreferenceScreen;
import android.content.Intent;
import android.util.Log;
import android.app.Activity;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;

public class ledSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener{
    private static final String KEY_LED_CHARGE = "led_charge";
    private static final String KEY_LED_LOW_BATTERY = "led_low_battery";
    private static final String KEY_LED_NOTIFY = "led_notify";

    private static final String PROPERTIES_LED_CHARGE = "persist.sys.ledcharge";
    private static final String PROPERTIES_LED_LOW_BATTERY = "persist.sys.ledlowbattery";
    private static final String PROPERTIES_LED_NOTIFY = "persist.sys.lednotify";

    private CheckBoxPreference chargePreference;
    private CheckBoxPreference notifyPreference;
    private CheckBoxPreference lowBatteryPreference;

    private boolean mChargeEnable = true;
    private boolean mNotifyEnable = true;
    private boolean mLowbatteryEnable = true;

    private static Activity mActivity;
    private static final String CHANGE_BATTERY_LIGHTS_STATUS = "hct.intent.action.ACTION_CHANGE_LIGHTS_STATUS";
    private static final String CHANGE_NOTIFICATION_LIGHTS_STATUS = "hct.intent.action.ACTION_NOTIFICATION_LIGHTS_STATUS";
    private static final String CHANGE_APP_LIGHTS_STATUS = "hct.intent.action.ACTION_APP_LIGHTS_STATUS";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.hct_led_setting);

        mActivity = getActivity();

        initScreen();
    }

    private void initScreen() {
        final PreferenceScreen parent = getPreferenceScreen();
        chargePreference = (CheckBoxPreference) parent
                .findPreference(KEY_LED_CHARGE);
        notifyPreference = (CheckBoxPreference) parent
                .findPreference(KEY_LED_NOTIFY);
        lowBatteryPreference = (CheckBoxPreference) parent
                .findPreference(KEY_LED_LOW_BATTERY);

        mChargeEnable = SystemProperties
                .getBoolean(PROPERTIES_LED_CHARGE, true);
        mNotifyEnable = SystemProperties
                .getBoolean(PROPERTIES_LED_NOTIFY, true);
        mLowbatteryEnable = SystemProperties.getBoolean(
                PROPERTIES_LED_LOW_BATTERY, true);

        chargePreference.setChecked(mChargeEnable);
        notifyPreference.setChecked(mNotifyEnable);
        lowBatteryPreference.setChecked(mLowbatteryEnable);

    }

/*
    public static void setPropertiesValue(final String Properties, boolean enable) {
        boolean temp;
        boolean oldValue = SystemProperties.getBoolean(Properties, true);

        if (oldValue != enable) {
            SystemProperties.set(Properties, enable ? "1" : "0");

            if (Properties.equals(PROPERTIES_LED_CHARGE)
                    || Properties.equals(PROPERTIES_LED_LOW_BATTERY)) {
                Log.d("BatteryService",
                        "liuyang---sendBroadcast--CHANGE_BATTERY_LIGHTS_STATUS");
                Intent intent = new Intent(CHANGE_BATTERY_LIGHTS_STATUS);
                // intent.putExtra("fromwhere", "ledsetting");
                mActivity.sendBroadcast(intent);
            } else if (Properties.equals(PROPERTIES_LED_NOTIFY)) {
                Log.d("BatteryService",
                        "liuyang---sendBroadcast--CHANGE_NOTIFICATION_LIGHTS_STATUS");
                Intent intent = new Intent(CHANGE_NOTIFICATION_LIGHTS_STATUS);
                // intent.putExtra("fromwhere", "ledsetting");
                mActivity.sendBroadcast(intent);
            } else if (Properties.equals(PROPERTIES_LED_APP)) {
                Log.d("BatteryService",
                        "liuyang---sendBroadcast--CHANGE_APP_LIGHTS_STATUS");
                Intent intent = new Intent(CHANGE_APP_LIGHTS_STATUS);
                mActivity.sendBroadcast(intent);
            }
        }
        
        temp = SystemProperties.getBoolean(Properties, true);
    }
*/

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (chargePreference == preference) {
            SystemProperties.set(PROPERTIES_LED_CHARGE, chargePreference.isChecked() ? "1" : "0");
            Intent intent = new Intent(CHANGE_BATTERY_LIGHTS_STATUS);
            mActivity.sendBroadcast(intent);
            return true;
        } else if (lowBatteryPreference == preference) {
            SystemProperties.set(PROPERTIES_LED_LOW_BATTERY, lowBatteryPreference.isChecked() ? "1" : "0");
            Intent intent = new Intent(CHANGE_BATTERY_LIGHTS_STATUS);
            mActivity.sendBroadcast(intent);
            return true;
        } else if (notifyPreference == preference) {
            SystemProperties.set(PROPERTIES_LED_NOTIFY, notifyPreference.isChecked() ? "1" : "0");
            Intent intent = new Intent(CHANGE_NOTIFICATION_LIGHTS_STATUS);
            mActivity.sendBroadcast(intent);
            return true;
        } 
        
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        return false;
    }


    
}
