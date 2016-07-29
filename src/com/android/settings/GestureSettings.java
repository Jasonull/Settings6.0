package com.android.settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.CheckBoxPreference;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.IFacedetectedService;
import android.os.SystemProperties;
public class GestureSettings extends SettingsPreferenceFragment implements DialogInterface.OnClickListener{

     private static final String TOGGLE_GALLERY_PREFERENCE =
             "toggle_gallery_preference";
     private static final String TOGGLE_MUSIC_PREFERENCE =
             "toggle_music_preference";
     private static final String TOGGLE_CAMERA_PREFERENCE =
             "toggle_camera_preference";
     private static final String TOGGLE_LAUNCHER_PREFERENCE =
             "toggle_launcher_preference";
     private static final String TOGGLE_UNLOCK_PREFERENCE =
             "toggle_unlock_preference";
     private static final String TOGGLE_MMSDIAL_PREFERENCE =
             "toggle_mmsdial_preference";
     private static final String TOGGLE_CONTACTDIAL_PREFERENCE =
             "toggle_contactdial_preference";
     private static final String TOGGLE_AUTOANSWER_PREFERENCE =
             "toggle_autoanswer_preference";
     private static final String TOGGLE_SMARTREAD_PREFERENCE =
             "toggle_smartread_preference";
	 //private static final String GESTURE_CATEGORY = "gesture_category";
     private PreferenceCategory mGestureCategory;
    
     private CheckBoxPreference mToggleGalleryPreference;
     private CheckBoxPreference mToggleMusicPreference;
     private CheckBoxPreference mToggleCameraPreference;
     private CheckBoxPreference mToggleLauncherPreference;
     private CheckBoxPreference mToggleUnlockPreference;
     private CheckBoxPreference mToggleMmsDialPreference;
     private CheckBoxPreference mToggleContactDialPreference;	//yangguodong  auto dial 
     private CheckBoxPreference mToggleAutoanswerPreference;
  private CheckBoxPreference mToggleSmartreadPreference; //add by xuejin face detected

     private DialogInterface mWarnAutoanswer;
     private DialogInterface mWarnAutoDial;

     @Override
     public void onCreate(Bundle icicle) {
         super.onCreate(icicle);
         addPreferencesFromResource(R.xml.gesture_settings);
         mToggleGalleryPreference =
             (CheckBoxPreference) findPreference(TOGGLE_GALLERY_PREFERENCE);
         mToggleMusicPreference =
             (CheckBoxPreference) findPreference(TOGGLE_MUSIC_PREFERENCE);
         mToggleCameraPreference =
             (CheckBoxPreference) findPreference(TOGGLE_CAMERA_PREFERENCE);
         mToggleLauncherPreference =
             (CheckBoxPreference) findPreference(TOGGLE_LAUNCHER_PREFERENCE);
         mToggleUnlockPreference =
             (CheckBoxPreference) findPreference(TOGGLE_UNLOCK_PREFERENCE);
         mToggleMmsDialPreference =
             (CheckBoxPreference) findPreference(TOGGLE_MMSDIAL_PREFERENCE);
         mToggleContactDialPreference =
             (CheckBoxPreference) findPreference(TOGGLE_CONTACTDIAL_PREFERENCE);
         mToggleAutoanswerPreference =
             (CheckBoxPreference) findPreference(TOGGLE_AUTOANSWER_PREFERENCE);
		 //add by xuejin face detected
         mToggleSmartreadPreference =
             (CheckBoxPreference) findPreference(TOGGLE_SMARTREAD_PREFERENCE);
		 //end by xuejin face detected
     }

     @Override
     public void onResume() {
         super.onResume();
		 //Utils.actionbarMade(getActivity(), true);
         initAllPreferences();
     }
    
     private void initAllPreferences(){
		 //mGestureCategory = (PreferenceCategory) findPreference(GESTURE_CATEGORY);

         mToggleGalleryPreference.setChecked(android.os.SystemProperties.getBoolean("persist.sys.ng_gallery",false));
         mToggleMusicPreference.setChecked(android.os.SystemProperties.getBoolean("persist.sys.ng_music",false));
         mToggleCameraPreference.setChecked(android.os.SystemProperties.getBoolean("persist.sys.ng_camera",false));
         mToggleLauncherPreference.setChecked(android.os.SystemProperties.getBoolean("persist.sys.ng_launcher",false));
         mToggleUnlockPreference.setChecked(android.os.SystemProperties.getBoolean("persist.sys.ng_keyguard",false));
         mToggleMmsDialPreference.setChecked(android.os.SystemProperties.getBoolean("persist.sys.ng_autodial",false));
         mToggleAutoanswerPreference.setChecked(android.os.SystemProperties.getBoolean("persist.sys.ng_autoanswer",false));
         mToggleContactDialPreference.setChecked(android.os.SystemProperties.getBoolean("persist.sys.ng_autodialContact",false));
	if(getResources().getBoolean(R.bool.remove_autoanswer_preference)){
		PreferenceCategory somatosensorycategory = (PreferenceCategory) findPreference("somatosensory_category");
		somatosensorycategory.removePreference(mToggleAutoanswerPreference);
	}
	if(getResources().getBoolean(R.bool.remove_music_preference)){
		PreferenceCategory gesturecategory = (PreferenceCategory) findPreference("gesture_category");
		gesturecategory.removePreference(mToggleMusicPreference);
	}
		 //add by xuejin face detected
		 //if(!FeatureOption.HCT_FACE_RECOGNITION){
//if(false){
if(SystemProperties.get("ro.hct_face_detected").equals("1")){
		    
			mToggleSmartreadPreference.setChecked(android.os.SystemProperties.getBoolean("persist.sys.facedetected_on",false));

		 }else{
      PreferenceCategory smartPreferenceCategory = (PreferenceCategory) findPreference("smartmode_category");   	
      if(smartPreferenceCategory != null){
				getPreferenceScreen().removePreference(smartPreferenceCategory);
			}
		 }
    	 //end by xuejin face detected
		 //hct macong remove launcher view in  Smart somatosensory 2015.9.7
        if(getResources().getBoolean(R.bool.config_remove_launcher_gesture)){
			PreferenceCategory gesture_category = (PreferenceCategory) findPreference("gesture_category");
            if(gesture_category != null && mToggleLauncherPreference != null){
               gesture_category.removePreference(mToggleLauncherPreference);
            }
        }
		//hct macong remove launcher view in  Smart somatosensory 2015.9.7 end
     }


     private void warnAutoanswer() {
        mWarnAutoanswer = new AlertDialog.Builder(getActivity()).setTitle(
                getResources().getString(R.string.error_title))
                .setIcon(com.android.internal.R.drawable.ic_dialog_alert)
                .setMessage(getResources().getString(R.string.open_auto_answer_warning))
                .setPositiveButton(android.R.string.yes, this)
                .setNegativeButton(android.R.string.no, null)
                .show();
     }

     public void onClick(DialogInterface dialog, int which) {
        if (dialog == mWarnAutoanswer && which == DialogInterface.BUTTON_POSITIVE) {
            if (mToggleAutoanswerPreference != null) {
                mToggleAutoanswerPreference.setChecked(true);
				android.os.SystemProperties.set("persist.sys.ng_autoanswer","true");
            }
        }
     }

     @Override
     public void onDestroy() {
        super.onDestroy();
        if (mWarnAutoanswer != null) {
            mWarnAutoanswer.dismiss();
        }
     }
    
     private boolean handlePreference(PreferenceScreen preferenceScreen, Preference preference){
         if (mToggleGalleryPreference == preference) {
             android.os.SystemProperties.set("persist.sys.ng_gallery",mToggleGalleryPreference.isChecked()? "true":"false");
             return true;
         } else if (mToggleMusicPreference == preference) {
             android.os.SystemProperties.set("persist.sys.ng_music",mToggleMusicPreference.isChecked()? "true":"false");
             return true;
         } else if (mToggleCameraPreference == preference) {
             android.os.SystemProperties.set("persist.sys.ng_camera",mToggleCameraPreference.isChecked()? "true":"false");
             return true;
         } else if (mToggleLauncherPreference == preference) {
             android.os.SystemProperties.set("persist.sys.ng_launcher",mToggleLauncherPreference.isChecked()? "true":"false");
             return true;
         } else if (mToggleUnlockPreference == preference) {
             android.os.SystemProperties.set("persist.sys.ng_keyguard",mToggleUnlockPreference.isChecked()? "true":"false");
             return true;
         } else if (mToggleMmsDialPreference == preference) {
             android.os.SystemProperties.set("persist.sys.ng_autodial",mToggleMmsDialPreference.isChecked()? "true":"false");
             return true;
         } else if (mToggleContactDialPreference == preference) {
             android.os.SystemProperties.set("persist.sys.ng_autodialContact",mToggleContactDialPreference.isChecked()? "true":"false");
             return true;
         } else if (mToggleAutoanswerPreference == preference) {
			if(getResources().getBoolean(R.bool.auto_answer_warning_enable)){
				if (mToggleAutoanswerPreference.isChecked()) {
		            mToggleAutoanswerPreference.setChecked(false);
		            warnAutoanswer();
		        } else {
		            android.os.SystemProperties.set("persist.sys.ng_autoanswer","false");
		        }
			}else{
				android.os.SystemProperties.set("persist.sys.ng_autoanswer",mToggleAutoanswerPreference.isChecked()? "true":"false");
			}
             return true;
         }
		 //add by xuejin face detected
		 else if (mToggleSmartreadPreference == preference) {
                android.os.SystemProperties.set("persist.sys.facedetected_on",mToggleSmartreadPreference.isChecked()? "true":"false");
				IBinder faceBinder = ServiceManager.getService("facedetected");
				IFacedetectedService iFacedetectedService = IFacedetectedService.Stub.asInterface(faceBinder);
				try {
					iFacedetectedService.end();
				} catch (RemoteException e) {
					//Log.e(TAG, "Facedetected service is unavailable for queries");
				}
                
				if(!mToggleSmartreadPreference.isChecked()){
					Intent intent = new Intent();
                    intent.setAction("com.hct.action.FACE_DETECTED");
                    intent.putExtra("face", 0);
                    getActivity().sendBroadcast(intent);
				}
                return true;
         }
    	//end by xuejin face detected
         return false;
     }
    
     @Override
     public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
         boolean ret = handlePreference(preferenceScreen, preference);
         if (!ret) {
            ret = super.onPreferenceTreeClick(preferenceScreen, preference);
         }
         return ret;
    }
}


