/*
* Copyright (C) 2014 MediaTek Inc.
* Modification based on code covered by the mentioned copyright
* and/or permission notice(s).
*/
/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings;

import com.android.internal.view.RotationPolicy;
import com.android.settings.notification.DropDownPreference;
import com.android.settings.notification.DropDownPreference.Callback;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import static android.provider.Settings.Secure.DOZE_ENABLED;
import static android.provider.Settings.Secure.WAKE_GESTURE_ENABLED;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;

import com.mediatek.settings.DisplaySettingsExt;
import com.mediatek.settings.FeatureOption;
import com.mediatek.xlog.Xlog;

import java.util.ArrayList;
import java.util.List;
//import com.hct.widget.SwitchPreferenceHCT;
import android.content.Intent;

import android.content.Intent;
import android.content.IntentFilter;   //zhangke add
//chenyichong add for easytouch begin.
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;
import com.hct.android.hcteasytouch.IEasyTouchService;
//chenyichong add for easytouch end.


public class DisplaySettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener, OnPreferenceClickListener, Indexable {
    private static final String TAG = "DisplaySettings";

    /** If there is no setting in the provider, use this. */
    private static final int FALLBACK_SCREEN_TIMEOUT_VALUE = 30000;

    private static final String KEY_SCREEN_TIMEOUT = "screen_timeout";
    private static final String KEY_FONT_SIZE = "font_size";
    private static final String KEY_SCREEN_SAVER = "screensaver";
    private static final String KEY_LIFT_TO_WAKE = "lift_to_wake";
    private static final String KEY_DOZE = "doze";
    private static final String KEY_AUTO_BRIGHTNESS = "auto_brightness";
    private static final String KEY_KEYBACK_BRIGHTNESS = "keyback_brightness";  //add by zhangke
    private static final String KEY_CONTROL_BRIGHTNESS = "brightness";
    private static final String KEY_AUTO_ROTATE = "auto_rotate";
    private static final String PROPERTIES_KEYBACK_BRIGHTNESS = "persist.sys.brightness";  //add by zhangke
    private static final int DLG_GLOBAL_CHANGE_WARNING = 1;
    private static final String CHANGE_KEYBACK_BRIGHTNESS_STATUS = "hct.intent.action.ACTION_KEYBACK_BRIGHTNESS_STATUS";  //add by zhangke

    private static final String KEY_HALL_SWITCH = "hall_switch";
    //private SwitchPreferenceHCT mHallSwitchPreference;
	  private SwitchPreference mHallSwitchPreference;
    private WarnedListPreference mFontSizePref;

    private final Configuration mCurConfig = new Configuration();

    private ListPreference mScreenTimeoutPreference;
    private Preference mScreenSaverPreference;
    private SwitchPreference mLiftToWakePreference;
    private SwitchPreference mDozePreference;
    private SwitchPreference mAutoBrightnessPreference;
    private SwitchPreference mKeyBrightnessPreference;   //add by zhangke
    private BrightnessPreference mControlBrightnessPreference;

    ///M: MTK feature
    private DisplaySettingsExt mDisplaySettingsExt;
    /*liuyang add begin @20141008*/
    private Preference mLedSettingsPref;
    private static final String KEY_LED_SETTINGS = "led_settings";
    /*liuyang add end @20141008*/

    private ContentObserver mScreenTimeoutObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                Xlog.d(TAG, "mScreenTimeoutObserver omChanged");
                int value = Settings.System.getInt(
                        getContentResolver(), SCREEN_OFF_TIMEOUT, FALLBACK_SCREEN_TIMEOUT_VALUE);
                updateTimeoutPreference(value);
            }

        };


//chenyichong add for easytouch begin.
    private static final String KEY_EASY_TOUCH_SWITCH = "easytouch_switch";
    private SwitchPreference mEasyTouchSwitchPreference;

	private static final String SERVERACTION = "hct.easytouch.action.START";

	
    IEasyTouchService myService = null;

    ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            myService = null;
            Log.e("Bob.chen", "IEasyTouchService. onServiceDisconnected. myService = " + myService);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            myService = IEasyTouchService.Stub.asInterface(service);
            Log.e("Bob.chen", "IEasyTouchService. onServiceConnected. myService = " + myService);
        }
    };
//chenyichong add for easytouch end.


    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = getActivity();
        final ContentResolver resolver = activity.getContentResolver();

        addPreferencesFromResource(R.xml.display_settings);

        ///M: MTK feature @{
        mDisplaySettingsExt = new DisplaySettingsExt(getActivity());
        mDisplaySettingsExt.onCreate(getPreferenceScreen());
        /// @}

        mScreenSaverPreference = findPreference(KEY_SCREEN_SAVER);
        if (mScreenSaverPreference != null
                && getResources().getBoolean(
                        com.android.internal.R.bool.config_dreamsSupported) == false) {
            mDisplaySettingsExt.removePreference(mScreenSaverPreference);
        }

        mScreenTimeoutPreference = (ListPreference) findPreference(KEY_SCREEN_TIMEOUT);
        /**M: for fix bug ALPS00266723 @{*/
        final long currentTimeout = getTimoutValue();
        Xlog.d(TAG, "currentTimeout=" + currentTimeout);
        /**@}*/
        mScreenTimeoutPreference.setValue(String.valueOf(currentTimeout));
        mScreenTimeoutPreference.setOnPreferenceChangeListener(this);
        disableUnusableTimeouts(mScreenTimeoutPreference);
        updateTimeoutPreferenceDescription(currentTimeout);

        mFontSizePref = (WarnedListPreference) findPreference(KEY_FONT_SIZE);
        mFontSizePref.setOnPreferenceChangeListener(this);
        mFontSizePref.setOnPreferenceClickListener(this);
        mControlBrightnessPreference = (BrightnessPreference) findPreference(KEY_CONTROL_BRIGHTNESS);

        if (isAutomaticBrightnessAvailable(getResources())) {
            mAutoBrightnessPreference = (SwitchPreference) findPreference(KEY_AUTO_BRIGHTNESS);
            mAutoBrightnessPreference.setOnPreferenceChangeListener(this);
        } else {
            // removePreference(KEY_AUTO_BRIGHTNESS);
            mDisplaySettingsExt.removePreference(findPreference(KEY_AUTO_BRIGHTNESS));
        }
        //mHallSwitchPreference = (SwitchPreferenceHCT) findPreference(KEY_HALL_SWITCH);
		mHallSwitchPreference = (SwitchPreference) findPreference(KEY_HALL_SWITCH);
        if(mDisplaySettingsExt != null && mHallSwitchPreference != null){
            mHallSwitchPreference.setOnPreferenceChangeListener(this);
            if(!getResources().getBoolean(R.bool.config_support_hall_switch)){
               mDisplaySettingsExt.removePreference(mHallSwitchPreference);
            }
        }

//chenyichong add for easytouch begin.
      mEasyTouchSwitchPreference = (SwitchPreference) findPreference(KEY_EASY_TOUCH_SWITCH);
      if(mDisplaySettingsExt != null && mEasyTouchSwitchPreference != null){
          mEasyTouchSwitchPreference.setOnPreferenceChangeListener(this);
          if(!getResources().getBoolean(R.bool.config_easy_touch_float_view)){//
             mDisplaySettingsExt.removePreference(mEasyTouchSwitchPreference);
          }
          else{
              Intent intent = new Intent();
              intent.setClassName("com.hct.android.hcteasytouch", "com.hct.android.hcteasytouch.EasyTouchService");
              boolean isSuccess = getActivity().bindService( intent, serviceConnection,   Context.BIND_AUTO_CREATE);
              Log.e("Bob.chen", "hcteasytouch. isSuccess = " + isSuccess);
          }
      }
//chenyichong add for easytouch end.


        
      boolean isKeyBackBrightnessAvailable = getResources().getBoolean(R.bool.config_keyback_brightness_available);
        if (isKeyBackBrightnessAvailable) {
            mKeyBrightnessPreference = (SwitchPreference) findPreference(KEY_KEYBACK_BRIGHTNESS);
            mKeyBrightnessPreference.setOnPreferenceChangeListener(this);
        } else {
            mDisplaySettingsExt.removePreference(findPreference(KEY_KEYBACK_BRIGHTNESS));
        }

        if (isLiftToWakeAvailable(activity)) {
            mLiftToWakePreference = (SwitchPreference) findPreference(KEY_LIFT_TO_WAKE);
            mLiftToWakePreference.setOnPreferenceChangeListener(this);
        } else {
            // removePreference(KEY_LIFT_TO_WAKE);
            mDisplaySettingsExt.removePreference(findPreference(KEY_LIFT_TO_WAKE));
        }

        if (isDozeAvailable(activity)) {
            mDozePreference = (SwitchPreference) findPreference(KEY_DOZE);
            mDozePreference.setOnPreferenceChangeListener(this);
        } else {
            // removePreference(KEY_DOZE);
            mDisplaySettingsExt.removePreference(findPreference(KEY_DOZE));
        }

        if (RotationPolicy.isRotationLockToggleVisible(activity)) {
            DropDownPreference rotatePreference =
                    (DropDownPreference) findPreference(KEY_AUTO_ROTATE);
            rotatePreference.addItem(activity.getString(R.string.display_auto_rotate_rotate),
                    false);
            int rotateLockedResourceId;
            // The following block sets the string used when rotation is locked.
            // If the device locks specifically to portrait or landscape (rather than current
            // rotation), then we use a different string to include this information.
            if (allowAllRotations(activity)) {
                rotateLockedResourceId = R.string.display_auto_rotate_stay_in_current;
            } else {
                if (RotationPolicy.getRotationLockOrientation(activity)
                        == Configuration.ORIENTATION_PORTRAIT) {
                    rotateLockedResourceId =
                            R.string.display_auto_rotate_stay_in_portrait;
                } else {
                    rotateLockedResourceId =
                            R.string.display_auto_rotate_stay_in_landscape;
                }
            }
            rotatePreference.addItem(activity.getString(rotateLockedResourceId), true);
            mDisplaySettingsExt.setRotatePreference(rotatePreference);
            rotatePreference.setSelectedItem(RotationPolicy.isRotationLocked(activity) ?
                    1 : 0);
            rotatePreference.setCallback(new Callback() {
                @Override
                public boolean onItemSelected(int pos, Object value) {
                    RotationPolicy.setRotationLock(activity, (Boolean) value);
                    return true;
                }
            });
        } else {
            // removePreference(KEY_AUTO_ROTATE);
            mDisplaySettingsExt.removePreference(findPreference(KEY_AUTO_ROTATE));
        }
        /*liuyang add begin @20141009*/
        if(!getResources().getBoolean(R.bool.hct_config_show_led_settings)){
            mLedSettingsPref = findPreference(KEY_LED_SETTINGS);
            mDisplaySettingsExt.removePreference(mLedSettingsPref);
        }
        /*liuyang add end @20141009*/
    }

    private static boolean allowAllRotations(Context context) {
        return Resources.getSystem().getBoolean(
                com.android.internal.R.bool.config_allowAllRotations);
    }

    private static boolean isLiftToWakeAvailable(Context context) {
        SensorManager sensors = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        return sensors != null && sensors.getDefaultSensor(Sensor.TYPE_WAKE_GESTURE) != null;
    }

    private static boolean isDozeAvailable(Context context) {
        String name = Build.IS_DEBUGGABLE ? SystemProperties.get("debug.doze.component") : null;
        if (TextUtils.isEmpty(name)) {
            name = context.getResources().getString(
                    com.android.internal.R.string.config_dozeComponent);
        }
        return !TextUtils.isEmpty(name);
    }

    private static boolean isAutomaticBrightnessAvailable(Resources res) {
        return res.getBoolean(com.android.internal.R.bool.config_automatic_brightness_available);
    }

 @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Xlog.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        mCurConfig.updateFrom(newConfig);
    }

    private int getTimoutValue() {
        int currentValue = Settings.System.getInt(getActivity()
                .getContentResolver(), SCREEN_OFF_TIMEOUT,
                FALLBACK_SCREEN_TIMEOUT_VALUE);
        Xlog.d(TAG, "getTimoutValue()---currentValue=" + currentValue);
        int bestMatch = 0;
        int timeout = 0;
        final CharSequence[] valuesTimeout = mScreenTimeoutPreference
                .getEntryValues();
        for (int i = 0; i < valuesTimeout.length; i++) {
            timeout = Integer.parseInt(valuesTimeout[i].toString());
            if (currentValue == timeout) {
                return currentValue;
            } else {
                if (currentValue > timeout) {
                    bestMatch = i;
                }
            }
        }
        Xlog.d(TAG, "getTimoutValue()---bestMatch=" + bestMatch);
        return Integer.parseInt(valuesTimeout[bestMatch].toString());

    }

    private void updateTimeoutPreferenceDescription(long currentTimeout) {
        ListPreference preference = mScreenTimeoutPreference;
        String summary;
        if (currentTimeout < 0) {
            // Unsupported value
            summary = "";
        } else {
            final CharSequence[] entries = preference.getEntries();
            final CharSequence[] values = preference.getEntryValues();
            if (entries == null || entries.length == 0) {
                summary = "";
            } else {
                int best = 0;
                for (int i = 0; i < values.length; i++) {
                    long timeout = Long.parseLong(values[i].toString());
                    if (currentTimeout >= timeout) {
                        best = i;
                    }
                }
            ///M: to prevent index out of bounds @{
            if (entries.length != 0) {
                summary = preference.getContext().getString(
                        R.string.screen_timeout_summary, entries[best]);
		if (best == 7){
			summary = summary.substring(0,summary.length()-1);
		}
            } else {
                summary = "";
            }
           ///M: @}

            }
        }
        preference.setSummary(summary);
    }

    private void disableUnusableTimeouts(ListPreference screenTimeoutPreference) {
        final DevicePolicyManager dpm =
                (DevicePolicyManager) getActivity().getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        final long maxTimeout = dpm != null ? dpm.getMaximumTimeToLock(null) : 0;
        if (maxTimeout == 0) {
            return; // policy not enforced
        }
        final CharSequence[] entries = screenTimeoutPreference.getEntries();
        final CharSequence[] values = screenTimeoutPreference.getEntryValues();
        ArrayList<CharSequence> revisedEntries = new ArrayList<CharSequence>();
        ArrayList<CharSequence> revisedValues = new ArrayList<CharSequence>();
        for (int i = 0; i < values.length; i++) {
            long timeout = Long.parseLong(values[i].toString());
            if (timeout <= maxTimeout) {
                revisedEntries.add(entries[i]);
                revisedValues.add(values[i]);
            }
        }
        if (revisedEntries.size() != entries.length || revisedValues.size() != values.length) {
            final int userPreference = Integer.parseInt(screenTimeoutPreference.getValue());
            screenTimeoutPreference.setEntries(
                    revisedEntries.toArray(new CharSequence[revisedEntries.size()]));
            screenTimeoutPreference.setEntryValues(
                    revisedValues.toArray(new CharSequence[revisedValues.size()]));
            if (userPreference <= maxTimeout) {
                screenTimeoutPreference.setValue(String.valueOf(userPreference));
            } else if (revisedValues.size() > 0
                    && Long.parseLong(revisedValues.get(revisedValues.size() - 1).toString())
                    == maxTimeout) {
                // If the last one happens to be the same as the max timeout, select that
                screenTimeoutPreference.setValue(String.valueOf(maxTimeout));
            } else {
                // There will be no highlighted selection since nothing in the list matches
                // maxTimeout. The user can still select anything less than maxTimeout.
                // TODO: maybe append maxTimeout to the list and mark selected.
            }
        }
        screenTimeoutPreference.setEnabled(revisedEntries.size() > 0);
    }

    int floatToIndex(float val) {
        Xlog.w(TAG, "floatToIndex enter val = " + val);
        ///M: modify by MTK for EM @{
        int res = mDisplaySettingsExt.floatToIndex(mFontSizePref, val);
        if (res != -1) {
            return res;
        }
        /// @}

        String[] indices = getResources().getStringArray(R.array.entryvalues_font_size);
        float lastVal = Float.parseFloat(indices[0]);
        for (int i=1; i<indices.length; i++) {
            float thisVal = Float.parseFloat(indices[i]);
            if (val < (lastVal + (thisVal-lastVal)*.5f)) {
                return i-1;
            }
            lastVal = thisVal;
        }
        return indices.length-1;
    }

    public void readFontSizePreference(ListPreference pref) {
        try {
            mCurConfig.updateFrom(ActivityManagerNative.getDefault().getConfiguration());
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to retrieve font size");
        }

        // mark the appropriate item in the preferences list
        int index = floatToIndex(mCurConfig.fontScale);
        Xlog.d(TAG, "readFontSizePreference index = " + index);
        pref.setValueIndex(index);

        // report the current size in the summary text
        final Resources res = getResources();
        String[] fontSizeNames = res.getStringArray(R.array.entries_font_size);
        pref.setSummary(String.format(res.getString(R.string.summary_font_size),
                fontSizeNames[index]));
    }

    @Override
    public void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(Settings.System.getUriFor(SCREEN_OFF_TIMEOUT),
                false, mScreenTimeoutObserver);
        updateState();
        ///M: MTK feature
        mDisplaySettingsExt.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        ///M: MTK feature
        mDisplaySettingsExt.onPause();
    }

    @Override
    public Dialog onCreateDialog(int dialogId) {
        if (dialogId == DLG_GLOBAL_CHANGE_WARNING) {
            return Utils.buildGlobalChangeWarningDialog(getActivity(),
                    R.string.global_font_change_title,
                    new Runnable() {
                        public void run() {
                            mFontSizePref.click();
                        }
                    });
        }
        return null;
    }

    private void updateState() {
        readFontSizePreference(mFontSizePref);
        updateScreenSaverSummary();

        // Update auto brightness if it is available.
        if (mAutoBrightnessPreference != null) {
            int brightnessMode = Settings.System.getInt(getContentResolver(),
                    SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_MANUAL);
            mAutoBrightnessPreference.setChecked(brightnessMode != SCREEN_BRIGHTNESS_MODE_MANUAL);
            mControlBrightnessPreference.setEnabled(brightnessMode == SCREEN_BRIGHTNESS_MODE_MANUAL);

        }
		if (mKeyBrightnessPreference != null) {
    		boolean brightnessMode = SystemProperties.get("persist.sys.brightness", "1").equals("1") ? true:false;
            mKeyBrightnessPreference.setChecked(brightnessMode);
        }
        // Update lift-to-wake if it is available.
        if (mLiftToWakePreference != null) {
            int value = Settings.Secure.getInt(getContentResolver(), WAKE_GESTURE_ENABLED, 0);
            mLiftToWakePreference.setChecked(value != 0);
        }

        // Update doze if it is available.
        if (mDozePreference != null) {
            int value = Settings.Secure.getInt(getContentResolver(), DOZE_ENABLED, 1);
            mDozePreference.setChecked(value != 0);
        }
        if(mHallSwitchPreference != null){
            int hallMode = Settings.System.getInt(getContentResolver(),
                    "hall_mode", 1);
            mHallSwitchPreference.setChecked(hallMode == 1);          
        }

//chenyichong add for easytouch begin.
        if(mEasyTouchSwitchPreference != null){
            int easytouch = Settings.System.getInt(getContentResolver(),
                    "easytouch", 1);
            mEasyTouchSwitchPreference.setChecked(easytouch == 2);          
        }
//chenyichong add for easytouch end.

        
    }

    /**M: for fix bug not sync status bar when lock screen @{*/
    private void updateTimeoutPreference(int currentTimeout) {
        Xlog.d(TAG, "currentTimeout=" + currentTimeout);
        mScreenTimeoutPreference.setValue(String.valueOf(currentTimeout));
        updateTimeoutPreferenceDescription(currentTimeout);
        AlertDialog dlg = (AlertDialog) mScreenTimeoutPreference.getDialog();
        if (dlg == null || !dlg.isShowing()) {
            return;
        }
        ListView listview = dlg.getListView();
        int checkedItem = mScreenTimeoutPreference.findIndexOfValue(
        mScreenTimeoutPreference.getValue());
        if (checkedItem > -1) {
            listview.setItemChecked(checkedItem, true);
            listview.setSelection(checkedItem);
        }
    }
    /**@}*/

    private void updateScreenSaverSummary() {
        if (mScreenSaverPreference != null) {
            mScreenSaverPreference.setSummary(
                    DreamSettings.getSummaryTextWithDreamName(getActivity()));
        }
    }

    public void writeFontSizePreference(Object objValue) {
        try {
            mCurConfig.fontScale = Float.parseFloat(objValue.toString());
            Xlog.d(TAG, "writeFontSizePreference font size =  " + Float.parseFloat(objValue.toString()));
            ActivityManagerNative.getDefault().updatePersistentConfiguration(mCurConfig);
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to save font size");
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        ///M: add MTK feature @{
        mDisplaySettingsExt.onPreferenceClick(preference);
        /// @}
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        if (KEY_SCREEN_TIMEOUT.equals(key)) {
            try {
                int value = Integer.parseInt((String) objValue);
                Settings.System.putInt(getContentResolver(), SCREEN_OFF_TIMEOUT, value);
                updateTimeoutPreferenceDescription(value);
            } catch (NumberFormatException e) {
                Log.e(TAG, "could not persist screen timeout setting", e);
            }
        }
        if (KEY_FONT_SIZE.equals(key)) {
            writeFontSizePreference(objValue);
        }
        if (preference == mAutoBrightnessPreference) {
            boolean auto = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(), SCREEN_BRIGHTNESS_MODE,
                    auto ? SCREEN_BRIGHTNESS_MODE_AUTOMATIC : SCREEN_BRIGHTNESS_MODE_MANUAL);
            mControlBrightnessPreference.setEnabled(!auto);
        }
        if (preference == mKeyBrightnessPreference) {
            boolean keyback = (Boolean) objValue;
                if(keyback){
            	Intent intent = new Intent(CHANGE_KEYBACK_BRIGHTNESS_STATUS);
            	intent.putExtra("brightness", "on");
            	getActivity().sendBroadcast(intent);
                }else{
            	Intent intent = new Intent(CHANGE_KEYBACK_BRIGHTNESS_STATUS);
            	intent.putExtra("brightness", "off");
            	getActivity().sendBroadcast(intent);
                }
        }
        if (preference == mLiftToWakePreference) {
            boolean value = (Boolean) objValue;
            Settings.Secure.putInt(getContentResolver(), WAKE_GESTURE_ENABLED, value ? 1 : 0);
        }
        if (preference == mDozePreference) {
            boolean value = (Boolean) objValue;
            Settings.Secure.putInt(getContentResolver(), DOZE_ENABLED, value ? 1 : 0);
        }
        if(mHallSwitchPreference == preference){
            boolean value = (Boolean) objValue;
            Settings.System.putInt(getContentResolver(),
                    "hall_mode", value ? 1 : 0);        
        }

//chenyichong add for easytouch begin.
        if(mEasyTouchSwitchPreference == preference){
            boolean value = (Boolean) objValue;
            EasyTouch_onPreferenceTreeClick(value);
        }
//chenyichong add for easytouch end.

        
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == mFontSizePref) {
            if (Utils.hasMultipleUsers(getActivity())) {
                showDialog(DLG_GLOBAL_CHANGE_WARNING);
                return true;
            } else {
                mFontSizePref.click();
            }
        }
        return false;
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    ArrayList<SearchIndexableResource> result =
                            new ArrayList<SearchIndexableResource>();

                    SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.display_settings;
                    result.add(sir);

                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    ArrayList<String> result = new ArrayList<String>();
                    if (!context.getResources().getBoolean(
                            com.android.internal.R.bool.config_dreamsSupported)
                            || FeatureOption.MTK_GMO_RAM_OPTIMIZE) {
                        result.add(KEY_SCREEN_SAVER);
                    }
                    if (!isAutomaticBrightnessAvailable(context.getResources())) {
                        result.add(KEY_AUTO_BRIGHTNESS);
                    }
                    if (!context.getResources().getBoolean(R.bool.config_keyback_brightness_available)) {
                        result.add(KEY_KEYBACK_BRIGHTNESS);
                    }
                    if (!isLiftToWakeAvailable(context)) {
                        result.add(KEY_LIFT_TO_WAKE);
                    }
                    if (!isDozeAvailable(context)) {
                        result.add(KEY_DOZE);
                    }
                    if (!RotationPolicy.isRotationLockToggleVisible(context)) {
                        result.add(KEY_AUTO_ROTATE);
                    }
                    return result;
                }
            };


//chenyichong add for easytouch begin.
            public void EasyTouch_onPreferenceTreeClick(boolean value) {
                    
                int state = value ? 2 : 1;
                                Intent intent = new Intent(SERVERACTION);
                Settings.System.putInt(getActivity().getContentResolver(),"easytouch", state);

                Log.e("Bob.chen", "EasyTouch_onPreferenceTreeClick = " + Settings.System.getInt(getActivity().getContentResolver(), "easytouch",  1) + ". value = " + value);

                if(value){
                    try {

                        Log.e("Bob.chen", "FloatViewSettings. startService. myService = " + myService);
                                            
                        myService.startService();
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                else{
                    try {
                        Log.e("Bob.chen", "FloatViewSettings. stopSelf. myService = " + myService);
                                            
                        myService.stopSelf();
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

               }
//chenyichong add for easytouch end.

}
