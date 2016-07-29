package com.android.settings.fingerprint;

import java.util.ArrayList;

import com.android.settings.R;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class FingerSelectActivity extends Activity implements OnClickListener {

	private ImageView left_little;
	private ImageView left_ring;
	private ImageView left_middle;
	private ImageView left_fore;
	private ImageView left_thumb;

	private ImageView right_little;
	private ImageView right_ring;
	private ImageView right_middle;
	private ImageView right_fore;
	private ImageView right_thumb;

	private Button mCancle;
	private Button mContinue;

	private int mSelectFingerID = -1;
	private boolean mIsFromSwitch = false;
	private int mFingerId = -1;
	private static final int FINGERNUM = 10;
	private int mImgId[] = { R.id.ftp_left_little, R.id.ftp_left_ring,
			R.id.ftp_left_middle, R.id.ftp_left_fore, R.id.ftp_left_thumb,
			R.id.ftp_right_thumb, R.id.ftp_right_fore, R.id.ftp_right_middle,
			R.id.ftp_right_ring, R.id.ftp_right_little };
	private String mFingerName[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ftp_fingerselect_activity_layout);
		Intent i = getIntent();
		mFingerId = i.getIntExtra("index", -1);
		if (mFingerId == -1) {
			mFingerId = FingerprintData.fingerId(this);
		}
		mIsFromSwitch = i.getBooleanExtra("fromswitch", false);
		mFingerName = getResources().getStringArray(R.array.finger_total_name);
		initView();
		initViewState();
	}

	private void initView() {
		left_little = (ImageView) findViewById(R.id.ftp_left_little);
		left_little.setOnClickListener(this);
		left_ring = (ImageView) findViewById(R.id.ftp_left_ring);
		left_ring.setOnClickListener(this);
		left_middle = (ImageView) findViewById(R.id.ftp_left_middle);
		left_middle.setOnClickListener(this);
		left_fore = (ImageView) findViewById(R.id.ftp_left_fore);
		left_fore.setOnClickListener(this);
		left_thumb = (ImageView) findViewById(R.id.ftp_left_thumb);
		left_thumb.setOnClickListener(this);

		right_little = (ImageView) findViewById(R.id.ftp_right_little);
		right_little.setOnClickListener(this);
		right_ring = (ImageView) findViewById(R.id.ftp_right_ring);
		right_ring.setOnClickListener(this);
		right_middle = (ImageView) findViewById(R.id.ftp_right_middle);
		right_middle.setOnClickListener(this);
		right_fore = (ImageView) findViewById(R.id.ftp_right_fore);
		right_fore.setOnClickListener(this);
		right_thumb = (ImageView) findViewById(R.id.ftp_right_thumb);
		right_thumb.setOnClickListener(this);

		mCancle = (Button) findViewById(R.id.ftp_hand_cancle);
		mCancle.setOnClickListener(this);
		mContinue = (Button) findViewById(R.id.ftp_hand_continue);
		mContinue.setOnClickListener(this);
		mContinue.setEnabled(false);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		InitTitleView();
	}
	
	private void InitTitleView() {
		int color = this.GetCurrentTheme(getPackageName());
		ColorDrawable cd = new ColorDrawable(getResources().getColor(color));
		if (getActionBar() != null)
			getActionBar().setBackgroundDrawable(cd);
		getWindow().setStatusBarColor(getResources().getColor(color));
	}
	
	private void updateView(int ID) {
		ImageView imageView;
		int id = -1;

		for (int j = 1; j < FingerprintData.FINGERPRINT_MAX + 1; j++) {
			if (FingerprintData.isDataExsitWithIndex(this, "fingerprint_" + j) == FingerprintData.DATA_NOT_EXSIT) {
				continue;
			}
			id = FingerprintData.getFingerprintToFinger(this, j);
			if ((id >= 0) && (id < FINGERNUM) && (ID == mImgId[id])) {
				Toast.makeText(
						this,
						R.string.ftp_quick_start_setting_camera_select_finger_toast,
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

		for (int i = 0; i < FINGERNUM; i++) {
			imageView = (ImageView) findViewById(mImgId[i]);
			if (ID == mImgId[i]) {
				imageView.setImageResource(R.drawable.ftp_circle_select);
				Log.e("fuck", "bbb------->mFingerId = " + mFingerId
						+ " title = " + mFingerName[i]);
				FingerprintData.setFingerTitle(this, mFingerId, mFingerName[i]);
				FingerprintData.setFingerprintToFinger(this, mFingerId, i);
				mContinue.setEnabled(true);
			} else {
				imageView.setImageResource(R.drawable.ftp_circle_normal);
			}
		}

		initViewState();
	}

	private void initViewState() {

		for (int i = 1; i < FingerprintData.FINGERPRINT_MAX + 1; i++) {
			if (FingerprintData.isDataExsitWithIndex(this, "fingerprint_" + i) == FingerprintData.DATA_NOT_EXSIT) {
				continue;
			}
			int id = FingerprintData.getFingerprintToFinger(this, i);
			if (id >= 0 && id < FINGERNUM) {
				ImageView imageView = (ImageView) findViewById(mImgId[id]);
				imageView.setImageResource(R.drawable.ftp_circle_selected);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ftp_left_little:
		case R.id.ftp_left_ring:
		case R.id.ftp_left_middle:
		case R.id.ftp_left_fore:
		case R.id.ftp_left_thumb:
		case R.id.ftp_right_little:
		case R.id.ftp_right_ring:
		case R.id.ftp_right_middle:
		case R.id.ftp_right_fore:
		case R.id.ftp_right_thumb:
			updateView(v.getId());
			break;
		case R.id.ftp_hand_cancle:
			FingerprintData.setFingerTitle(this, mFingerId, "");
			finish();
			break;
		case R.id.ftp_hand_continue:
			enterEnrollActivity(mFingerId);
			finish();
			break;
		}
	}

	private void enterEnrollActivity(int fingerIndex) {
		Intent i = new Intent();
		i.setClassName(this,
				"com.android.settings.fingerprint.FingerprintEnrollActivity");
		i.putExtra("index", fingerIndex);
		i.putExtra("fromswitch", mIsFromSwitch);
		startActivity(i);
	}
}
