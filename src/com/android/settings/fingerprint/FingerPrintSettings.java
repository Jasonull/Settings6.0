package com.android.settings.fingerprint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.settings.ChooseLockPassword;
import com.android.settings.ChooseLockPattern;
import com.android.settings.ChooseLockSettingsHelper;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.fingerprint.FingerprintEnrollActivity;
//egistec start 
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.UEventObserver;
import egistec.fingerauth.api.FPAuthListeners.EnrollListener;
import egistec.fingerauth.api.FPAuthListeners.EnrollMapProgressListener;
import egistec.fingerauth.api.FPAuthListeners.StatusListener;
import egistec.fingerauth.api.FPAuthListeners.VerifyListener;
import egistec.fingerauth.api.SettingLib;
import android.app.ProgressDialog;
import android.widget.Toast;
import android.content.pm.ActivityInfo;
import com.android.settings.fingerprint.AutoInterrpt.GetTHDCValueListener;
//egistec end
public class FingerPrintSettings extends SettingsPreferenceFragment
  implements FingerprintSwitchPreference.SwitchChange	{
	private static final String CONFIRM_CREDENTIALS = "confirm_credentials";
	public static final String KEY_FINGERPRINT_1 = "fingerprint_1";
	public static final String KEY_FINGERPRINT_2 = "fingerprint_2";
	public static final String KEY_FINGERPRINT_3 = "fingerprint_3";
	public static final String KEY_FINGERPRINT_4 = "fingerprint_4";
	public static final String KEY_FINGERPRINT_5 = "fingerprint_5";
	private static final String KEY_FINGERPRINT_LIST = "fingerprint_list";
	private static final String KEY_FINGERPRINT_NEW = "fingerprint_new";
	public static final String KEY_FINGERPRINT_NAME1 = "fingerprint_title1";
	public static final String KEY_FINGERPRINT_NAME2 = "fingerprint_title2";
	public static final String KEY_FINGERPRINT_NAME3 = "fingerprint_title3";
	public static final String KEY_FINGERPRINT_NAME4 = "fingerprint_title4";
	public static final String KEY_FINGERPRINT_NAME5 = "fingerprint_title5";
	public static final String KEY_FINGERPRINT_UNLOCK_KEYGUARD = "fingerprint_unlock_keyguard";
    public static final String KEY_FINGERPRINT_UNLOCK_APPLICATION = "fingerprint_unlock_application";
    public static final String KEY_FINGERPRINT_UNLOCK_QUCIKSTART = "fingerprint_unlock_quickstart";  
    public static final String KEY_FINGERPRINT_MORE = "fingerprint_more";
	private static final int RESULT_FINISHED = 1;
  
	private static final String TAG = "FingerPrintSettings";
	private ChooseLockSettingsHelper mChooseLockSettingsHelper;

	private FingerPrintPrefrnce mFingerprint1;
	private FingerPrintPrefrnce mFingerprint2;
	private FingerPrintPrefrnce mFingerprint3;
	private FingerPrintPrefrnce mFingerprint4;
	private FingerPrintPrefrnce mFingerprint5;
	private Preference mFingerprintNew;
	private PreferenceCategory mPreferenceCategoryList;
	private FingerprintSwitchPreference mUnlockKeyguard;
	private FingerprintSwitchPreference mUnlockApplication;
	private FingerprintSwitchPreference mUnlockQuickstart;
	private FingerprintSwitchPreference mTakePhoto;
	private Preference mFingerprintMore;
	private Context mContext;
	private int mDefaultMaxFinger = 5;
	//egistec start 
	private SettingLib mFPLib = null;
	private Activity mActivity = null;
    private Preference mFingerCal;
    private static final String KEY_FINGERPRINT_CAL = "fingerprint_calibration";
	private int mFPRet = 0;
	private ProgressDialog mProgressDialog = null;
	private static final int MSG_PROGRESS_SHOW = 1;
	private static final int MSG_PROGRESS_DISMISS = 2;
    public static final String KEY_FINGERPRINT_DOLF_UNLOCK_QUCIKSTART = "fingerprint_dolf_unlock_quickstart";
	private FingerprintSwitchPreference mDolfUnlockQuickstart;
	private boolean mIsFromSwitch = false;
    private static final String KEY_FINGERPRINT_TAKE_PHOTO = "fingerprint_camera_takephoto";
	private UEventObserver mPowerSupplyObserver = null;
    //add for dolf et310
    private AutoInterrpt mAutoInterrpt = null;
   //add for dolf et310
	private int mFingerId[] = {
			R.drawable.ftp_handleft_little,R.drawable.ftp_handleft_ring,R.drawable.ftp_handleft_middle,
			R.drawable.ftp_handleft_index,R.drawable.ftp_handleft_thumb,R.drawable.ftp_handright_thumb,
			R.drawable.ftp_handright_index,R.drawable.ftp_handright_middle,R.drawable.ftp_handright_ring,
			R.drawable.ftp_handright_little };
	private Handler mHandler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case MSG_PROGRESS_SHOW:
				if (null != mProgressDialog) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
				try {
					mProgressDialog = ProgressDialog.show((Activity) mActivity,
							null, ((Activity) mActivity)
									.getString(R.string.ftp_please_wait), true,
							false);
				} catch (Exception e) {
					Log.e(TAG, "MSG_PROGRESS_SHOW catch a exception: " + e);
				}
				break;
			case MSG_PROGRESS_DISMISS:
				if (null != mProgressDialog) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
					Log.d(TAG, "mProgressDialog dismiss");
					String toastStr = getActivity().getResources().getString(
							R.string.ftp_init_fail);
					if (mFPRet == 1) {
						toastStr = getActivity().getResources().getString(
								R.string.ftp_init_success);
					    Toast.makeText(getActivity(), toastStr, Toast.LENGTH_LONG)
							    .show();
                        return;
					}
					Toast.makeText(getActivity(), toastStr + " " + mFPRet, Toast.LENGTH_LONG)
							.show();
				}
				break;
			}
		}
	};
	// //egistec end

	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		addPreferencesFromResource(R.xml.fingerprint_main);
		mUnlockKeyguard = ((FingerprintSwitchPreference)findPreference(KEY_FINGERPRINT_UNLOCK_KEYGUARD));
		mUnlockApplication = (FingerprintSwitchPreference) findPreference(KEY_FINGERPRINT_UNLOCK_APPLICATION);
		mUnlockQuickstart = (FingerprintSwitchPreference) findPreference(KEY_FINGERPRINT_UNLOCK_QUCIKSTART);
		mPreferenceCategoryList = ((PreferenceCategory)findPreference(KEY_FINGERPRINT_LIST));
		mFingerprint1 = (FingerPrintPrefrnce)findPreference(KEY_FINGERPRINT_1);
		mFingerprint2 = (FingerPrintPrefrnce)findPreference(KEY_FINGERPRINT_2);
		mFingerprint3 = (FingerPrintPrefrnce)findPreference(KEY_FINGERPRINT_3);
		mFingerprint4 = (FingerPrintPrefrnce)findPreference(KEY_FINGERPRINT_4);
		mFingerprint5 = (FingerPrintPrefrnce)findPreference(KEY_FINGERPRINT_5);
		mFingerprintNew = findPreference(KEY_FINGERPRINT_NEW);
		mFingerprintMore = findPreference(KEY_FINGERPRINT_MORE);
		mUnlockKeyguard.setSwitchChange(this);
		mUnlockApplication.setSwitchChange(this);
		mUnlockQuickstart.setSwitchChange(this);
		mChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity());
		mContext = getActivity();
		mDefaultMaxFinger = mContext.getResources().getInteger(R.integer.default_max_fingerprint);
       //egistec start 
		mActivity = getActivity();
        mFingerCal = findPreference(KEY_FINGERPRINT_CAL);
        mDolfUnlockQuickstart = (FingerprintSwitchPreference)findPreference(KEY_FINGERPRINT_DOLF_UNLOCK_QUCIKSTART);
		mDolfUnlockQuickstart.setSwitchChange(this);
        mTakePhoto = (FingerprintSwitchPreference)findPreference(KEY_FINGERPRINT_TAKE_PHOTO);
        mTakePhoto.setSwitchChange(this);
		//egistec end 
	}
  
	@Override
	public void onResume(){
		super.onResume();
		getActivity().setTitle(R.string.fingerprint_settings_title);
		initAllPreferences();
		//egistec start 
		if(android.os.SystemProperties.get("ro.hct_fingerprint_et300").equals("1")){
			if (android.os.SystemProperties.getInt("persist.sys.initegistec", -1) != 1) {
				Message msg = new Message();
				msg.what = MSG_PROGRESS_SHOW;
				mHandler.sendMessage(msg);
                if(android.os.SystemProperties.get("ro.hct_fingerprint_et310").equals("1")){
                  mAutoInterrpt = new AutoInterrpt(mContext);
                  mAutoInterrpt.setGetTHDCValueListener(new GetTHDCValueListener() {
					
					@Override
					public void getTHDCValue(int iDCvalue, int iTHvalue) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void OnSuccess() {
						// TODO Auto-generated method stub
                        mFPRet = 1;
		                Message msg = new Message();
	                    msg.what = MSG_PROGRESS_DISMISS;
	                    mHandler.sendMessage(msg);
					}
					
					@Override
					public void OnFail() {
						// TODO Auto-generated method stub
                        mFPRet = android.os.SystemProperties.getInt("persist.sys.initegistec", -1);
	                    Message msg = new Message();
	                    msg.what = MSG_PROGRESS_DISMISS;
	                    mHandler.sendMessage(msg);
					}
				});
                  mAutoInterrpt.GetAutoTHDCValue();
                }else{
			      initLib();
                }
			}
       } 
		//egistec end
	}
	//egistec start 
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		//egistec start
       if(android.os.SystemProperties.get("ro.hct_fingerprint_et300").equals("1")){
            if(android.os.SystemProperties.get("ro.hct_fingerprint_et310").equals("1")){
                 if(mAutoInterrpt != null)
                 mAutoInterrpt.finish();
	             Message msg = new Message();
	             msg.what = MSG_PROGRESS_DISMISS;
	             mHandler.sendMessage(msg);
            }else{
		        if (mFPLib != null && isConnected) {
                    try{
			            mFPLib.abort();
			            mFPLib.unbind();
	                    Message msg = new Message();
	                    msg.what = MSG_PROGRESS_DISMISS;
	                    mHandler.sendMessage(msg);
                    } catch(Exception e){

                    }
		        }
            }
        }
		//egistec end
		super.onPause();
	}
	//egistec end
	private void initAllPreferences(){
		getPreferenceScreen().addPreference(mFingerprint1);
		getPreferenceScreen().addPreference(mFingerprint2);
		getPreferenceScreen().addPreference(mFingerprint3);
		getPreferenceScreen().addPreference(mFingerprint4);
		getPreferenceScreen().addPreference(mFingerprint5);
		getPreferenceScreen().addPreference(mFingerprintNew);
		if(android.os.SystemProperties.get("ro.hct_fingerprint_et300").equals("1")){
		   if(android.os.SystemProperties.get("ro.hct_dolf_quickstart").equals("1")){
			    FingerprintData.setMaxFingerCount(5);
			}else{
		        getPreferenceScreen().removePreference(mFingerprint4);
			    getPreferenceScreen().removePreference(mFingerprint5);
                if(mDolfUnlockQuickstart != null)
                   getPreferenceScreen().removePreference(mDolfUnlockQuickstart);
			    FingerprintData.setMaxFingerCount(3);
                if(mTakePhoto != null)
                   getPreferenceScreen().removePreference(mTakePhoto);
			}
			getPreferenceScreen().removePreference(mUnlockQuickstart);
			getPreferenceScreen().removePreference(mFingerprintMore);
		}else{
			FingerprintData.setMaxFingerCount(mDefaultMaxFinger);
            //egistec start
            if(mFingerCal != null)
               getPreferenceScreen().removePreference(mFingerCal);
            if(mDolfUnlockQuickstart != null)
               getPreferenceScreen().removePreference(mDolfUnlockQuickstart);
            if(mTakePhoto != null)
               getPreferenceScreen().removePreference(mTakePhoto);
            //egistec end
		}
		
		if (FingerprintData.isDataExsitWithIndex(mContext, "fingerprint_1") == FingerprintData.DATA_NOT_EXSIT){
			getPreferenceScreen().removePreference(this.mFingerprint1);
			FingerprintData.setFingerSummary(mContext, FingerprintData.FINGERPRINT_1, "");
			FingerprintData.setFingerQuickApplication(mContext, FingerprintData.FINGERPRINT_1, "");
		}else{
			mFingerprint1.setTitle(FingerprintData.fingerTitle(mContext, FingerprintData.FINGERPRINT_1));
			//egistec start
			if(android.os.SystemProperties.get("ro.hct_fingerprint_et300").equals("1")){
				setPreferenceSummaryAndIcon(mFingerprint1,FingerprintData.FINGERPRINT_1);
			}
			//egistec end
		}
	
		if (FingerprintData.isDataExsitWithIndex(mContext, "fingerprint_2") == FingerprintData.DATA_NOT_EXSIT){
			getPreferenceScreen().removePreference(this.mFingerprint2);
			FingerprintData.setFingerSummary(mContext, FingerprintData.FINGERPRINT_2, "");
			FingerprintData.setFingerQuickApplication(mContext, FingerprintData.FINGERPRINT_2, "");
		}else{
			mFingerprint2.setTitle(FingerprintData.fingerTitle(mContext, FingerprintData.FINGERPRINT_2));
			//egistec start
			if(android.os.SystemProperties.get("ro.hct_fingerprint_et300").equals("1")){
				setPreferenceSummaryAndIcon(mFingerprint2,FingerprintData.FINGERPRINT_2);
			}
			//egistec end
		}
	
		if (FingerprintData.isDataExsitWithIndex(mContext, "fingerprint_3") == FingerprintData.DATA_NOT_EXSIT){
			getPreferenceScreen().removePreference(this.mFingerprint3);
			FingerprintData.setFingerSummary(mContext, FingerprintData.FINGERPRINT_3, "");
			FingerprintData.setFingerQuickApplication(mContext, FingerprintData.FINGERPRINT_3, "");
		}else{
			mFingerprint3.setTitle(FingerprintData.fingerTitle(mContext, FingerprintData.FINGERPRINT_3));
			//egistec start
			if(android.os.SystemProperties.get("ro.hct_fingerprint_et300").equals("1")){
				setPreferenceSummaryAndIcon(mFingerprint3,FingerprintData.FINGERPRINT_3);
			}
			//egistec end
		}
		
		if (FingerprintData.isDataExsitWithIndex(mContext, "fingerprint_4") == FingerprintData.DATA_NOT_EXSIT){
			getPreferenceScreen().removePreference(this.mFingerprint4);
			FingerprintData.setFingerSummary(mContext, FingerprintData.FINGERPRINT_4, "");
			FingerprintData.setFingerQuickApplication(mContext, FingerprintData.FINGERPRINT_4, "");
		}else{
			mFingerprint4.setTitle(FingerprintData.fingerTitle(mContext, FingerprintData.FINGERPRINT_4));
			//egistec start
			if(android.os.SystemProperties.get("ro.hct_fingerprint_et300").equals("1")){
				setPreferenceSummaryAndIcon(mFingerprint4,FingerprintData.FINGERPRINT_4);
			}
			//egistec end
		}
		
		if (FingerprintData.isDataExsitWithIndex(mContext, "fingerprint_5") == FingerprintData.DATA_NOT_EXSIT){
			getPreferenceScreen().removePreference(this.mFingerprint5);
			FingerprintData.setFingerSummary(mContext, FingerprintData.FINGERPRINT_5, "");
			FingerprintData.setFingerQuickApplication(mContext, FingerprintData.FINGERPRINT_5, "");
		}else{
			mFingerprint5.setTitle(FingerprintData.fingerTitle(mContext, FingerprintData.FINGERPRINT_5));
			//egistec start
			if(android.os.SystemProperties.get("ro.hct_fingerprint_et300").equals("1")){
				setPreferenceSummaryAndIcon(mFingerprint5,FingerprintData.FINGERPRINT_5);
			}
			//egistec end
		}
		
		int fingerCount = FingerprintData.fingerCount(mContext);
		if (fingerCount == FingerprintData.getMaxFingerCount()) {
			getPreferenceScreen().removePreference(mFingerprintNew);
		}

		int keyguard  = FingerprintData.isDataExsitWithIndex(mContext, KEY_FINGERPRINT_UNLOCK_KEYGUARD);
		if(keyguard == 1){
			mUnlockKeyguard.setCurrentStatus(true);
		}else{
			mUnlockKeyguard.setCurrentStatus(false);
		}
		
		if(fingerCount == 0){
            mUnlockApplication.setCurrentStatus(false);
		}
		
		int application = FingerprintData.isDataExsitWithIndex(mContext, KEY_FINGERPRINT_UNLOCK_APPLICATION);
		if(application == 1){
			mUnlockApplication.setCurrentStatus(true);
		}else{
			mUnlockApplication.setCurrentStatus(false);
		}
		
		int quickstart = FingerprintData.isDataExsitWithIndex(mContext, KEY_FINGERPRINT_UNLOCK_QUCIKSTART);
		if(quickstart == 1){
			mUnlockQuickstart.setCurrentStatus(true);
		}else{
			mUnlockQuickstart.setCurrentStatus(false);
		}

       //egistec start
        mTakePhoto.setCurrentStatus(Settings.System.getInt(mContext.getContentResolver(), "fingerprint_camera_takephoto", 0) == 1);
        mDolfUnlockQuickstart.setCurrentStatus(Settings.System.getInt(mContext.getContentResolver(), "fingerprint_dolf_unlock_quickstart", 0) == 1);
       //egistec end 
	}	
  

	
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference){
		if(paramPreference instanceof FingerprintSwitchPreference){
			if(paramPreference == mUnlockApplication){
		        final Intent intent = new Intent();
		        intent.setClassName("com.android.settings", "com.android.settings.fingerprint.LockAppMainActivity");
				startActivity(intent);
			}else if(paramPreference == mUnlockQuickstart){
				if(FingerprintData.fingerCount(mContext) == 0){
					onCreateDialogAlert(KEY_FINGERPRINT_UNLOCK_QUCIKSTART, getString(R.string.fingerprint_string_title), getString(R.string.fingerprint_bind_start_enroll_quickstart));
				}else{
			        final Intent intent = new Intent();
			        intent.setClassName("com.android.settings", "com.android.settings.fingerprint.QuickStartActivity");
					startActivity(intent);
				}
			}
		}else if (paramPreference == mFingerprintNew) {
			callEnrollActivity(FingerprintData.ENROLL, -1);
		}else if(paramPreference == mFingerprint1){
			callEnrollActivity(FingerprintData.EDITOR, FingerprintData.FINGERPRINT_1);
		}else if(paramPreference == mFingerprint2){
			callEnrollActivity(FingerprintData.EDITOR, FingerprintData.FINGERPRINT_2);
		}else if(paramPreference == mFingerprint3){
			callEnrollActivity(FingerprintData.EDITOR, FingerprintData.FINGERPRINT_3);
		}else if(paramPreference == mFingerprint4){
			callEnrollActivity(FingerprintData.EDITOR, FingerprintData.FINGERPRINT_4);
		}else if(paramPreference == mFingerprint5){
			callEnrollActivity(FingerprintData.EDITOR, FingerprintData.FINGERPRINT_5);
		}
//egistec start
        else if(paramPreference == mFingerCal){
           Intent i = new Intent();
           i.setClassName("com.android.settings","com.android.settings.fingerprint.FingerPrintCalibrationActivity");
           startActivity(i);
        }
//egistec end
        else if(paramPreference == mFingerprintMore){
	        final Intent intent = new Intent();
	        intent.setClassName("com.android.settings", "com.android.settings.fingerprint.FingerprintMoreSettings");
			startActivity(intent);
        }
		return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);	
	}
  
	@Override
	public void onSwitchChange(String type, boolean state){
		if(KEY_FINGERPRINT_UNLOCK_KEYGUARD.equals(type)){
			if(state){
				if(FingerprintData.fingerCount(mContext) == 0){
					onCreateDialogAlert(type, getString(R.string.fingerprint_string_title), getString(R.string.fingerprint_bind_start_enroll_keyguard));
				}else{
					FingerprintData.saveToDatabase(mContext, type, 1);
				}
			}else{
				FingerprintData.saveToDatabase(mContext, type, 0);
			}
		}else if(KEY_FINGERPRINT_UNLOCK_APPLICATION.equals(type)){
			if(state){
				if(FingerprintData.fingerCount(mContext) == 0){
					onCreateDialogAlert(type, getString(R.string.fingerprint_string_title), getString(R.string.fingerprint_bind_start_enroll_applock));
				}else{
					FingerprintData.saveToDatabase(mContext, type, 1);
				}
			}else{
				FingerprintData.saveToDatabase(mContext, type, 0);
			}
		}else if(KEY_FINGERPRINT_UNLOCK_QUCIKSTART.equals(type)){
			if(state){
				if(FingerprintData.fingerCount(mContext) == 0){
					onCreateDialogAlert(type, getString(R.string.fingerprint_string_title), getString(R.string.fingerprint_bind_start_enroll_quickstart));
				}else{
					FingerprintData.saveToDatabase(mContext, type, 1);
				}
			}else{
				FingerprintData.saveToDatabase(mContext, type, 0);
			}
		}else if(KEY_FINGERPRINT_TAKE_PHOTO.equals(type)){//egistec start
           if(state)
             Settings.System.putInt(mContext.getContentResolver(), "fingerprint_camera_takephoto", 1);
           else
             Settings.System.putInt(mContext.getContentResolver(), "fingerprint_camera_takephoto", 0);             
        }else if(KEY_FINGERPRINT_DOLF_UNLOCK_QUCIKSTART.equals(type)){
           if(state)
             Settings.System.putInt(mContext.getContentResolver(), "fingerprint_dolf_unlock_quickstart", 1);
           else
             Settings.System.putInt(mContext.getContentResolver(), "fingerprint_dolf_unlock_quickstart", 0);            
        }
         //egistec end
	}
	
	private void onCreateDialogAlert(final String key, String title, String context){
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setCancelable(true);
        b.setTitle(title);
        b.setMessage(context);
        b.setPositiveButton(R.string.fingerprint_start_enroll_now, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//add for egistec start
				if(android.os.SystemProperties.get("ro.hct_fingerprint_et300").equals("1") 
						&& android.os.SystemProperties.get("ro.hct_dolf_quickstart").equals("1")){
					if(KEY_FINGERPRINT_UNLOCK_KEYGUARD.equals(key)
						|| KEY_FINGERPRINT_UNLOCK_APPLICATION.equals(key)){
						mIsFromSwitch = true;
					}
				}
				//add for egistec start end
				callEnrollActivity(FingerprintData.ENROLL, -1);
			}
		});
        b.setNegativeButton(R.string.fingerprint_start_enroll_then, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(KEY_FINGERPRINT_UNLOCK_KEYGUARD.equals(key)){
					mUnlockKeyguard.setCurrentStatus(false);
				}else if(KEY_FINGERPRINT_UNLOCK_APPLICATION.equals(key)){
					mUnlockApplication.setCurrentStatus(false);
				}else if(KEY_FINGERPRINT_UNLOCK_QUCIKSTART.equals(key)){
					mUnlockQuickstart.setCurrentStatus(false);
				}
			}
		});
		AlertDialog dialog = b.create();
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
            	if(KEY_FINGERPRINT_UNLOCK_KEYGUARD.equals(key)){
					mUnlockKeyguard.setCurrentStatus(false);
				}else if(KEY_FINGERPRINT_UNLOCK_APPLICATION.equals(key)){
					mUnlockApplication.setCurrentStatus(false);
				}else if(KEY_FINGERPRINT_UNLOCK_QUCIKSTART.equals(key)){
					mUnlockQuickstart.setCurrentStatus(false);
				}
            }
        });
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}
	
	private void callEnrollActivity(int state, int index){
		Intent intent = new Intent();
		//add for egistec start
		if(android.os.SystemProperties.get("ro.hct_fingerprint_et300").equals("1")
		   && android.os.SystemProperties.get("ro.hct_dolf_quickstart").equals("1")){
		    if(state == FingerprintData.EDITOR){
		    	intent.setClass(getActivity(), FingerprintEnrollActivity.class);
		    }else{
			    intent.setClass(getActivity(), FingerSelectActivity.class);
		    }
		    if(mIsFromSwitch){
		    	intent.putExtra("fromswitch", true);
		    	mIsFromSwitch = false;
		    }
		}else{
	    //add for egistec start
		   intent.setClass(getActivity(), FingerprintEnrollActivity.class);
		}
		intent.putExtra("state", state);
		intent.putExtra("index", index);
		startActivity(intent);
    }


	//egistec start 
	private static final String DEV_PATH = "DEVPATH=/devices/virtual/et300/esfp0";
	private static final int BINARY_FINISH = 0;
	private static final int CALCULATE_START = 1;
	private static final int TIME_DELAY_CHECKINT = 60;
	private static final int TIME_WAIT_UEVENT_DONE = 15;
	private static int FINISH_DELAY = 300;
	private static final int INTERRUPT_TRIGGERED_COUNT = 3;
	private final int dc_upper_bound = 64;
	private final int dc_lower_bound = 53;
	private final int dc_count = (dc_upper_bound - dc_lower_bound) + 1;

	int high = 0x3F;
	int low = 0;
	int mid = (high + low) / 2;

	private int mThreshold = 0x7E;
	private int mIntCount = 0;

	private boolean mFound = false;
	private boolean initial = true;
	private static boolean isConnected = false;
	private boolean isInterrupt = false;

	int mDCOffset = dc_upper_bound;
	int mIndex = 0;
	int thresholdTable[] = new int[dc_count];
	int dcoffsetTable[] = new int[dc_count];

	private HandlerThread mNonUIThread;
	private Looper mHandlerLooper;
	private NonUIHandler mNonUIHandler;

	public final class NonUIHandler extends Handler {

		public static final int NON_UI_MSG_START_DTVRT = 1;
		public static final int NON_UI_MSG_CALIBRATION_DTVRT = 2;
		public static final int NON_UI_MSG_FINISH_DTVRT = 3;
		public static final int NON_UI_MSG_RCV_UEVENT = 4;
		public static final int NON_UI_MSG_CALIBRATION_FINISH = 5;

		NonUIHandler(Looper looper) {
			super(looper);
		}

		public void handleMessage(Message message) {

			switch (message.what) {
			case NonUIHandler.NON_UI_MSG_START_DTVRT: {
				Log.d("Vhaocheng", "NON_UI_MSG_START_DVRT");
				startDTVRT();
			}
				break;
			case NonUIHandler.NON_UI_MSG_CALIBRATION_DTVRT: {
				Log.d("Vhaocheng", "NON_UI_MSG_SCALIBRATION_DTVRT");
				calibrationDTVRT();
			}
				break;
			case NonUIHandler.NON_UI_MSG_FINISH_DTVRT: {
				Log.d("Vhaocheng", "NON_UI_MSG_FINISH_DTVRT");
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						finishDTVRT();
					}
				});
			}
				break;
			case NonUIHandler.NON_UI_MSG_CALIBRATION_FINISH: {
				Log.d("Vhaocheng", "NON_UI_MSG_CALIBRATION_FINISH");
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						calculateDTVRT(BINARY_FINISH);
					}
				});
			}
				break;

			default:
				throw new IllegalStateException("unhandled message: "
						+ message.what);
			}
		}
	}

	int startDTVRT() {
		mFound = false;
		high = 0x3F;
		low = 0;
		mid = (high + low) / 2;

		mIntCount = 0;

		mFPLib.setIntThreshold(mid << 1);
		mIntCount = 0;
		Log.d("Vhaocheng", "set threshold = " + mid + " " + (mid << 1));

		isInterrupt = true;

		try {
			Thread.sleep(TIME_DELAY_CHECKINT);
		} catch (InterruptedException e) {
			e.printStackTrace(); 
		}
		
		calibrationDTVRT();
		
		return 0;
	}

	void calibrationDTVRT() {
		Log.d("Vhaocheng", "1111calculateDTVRT mFPLib = " + mFPLib);
		mFPLib.setIntThreshold(0x3F << 1);

		isInterrupt = false;
		Message mesg = new Message();

		if (mIntCount < INTERRUPT_TRIGGERED_COUNT) {
			high = mid;
			mid = (high + low) / 2;
		} else {
			mIntCount = 0;
			low = mid;
			mid = (high + low) / 2;
			mFound = true;
		}

		Log.d("Vhaocheng", "high = " + high + " low = " + low);

		if (high - low <= 1) {
			if (mFound) {
				mThreshold = high << 1;
				mIntCount = 0;
			} else {
				mThreshold = 0x3F << 1;
				mFPLib.saveIntThreshold(mThreshold);
			}
			mesg.what = NonUIHandler.NON_UI_MSG_FINISH_DTVRT;
			mNonUIHandler.sendMessageDelayed(mesg, FINISH_DELAY);
			return;
		}

		mFPLib.setIntThreshold(mid << 1);
		try {
			Thread.sleep(TIME_WAIT_UEVENT_DONE);
		} catch (InterruptedException e) {
			e.printStackTrace(); 
		}
		
		mIntCount = 0;
		isInterrupt = true;
		
		try {
			Thread.sleep(TIME_DELAY_CHECKINT);
		} catch (InterruptedException e) {
			e.printStackTrace(); 
		}
		
		calibrationDTVRT();
	}

	int finishDTVRT() {
		isInterrupt = false;
		
		if (mFound)
		{
			Log.d("Vhaocheng", "finishDTVRT() : RESULT TH= " + mThreshold + " DC= " + mDCOffset);
		} else
		{
			Log.d("Vhaocheng", "finishDTVRT() : NOT FOUND set threshold as " + mThreshold);
		}
		
		mIntCount = 0;
	
		calculateDTVRT(BINARY_FINISH);
		return 0;
	}

	void calculateDTVRT(int isStart) {
		Log.d("Vhaocheng", "calculateDTVRT(int isStart) start  isStart = "
				+ isStart);
		if (isStart != CALCULATE_START) {
			if (!mFound) {
				mThreshold = 0x3F << 1;
				mDCOffset = 127;
				mFPLib.setIntThreshold(mThreshold);
				mFPLib.saveIntThreshold(mThreshold);
				mFPLib.setIntDCOffset(mDCOffset << 1);
				mFPLib.saveIntDCOffset(mDCOffset << 1);
				mFPLib.finishInterruptCalibration();
				mFPRet = -2;
				android.os.SystemProperties.set("persist.sys.initegistec", "-2");
				Message msg = new Message();
				msg.what = MSG_PROGRESS_DISMISS;
				mHandler.sendMessage(msg);
				return;
			} else {
				dcoffsetTable[mIndex] = mDCOffset;
				thresholdTable[mIndex] = mThreshold;
			}
			Log.d("Vhaocheng", " dcOffset = " + mDCOffset + " Threshold = "
					+ mThreshold);
			mDCOffset--;
			mIndex++;
		}

		if ((dc_upper_bound - mDCOffset) == (dc_count)) {
			int preTH =0;
			int nowTH = 0;
			int nextTH =0;
			
			for (int i = 0; i < dc_count-1; i++)
			{
				if(i>0){
					preTH = thresholdTable[i-1];
				}else{ 
					preTH = thresholdTable[i];
				}
				nowTH = thresholdTable[i];
				nextTH = thresholdTable[i+1];
				
				if((preTH == nextTH) && (preTH != nowTH))
				{
					thresholdTable[i] = preTH;
					Log.d("Vhaocheng", "Change TH:"+ nowTH +" to"+ preTH);
				}
			}
			
			int tempTH=0;
			int maxCntTH = 0;
			int tempCnt=0;
			int maxCnt = 0;

			// finding result
			for (int i = 0; i < dc_count-1; i++)
			{
				if(thresholdTable[i] == thresholdTable[i+1])
				{
					tempCnt++;
					tempTH = thresholdTable[i];
					if(i==dc_count-1){
						tempCnt++;
					}
				}
				else{
					tempCnt = 0;
				}
				
				if(tempCnt>maxCnt){
					maxCnt = tempCnt;
					maxCntTH = tempTH;
				}
			}
			
			int baseDC=-1;
			int targetDC=-1; 
			for (int i = 0; i < dc_count; i++)
			{
				if(thresholdTable[i] == maxCntTH)
				{
					targetDC = dcoffsetTable[i];
					baseDC = dcoffsetTable[i];
				}
			}
			mDCOffset = baseDC+1;
			mThreshold = maxCntTH;

			if (baseDC==-1) // failed.
			{
				Log.d(TAG, "dcoffset = " + mDCOffset + " mIndex = " + mIndex);
				Log.d(TAG, "not found interrupt threshold");
				mThreshold = 0x3F << 1;
				mDCOffset = 127;

				mFPLib.setIntThreshold(mThreshold);
				mFPLib.saveIntThreshold(mThreshold);
				mFPLib.setIntDCOffset(mDCOffset << 1);
				mFPLib.saveIntDCOffset(mDCOffset << 1);
				mFPLib.finishInterruptCalibration();
				mFPRet = -1;
				android.os.SystemProperties.set("persist.sys.initegistec", "-1");
				Message msg = new Message();
				msg.what = MSG_PROGRESS_DISMISS;
				mHandler.sendMessage(msg);
				return;
			}

			Log.d("Vhaocheng", "FINAL TH= " + mThreshold + " DC= " + mDCOffset);
			
			mPowerSupplyObserver.stopObserving();
			isInterrupt = false;
			mFPLib.setIntThreshold(mThreshold);
			mFPLib.saveIntThreshold(mThreshold);
			mFPLib.setIntDCOffset(mDCOffset << 1);
			mFPLib.saveIntDCOffset(mDCOffset << 1);
			mFPLib.finishInterruptCalibration();
			mFPRet = 1;
			android.os.SystemProperties.set("persist.sys.initegistec", "1");
			Message msg = new Message();
			msg.what = MSG_PROGRESS_DISMISS;
			mHandler.sendMessage(msg);
			return;
		} else {
			if (!initial) {
				mFPLib.setIntDCOffset(mDCOffset << 1);
				Message mesg = new Message();
				mesg.what = NonUIHandler.NON_UI_MSG_START_DTVRT;
				mNonUIHandler.sendMessageDelayed(mesg, 0);
				return;
			}
			initial = false;
			Log.d("Vhaocheng", "mFPLib.startInterruptCalibration() start");
			int ret = mFPLib.startInterruptCalibration();
			Log.d("Vhaocheng", "mFPLib.startInterruptCalibration() return = "
					+ ret);
            if(ret != 0){
                mFPRet = -3;
			    android.os.SystemProperties.set("persist.sys.initegistec", "-3");
			    Message msg = new Message();
			    msg.what = MSG_PROGRESS_DISMISS;
			    mHandler.sendMessage(msg);
            }

			if (ret == 0) {
				mFPLib.setIntDCOffset(mDCOffset << 1);
				mPowerSupplyObserver.startObserving(DEV_PATH);
				Message mesg = new Message();
				mesg.what = NonUIHandler.NON_UI_MSG_START_DTVRT;
				mNonUIHandler.sendMessageDelayed(mesg, 0);
			}
		}
	}

	void initNonUIThread() {
		mNonUIThread = new HandlerThread("EgisInterruptToolHandlerThread");
		mNonUIThread.start();
		mHandlerLooper = mNonUIThread.getLooper();
		mNonUIHandler = new NonUIHandler(mHandlerLooper);
	}

	private void initLib() {
		mFPLib = new SettingLib(mContext);
		mFPLib.cleanListeners();
		Log.d("Vhaocheng", "initLib mFPLib = " + mFPLib);
        if(mPowerSupplyObserver == null)
		    mPowerSupplyObserver = new UEventObserver() {
			    @Override
			    public void onUEvent(UEventObserver.UEvent event) {
				    if (isInterrupt) {
					    mIntCount++;
				    }
				    Log.d("Vhaocheng", "Interrupt triggered" + mIntCount);
			    }
		    };


		mFPLib.setStatusListener(new StatusListener() {

			@Override
			public void onUserAbort() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStatus(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onServiceDisConnected() {
				// TODO Auto-generated method stub
				isConnected = false;
			}

			@Override
			public void onServiceConnected() {
				// TODO Auto-generated method stub
				isConnected = true;
				Log.d("Vhaocheng",
						"onServiceDisConnected isFingerprintTest = false ");
                mFPLib.removeCalibration(); 
                mFPLib.disconnectDevice();
				interruptInitial();
				// new Thread(mRunnable).start();
			}

			@Override
			public void onFingerImageGetted() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFingerFetch() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onBadImage(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		mFPLib.bind();
	}

	Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			interruptInitial();
		}
	};

	private void interruptInitial() {
		initNonUIThread();
		initial = true;
		mDCOffset = dc_upper_bound;
		mIndex = 0;
		mThreshold = 0x3F << 1;
		thresholdTable = new int[dc_count];
		dcoffsetTable = new int[dc_count];

		calculateDTVRT(CALCULATE_START);
	}
	
	private void setPreferenceSummaryAndIcon(Preference preference,int fingerId){
	    if(!android.os.SystemProperties.get("ro.hct_dolf_quickstart").equals("1"))
		    return;
		String fingerSummary = FingerprintData.fingerSummary(mContext, fingerId);
		Log.e("fuck",fingerId + "--------->fingerSummary = " + fingerSummary);
		if(fingerSummary != null){
			String summary;
			if(fingerSummary.equals("")){
				summary = getResources().getString(R.string.ftp_quick_start_setting_app_onlylock_summary);
			}else{
				summary = getResources().getString(R.string.ftp_quick_start_setting_app_summary, fingerSummary);
			}
			preference.setSummary(summary);
		}
		int fingerid = FingerprintData.getFingerprintToFinger(mContext,fingerId);
		if(fingerid != -1){
			int imgId = mFingerId[fingerid];
			preference.setIcon(imgId);
		}
	}
	//egistec end
}
