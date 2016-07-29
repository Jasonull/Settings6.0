package com.android.settings.fingerprint;

import java.lang.ref.WeakReference;
import java.util.concurrent.Semaphore;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.Utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;

import com.android.settings.R;

import com.android.settings.fingerprint.FingerprintApiWrapper;
import com.android.settings.fingerprint.FingerprintApiWrapper.EnrollCaptureStatus;
import com.android.settings.fingerprint.FingerprintApiWrapper.FingerprintBitmap;
import com.android.settings.fingerprint.FingerprintApiWrapper.FingerprintEvent;

import egistec.fingerauth.api.FPAuthListeners;
import egistec.fingerauth.api.FPAuthListeners.EnrollListener;
import egistec.fingerauth.api.FPAuthListeners.EnrollMapProgressListener;
import egistec.fingerauth.api.FPAuthListeners.StatusListener;
import egistec.fingerauth.api.SettingLib;
import android.gxFP.IEnrollCallback;
import android.gxFP.IVerifyCallback;
import android.gxFP.FingerprintManager;
import android.gxFP.FingerprintManager.EnrollSession;
import android.gxFP.FingerprintManager.VerifySession;

//HCT: xuejin hct fingerprint
import android.service.fingerprint.HctFingerManager;
import android.service.fingerprint.IHctFingerCallback;
import android.service.fingerprint.IHctEnrollCallback;
//HCT: xuejin hct fingerprint
public class FingerprintEnrollActivity extends Activity implements FingerprintApiWrapper.EventListener{
	private static final String TAG = "FingerprintSettings";
	private boolean mCanEditor = false;
	private boolean mOkClick = false;
	private TextView mTextViewTitle;
	private TextView mTextViewPrompt;
	private ImageView mImageViewProgress;
	private AnimationDrawable mAnimationDrawable;
	private LinearLayout mButtonLinerLayout;
	private Button mDeleteButton;
	private Button mOkButton;
	private String mEnrollSuccess;
	private String mEnrollFail;
	private WakeLock wakeLock ;
	private int mFingerIndex;
	private String mFingerTitle;
	private Context mContext;
	private SettingLib mLib;
	private static final int RETRY_TIMES = 5;
	private PowerManager powerManager;
    private Vibrator mVibrator;
    private int mEnrollCount = 0;
//add for egistec start
    private boolean isDolfFingerprint = false;
	private boolean isDolfFingerprintQuickStart = false;
	private AlertDialog mAlertDialog;
	private TextView mPrompt_progress;
    private Button mEnrollBack;
	private boolean mIsFromSwitch;
	private int mPromptProgressImg[] = {
		R.drawable.fingerprint_ind1,
		R.drawable.fingerprint_ind2,
		R.drawable.fingerprint_ind3,
		R.drawable.fingerprint_ind4,
		R.drawable.fingerprint_ind5,
		R.drawable.fingerprint_ind6,
		R.drawable.fingerprint_ind7,
		R.drawable.fingerprint_ind8,
		R.drawable.fingerprint_ind9,
		R.drawable.fingerprint_ind10
	};

	private int mEt310ProgressImg[] = {
		R.drawable.fingerprint_0,
		R.drawable.fingerprint_1,
		R.drawable.fingerprint_2,
		R.drawable.fingerprint_3,
		R.drawable.fingerprint_4,
		R.drawable.fingerprint_5,
		R.drawable.fingerprint_6,
		R.drawable.fingerprint_7,
		R.drawable.fingerprint_8,
		R.drawable.fingerprint_9,
		R.drawable.fingerprint_10,
		R.drawable.fingerprint_11,
		R.drawable.fingerprint_12,
		R.drawable.fingerprint_13,
		R.drawable.fingerprint_14,
		R.drawable.fingerprint_15,
	};
//add for egistec end
	private int FingerprintImg[] = {
			R.drawable.fingerprint_1,
			R.drawable.fingerprint_3,
			R.drawable.fingerprint_5,
			R.drawable.fingerprint_7,
			R.drawable.fingerprint_9,
			R.drawable.fingerprint_11,
			R.drawable.fingerprint_12,
			R.drawable.fingerprint_14,
			R.drawable.fingerprint_16,
			R.drawable.fingerprint_done
	};
    //HCT: xuejin newschip fingerprint
	private int mStep = 0;
	private Semaphore mLock = new Semaphore(1);
	private static Thread mThread;
	private static FingerprintApiWrapper mFingerprint = null;
	private static final String USER_ID = "system";
    private static final int MSG_API_ERROR  = 501;
    private static final int MSG_WAIT       = 502;
	private int FingerprintImgNC[] = {
			R.drawable.fingerprint_2,
			R.drawable.fingerprint_4,
			R.drawable.fingerprint_5,
			R.drawable.fingerprint_8,
			R.drawable.fingerprint_10,
			R.drawable.fingerprint_14,
			R.drawable.fingerprint_16,
			R.drawable.fingerprint_done
	};
	//HCT: xuejin newschip fingerprint

	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fingerprint_enroll_activity);
		mContext = this;
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//add for egistec start
       isDolfFingerprint = ("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_et300", "0")));
       isDolfFingerprintQuickStart = (android.os.SystemProperties.get("ro.hct_dolf_quickstart").equals("1"));
       mPrompt_progress = (TextView) findViewById(R.id.fingerprint_prompt_progress);
//add for egistec end

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mCanEditor = bundle.getInt("state") == FingerprintData.EDITOR ? true : false;
		mFingerIndex = bundle.getInt("index");
		
		//add for egistec start
		mIsFromSwitch = bundle.getBoolean("fromswitch", false);
        mEnrollBack = (Button) findViewById(R.id.fingerprint_back_button);
        mEnrollBack.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				    if(mLib != null){
					    mLib.deleteFeature("FP_0" + mFingerIndex);
				    }
                    callFingerSelectActivity();
                    finish();
				}
			});
		mVibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		if(mFingerIndex == -1){
			mFingerIndex = FingerprintData.fingerId(mContext);
		}
		mFingerTitle = FingerprintData.fingerTitle(mContext, mFingerIndex);
		mTextViewTitle = (TextView) findViewById(R.id.fingerprint_title);
		mTextViewPrompt = (TextView) findViewById(R.id.fingerprint_prompt);
		mImageViewProgress = (ImageView) findViewById(R.id.fingerprint_progress);
		mButtonLinerLayout = (LinearLayout) findViewById(R.id.fingerprint_button);
		mDeleteButton = (Button) findViewById(R.id.fingerprint_delete_button);
		mOkButton = (Button) findViewById(R.id.fingerprint_ok_button);
		initFinger();
		if (mCanEditor) {
            mEnrollBack.setVisibility(View.GONE);
            mDeleteButton.setVisibility(View.VISIBLE);
            mOkButton.setVisibility(View.VISIBLE);
			mImageViewProgress.setBackgroundResource(R.drawable.fingerprint_done);
			mTextViewTitle.setText(mFingerTitle);
			mTextViewPrompt.setText(R.string.fingerprint_detail_title);
			mDeleteButton.setText(R.string.fingerprint_string_delete);
			mDeleteButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(isDolfFingerprint && isDolfFingerprintQuickStart){
						createDeleteDialog(mFingerIndex);
					}else{
					   deleteFingerprint(mFingerIndex);
					}
				}
			});
            if(isDolfFingerprint && isDolfFingerprintQuickStart){
               if(mIsFromSwitch)
            	   mOkButton.setText(R.string.fingerprint_string_ok);
               else
                   mOkButton.setText(R.string.ftp_quick_start_setting);
            }else{
			   mOkButton.setText(R.string.fingerprint_string_rename);
            }
			mOkButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
                    if(isDolfFingerprint && isDolfFingerprintQuickStart){
                    	if(mIsFromSwitch)
                    		finish();
                    	else
                    	    enterQuckStartAppSele(mFingerIndex,true);
                    }else{
					  showRenameAlertDialog(mContext);
                    }
				}
			});
		} else {
            if(isDolfFingerprint && isDolfFingerprintQuickStart)
               mEnrollBack.setVisibility(View.VISIBLE);
            mDeleteButton.setVisibility(View.GONE);
            mOkButton.setVisibility(View.GONE);
			mImageViewProgress.setBackground(getResources().getDrawable(R.anim.fingerprint_prompt_anim));
			mAnimationDrawable = (AnimationDrawable) mImageViewProgress.getBackground();
			mAnimationDrawable.start();
            if(isDolfFingerprint && isDolfFingerprintQuickStart){
               mDeleteButton.setText(R.string.fingerprint_string_delete);
            }else{
			   mDeleteButton.setText(R.string.fingerprint_string_rename);
            }
			mDeleteButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
                    if(isDolfFingerprint && isDolfFingerprintQuickStart){
                    	createDeleteDialog(mFingerIndex);
                    }else{
					  showRenameAlertDialog(mContext);
                    }
				}
			});
			mOkButton.setVisibility(View.GONE);
            if(isDolfFingerprint && isDolfFingerprintQuickStart){
            	if(mIsFromSwitch)
                    mOkButton.setText(R.string.fingerprint_string_ok);
            	else
                   mOkButton.setText(R.string.ftp_quick_start_setting);
            }else{
			   mOkButton.setText(R.string.fingerprint_string_ok);
            }
			mOkButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mOkClick = true;
                    if(isDolfFingerprint && isDolfFingerprintQuickStart){
                    	if(!mIsFromSwitch)
                            enterQuckStartAppSele(mFingerIndex,false);
                    }else{
					   addFingerprint(mFingerIndex);
                    }
					finish();
				}
			});
		   if("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint", "0"))){
				startFingerVerify();
				startVerifyListening();
			}
		}
		setTitle(mFingerTitle);
		
		if(isDolfFingerprint){
			mLib = new SettingLib(mContext);
		}
	}
  
	//add for egistec start
	private void callFingerSelectActivity(){
		Intent intent = new Intent();
		if(isDolfFingerprint && isDolfFingerprintQuickStart){
			intent.setClass(this, FingerSelectActivity.class);
		    intent.putExtra("fromswitch", mIsFromSwitch);
		    intent.putExtra("state", mCanEditor);
		    intent.putExtra("index", mFingerIndex);
		    startActivity(intent);
		}
    }
	//add for egistec start


	@Override
	public void onResume(){
		super.onResume();	
		acquireWakeLock();
		if("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_newschip", "0"))){
			startEnroll();
		}else if(isDolfFingerprint){
			initFPListener();
		}
	}
	
	@Override
	public void onPause(){		
		super.onPause();
		if(!mOkClick && !mCanEditor){
			if("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_newschip", "0"))){
				if(mFingerprint != null){
					mFingerprint.removeEnrolledFinger(USER_ID, mFingerIndex);
				}
			}else if(isDolfFingerprint && !isDolfFingerprintQuickStart){
				if(mLib != null){
					mLib.deleteFeature("FP_0" + mFingerIndex);
				}
			}else{
				deleteFinger(mFingerIndex);
			}
		}
		releaseWakeLock();
		if("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_newschip", "0"))){
			cancel();
		}else if(isDolfFingerprint){
			if (mLib != null && !mCanEditor){
                closeIdentify();
			}
        }else if("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_goodix", "0"))){
		  	if (isFingerServiceRunning){
				stopfingerverify();
				stopVerifyListening();
			}
			exitRegister();
		}
		if("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint", "0"))){
			stopVerifyListening();
			resumeFingerVerify();
			stopVerifyListening();
		}
        mEnrollCount = 0;
        finish();
	}
	
	@Override
	public void onDestroy (){	
		if("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_newschip", "0"))){
			mFingerprint = null;
		}else if(isDolfFingerprint){
			mLib = null;
		}
		super.onDestroy();
	}
	
    private void enterQuckStartAppSele(int fingerIndex,boolean isEdit){
        Intent i = new Intent();
        i.setClassName(this, "com.android.settings.fingerprint.QuickStartPrefixActivity");
        if(isEdit){
        	i.putExtra("fingerEdit", true);
        }
        i.putExtra("index", fingerIndex);
        startActivity(i);
    }


	private void updateFingerprint(int step) {
		mTextViewTitle.setText(R.string.fingerprint_string_title_2);
		String prompt = getString(R.string.fingerprint_string_prompt_2);
		if(step > 80){
		    prompt = getString(R.string.fingerprint_string_tip);
		}else{
		    prompt = getString(R.string.fingerprint_string_tip1);
		}
		String progress = getString(R.string.fingerprint_string_prompt_3) + step + "%";
		if(isDolfFingerprint && isDolfFingerprintQuickStart){
			mTextViewPrompt.setText(prompt);
		}else{
			mTextViewPrompt.setText(prompt + "\n " + progress);
		}
		if(mAnimationDrawable != null){
			mAnimationDrawable.stop();
		}
        if(mEnrollCount > 9)
           mEnrollCount = 9;
		mImageViewProgress.setBackground(getResources().getDrawable(FingerprintImg[mEnrollCount]));
		if(isDolfFingerprint && isDolfFingerprintQuickStart){
			mPrompt_progress.setVisibility(View.VISIBLE);
			Drawable drawable = getResources().getDrawable(mPromptProgressImg[mEnrollCount]);
			drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
			mPrompt_progress.setCompoundDrawables(null, null, drawable, null);
		}
        mEnrollCount++;
	}
	
	private void updateFingerprintForet310(int step) {
		mTextViewTitle.setText(R.string.fingerprint_string_title_2);
		String prompt = getString(R.string.fingerprint_string_prompt_2);
		if(step > 80){
		    prompt = getString(R.string.fingerprint_string_tip);
		}else{
		    prompt = getString(R.string.fingerprint_string_tip1);
		}
		String progress = getString(R.string.fingerprint_string_prompt_3) + step + "%";
	    mTextViewPrompt.setText(prompt + "\n " + progress);
		if(mAnimationDrawable != null){
			mAnimationDrawable.stop();
		}
		mImageViewProgress.setBackground(getResources().getDrawable(mEt310ProgressImg[mEnrollCount]));
        mEnrollCount++;
	}

	private void doneFingerprint(boolean state) {
        mEnrollBack.setVisibility(View.GONE);
        mDeleteButton.setVisibility(View.VISIBLE);
        mOkButton.setVisibility(View.VISIBLE);
		if (state) {
			mTextViewTitle.setText(R.string.fingerprint_enrolled_title);
			if(isDolfFingerprint && isDolfFingerprintQuickStart){
				
			}else{
				String progress = getString(R.string.fingerprint_string_prompt_3) + "100%";
			    mTextViewPrompt.setText(progress);
			}
			mImageViewProgress.setBackgroundResource(R.drawable.fingerprint_done);
		} else {
			mTextViewTitle.setText(R.string.fingerprint_enrolled_fail_title);
		}
	}
	
	public void showRenameAlertDialog(Context context) {  
        LayoutInflater inflater = LayoutInflater.from(this);  
        final View textEntryView = inflater.inflate(R.layout.fingerprint_rename_dialog, null);  
        final EditText edtInput=(EditText)textEntryView.findViewById(R.id.fingerprint_editinput);  
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);  
        builder.setCancelable(false);  
        builder.setTitle(mFingerTitle);  
        builder.setView(textEntryView);  
        builder.setPositiveButton(getString(R.string.fingerprint_string_ok),  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	String title = edtInput.getText().toString();
                    	Log.d(TAG, "showRenameAlertDialog --- title = " + title);
                    	if(title != null && !title.equals("")){
                    		FingerprintData.setFingerTitle(mContext, mFingerIndex, title);
	    					if(mCanEditor){

	    					}else{
	    					    mOkClick = true;
	    						addFingerprint(mFingerIndex);
	    					}
							finish();
                    	}
                    }  
                });  
        builder.setNegativeButton(getString(R.string.fingerprint_string_cancel),  
                new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int whichButton) {  
                    	if(mCanEditor){
	    					finish();
                    	}
                    }  
                });  
        builder.show();  
    }  
	private void addFingerprint(int index){
		if("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_goodix", "0"))){
			saveRegister(index);
		}
		String key = "";
		switch(index){
			case FingerprintData.FINGERPRINT_1:
				key = FingerPrintSettings.KEY_FINGERPRINT_1;
				break;
			case FingerprintData.FINGERPRINT_2:
				key = FingerPrintSettings.KEY_FINGERPRINT_2;
				break;
			case FingerprintData.FINGERPRINT_3:
				key = FingerPrintSettings.KEY_FINGERPRINT_3;
				break;
			case FingerprintData.FINGERPRINT_4:
				key = FingerPrintSettings.KEY_FINGERPRINT_4;
				break;
			case FingerprintData.FINGERPRINT_5:
				key = FingerPrintSettings.KEY_FINGERPRINT_5;
				break;
			default:
				break;
		}
		FingerprintData.saveToDatabase(mContext, key, 1);
		if (FingerprintData.fingerCount(mContext) >= 1) {
			FingerprintData.saveToDatabase(mContext, "fingerprint_unlock_keyguard", 1);
		}
	}
  
	
	private void createDeleteDialog(final int index){
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.ftp_delete_dialog_title);
		builder.setMessage(R.string.ftp_delete_dialog_msg);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            	deleteFingerprint(index);
                mAlertDialog.dismiss();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                mAlertDialog.dismiss();
            }
        });
        mAlertDialog = builder.create();
        mAlertDialog.show();
	}
	
	
	private void deleteFingerprint(int index){
		String key = "";
		switch(index){
			case FingerprintData.FINGERPRINT_1:
				key = FingerPrintSettings.KEY_FINGERPRINT_1;
				break;
			case FingerprintData.FINGERPRINT_2:
				key = FingerPrintSettings.KEY_FINGERPRINT_2;
				break;
			case FingerprintData.FINGERPRINT_3:
				key = FingerPrintSettings.KEY_FINGERPRINT_3;
				break;
			case FingerprintData.FINGERPRINT_4:
				key = FingerPrintSettings.KEY_FINGERPRINT_4;
				break;
			case FingerprintData.FINGERPRINT_5:
				key = FingerPrintSettings.KEY_FINGERPRINT_5;
				break;
			default:
				break;
		}
		FingerprintData.saveToDatabase(mContext, key, 0);
		FingerprintData.setFingerTitle(mContext, index, "");
		if (FingerprintData.fingerCount(mContext) == 0) {
			FingerprintData.saveToDatabase(mContext, "fingerprint_unlock_keyguard", 0);
			FingerprintData.saveToDatabase(mContext, "fingerprint_unlock_application", 0);
			FingerprintData.saveToDatabase(mContext, "fingerprint_unlock_quickstart", 0);
		}
		if("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_newschip", "0"))){
			if(mFingerprint != null){
				mFingerprint.removeEnrolledFinger(USER_ID, mFingerIndex);
			}
		}else if(isDolfFingerprint){
			if(mLib != null){
				mLib.deleteFeature("FP_0" + index);
			}
			if(isDolfFingerprintQuickStart){
			    FingerprintData.setFingerSummary(this, index, "");
			    String componentName = FingerprintData.getFingerQuickApplication(
					mContext, index);
			    if(componentName == null){
				    componentName = "";
			    }
			    if(componentName.equals(FingerprintData.ITEM_CAMERA)){
				    FingerprintData.saveToDatabase(mContext,
						"ftp_camera_id", 0);
			    }
			    FingerprintData.setFingerQuickApplication(this, index, "");
			    FingerprintData.setFingerprintOnlyUnlock(mContext,mFingerIndex,-1);
			}
        }else if("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_goodix", "0"))){
			deleteRegister(index);
		}else {
			deleteFinger(index);
		}
		finish();
	}

	private void doIdentify(){
		Log.d(TAG, "doIdentify");
		for(int count = 0; count < RETRY_TIMES + 1; count++){
			if(mLib != null && mLib.identify()){
		        Log.d(TAG, "rellay doIdentify");
				return;
			}
			Log.w(TAG, "Try to start identifying fail, retry " + count);
            if(mLib != null)
			   mLib.abort();
               sleep(100);
		}
		Log.e(TAG, "Try to start identifying fail");
	}

	private void closeIdentify(){
		Log.d(TAG, "closeIdentify mLib = " + mLib);
		if(mLib != null){
			mLib.abort();
			mLib.cleanListeners();
			mLib.disconnectDevice();
			Log.d(TAG, "closeIdentify => unbind");
			mLib.unbind();
			mLib = null;
		}
	}

	private void sleep(long time){
       try{
          Thread.sleep(time);          
       }catch(Exception e){
         e.printStackTrace(); 
       }
    }
  
	private void initFPListener(){
		mLib = new SettingLib(this.mContext);
		mLib.cleanListeners();
		mLib.setEnrollListener(new FPAuthListeners.EnrollListener(){
			public void onFail(){
				Log.d(TAG, "onFail");
			}
      
			public void onProgress(){
				Log.d(TAG, "onProgress");
			}
      
			public void onSuccess(){
				Log.d(TAG, "onSuccess");
				vibrateShort();
				doneFingerprint(true);
				if(isDolfFingerprintQuickStart){
                    addFingerprint(mFingerIndex);
				}
			}

            public void onDoubleCheckedFail(){

            }
		});
    
		mLib.setStatusListener(new FPAuthListeners.StatusListener(){
			public void onBadImage(int status){
				Log.d(TAG, "onBadImage arg0" + status);
			}
      
			public void onFingerFetch(){
				Log.d(TAG, "onFingerFetch");
			}
      
			public void onFingerImageGetted(){
				Log.d(TAG, "onFingerImageGetted");
			}
      
			public void onServiceConnected(){
				Log.d(TAG, "onServiceConnected");
                if(!mCanEditor){
				   if(isDolfFingerprintQuickStart){
				       String fplist = mLib.getEnrollList("FP_0");
                       Log.e("fuck","--------->fplist = " + fplist);
				       if(fplist == null || fplist.equals(""))
				          mLib.enroll("FP_0" + mFingerIndex);
                       else
                          doIdentify();
				   }else{
				       mLib.enroll("FP_0" + mFingerIndex);
				   }

                }
			}
      
			public void onServiceDisConnected(){
				Log.d(TAG, "onServiceDisConnected");
			}
      
			public void onStatus(int status){
				Log.d(TAG, "onStatus arg0 = " + status);
			}
      
			public void onUserAbort(){
				Log.d(TAG, "onUserAbort ");
				finish();	
			}
		});
    
        mLib.setVerifyListener(new FPAuthListeners.VerifyListener(){
	        @Override
	        public void onFail() {
		        // TODO Auto-generated method stub
		        Log.d(TAG, "onFail");
                mLib.abort();
                sleep(100);
                mLib.enroll("FP_0" + mFingerIndex);
	        }

	        @Override
	        public void onSuccess() {
		        // TODO Auto-generated method stub
		        Log.d(TAG, "onSuccess");
                Toast.makeText(mContext,R.string.ftp_is_repeat,Toast.LENGTH_SHORT).show();
                sleep(500);
                doIdentify();
	        }
        });

		mLib.setEnrollMapProgressListener(new FPAuthListeners.EnrollMapProgressListener(){
			public void onEnrollMapProgress(int progress){
                if(("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_et310", "0")))){
                  updateFingerprintForet310(progress);
                }else{
				  updateFingerprint(progress);
                }
                Log.d(TAG, "setEnrollMapProgressListener --- progress = " + progress);
                vibrateStop();
                vibrateShort();
			}
		});
		mLib.bind();
	}
 
	private void acquireWakeLock(){
		if (powerManager == null){
			powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
		}
		if (wakeLock != null){
			if (!wakeLock.isHeld()){
				wakeLock.acquire();
			}
		}else{
			wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
			wakeLock.acquire();
		}
    }

	private void releaseWakeLock(){
		if (wakeLock != null){
			if (wakeLock.isHeld()){
				wakeLock.release();
				wakeLock = null;
			}
		}
	}
 
	private void vibrateShort(){
		if (mVibrator != null) {
			mVibrator.vibrate(100);
		}
	}

	private void vibrateStop(){
	    if(mVibrator != null){
	        mVibrator.cancel();
	    }
	}
	//HCT: xuejin newschip fingerprint
	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FingerprintEvent event_data = null;
            String prompt = "";
            switch(msg.what) {
                // during initialization
                case MSG_API_ERROR:
                case FingerprintApiWrapper.VCS_RESULT_FAILED:
                    break;

                case MSG_WAIT:
                case FingerprintApiWrapper.VCS_EVT_SENSOR_READY_FOR_USE:
                    break;

                case FingerprintApiWrapper.VCS_EVT_SENSOR_FINGERPRINT_CAPTURE_START:
                case FingerprintApiWrapper.VCS_EVT_ENROLL_NEXT_CAPTURE_START:
                    break;

                case FingerprintApiWrapper.VCS_EVT_FINGER_SETTLED:
                    break;

                case FingerprintApiWrapper.VCS_EVT_ENROLL_CAPTURE_STATUS:
                    event_data = (FingerprintEvent) msg.obj;
                    if (event_data == null) {
                        Log.e(TAG, "Invalid event data");
                        return;
                    }

                    EnrollCaptureStatus enrollStatus = mFingerprint.getEnrollStatus(event_data.eventData);
                    if (enrollStatus == null) {
                        Log.e(TAG, "Invalid enroll status object");
                        return;
                    }

                    //enrollment progress
                    Log.i(TAG, "Enroll progress:" + enrollStatus.progress);
                    if (enrollStatus.templateResult == 0) { //good swipe
                    	vibrateShort();
                    	updateFingerprintNC(enrollStatus.progress);
                    } else { //bad swipe

                    }

                    break;

                case FingerprintApiWrapper.VCS_EVT_EIV_FINGERPRINT_CAPTURED:
                    break;

                case FingerprintApiWrapper.VCS_EVT_ENROLL_SUCCESS:
                	doneFingerprint(true);
                    return;
                    //break;

                case FingerprintApiWrapper.VCS_EVT_ENROLL_FAILED:
                    int opResult = FingerprintApiWrapper.VCS_RESULT_FAILED;
                    if (msg.obj != null) {
                        FingerprintEvent fpEvent = (FingerprintEvent) msg.obj;
                        if (fpEvent != null && fpEvent.eventData instanceof Integer) {
                            opResult = (Integer) fpEvent.eventData;
                        } else {
                            Log.w(TAG, "handleMessage()::Result flag is not an Integer");
                        }
                    } else {
                        Log.w(TAG, "handleMessage()::Additional event data not available");
                    }
                    switch(opResult) {

                    //verify cancelled?
                    case FingerprintApiWrapper.VCS_RESULT_OPERATION_CANCELED:
                        break;

                    //sensor removed?
                    case FingerprintApiWrapper.VCS_RESULT_SENSOR_IS_REMOVED:
                    	prompt = mContext.getResources().getString(R.string.sensor_removed);
                        break;

                    //sensor not found?
                    case FingerprintApiWrapper.VCS_RESULT_SENSOR_NOT_FOUND:
                    	prompt = mContext.getResources().getString(R.string.sensor_not_found);
                        break;

                    //verify failed?
                    case FingerprintApiWrapper.VCS_RESULT_USER_FINGER_ALREADY_ENROLLED:
                    	prompt = mContext.getResources().getString(R.string.finger_previously_enrolled);
                        break;

                    case FingerprintApiWrapper.VCS_RESULT_TOO_MANY_BAD_SWIPES:
                    	prompt = mContext.getResources().getString(R.string.too_many_bad_swipes);
                        break;

                    case FingerprintApiWrapper.VCS_RESULT_MATCHER_ADD_IMAGE_FAILED:
                    	prompt = mContext.getResources().getString(R.string.mather_add_image_failed);
                        break;

                    case FingerprintApiWrapper.VCS_RESULT_USER_DOESNT_EXIST:
                    default:
                        break;
                    } //switch(eventdata.opResult)
                    break;  //case VCS_EVT_ENROLL_COMPLETED

                default:
                    Log.d(TAG, "handleMessage() -unhandled event: " + msg.what);
                    break;
            }   // switch(msg.what)
            if (!prompt.equals("")) {
            	showFingerprintError(prompt);
            }
        }   // handleMessage
    };  // Handler
    private void startEnroll() {
        // create new thread for fingerprint enrolling
    	mStep = 0;
        mThread = new Thread( new Runnable() {
            public void run() {
	            initValidityLib();
	            //Enroll fingerprint
	            Log.d(TAG, "start enroll user!");
	            //Invoke enroll API
	            int result = mFingerprint.enroll(USER_ID, mFingerIndex, 
	                    FingerprintApiWrapper.VCS_ENROLL_MODE_DEFAULT);
	            if (result != FingerprintApiWrapper.VCS_RESULT_OK) {
	                Log.e(TAG, "enroll() failed, fpIndex=" + mFingerIndex);
	                mHandler.sendMessage(Message.obtain(mHandler, MSG_API_ERROR));
	            } else {
	                Log.d(TAG, "enroll() success, fpIndex=" + mFingerIndex);
	                mHandler.sendMessage(Message.obtain(mHandler, MSG_WAIT));
	            }
	            mLock.release();
            } // run()
        });
        try {
            mLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mThread.start();
    }

    private void cancel() {
        mThread = new Thread( new Runnable() {
            public void run() {
	            if (mFingerprint !=  null) {
	                mFingerprint.cancel();
	                Log.d(TAG,"Cancel complete!");
	            } else {
	                Log.e(TAG, "cancel()::fingerprint object does not exists!");
	            }
	            mLock.release();
            } // run()
        });
        try {
            mLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mThread.start();
    }
    private void initValidityLib() {
        if (mFingerprint == null) {
            //create instance of fingerprint wrapper
            mFingerprint = new FingerprintApiWrapper(this, this);
        }
    }
    /** Callback implementation */
    @Override
    public void onEvent(FingerprintEvent eventdata) {

        if (eventdata ==  null) {
            Log.e(TAG, "onEventsCB()::Invalid event data.");
            return;
        }

        mHandler.sendMessage(Message.obtain(mHandler, eventdata.eventId, eventdata));
    }
    private void updateFingerprintNC(int step) {
		mTextViewTitle.setText(R.string.fingerprint_string_title_2);
		String prompt = getString(R.string.fingerprint_string_prompt_2);
		prompt = getString(R.string.fingerprint_string_tip1);
		String progress = getString(R.string.fingerprint_string_prompt_3) + step + "%";
		mTextViewPrompt.setText(prompt + "\n " + progress);
		if(mAnimationDrawable != null){
			mAnimationDrawable.stop();
		}
		Log.d(TAG, "updateFingerprint step = " + step + ", mStep = " + mStep);
		if(mStep < FingerprintImg.length){
		    mImageViewProgress.setBackground(getResources().getDrawable(FingerprintImgNC[mStep]));
		}
		mStep ++;
	}
    private void showFingerprintError(String prompt){
    	Toast.makeText(mContext, prompt, Toast.LENGTH_LONG).show();
    	finish();
    }
	//HCT: xuejin newschip fingerprint
//HCT: xuejin goodix fingerprint
    private int mPercent = 0;
    private EnrollSession mSession;
    private RegisterHandler mRegisterHandler = new RegisterHandler(this);

    private void updateFingerprintGoodix(int step) {
		mTextViewTitle.setText(R.string.fingerprint_string_title_2);
		String prompt = getString(R.string.fingerprint_string_prompt_2);
		prompt = getString(R.string.fingerprint_string_tip1);
		String progress = getString(R.string.fingerprint_string_prompt_3) + step + "%";
		mTextViewPrompt.setText(prompt + "\n " + progress);
	}
    private void initRegister() {
    	Log.d(TAG, "initRegister mSession = " + mSession);
		if (null == mSession) {
			mSession = FingerprintManager.getFpManager().newEnrollSession(mEnrollCallback);
		}
		mSession.enter();
	}
    private void exitRegister(){
    	Log.d(TAG, "exitRegister mSession = " + mSession);
    	if(null != mSession){
    		mSession.exit();
    	}
    }
    private void saveRegister(int index){
    	Log.d(TAG, "saveRegister mSession = " + mSession + ", index = " + index);
    	if(null != mSession){
    		mSession.save(index);
    	}
    }
    private void deleteRegister(int index){
    	Log.d(TAG, "deleteRegister index = " + index);
    	if(null != FingerprintManager.getFpManager()){
    		FingerprintManager.getFpManager().delete(index);
    	}
    }
	private IEnrollCallback mEnrollCallback = new IEnrollCallback.Stub() {
		@Override
		public void handleMessage(int msg, int arg0, int arg1, byte[] data) throws RemoteException {
			Log.v(TAG, String.format("msg = %d , arg0 = %d ,arg1 = %d", msg, arg0, arg1));
			mRegisterHandler.sendMessage(mRegisterHandler.obtainMessage(msg, arg1, arg0, data));
	//		return false;
		}
	};
	private static class RegisterHandler extends Handler {
		private final WeakReference<FingerprintEnrollActivity> mActivityRef;
		private final static int MAX_ACTION_ERROR = 4;
		private int mBadImageCount = 0;
		private int mNoPieceTime = 0;
		private int mNoMoveTime = 0;


		public RegisterHandler(FingerprintEnrollActivity activity) {
			mActivityRef = new WeakReference<FingerprintEnrollActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			if (mActivityRef.get() == null)
				return;
			final FingerprintEnrollActivity activity = (FingerprintEnrollActivity) mActivityRef.get();
			if (null == activity) {
				return;
			}

			switch (msg.what) {
				case MessageType.MSG_TYPE_COMMON_NOTIFY_INFO :
					Object obj = msg.obj;
					if (obj != null) {
						byte[] loginfo = (byte[]) obj;
						String str = new String(loginfo);
						Log.d(TAG, "MSG_TYPE_COMMON_NOTIFY_INFO ----- str = " + str);
					}
					break;
				case MessageType.MSG_TYPE_REGISTER_DUPLICATE_REG :
					Log.d(TAG, "MSG_TYPE_COMMON_NOTIFY_INFO ----- msg.arg1 = " + msg.arg1);
					break;
				case MessageType.MSG_TYPE_REGISTER_PIECE :
				case MessageType.MSG_TYPE_REGISTER_NO_PIECE :
				case MessageType.MSG_TYPE_REGISTER_NO_EXTRAINFO :
				case MessageType.MSG_TYPE_REGISTER_LOW_COVER :
				case MessageType.MSG_TYPE_REGISTER_GET_DATA_FAILED :
				case MessageType.MSG_TYPE_REGISTER_BAD_IMAGE :
					Log.d(TAG, "RegisterHandler: Result");

					if (msg.what == MessageType.MSG_TYPE_REGISTER_PIECE || msg.what == MessageType.MSG_TYPE_REGISTER_NO_PIECE) {
						Log.d(TAG, "MSG_TYPE_REGISTER_PIECE ----- msg.arg2 = " + msg.arg2);
						activity.mPercent = msg.arg2;
						activity.vibrateShort();
					}
					if (msg.what == MessageType.MSG_TYPE_REGISTER_BAD_IMAGE || msg.what == MessageType.MSG_TYPE_REGISTER_GET_DATA_FAILED) {
						Log.d(TAG, "MSG_TYPE_REGISTER_BAD_IMAGE ----- mBadImageCount = " + mBadImageCount);
						mBadImageCount++;
					} else if (msg.what != MessageType.MSG_TYPE_REGISTER_BAD_IMAGE && msg.what != MessageType.MSG_TYPE_REGISTER_GET_DATA_FAILED && msg.what != MessageType.MSG_TYPE_COMMON_TOUCH
							&& msg.what != MessageType.MSG_TYPE_COMMON_UNTOUCH) {
						Log.d(TAG, "!MSG_TYPE_REGISTER_BAD_IMAGE ----- mBadImageCount = " + mBadImageCount);
						mBadImageCount = 0;
					}

					if (msg.what == MessageType.MSG_TYPE_REGISTER_NO_EXTRAINFO) {
						Log.d(TAG, "MSG_TYPE_REGISTER_NO_EXTRAINFO ----- mNoMoveTime = " + mNoMoveTime);
						mNoMoveTime = 0;
					} else if (msg.what != MessageType.MSG_TYPE_REGISTER_NO_EXTRAINFO && msg.what != MessageType.MSG_TYPE_COMMON_TOUCH && msg.what != MessageType.MSG_TYPE_COMMON_UNTOUCH) {
						Log.d(TAG, "!MSG_TYPE_REGISTER_NO_EXTRAINFO ----- mNoMoveTime = " + mNoMoveTime);
						mNoMoveTime = 0;
					}

					if (msg.what == MessageType.MSG_TYPE_REGISTER_NO_PIECE) {
						mNoPieceTime++;
						Log.d(TAG, "MSG_TYPE_REGISTER_NO_PIECE ----- mNoPieceTime = " + mNoPieceTime);
						if (mNoPieceTime >= MAX_ACTION_ERROR) {
							mNoPieceTime = 0;
						}
					} else if (msg.what != MessageType.MSG_TYPE_REGISTER_NO_PIECE && msg.what != MessageType.MSG_TYPE_COMMON_TOUCH && msg.what != MessageType.MSG_TYPE_COMMON_UNTOUCH) {
						Log.d(TAG, "!MSG_TYPE_REGISTER_NO_PIECE ----- mNoPieceTime = " + mNoPieceTime);
						mNoPieceTime = 0;
					}

					int index = msg.arg1;
					if(activity.mPercent > 0){
						activity.updateFingerprintGoodix(activity.mPercent);
					}

					Log.v(TAG, "RegisterHandler: mPercent" + activity.mPercent);
					if(activity.mPercent >= 100){
						activity.doneFingerprint(true);
					}
					break;
				case MessageType.MSG_TYPE_COMMON_TOUCH :
					Log.v(TAG, "RegisterHandler:MSG_TYPE_COMMON_TOUCH");
					break;
				case MessageType.MSG_TYPE_COMMON_UNTOUCH :
					Log.v(TAG, "RegisterHandler:MSG_TYPE_COMMON_UNTOUCH");
					break;

				default :
					break;
			}
		}
	}
    //HCT: xuejin goodix fingerprint
	//HCT: xuejin hct fingerprint
	private HctFingerManager mHctFingerManager = null;
	private boolean isFingerServiceRunning = false;
	private final int FINGER_ENROLL = 101;
	private void initFinger(){
		if(mHctFingerManager == null){
			mHctFingerManager = HctFingerManager.getHctFingerManager();
		}
	}
	
	private void resumeFingerVerify(){
		if(mHctFingerManager != null){
			mHctFingerManager.verify();
			isFingerServiceRunning = true;
		}
	}
	
	private void startFingerVerify() {
		if(FingerprintData.fingerCount(mContext) == 0){
			enrollFingerprint();
		}else{
			if(mHctFingerManager != null){
				mHctFingerManager.verify();
				isFingerServiceRunning = true;
			}
		}
	}

	private void stopfingerverify() {
		if (mHctFingerManager != null) {
			mHctFingerManager.cancelVerify();
			isFingerServiceRunning = false;
		}
	}
	
	private void stopVerify() {
		Log.d(TAG, "stopVerify");
		if (mHctFingerManager != null) {
			mHctFingerManager.stopVerify();
		}
	}
	
	private void startVerifyListening() {
		if (mHctFingerManager != null) {
			mHctFingerManager.startListener(mHctFingerCallback);
		}
	}

	private void stopVerifyListening() {
		if (mHctFingerManager != null) {
			mHctFingerManager.stopListener();
		}
	}
	
	private void deleteFinger(int index){
		Log.d(TAG, "deleteFinger");
		if (mHctFingerManager != null) {
			mHctFingerManager.delete(index);
		}
	}
	
	private void cancelEnroll(){
		Log.d(TAG, "cancelEnroll");
		if(mHctFingerManager != null){
			mHctFingerManager.cancelEnroll();
		}
	}
	
	private final IHctFingerCallback.Stub mHctFingerCallback = new IHctFingerCallback.Stub() {
		public void handleMessage(int what) throws RemoteException {
			mHctFpHandler.sendEmptyMessage(what);
		}
	};

	Handler mHctFpHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Log.d(TAG, "verify success = %d" + msg);
				Toast.makeText(mContext,R.string.finger_previously_enrolled,Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Log.d(TAG, "verify fail = %d" + msg);
				if (isFingerServiceRunning) {
					stopfingerverify();
					stopVerifyListening();
				}
				enrollFingerprint();
				break;
			case 2:
				Log.d(TAG, "untouch = %d" + msg);
				break;
			case FINGER_ENROLL:
				int index = msg.getData().getInt("index");
				int score = msg.getData().getInt("score");
				if(score >= 100){
					vibrateShort();
					doneFingerprint(true);
				}else{
					vibrateShort();
					updateHctFingerprint(score);
				}
				break;
			default:
				break;
			}
		}
	};
	private void enrollFingerprint(){
		if("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_goodix", "0"))){
			initRegister();
		}
		if(mHctFingerManager != null){
			mHctFingerManager.enroll(mFingerIndex, mHctEnrollCallback);
		}
	}
	
	private final IHctEnrollCallback.Stub mHctEnrollCallback = new IHctEnrollCallback.Stub() {
		public void handleMessage(int index, int what) throws RemoteException {
			Message msg = new Message();
            Bundle data = new Bundle();
            msg.what = FINGER_ENROLL;
            data.putInt("index", index);
            data.putInt("score", what);
            msg.setData(data);
            mHctFpHandler.sendMessage(msg);
		}
	};
	
    private void updateHctFingerprint(int step) {
		mTextViewTitle.setText(R.string.fingerprint_string_title_2);
		String prompt = getString(R.string.fingerprint_string_prompt_2);
		prompt = getString(R.string.fingerprint_string_tip1);
		String progress = getString(R.string.fingerprint_string_prompt_3) + step + "%";
		mTextViewPrompt.setText(prompt + "\n " + progress);
	}
	// HCT: xuejin hct fingerprint
}

