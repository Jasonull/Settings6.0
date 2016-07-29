package com.android.settings.fingerprint;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.Utils;
public class QuickStartActivity extends PreferenceActivity{
	private static final String TAG = "QuickStartActivity";
	public static final String KEY_FINGERPRINT_1 = "fingerprint_1";
	public static final String KEY_FINGERPRINT_2 = "fingerprint_2";
	public static final String KEY_FINGERPRINT_3 = "fingerprint_3";
	public static final String KEY_FINGERPRINT_4 = "fingerprint_4";
	public static final String KEY_FINGERPRINT_5 = "fingerprint_5";
	public static final String KEY_FINGERPRINT_NAME1 = "fingerprint_title1";
	public static final String KEY_FINGERPRINT_NAME2 = "fingerprint_title2";
	public static final String KEY_FINGERPRINT_NAME3 = "fingerprint_title3";
	public static final String KEY_FINGERPRINT_NAME4 = "fingerprint_title4";
	public static final String KEY_FINGERPRINT_NAME5 = "fingerprint_title5";
	public static final String KEY_FINGERPRINT_SUMMARY1 = "fingerprint_summary1";
	public static final String KEY_FINGERPRINT_SUMMARY2 = "fingerprint_summary2";
	public static final String KEY_FINGERPRINT_SUMMARY3 = "fingerprint_summary3";
	public static final String KEY_FINGERPRINT_SUMMARY4 = "fingerprint_summary4";
	public static final String KEY_FINGERPRINT_SUMMARY5 = "fingerprint_summary5";


	private Preference mFingerprint1;
	private Preference mFingerprint2;
	private Preference mFingerprint3;
	private Preference mFingerprint4;
	private Preference mFingerprint5;
	private Preference mFingerprintNew;
	private Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.fingerprint_quickstart);
		mFingerprint1 = findPreference(KEY_FINGERPRINT_1);
		mFingerprint2 = findPreference(KEY_FINGERPRINT_2);
		mFingerprint3 = findPreference(KEY_FINGERPRINT_3);
		mFingerprint4 = findPreference(KEY_FINGERPRINT_4);
		mFingerprint5 = findPreference(KEY_FINGERPRINT_5);
		mContext = this;
	}
	@Override
	public void onResume(){
		super.onResume();
		setTitle(R.string.fingerprint_settings_title);
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
		getPreferenceScreen().addPreference(mFingerprint1);
		getPreferenceScreen().addPreference(mFingerprint2);
		getPreferenceScreen().addPreference(mFingerprint3);
		getPreferenceScreen().addPreference(mFingerprint4);
		getPreferenceScreen().addPreference(mFingerprint5);
		
		FingerprintData.setMaxFingerCount(5);
		
		if (FingerprintData.isDataExsitWithIndex(mContext, "fingerprint_1") == FingerprintData.DATA_NOT_EXSIT){
			getPreferenceScreen().removePreference(this.mFingerprint1);
		}else{
			mFingerprint1.setTitle(FingerprintData.fingerTitle(mContext, FingerprintData.FINGERPRINT_1));
			mFingerprint1.setSummary(FingerprintData.fingerSummary(mContext, FingerprintData.FINGERPRINT_1));
		}
	
		if (FingerprintData.isDataExsitWithIndex(mContext, "fingerprint_2") == FingerprintData.DATA_NOT_EXSIT){
			getPreferenceScreen().removePreference(this.mFingerprint2);
		}else{
			mFingerprint2.setTitle(FingerprintData.fingerTitle(mContext, FingerprintData.FINGERPRINT_2));
			mFingerprint2.setSummary(FingerprintData.fingerSummary(mContext, FingerprintData.FINGERPRINT_2));
		}
	
		if (FingerprintData.isDataExsitWithIndex(mContext, "fingerprint_3") == FingerprintData.DATA_NOT_EXSIT){
			getPreferenceScreen().removePreference(this.mFingerprint3);
		}else{
			mFingerprint3.setTitle(FingerprintData.fingerTitle(mContext, FingerprintData.FINGERPRINT_3));
			mFingerprint3.setSummary(FingerprintData.fingerSummary(mContext, FingerprintData.FINGERPRINT_3));
		}
		
		if (FingerprintData.isDataExsitWithIndex(mContext, "fingerprint_4") == FingerprintData.DATA_NOT_EXSIT){
			getPreferenceScreen().removePreference(this.mFingerprint4);
		}else{
			mFingerprint4.setTitle(FingerprintData.fingerTitle(mContext, FingerprintData.FINGERPRINT_4));
			mFingerprint4.setSummary(FingerprintData.fingerSummary(mContext, FingerprintData.FINGERPRINT_4));
		}
		
		if (FingerprintData.isDataExsitWithIndex(mContext, "fingerprint_5") == FingerprintData.DATA_NOT_EXSIT){
			getPreferenceScreen().removePreference(this.mFingerprint5);
		}else{
			mFingerprint5.setTitle(FingerprintData.fingerTitle(mContext, FingerprintData.FINGERPRINT_5));
			mFingerprint5.setSummary(FingerprintData.fingerSummary(mContext, FingerprintData.FINGERPRINT_5));
		}
		
	}	
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference){
		if(preference == mFingerprint1){
			quickStartApplication(FingerprintData.FINGERPRINT_1);
		}else if(preference == mFingerprint2){
			quickStartApplication(FingerprintData.FINGERPRINT_2);
		}else if(preference == mFingerprint3){
			quickStartApplication(FingerprintData.FINGERPRINT_3);
		}else if(preference == mFingerprint4){
			quickStartApplication(FingerprintData.FINGERPRINT_4);
		}else if(preference == mFingerprint5){
			quickStartApplication(FingerprintData.FINGERPRINT_5);
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);	
	}
	
	private void quickStartApplication(int index){
		Intent intent = new Intent();
		intent.setClassName("com.android.settings", "com.android.settings.fingerprint.QuickStartAppliaction");
		intent.putExtra("index", index);
		startActivity(intent);
	}
	
}
