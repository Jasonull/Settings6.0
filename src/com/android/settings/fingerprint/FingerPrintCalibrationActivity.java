
package com.android.settings.fingerprint;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.app.Activity;
import android.content.Intent;
import com.android.settings.R;
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
import android.util.Log;
import android.content.Context;
import com.android.settings.fingerprint.AutoInterrpt.GetTHDCValueListener;
public class FingerPrintCalibrationActivity extends Activity {
	private static final String TAG = "FingerPrintCalibrationActivity";
    private Button mCalibrationBtn;
	private SettingLib mFPLib = null;
	private Context mContext = null;
	private Activity mActivity = null;
	private int mFPRet = 0;
	private ProgressDialog mProgressDialog = null;
	private static final int MSG_PROGRESS_SHOW = 1;
	private static final int MSG_PROGRESS_DISMISS = 2;
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
    //add for dolf et310
    private AutoInterrpt mAutoInterrpt = null;
   //add for dolf et310
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
									.getString(R.string.ftp_please_wait_ext), true,
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
                    mCalibrationBtn.setEnabled(true);
					String toastStr = getResources().getString(
							R.string.ftp_init_fail_ext);
					if (mFPRet == 1) {
						toastStr = mContext.getResources().getString(
								R.string.ftp_init_success);
					    Toast.makeText(mContext, toastStr, Toast.LENGTH_LONG)
							    .show();
                        return;
					}
					Toast.makeText(mContext, toastStr + " " + mFPRet, Toast.LENGTH_LONG)
							.show();
				}
				break;
			}
		}
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finger_print_calibration_activity_main);

        mCalibrationBtn = (Button) findViewById(R.id.calibration);
        mCalibrationBtn.setEnabled(true);
        mCalibrationBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mCalibrationBtn.setEnabled(false);
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
        });

		mContext = this;
		mActivity = this;
    }

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
	    if (mFPLib != null && isConnected) {
            if(android.os.SystemProperties.get("ro.hct_fingerprint_et310").equals("1")){
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
		super.onPause();
	}


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

	private UEventObserver mPowerSupplyObserver = new UEventObserver() {
		@Override
		public void onUEvent(UEventObserver.UEvent event) {
			if (isInterrupt) {
				mIntCount++;
			}
			Log.d("Vhaocheng", "Interrupt triggered" + mIntCount);
		}
	};

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


}
