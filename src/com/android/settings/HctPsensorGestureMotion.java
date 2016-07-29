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
import android.preference.PreferenceCategory;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.IFacedetectedService;

public class HctPsensorGestureMotion extends SettingsPreferenceFragment implements
		Preference.OnPreferenceClickListener/*, Preference.OnPreferenceChangeListener */{

    private CheckBoxPreference mPsensorGalleryPreference;
       private CheckBoxPreference mPsensorLauncherPreference;
        private CheckBoxPreference mPsensorFmPreference;
     private CheckBoxPreference mPsensorAutoanswerPreference;
     private CheckBoxPreference mPsensorMusicPreference;
 private CheckBoxPreference smartPreferenceCategory;//hct macong add smart mode
	private final static String PROPERTIES_GALLERY_ON = "persist.sys.ps_gallery";
	private final static String PROPERTIES_LAUNCHER_ON = "persist.sys.ps_launcher";
	private final static String PROPERTIES_FMRADIO_ON = "persist.sys.ps_fmradio";
	private final static String PROPERTIES_AUTOANSWER_ON = "persist.sys.ps_autoanswer";
	private final static String PROPERTIES_MUSIC_ON = "persist.sys.ps_music";
    private final static String PROPERTIES_SMART_FACE = "persist.sys.facedetected_on";//hct macong add smart mode

        private final static String TAG = "ps_ges_setting";
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.hct_psensor_gesture_motion);
		
		//final PreferenceScreen parent = getPreferenceScreen();
                      
                      mPsensorGalleryPreference = (CheckBoxPreference)findPreference("key_psensor_gesture_gallery");
	           mPsensorLauncherPreference = (CheckBoxPreference)findPreference("key_psensor_gesture_launcher");
		mPsensorFmPreference = (CheckBoxPreference)findPreference("key_psensor_gesture_fmradio");
		mPsensorAutoanswerPreference = null;//(CheckBoxPreference)findPreference("key_psensor_gesture_autoanswer");
		mPsensorMusicPreference = (CheckBoxPreference)findPreference("key_psensor_gesture_music");
		smartPreferenceCategory = (CheckBoxPreference) findPreference("toggle_smartread_preference");//hct macong add smart mode
        

		if(mPsensorGalleryPreference!=null){
		mPsensorGalleryPreference.setOnPreferenceClickListener(this);
		mPsensorGalleryPreference.setChecked(SystemProperties.getBoolean(PROPERTIES_GALLERY_ON, false));
		}

		if(mPsensorLauncherPreference != null){
            mPsensorLauncherPreference.setOnPreferenceClickListener(this);
            mPsensorLauncherPreference.setChecked(SystemProperties.getBoolean(PROPERTIES_LAUNCHER_ON, false));
		}

		if(mPsensorFmPreference != null){
			mPsensorFmPreference.setOnPreferenceClickListener(this);
			mPsensorFmPreference.setChecked(SystemProperties.getBoolean(PROPERTIES_FMRADIO_ON, false));
		}
		
		if(mPsensorAutoanswerPreference != null){
			mPsensorAutoanswerPreference.setOnPreferenceClickListener(this);
			mPsensorAutoanswerPreference.setChecked(SystemProperties.getBoolean(PROPERTIES_AUTOANSWER_ON, false));
		}
	
		if(mPsensorMusicPreference != null){
			mPsensorMusicPreference.setOnPreferenceClickListener(this);
			mPsensorMusicPreference.setChecked(SystemProperties.getBoolean(PROPERTIES_MUSIC_ON, false));
		}
     //hct macong add smart mode
        if(SystemProperties.get("ro.hct_face_detected").equals("1")){
			smartPreferenceCategory.setOnPreferenceClickListener(this);
			smartPreferenceCategory.setChecked(android.os.SystemProperties.getBoolean(PROPERTIES_SMART_FACE,false));
		 }else{
             PreferenceCategory smart =  (PreferenceCategory)findPreference("smartmode_category");
             if(smartPreferenceCategory != null && smart != null){
		        smart.removePreference(smartPreferenceCategory);
		      }
		 }
		 //hct macong add smart mode end
	}

	@Override
	public void onResume() {
		super.onResume();
	}


	@Override
	public boolean onPreferenceClick(Preference preference) {
		Log.v(TAG, "onPreferenceClick():"+((CheckBoxPreference)preference).isChecked());
		if(mPsensorGalleryPreference == preference){
			SystemProperties.set(PROPERTIES_GALLERY_ON, mPsensorGalleryPreference.isChecked() ? "true" : "false");
		}else if(mPsensorLauncherPreference == preference){
		    Log.v(TAG, "set prop:"+PROPERTIES_LAUNCHER_ON+" mPsensorLauncherPreference.isChecked():"+mPsensorLauncherPreference.isChecked());
			SystemProperties.set(PROPERTIES_LAUNCHER_ON, mPsensorLauncherPreference.isChecked() ? "true" : "false");
		}else if(mPsensorFmPreference == preference){
			SystemProperties.set(PROPERTIES_FMRADIO_ON, mPsensorFmPreference.isChecked() ? "true" : "false");
		}else if(mPsensorAutoanswerPreference == preference){
			SystemProperties.set(PROPERTIES_AUTOANSWER_ON, mPsensorAutoanswerPreference.isChecked() ? "true" : "false");
		}else if(mPsensorMusicPreference == preference){
			SystemProperties.set(PROPERTIES_MUSIC_ON, mPsensorMusicPreference.isChecked() ? "true" : "false");
			//hct macong add smart mode
		}else if(smartPreferenceCategory == preference){
			SystemProperties.set(PROPERTIES_SMART_FACE, smartPreferenceCategory.isChecked() ? "true" : "false");
            IBinder faceBinder = ServiceManager.getService("facedetected");
            IFacedetectedService iFacedetectedService = IFacedetectedService.Stub.asInterface(faceBinder);
            try {
        	iFacedetectedService.end();
            } catch (RemoteException e) {
              //Log.e(TAG, "Facedetected service is unavailable for queries");
            }
            if(!smartPreferenceCategory.isChecked()){
               Intent intent = new Intent();
               intent.setAction("com.hct.action.FACE_DETECTED");
               intent.putExtra("face", 0);
               getActivity().sendBroadcast(intent);
            } 
			//hct macong add smart mode end        
		}
		
		switchPsSensor();
	    return true;
	}
	
	public static void switchPsSensor(){

		boolean galleryOn = SystemProperties.getBoolean(PROPERTIES_GALLERY_ON, false);
		boolean luncherOn = SystemProperties.getBoolean(PROPERTIES_LAUNCHER_ON, false);
		boolean fmOn = SystemProperties.getBoolean(PROPERTIES_FMRADIO_ON, false);
		boolean autoAnswerOn = SystemProperties.getBoolean(PROPERTIES_AUTOANSWER_ON, false);
		boolean musicOn = SystemProperties.getBoolean(PROPERTIES_MUSIC_ON, false);

                boolean curSetOpen = SystemProperties.get("persist.sys.ps_ges_open").equals("1");
                Log.v(TAG, "galleryOn:"+galleryOn+" luncherOn:"+luncherOn+" fmOn:"+fmOn+" autoAnswerOn:"+autoAnswerOn+" musicOn:"+musicOn);
		if(galleryOn || luncherOn || fmOn || autoAnswerOn || musicOn){
		    //on
		    if(curSetOpen == false){
		        SystemProperties.set("persist.sys.ps_ges_open", "1");
		        Log.v(TAG, "set open");
		    }
		}else{
		    //off
		    if(curSetOpen == true){
		        SystemProperties.set("persist.sys.ps_ges_open", "0");
		        Log.v(TAG, "set close");
		    }
		}

           }
/*
	@Override
	public boolean onPreferenceChange(Preference arg0, Object newValue) {
		Log.v("aabbcc", "onPreferenceChange():"+mDoubleClick.isChecked());
	    return true;
	}
*/
}
