
package com.android.settings.fingerprint;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import com.android.settings.R;
import android.os.SystemProperties;
//HCT: xuejin dolfa fingerprint et300
import egistec.fingerauth.api.SettingLib;
import egistec.fingerauth.api.FpResDef;
import egistec.fingerauth.api.FPAuthListeners;
//HCT: xuejin dolfa fingerprint et300
//HCT: xuejin newschip fingerprint
import java.util.concurrent.Semaphore;
//HCT: xuejin newschip fingerprint
//HCT: xuejin hct fingerprint
import android.service.fingerprint.HctFingerManager;
import android.service.fingerprint.IHctFingerCallback;
//HCT: xuejin hct fingerprint
public class AppLockUnLockActivity extends Activity implements OnClickListener, FPAuthListeners.VerifyListener, FPAuthListeners.StatusListener,
		FingerprintApiWrapper.EventListener{
    private static final String TAG = "FingerprintHandlerAppLock";

    private static final char[] ACCEPTED_CHARS = "1234567890".toCharArray();

    private EditText mPasswordEntry;

    private int mLockAppID = -1;

    private Button mSure;

    private ImageButton mOne;

    private ImageButton mTwo;

    private ImageButton mThree;

    private ImageButton mFour;

    private ImageButton mFive;

    private ImageButton mSix;

    private ImageButton mSeven;

    private ImageButton mEight;

    private ImageButton mNine;

    private ImageButton mZero;

    private Button mClearAll;

    private ImageButton mDel;

    private boolean mIsUnlockApp = false;
    private Vibrator mVibrator = null;
	private Context mContext;
    //HCT: xuejin dolfa fingerprint et300
    private static final String TAGFINGER = "ApplicationFingerprint";
	private final int MSG_EMPTY = 0;
	private final int MSG_START = 100;
    private final int MSG_VERIFY = 101;
    private final int MSG_DONE = 102;
    private final int MSG_RESET = 103;
    private final int MSG_FAIL = 104;
	private final int MSG_PWD_VERIFY = 105;
	private final int MSG_INIT_LOCKPIC = 106;
	private final int MSG_SOMTHING_ON_SENSOR = 107;
	private final int MSG_CONNECTED_SERVICE = 108;
	private final int MSG_KEYGUARD_DONE = 109;
    private final int BACKUP_LOCK_TIMEOUT = 20000;
    private final int ENROLL_FAIL_TIMEOUT = 5000;
    private final int DELAY_TIME = 100;
    private final int RETRY_TIMES = 5;
    private SettingLib mLib;
    private boolean mIsConnectService = false;
	private Timer mTimer;
	private int mTimerMessageID;
	private static final int WAKE_LOCK_TIMEOUT = 15000;
	private boolean mFingerprintEnable = false;
    private boolean mIsConnectingService = false;
    private int mFailCount = 0;
    //HCT: xuejin dolfa fingerprint et300
	//HCT: xuejin slw fingerprint gls6162
	private boolean mFingerprintSlwEnable = false;
	//HCT: xuejin slw fingerprint gls6162
	//HCT: xuejin newschip fingerprint
    private Handler mDelayHandler = new Handler();
    private Semaphore mLock = new Semaphore(1);
    private static Thread mThread, mCancelThread;
    private static FingerprintApiWrapper mFingerprintNC = null;
    private static final String USER_ID = "system";
    private final int MSG_UNLOCK_NC = 1112;
    private final int MSG_CANCEL_NC = 1113;
    private final int MSG_REPORT_FAILED_ATTEMPT_NC = 1114;
    private final int MSG_POKE_WAKELOCK_NC = 1115;
    private final int MSG_WAIT_NC = 1116;
    private final int MSG_API_ERROR_NC = 1117;
    private volatile boolean mIsRunning = false;
    private static boolean mVerifyOnBootup = true;
    private final int BACKUP_LOCK_TIMEOUT_NC = 30000;
    private boolean mNCFingerprintEnable = true;
    private int mNCFailCount = 0;
    private static final int RETRY_TIMES_NC = 5;
    //HCT: xuejin newschip fingerprint
	//HCT: xuejin hct fingerprint
	private boolean mHctFingerprintEnable = false;
	//HCT: xuejin hct fingerprint
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applock_app_unlock);

        mPasswordEntry = (EditText) findViewById(R.id.lockapp_password_entry);
        mPasswordEntry.addTextChangedListener(mTextWatcher);
        mPasswordEntry.setKeyListener(new NumberKeyListener() {

            @Override
            public int getInputType() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            protected char[] getAcceptedChars() {
                // TODO Auto-generated method stub
                return ACCEPTED_CHARS;
            }
        });

        mLockAppID = getIntent().getIntExtra("app_lock_id", -1);
        mIsUnlockApp = getIntent().getBooleanExtra("app_lock_unlock", false);
        setupKeyBordView();
        mContext = this;
        //HCT: xuejin dolfa fingerprint et300
        mFingerprintEnable = ("1".equals(SystemProperties.get("ro.hct_fingerprint_et300", "0"))) ? true : false;
        //HCT: xuejin dolfa fingerprint et300
        //HCT: xuejin slw fingerprint gls6162
        mFingerprintSlwEnable = ("1".equals(SystemProperties.get("ro.hct_fingerprint_gsl6162", "0"))) ? true : false;
        if(mFingerprintSlwEnable){
            String action = "com.silead.fp.lockscreen.fpservice.ACTION";
            Log.d(TAG, "before start FpService.");
            Intent intent = new Intent(action);
            intent.setPackage("com.silead.fp.lockscreen");
            mContext.startService(intent);
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.silead.fp.applock.action.MATCH");
            filter.addAction("com.silead.fp.applock.action.UNMATCH");
            registerReceiver(screenReceiver, filter);
        }
        //HCT: xuejin slw fingerprint gls6162
        //HCT: xuejin newschip fingerprint
        mNCFingerprintEnable = ("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_newschip", "0"))) ? true : false;
        //HCT: xuejin newschip fingerprint
		//HCT: xuejin hct fingerprint
		mHctFingerprintEnable = ("1".equals(SystemProperties.get("ro.hct_fingerprint", "0"))) ? true : false;
		//HCT: xuejin hct fingerprint
    }
    TextWatcher mTextWatcher = new TextWatcher() {
    	private CharSequence temp;
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			temp = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			Log.d(TAG, "mTextWatcher -- temp = " + temp + ", length = " + temp.length());
			if(temp.length() >= 4){
				String pws = mPasswordEntry.getText().toString();
                if (TextUtils.isEmpty(pws)) {
                    return;
                }
                String savePsw = android.os.SystemProperties.get("persist.sys.lock_app_psw");
                String hashPws = AppLockUntil.passwordsToHash(pws);
                if (hashPws.equals(savePsw)) {
                    if (mIsUnlockApp) {
                        Intent i = new Intent();
                        i.setClassName("com.android.settings",
                                "com.android.settings.fingerprint.LockAppMainActivity");
                        startActivity(i);
                    } else {
                        if (mLockAppID != -1)
                            updateData();
                    }
                    finish();
                } else {
                    mPasswordEntry.setText("");
                    mPasswordEntry.setHint(R.string.applock_lockapp_psw_wrong);
                }
			}
		}
    	
    };
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        //HCT: xuejin dolfa fingerprint et300
        if(mFingerprintEnable && !mIsConnectingService && !mIsConnectService){
            mIsConnectingService = true;
        	mFingerprintHandler.sendEmptyMessage(MSG_START);
        }
        //HCT: xuejin dolfa fingerprint et300
        //HCT: xuejin slw fingerprint gls6162
        if(mFingerprintSlwEnable){
            Intent intent = new Intent();
            intent.setAction("com.silead.fp.lockscreen.service.ACTION");
            intent.putExtra("action", "start");
            sendBroadcast(intent);
        }
        //HCT: xuejin slw fingerprint gls6162
        //HCT: xuejin newschip fingerprint
        if(mNCFingerprintEnable){
        	mNCFailCount = 0;
        	startNC();
        }
        //HCT: xuejin newschip fingerprint
		//HCT: xuejin hct fingerprint
		if(mHctFingerprintEnable){
			mFailCount = 0;
        	startFingerVerify();
        	startVerifyListening();
		}
		//HCT: xuejin hct fingerprint
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        //HCT: xuejin dolfa fingerprint et300
        if(mFingerprintEnable){
            mFailCount = 0;
        	mFingerprintHandler.sendEmptyMessage(MSG_KEYGUARD_DONE);
        }
        //HCT: xuejin dolfa fingerprint et300
        //HCT: xuejin slw fingerprint gls6162
        if(mFingerprintSlwEnable){
            Intent intent = new Intent();
            intent.setAction("com.silead.fp.lockscreen.service.ACTION");
            intent.putExtra("action", "stop");
            sendBroadcast(intent);
        }
        //HCT: xuejin slw fingerprint gls6162
        //HCT: xuejin newschip fingerprint
        if(mNCFingerprintEnable){
        	mNCFailCount = 0;
        	stopNC();
        }
        //HCT: xuejin newschip fingerprint
		//HCT: xuejin hct fingerprint
		if(mHctFingerprintEnable){
			mFailCount = 0;
		  	if (isFingerServiceRunning){
				stopfingerverify();
				stopVerifyListening();
			}
		}
		//HCT: xuejin hct fingerprint
    }
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	//HCT: xuejin slw fingerprint gls6162
    	if(mFingerprintSlwEnable){
    		unregisterReceiver(screenReceiver);
    	}
    	//HCT: xuejin slw fingerprint gls6162
        //HCT: xuejin newschip fingerprint
        if(mNCFingerprintEnable){
        	mNCFailCount = 0;
        	cleanUpNC();
        }
        //HCT: xuejin newschip fingerprint
    }
    private void setupKeyBordView() {
        mOne = (ImageButton) findViewById(R.id.keyboard_one);
        mOne.setOnClickListener(this);
        mTwo = (ImageButton) findViewById(R.id.keyboard_two);
        mTwo.setOnClickListener(this);
        mThree = (ImageButton) findViewById(R.id.keyboard_three);
        mThree.setOnClickListener(this);
        mFour = (ImageButton) findViewById(R.id.keyboard_four);
        mFour.setOnClickListener(this);
        mFive = (ImageButton) findViewById(R.id.keyboard_five);
        mFive.setOnClickListener(this);
        mSix = (ImageButton) findViewById(R.id.keyboard_six);
        mSix.setOnClickListener(this);
        mSeven = (ImageButton) findViewById(R.id.keyboard_seven);
        mSeven.setOnClickListener(this);
        mEight = (ImageButton) findViewById(R.id.keyboard_eight);
        mEight.setOnClickListener(this);
        mNine = (ImageButton) findViewById(R.id.keyboard_nine);
        mNine.setOnClickListener(this);
        mZero = (ImageButton) findViewById(R.id.keyboard_zero);
        mZero.setOnClickListener(this);
        mClearAll = (Button) findViewById(R.id.keyboard_clearall);
        mClearAll.setOnClickListener(this);
        mDel = (ImageButton) findViewById(R.id.keyboard_del);
        mDel.setOnClickListener(this);

        mSure = (Button) findViewById(R.id.button_sure);
        mSure.setOnClickListener(this);
        mSure.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.keyboard_del:
                mPasswordEntry.dispatchKeyEvent(new KeyEvent(0, 67));
                if (TextUtils.isEmpty(mPasswordEntry.getText().toString())) {
                    mPasswordEntry.setHint(R.string.applock_enter_psw_16);
                }
                break;

            case R.id.button_sure:
                String pws = mPasswordEntry.getText().toString();
                if (TextUtils.isEmpty(pws)) {
                    return;
                }
                String savePsw = android.os.SystemProperties.get("persist.sys.lock_app_psw");
                String hashPws = AppLockUntil.passwordsToHash(pws);
                if (hashPws.equals(savePsw)) {
                    if (mIsUnlockApp) {
                        Intent i = new Intent();
                        i.setClassName("com.android.settings",
                                "com.android.settings.fingerprint.LockAppMainActivity");
                        startActivity(i);
                    } else {
                        if (mLockAppID != -1)
                            updateData();
                    }
                    finish();
                } else {
                    mPasswordEntry.setText("");
                    mPasswordEntry.setHint(R.string.applock_lockapp_psw_wrong);
                }
                break;
            case R.id.keyboard_clearall:
                mPasswordEntry.setText("");
                mPasswordEntry.setHint(R.string.applock_enter_psw_16);
                break;
            default:
                String str = v.getTag().toString();
                if (mPasswordEntry.getSelectionStart() != mPasswordEntry.getSelectionEnd())
                    mPasswordEntry.dispatchKeyEvent(new KeyEvent(0, 67));
                mPasswordEntry.getText().insert(mPasswordEntry.getSelectionStart(), str);
                break;
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (mIsUnlockApp) {
            return super.onKeyUp(keyCode, event);
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_HOME);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(i);
            finish();
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            finish();
        }
        return super.onKeyUp(keyCode, event);
    }

    private void updateData() {
        ContentResolver contentResolver = getContentResolver();
        Uri content_uri = Uri.parse("content://com.android.applock.authority/lockapp");
        ContentValues values = new ContentValues();
        String updateToWhere = "tid" + "=" + mLockAppID;
        values.put("tid", mLockAppID);
        values.put("packagename", getIntent().getStringExtra("app_lock_pkgname"));
        values.put("lock", 1);
        contentResolver.update(content_uri, values, updateToWhere, null);
    }
	
    private void vibrateShort(){
        if(mVibrator == null){
		    mVibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		}
		if(mVibrator != null){
			mVibrator.vibrate(100);
		}
	}
	private void vibrateStop(){
	    if(mVibrator != null){
	        mVibrator.cancel();
	    }
	}
	//HCT: xuejin dolfa fingerprint et300
	private final Handler mFingerprintHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_START:{
					Log.d(TAGFINGER, "handler msg.what = MSG_START");
					initFPListener(mContext);
					break;
				}
				case MSG_VERIFY: {
					Log.d(TAGFINGER, "handler msg.what = MSG_VERIFY");
					doIdentify();
					break;
				}
				case MSG_FAIL: {
					Log.d(TAGFINGER, "handler msg.what = MSG_FAIL");
                    mFailCount++;
					identifyFailed();
					//doIdentify();
					break;
				}
				case MSG_PWD_VERIFY: {
					Log.d(TAGFINGER, "handler msg.what = MSG_PWD_VERIFY");
					closeIdentify();
					showPassWord();
					break;
				}
				case MSG_RESET: {
					Log.d(TAGFINGER, "handler msg.what = MSG_RESET");
                    mFailCount++;
					identifyFailed();
					//doIdentify();
					break;
				}
				case MSG_DONE: {
					Log.d(TAGFINGER, "tryUnlock msg.what = MSG_DONE");
					tryUnlock();
					closeIdentify();
					break;
				}
				case MSG_KEYGUARD_DONE: {
					Log.d(TAGFINGER, "other Unlock msg.what = MSG_KEYGUARD_DONE");
					closeIdentify();
					break;
				}
				case MSG_INIT_LOCKPIC: {
					Log.d(TAGFINGER, "handler msg.what = MSG_INIT_LOCKPIC");
					break;
				}
				case MSG_SOMTHING_ON_SENSOR: {
					Log.d(TAGFINGER, "handler msg.what = MSG_SOMTHING_ON_SENSOR");
					break;
				}
				case MSG_CONNECTED_SERVICE: {
					Log.d(TAGFINGER, "handler msg.what = MSG_CONNECTED_SERVICE");
					String fplist = null;
                    if(mLib != null)
                        fplist = mLib.getEnrollList("FP_0");
					if (fplist == null || fplist.equals("")) {
						closeIdentify();
						showPassWord();
						return;
					}
					doIdentify();
					break;
				}
			}// switch
		}
	};
	
    private void initFPListener(Context context){
    	mTimer = new Timer();
    	mLib = new SettingLib(context);
		Log.d(TAGFINGER, "initFPListener mLib:" + mLib + " ,mIsConnectService:" + mIsConnectService);
		if(mLib != null){
			mLib.setVerifyListener(this);
			mLib.setStatusListener(this);
			mLib.bind();
		}
    }
    
	private void doIdentify(){
		Log.d(TAGFINGER, "doIdentify");
		for(int count = 0; count < RETRY_TIMES + 1; count++){
			if(mLib != null && mLib.identify()){
		        Log.d(TAGFINGER, "rellay doIdentify");
				return;
			}
			Log.w(TAGFINGER, "Try to start identifying fail, retry " + count);
            if(mLib != null)
			   mLib.abort();
			try {
				synchronized(this) {
					wait(100);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Log.e(TAGFINGER, "Try to start identifying fail");
		mTimerMessageID = MSG_PWD_VERIFY;
		mTimer.schedule(new onPostMessage(), DELAY_TIME, 5000);
	}

	private void closeIdentify(){
		Log.d(TAGFINGER, "closeIdentify mLib = " + mLib + " , mIsConnectService = " + mIsConnectService);
		if(mLib != null && mIsConnectService == true){
			mLib.abort();
			mLib.cleanListeners();
			mLib.disconnectDevice();
			Log.d(TAGFINGER, "closeIdentify => unbind");
			mLib.unbind();
			mLib = null;
		}
		mIsConnectService = false;
        mIsConnectingService = false;
	}
	
	private class onPostMessage extends TimerTask{
		@Override
		public void run() {
			this.cancel();
			if (mTimerMessageID == MSG_EMPTY) {
				Log.e(TAGFINGER, "mTimerMessageID is empty");
				return;
			}
			mFingerprintHandler.obtainMessage(mTimerMessageID, 0, 0).sendToTarget();
			mTimerMessageID = MSG_EMPTY;
		}
    }
	
	private void tryUnlock(){
		if (mIsUnlockApp) {
            Intent i = new Intent();
            i.setClassName("com.android.settings",
                    "com.android.settings.fingerprint.LockAppMainActivity");
            startActivity(i);
        } else {
            if (mLockAppID != -1){
                updateData();
            }
        }
        finish();
	    Log.d(TAGFINGER, "fingerprint Unlock DONE");
	}
	
	private void showPassWord(){
		vibrateShort();
        if(mFailCount > RETRY_TIMES)
           mPasswordEntry.setHint(R.string.fingerprint_error_too_many);
        else
		  mPasswordEntry.setHint(R.string.fingerprint_error);
	}
	
	private void identifyFailed(){
		vibrateShort();
        if(mFailCount > RETRY_TIMES)
           mPasswordEntry.setHint(R.string.fingerprint_error_too_many);
        else
		  mPasswordEntry.setHint(R.string.fingerprint_error);
		try {
			synchronized(this) {
				wait(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        doIdentify();
	}
	
	@Override
	public void onBadImage(int arg0) {
		// TODO Auto-generated method stub
		Log.d(TAGFINGER, "onBadImage arg0 = " + arg0);
		//mTimerMessageID = MSG_RESET;
		//mTimer.schedule(new onPostMessage(), DELAY_TIME, 5000);
	}

	@Override
	public void onFingerFetch() {
		// TODO Auto-generated method stub
		Log.d(TAGFINGER, "onFingerFetch");
	}

	@Override
	public void onFingerImageGetted() {
		// TODO Auto-generated method stub
		Log.d(TAGFINGER, "onFingerImageGetted");
	}

	@Override
	public void onServiceConnected() {
		// TODO Auto-generated method stub
		Log.d(TAGFINGER, "onServiceConnected ");
		mIsConnectService = true;
        mIsConnectingService = false;
		mFingerprintHandler.sendEmptyMessageDelayed(MSG_CONNECTED_SERVICE, 300);
	}

	@Override
	public void onServiceDisConnected() {
		// TODO Auto-generated method stub
		Log.d(TAGFINGER, "onServiceDisConnected");
		mIsConnectService = false;
        mIsConnectingService = false;
	}

	@Override
	public void onStatus(int arg0) {
		// TODO Auto-generated method stub
		Log.d(TAGFINGER, "onStatus arg0=" + arg0);
	}

	@Override
	public void onUserAbort() {
		// TODO Auto-generated method stub
		Log.d(TAGFINGER, "onUserAbort");
	}

	@Override
	public void onFail() {
		// TODO Auto-generated method stub
		Log.d(TAGFINGER, "onFail");
	    //mTimerMessageID = MSG_FAIL;
		//mTimer.schedule(new onPostMessage(), DELAY_TIME, 5000);
        mFingerprintHandler.sendEmptyMessageDelayed(MSG_FAIL,1000);
	}

	@Override
	public void onSuccess() {
		// TODO Auto-generated method stub
		Log.d(TAGFINGER, "onSuccess");
		mTimerMessageID = MSG_DONE;
		mTimer.schedule(new onPostMessage(), DELAY_TIME, 5000);
	}
	//HCT: xuejin dolfa fingerprint et300
	//HCT: xuejin slw fingerprint gls6162
	private BroadcastReceiver screenReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if("com.silead.fp.applock.action.MATCH".equals(action)){
				tryUnlock();
			}else if("com.silead.fp.applock.action.UNMATCH".equals(action)){
				showPassWord();
			}
		}
	};
	//HCT: xuejin slw fingerprint gls6162
	//HCT: xuejin newschip fingerprint
    private final Handler mHandlerNC = new Handler(){
    	public void handleMessage(Message msg) {
    		Log.d(TAG, "mHandlerNC msg.what = " + msg.what);
            switch (msg.what) {
                case MSG_UNLOCK_NC:
                    handleUnlockNC();
                    break;
                case MSG_CANCEL_NC:
                    handleCancelNC();
                    break;
                case MSG_REPORT_FAILED_ATTEMPT_NC:
                    handleReportFailedAttemptNC();
                    break;
                case MSG_POKE_WAKELOCK_NC:
                    handlePokeWakelockNC(msg.arg1);
                    break;
                case FingerprintApiWrapper.VCS_RESULT_SERVICE_NOT_RUNNING:
                    startWithDelayNC();
                    break;
                case FingerprintApiWrapper.VCS_RESULT_FAILED:
                    break;
                case FingerprintApiWrapper.VCS_EVT_SENSOR_READY_FOR_USE:
                    break;
                case FingerprintApiWrapper.VCS_EVT_SENSOR_FINGERPRINT_CAPTURE_START:
                    handlePokeWakelockNC(BACKUP_LOCK_TIMEOUT_NC);
                    break;
                case FingerprintApiWrapper.VCS_EVT_FINGER_SETTLED:
                    break;
                case FingerprintApiWrapper.VCS_EVT_EIV_FINGERPRINT_CAPTURED:
                    break;
                case FingerprintApiWrapper.VCS_EVT_VERIFY_SUCCESS:
                case FingerprintApiWrapper.VCS_EVT_IDENTIFY_SUCCESS:
                    handleUnlockNC();
                    break;
                case FingerprintApiWrapper.VCS_EVT_VERIFY_FAILED:
                case FingerprintApiWrapper.VCS_EVT_IDENTIFY_FAILED:
                    int opResult = FingerprintApiWrapper.VCS_RESULT_FAILED;
					Log.d(TAG, "handler msg.what = MSG_RESET mNCFailCount = " + mNCFailCount);
                    if (msg.obj == null) {
                        Log.w(TAG, "handleMessage()::Additional event data not available");
                        startWithDelayNC();
                        break;
                    }
                    FingerprintApiWrapper.FingerprintEvent fpEvent = (FingerprintApiWrapper.FingerprintEvent) msg.obj;
                    if (fpEvent == null || fpEvent.eventData == null) {
                        Log.w(TAG, "handleMessage()::Invalid FingerprintEvent");
                        startWithDelayNC();
                        break;
                    }
                    opResult = (Integer) fpEvent.eventData;
                    switch(opResult) {
	                    //verify cancelled?
	                    case FingerprintApiWrapper.VCS_RESULT_OPERATION_CANCELED:
	                    	break;
	                    //sensor removed?
	                    case FingerprintApiWrapper.VCS_RESULT_SENSOR_IS_REMOVED:
	                        startWithDelayNC();
	                        break;
	                    //sensor not found?
	                    case FingerprintApiWrapper.VCS_RESULT_SENSOR_NOT_FOUND:
	                        startWithDelayNC();
	                        break;
	                    //verify failed?
	                    default:
	                        Log.e(TAG, "Verification Failed " +  opResult);
	                        mNCFailCount++;
	                        handleReportFailedAttemptNC();
	                        startWithDelayNC();
	                        break;
                    }
                    break;
                default:
                    Log.e(TAG, "Unhandled message");
                    break;
            }
    	}
    };
    public void onEvent(FingerprintApiWrapper.FingerprintEvent eventdata) {
        if (eventdata !=  null) {
            mHandlerNC.sendMessage(Message.obtain(mHandlerNC, eventdata.eventId, eventdata));
        } else {
            Log.e(TAG, "Invalid FingerprintEvent");
        }
    }
    void handleUnlockNC() {
        Log.d(TAG, "handleUnlockNC()");
        tryUnlock();
    }
    void handleCancelNC() {
        Log.d(TAG, "handleCancel()");
        identifyFailed();
        stopNC();
    }
    void handleReportFailedAttemptNC() {
        Log.d(TAG, "handleReportFailedAttemptNC()");
        showPassWord();
    }
    public boolean startNC() {
        Log.d(TAG, "startNC()");
        if (mVerifyOnBootup) {
            Log.d(TAG, "Verify user on bootup...");
            mHandlerNC.sendEmptyMessage(MSG_WAIT_NC);
            startWithDelayNC();
            mVerifyOnBootup = false;
            return true;
        }

        if (mHandlerNC.getLooper() != Looper.myLooper()) {
            Log.e(TAG, "start() called off of the UI thread");
        }
        // create new thread for fingerprint
        mThread = new Thread( new Runnable() {
            public void run() {
            if (mFingerprintNC == null) {
                Log.d(TAG, "Creating Fingerprint instance...");
                mFingerprintNC = new FingerprintApiWrapper(mContext, AppLockUnLockActivity.this);
            }
            Log.d(TAG, "Calling identify()...");
            int result = mFingerprintNC.identify(USER_ID);
            if (result == FingerprintApiWrapper.VCS_RESULT_OK) {
                mHandlerNC.sendEmptyMessage(FingerprintApiWrapper.VCS_EVT_SENSOR_READY_FOR_USE);
                Log.d(TAG, "identify() success!");
            } else if (result == FingerprintApiWrapper.VCS_RESULT_ALREADY_INPROGRESS) { //do nothing
                Log.w(TAG, "identify() call ignored! Operation already in progress!");
            } else if (result == FingerprintApiWrapper.VCS_RESULT_SERVICE_NOT_RUNNING) {
                Log.e(TAG, "Service not running!");
                mHandlerNC.sendEmptyMessage(result);
            } else {
                Log.e(TAG, "identify() failed, result code:" + result);
                mHandlerNC.sendEmptyMessage(FingerprintApiWrapper.VCS_RESULT_FAILED);
            }

            mLock.release();
            Log.d(TAG, "start()::Lock released");

            } // run()
        });

        try {
            mLock.acquire();
            Log.d(TAG, "start()::Lock acquired");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mThread.start();
        return true;
    }
    public boolean stopNC(){
    	Log.d(TAG, "stop()");
        if (mHandlerNC.getLooper() != Looper.myLooper()) {
            Log.e(TAG, "stop() called from non-UI thread");
        }
        mDelayHandler.removeCallbacksAndMessages(null);
        mCancelThread = new Thread( new Runnable() {
            public void run() {
            try {
                //Cancel operation
                if (mFingerprintNC != null) {
                	mFingerprintNC.cancel();
                    Log.i(TAG, "mFingerprintNc.cancel() complete");
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception Caught in Cancel");
                e.printStackTrace();
            }
            mLock.release();
            Log.d(TAG, "stop()::Lock released");

            } // run()
        });

        try {
            mLock.acquire();
            Log.i(TAG, "stop()::Lock acquired");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mCancelThread.start();

        return true;
    }
    private void startWithDelayNC() {
        Log.d(TAG, "startWithDelayNC()");
        mDelayHandler.removeCallbacksAndMessages(null);
        mDelayHandler.postDelayed(new Runnable() {
            public void run() {
                startNC();
             }
        }, 1500);
    }
    void handlePokeWakelockNC(int millis) {

    }
    public void cleanUpNC() {
        Log.d(TAG, "cleanUpNC()");
        mDelayHandler.removeCallbacksAndMessages(null);
        try {
            mLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (mFingerprintNC != null) {
                mFingerprintNC.cleanUp();
                Log.d(TAG, "cleanUpNC() complete");
            }
        }
        mFingerprintNC =  null;
        mLock.release();
    }
    //HCT: xuejin newschip fingerprint
	// HCT: xuejin hct fingerprint
	private HctFingerManager mHctFingerManager = null;
	private boolean isFingerServiceRunning = false;

	private void startFingerVerify() {

		mHctFingerManager = HctFingerManager.getHctFingerManager();
		mHctFingerManager.verify();
		isFingerServiceRunning = true;
	}

	private void stopfingerverify() {
		Log.d(TAGFINGER, "stopfingerverify mHctFingerManager = "
				+ mHctFingerManager);
		if (mHctFingerManager != null) {
			mHctFingerManager.cancelVerify();
			isFingerServiceRunning = false;
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

	private final IHctFingerCallback.Stub mHctFingerCallback = new IHctFingerCallback.Stub() {
		public void handleMessage(int what) throws RemoteException {
			mHctFpHandler.sendEmptyMessage(what);
		}
	};

	Handler mHctFpHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Log.d(TAG, "verify success msg.arg2 " + msg.arg2);
				tryUnlock();
				break;
			case 1:
				Log.d(TAG, "verify fail = %d" + msg);
				showPassWord();
				break;
			case 2:
				Log.d(TAG, "untouch = %d" + msg);
				break;
			default:
				break;
			}
		}
	};
	// HCT: xuejin hct fingerprint
}
