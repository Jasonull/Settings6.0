package com.android.settings;
import android.os.Bundle;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import android.preference.Preference;

public class HctGestureMotionSetting extends SettingsPreferenceFragment{


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.hct_gesture_motion_settings);
		if (getResources().getBoolean(R.bool.config_remove_system_motion)) {
			removePreference("hct_system_motion");
		}
		if (getResources().getBoolean(R.bool.config_remove_tele_motion)) {
			removePreference("hct_tele_motion");
		}
/*
		Preference gesture_unlock_set = findPreference("gesture_unlock_settings_to_nubia");
		Preference gesture_set = findPreference("gesture_settings_to_nubia");
		Preference floatview_set = findPreference("settings_floatview_key");

		if (!getResources().getBoolean(com.android.internal.R.bool.hct_tp_gesture_enable)) {
			removePreference("gesture_unlock_settings_to_nubia");
		}
		
		//<<hct:mingming.wu
		//if (!getResources().getBoolean(R.bool.config_show_gesture_menu)) {
		//	removePreference(gesture_set);
		//}
		//hct:mingming.wu

		if(!FeatureOption.HCT_FLOATVIEW){
			removePreference("settings_floatview_key");
		}
*/
    }

	@Override
	public void onResume() {
		super.onResume();
	}

/*
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mGesturePref) {
        }else if(preference == mControllerPref) {
        }
    }
*/
}
