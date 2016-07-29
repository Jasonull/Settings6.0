package com.android.settings.fingerprint;

import java.io.File;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.android.settings.ChooseLockPassword;
import com.android.settings.ChooseLockPattern;
import com.android.settings.ChooseLockSettingsHelper;
import com.android.settings.ConfirmLockPattern;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R;
public class FingerPrintScreen extends SettingsPreferenceFragment{
	private static final String TAG = "FingerPrintScreen";
	private static final int CONFIRM_EXISTING_REQUEST = 100;
	private static final int FALLBACK_REQUEST = 101;
	private static final int MIN_PASSWORD_LENGTH = 4;
	private static final String KEY_FINGERPRINT_PATTERN = "fingerprint_pattern";
	private static final String KEY_FINGERPRINT_PIN = "fingerprint_pin";
	private ChooseLockSettingsHelper mChooseLockSettingsHelper;
	private Preference mFingerprintPattern;
	private Preference mFingerprintPin;
	private DevicePolicyManager mDPM;
	private boolean mRequirePassword = true;
	@Override
	public void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.fingerprint_chooselock);
		mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity());
		LockPatternUtils utils = mChooseLockSettingsHelper.utils();
		mFingerprintPattern = findPreference(KEY_FINGERPRINT_PATTERN);
		mFingerprintPin = findPreference(KEY_FINGERPRINT_PIN);
		/*if(utils.isLockPatternEnabled()){
			confirmPattern(CONFIRM_EXISTING_REQUEST);
		}else */
		boolean isCheck = getActivity().getIntent().getBooleanExtra("fingerprint_check", false);
		if(!isCheck){
			if(utils.isLockPinEnabled()){
				confirmPassword(CONFIRM_EXISTING_REQUEST);
			}else{
				updateUnlockMethodAndFinish(DevicePolicyManager.PASSWORD_QUALITY_NUMERIC, false);
			}
		}else{
			String password = getActivity().getIntent().getExtras().getString("fingerprint");
        	if(password != null){
	            String lockpsw = AppLockUntil.passwordsToHash(password);
	            android.os.SystemProperties.set("persist.sys.lock_app_psw", lockpsw);
        	}
            if("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_jmt", "0"))){
        		startJmtFingerprint();
        	}else if("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_synochip", "0"))){
        		startSynochipFingerprint();//add by synochip
        	}else{
        		startFragment(this, "com.android.settings.fingerprint.FingerPrintSettings", R.string.fingerprint_settings_title, CONFIRM_EXISTING_REQUEST, null);
        	}
			getActivity().finish();
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		getActivity().setTitle(R.string.fingerprint_choose_lock_title);
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub
		if(preference == mFingerprintPattern){
			updateUnlockMethodAndFinish(DevicePolicyManager.PASSWORD_QUALITY_SOMETHING, false);
		}else if(preference == mFingerprintPin){
			updateUnlockMethodAndFinish(DevicePolicyManager.PASSWORD_QUALITY_NUMERIC, false);
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
	
	void updateUnlockMethodAndFinish(int quality, boolean disabled) {

        final boolean isFallback = getActivity().getIntent()
            .getBooleanExtra(LockPatternUtils.LOCKSCREEN_WEAK_FALLBACK, false);  //M: Modify for voice unlock

        final Context context = getActivity();
        if (quality >= DevicePolicyManager.PASSWORD_QUALITY_NUMERIC) {
            int minLength = mDPM.getPasswordMinimumLength(null);
            if (minLength < MIN_PASSWORD_LENGTH) {
                minLength = MIN_PASSWORD_LENGTH;
            }
            //final int maxLength = mDPM.getPasswordMaximumLength(quality);
            Intent intent = getLockPasswordIntent(context, quality, isFallback, minLength,
            		MIN_PASSWORD_LENGTH, mRequirePassword,  /* confirm credentials */false);
            if (isFallback) {
                //M: Add for voice unlock @{
                String isFallbackFor = getActivity().getIntent().getStringExtra(LockPatternUtils.LOCKSCREEN_WEAK_FALLBACK_FOR);
                String commandKey = getActivity().getIntent().
                    getStringExtra(LockPatternUtils.SETTINGS_COMMAND_KEY);
                String commandValue = getActivity().getIntent().
                    getStringExtra(LockPatternUtils.SETTINGS_COMMAND_VALUE);
                intent.putExtra(LockPatternUtils.SETTINGS_COMMAND_KEY, commandKey);
                intent.putExtra(LockPatternUtils.SETTINGS_COMMAND_VALUE, commandValue);
                intent.putExtra(LockPatternUtils.LOCKSCREEN_WEAK_FALLBACK_FOR, isFallbackFor);
                //@}
                startActivityForResult(intent, CONFIRM_EXISTING_REQUEST);
                return;
            } else {
            	intent.putExtra("fingerprint_in", true);
            	startActivityForResult(intent, CONFIRM_EXISTING_REQUEST);
            	getActivity().finish();
            }
        } else if (quality == DevicePolicyManager.PASSWORD_QUALITY_SOMETHING) {
            Intent intent = getLockPatternIntent(context, isFallback, mRequirePassword,
                    /* confirm credentials */false);
            if (isFallback) {
                //M: Add for voice unlock @{
                String isFallbackFor = getActivity().getIntent().
                    getStringExtra(LockPatternUtils.LOCKSCREEN_WEAK_FALLBACK_FOR);
                String commandKey = getActivity().getIntent().
                    getStringExtra(LockPatternUtils.SETTINGS_COMMAND_KEY);
                String commandValue = getActivity().getIntent().
                    getStringExtra(LockPatternUtils.SETTINGS_COMMAND_VALUE);
                intent.putExtra(LockPatternUtils.SETTINGS_COMMAND_KEY, commandKey);
                intent.putExtra(LockPatternUtils.SETTINGS_COMMAND_VALUE, commandValue);
                intent.putExtra(LockPatternUtils.LOCKSCREEN_WEAK_FALLBACK_FOR, isFallbackFor);
                //@}
                startActivityForResult(intent, CONFIRM_EXISTING_REQUEST);
                return;
            } else {
            	intent.putExtra("fingerprint_in", true);
            	startActivityForResult(intent, CONFIRM_EXISTING_REQUEST);
            	getActivity().finish();
            }
        }
    }
	
	protected Intent getLockPasswordIntent(Context context, int quality,
			final boolean isFallback, int minLength, final int maxLength,
         boolean requirePasswordToDecrypt, boolean confirmCredentials) {
		 return ChooseLockPassword.createIntent(context, quality, isFallback, minLength,
             maxLength, requirePasswordToDecrypt, confirmCredentials);
	}

	protected Intent getLockPatternIntent(Context context, final boolean isFallback,
			final boolean requirePassword, final boolean confirmCredentials) {
		return ChooseLockPattern.createIntent(context, isFallback, requirePassword,
			confirmCredentials);
	}
     
	private boolean confirmPassword(int request) {
	    final Intent intent = new Intent();
	    intent.setClassName("com.android.settings", "com.android.settings.ConfirmLockPassword");
	    intent.putExtra("fingerprint_in", true);
	    startActivityForResult(intent, request);
	    getActivity().finish();
	    return true;
	}
	
	private boolean confirmPattern(int request) {
	    final Intent intent = new Intent();
	    intent.setClassName("com.android.settings", "com.android.settings.ConfirmLockPattern");
	    intent.putExtra("fingerprint_in", true);
	    startActivityForResult(intent, request);
		return true;
	}
	
    //add by sileadinc start
    private void startFingerPrint(){
    	Intent intent = new Intent();
        intent.setClassName("com.silead.fp", "com.silead.fp.MainActivity");
        startActivity(intent);
    }
    //add by sileadinc end
    
    //add by xuejin jmt start
    private void startJmtFingerprint(){
    	Intent intent = new Intent();
        intent.setClassName("com.eshare.fplock", "com.eshare.fplock.SetupIntro");
        startActivity(intent);
    }
    //add by xuejin jmt start
    
    //add by xuejin newschip
    private void startNewschipFingerprint(){
        File validityLib = new File ("/system/lib/libvcsfp.so");
        if (validityLib.exists()) {
    	    Intent intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.ChooseLockFingerprint");
            startActivity(intent);
        }
    }
    //add by xuejin newschip
    // synochip fingerprint
    private void startSynochipFingerprint(){
	Intent intent = new Intent();
	intent.setClassName("com.synochip.settings", "com.synochip.settings.MainActivity");
	startActivity(intent);
    }
    // synochip fingerprint
}
