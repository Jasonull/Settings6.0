package com.android.settings.fingerprint;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.android.settings.R;
public class FingerprintMoreSettings extends PreferenceActivity implements FingerprintSwitchPreference.SwitchChange{
    public static final String KEY_FINGERPRINT_UNLOCK_HOME = "fingerprint_unlock_home";  
    public static final String KEY_FINGERPRINT_UNLOCK_CAMERA = "fingerprint_unlock_camera";
    public static final String KEY_FINGERPRINT_UNLOCK_CALL = "fingerprint_unlock_call";  
    public static final String KEY_FINGERPRINT_UNLOCK_IMAGE = "fingerprint_unlock_image";  
    public static final String KEY_FINGERPRINT_UNLOCK_MUSIC = "fingerprint_unlock_music"; 
    public static final String KEY_FINGERPRINT_UNLOCK_VIDEO = "fingerprint_unlock_video";
    private Context mContext;
    private FingerprintSwitchPreference mUnlockHome;
	private FingerprintSwitchPreference mUnlockCamera;
	private FingerprintSwitchPreference mUnlockCall;
	private FingerprintSwitchPreference mUnlockImage;
	private FingerprintSwitchPreference mUnlockMusic;
	private FingerprintSwitchPreference mUnlockVideo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	addPreferencesFromResource(R.xml.fingerprint_more_settings);
    	mUnlockHome = (FingerprintSwitchPreference) findPreference(KEY_FINGERPRINT_UNLOCK_HOME);
		mUnlockCamera = (FingerprintSwitchPreference) findPreference(KEY_FINGERPRINT_UNLOCK_CAMERA);
		mUnlockCall = (FingerprintSwitchPreference) findPreference(KEY_FINGERPRINT_UNLOCK_CALL);
		mUnlockImage = (FingerprintSwitchPreference) findPreference(KEY_FINGERPRINT_UNLOCK_IMAGE);
		mUnlockMusic = (FingerprintSwitchPreference) findPreference(KEY_FINGERPRINT_UNLOCK_MUSIC);
		mUnlockVideo = (FingerprintSwitchPreference) findPreference(KEY_FINGERPRINT_UNLOCK_VIDEO);
		mUnlockHome.setSwitchChange(this);
		mUnlockCamera.setSwitchChange(this);
		mUnlockCall.setSwitchChange(this);
		mUnlockImage.setSwitchChange(this);
		mUnlockMusic.setSwitchChange(this);
		mUnlockVideo.setSwitchChange(this);
		mContext = this;
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	setTitle(R.string.ftp_string_more);
    	InitTitleView();
    	initAllPreferences();
    }
    
	private void InitTitleView() {
		int color = this.GetCurrentTheme(getPackageName());
		getWindow().setStatusBarColor(getResources().getColor(color));
		ColorDrawable cd = new ColorDrawable(getResources().getColor(color));
		if(getActionBar() != null)
			getActionBar().setBackgroundDrawable(cd);
	}
	
    private void initAllPreferences(){
    	int home = FingerprintData.isDataExsitWithIndex(mContext, KEY_FINGERPRINT_UNLOCK_HOME);
		if(home == 1){
			mUnlockHome.setCurrentStatus(true);
		}else{
			mUnlockHome.setCurrentStatus(false);
		}
		
		int camera = FingerprintData.isDataExsitWithIndex(mContext, KEY_FINGERPRINT_UNLOCK_CAMERA);
		if(camera == 1){
			mUnlockCamera.setCurrentStatus(true);
		}else{
			mUnlockCamera.setCurrentStatus(false);
		}
		
		int call = FingerprintData.isDataExsitWithIndex(mContext, KEY_FINGERPRINT_UNLOCK_CALL);
		if(call == 1){
			mUnlockCall.setCurrentStatus(true);
		}else{
			mUnlockCall.setCurrentStatus(false);
		}
		
		int image = FingerprintData.isDataExsitWithIndex(mContext, KEY_FINGERPRINT_UNLOCK_IMAGE);
		if(image == 1){
			mUnlockImage.setCurrentStatus(true);
		}else{
			mUnlockImage.setCurrentStatus(false);
		}
		
		int music = FingerprintData.isDataExsitWithIndex(mContext, KEY_FINGERPRINT_UNLOCK_MUSIC);
		if(music == 1){
			mUnlockMusic.setCurrentStatus(true);
		}else{
			mUnlockMusic.setCurrentStatus(false);
		}
		
		int video = FingerprintData.isDataExsitWithIndex(mContext, KEY_FINGERPRINT_UNLOCK_VIDEO);
		if(video == 1){
			mUnlockVideo.setCurrentStatus(true);
		}else{
			mUnlockVideo.setCurrentStatus(false);
		}
    }

	@Override
	public void onSwitchChange(String type, boolean state) {
		// TODO Auto-generated method stub
		FingerprintData.saveToDatabase(mContext, type, state ? 1 : 0);
	}
}
