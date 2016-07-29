package com.android.settings;

import android.os.Bundle;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import android.os.SystemProperties;
import android.preference.PreferenceScreen;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
//import android.widget.Switch;
import android.widget.CompoundButton;
import android.app.Activity;
import android.view.LayoutInflater;
import android.content.Context;
import android.app.ActionBar;
import android.view.Gravity;
import android.preference.PreferenceActivity;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.content.Intent;
import android.content.IntentFilter;


public class HctSystemMotion extends SettingsPreferenceFragment implements
		Preference.OnPreferenceClickListener/*, Preference.OnPreferenceChangeListener */{

    private CheckBoxPreference mClipScreenPreference;
	private CheckBoxPreference mThreePointScreenshot;
	private CheckBoxPreference mThreePointEntrycamera;
	private CheckBoxPreference mTwoPointAdjustVolume;
	private CheckBoxPreference mDoubleClick;

	private final static String PROPERTIES_CLIPSCREEN_ON = "persist.sys.hctclpscr";
	private final static String PROPERTIES_SHOTCAT_ON = "persist.sys.hctshotcat";
	private final static String PROPERTIES_ENTRY_CAMERA = "persist.sys.hctentrycamera";
	private final static String PROPERTIES_ADJUST_VOLUME = "persist.sys.hctadjstv";
	private static final String PERE_SYS_DUALCLK = "persist.sys.duleclksl";
	//add by yuanzhenlan
	//add by yuanzhenlan end
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.hct_telephony_motion);
		
		final PreferenceScreen parent = getPreferenceScreen();

		mThreePointScreenshot = (CheckBoxPreference)parent.findPreference("key_three_point_screenshot");
		mTwoPointAdjustVolume = (CheckBoxPreference)parent.findPreference("key_two_point_adjust_volume");
		mDoubleClick = (CheckBoxPreference)parent.findPreference("key_hct_double_tap_to_lock");
		mThreePointEntrycamera = (CheckBoxPreference)parent.findPreference("key_three_point_entrycamera");
		mClipScreenPreference = (CheckBoxPreference)parent.findPreference("key_hct_nubia_clip_screen");
		//add by yuanzhenlan
		//add by yuanzhenlan end

		if(mThreePointScreenshot!=null){
		mThreePointScreenshot.setOnPreferenceClickListener(this);
		mThreePointScreenshot.setChecked(SystemProperties.getBoolean(PROPERTIES_SHOTCAT_ON, false));
		}

		if(mClipScreenPreference != null){
            mClipScreenPreference.setOnPreferenceClickListener(this);
            mClipScreenPreference.setChecked(SystemProperties.getBoolean(PROPERTIES_CLIPSCREEN_ON, false));
		}

		if(mThreePointEntrycamera != null){
			mThreePointEntrycamera.setOnPreferenceClickListener(this);
			mThreePointEntrycamera.setChecked(SystemProperties.getBoolean(PROPERTIES_ENTRY_CAMERA, false));
		}
		
		if(mTwoPointAdjustVolume != null){
			mTwoPointAdjustVolume.setOnPreferenceClickListener(this);
			mTwoPointAdjustVolume.setChecked(SystemProperties.getBoolean(PROPERTIES_ADJUST_VOLUME, false));
		}

		if(mDoubleClick != null){
		mDoubleClick.setOnPreferenceClickListener(this);
		///mDoubleClick.setOnPreferenceChangeListener(this);
		mDoubleClick.setChecked(SystemProperties.getBoolean(PERE_SYS_DUALCLK, false));
		}
		//add by yuanzhenlan

		//add by yuanzhenlan end

		   if(getResources().getBoolean(R.bool.config_hct_remove_threepointscreenshot))
		         {
		             parent.removePreference(mThreePointScreenshot);//add by jiashixian
		          }	

            if(getResources().getBoolean(R.bool.config_hct_remove_clipscreen))
            {
                parent.removePreference(mClipScreenPreference);
            }	

		   if(getResources().getBoolean(R.bool.config_hct_remove_threepointentrycamera))
		         {
            parent.removePreference(mThreePointEntrycamera);
		          }
           
		   if(getResources().getBoolean(R.bool.config_hct_remove_doubleclick))
		         {
            parent.removePreference(mDoubleClick);
		          }

		   if(getResources().getBoolean(R.bool.config_hct_remove_twopoint_volume))
		         {
		             parent.removePreference(mTwoPointAdjustVolume);//add by jiashixian

 
		          }         //add by yuanzhenlan 

	}

	@Override
	public void onResume() {
		super.onResume();
	}


	@Override
	public boolean onPreferenceClick(Preference preference) {
		Log.v("aabbcc", "onPreferenceClick():"+((CheckBoxPreference)preference).isChecked());
		if(mThreePointScreenshot == preference){
			SystemProperties.set(PROPERTIES_SHOTCAT_ON, mThreePointScreenshot.isChecked() ? "true" : "false");
		}else if(mClipScreenPreference == preference){
		    Log.v("aabbcc", "set prop:"+PROPERTIES_CLIPSCREEN_ON+" mClipScreenPreference.isChecked():"+mClipScreenPreference.isChecked());
			SystemProperties.set(PROPERTIES_CLIPSCREEN_ON, mClipScreenPreference.isChecked() ? "true" : "false");
		}else if(mThreePointEntrycamera == preference){
			SystemProperties.set(PROPERTIES_ENTRY_CAMERA, mThreePointEntrycamera.isChecked() ? "true" : "false");
		}else if(mTwoPointAdjustVolume == preference){
			SystemProperties.set(PROPERTIES_ADJUST_VOLUME, mTwoPointAdjustVolume.isChecked() ? "true" : "false");
		}else if(mDoubleClick == preference){
			Log.v("aabbcc", "find");
			SystemProperties.set(PERE_SYS_DUALCLK, mDoubleClick.isChecked() ? "true" : "false");
		}
	    return true;
	}

/*
	@Override
	public boolean onPreferenceChange(Preference arg0, Object newValue) {
		Log.v("aabbcc", "onPreferenceChange():"+mDoubleClick.isChecked());
	    return true;
	}
*/
}
