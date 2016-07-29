package com.android.settings.fingerprint;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.android.settings.R;

public class FingerprintSwitchPreference extends Preference implements
		CompoundButton.OnCheckedChangeListener {
	private static final String TAG = "FingerprintSwitchPreference";
	private boolean mCurrentStatus = true;
	private boolean mEnabled = true;
	private String mProperties = null;
	private Switch mSwitch;
	private SwitchChange mSwitchChange;

	public FingerprintSwitchPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public boolean getCurrentStatus() {
		return this.mSwitch.isChecked();
	}

	protected void onBindView(View view) {
		super.onBindView(view);
		mSwitch = (Switch) view.findViewById(R.id.hct_switchWidget);
		mSwitch.setOnCheckedChangeListener(this);
		mSwitch.setChecked(mCurrentStatus);
		mSwitch.setEnabled(mEnabled);
	}

	public void onCheckedChanged(CompoundButton paramCompoundButton,
			boolean isChecked) {
		Log.v("FingerprintSwitchPreference",
				"FingerprintSwitchPreference onCheckedChanged--key=" + getKey()
						+ ",isChecked=" + isChecked);
		mCurrentStatus = isChecked;
		if (mSwitchChange != null) {
			mSwitchChange.onSwitchChange(getKey(), isChecked);
		}
	}

	protected View onCreateView(ViewGroup parent) {
		return super.onCreateView(parent);
	}

	public void setCurrentStatus(boolean isCheck) {
		mCurrentStatus = isCheck;
		if (mSwitch != null) {
			mSwitch.setChecked(mCurrentStatus);
		}
	}

	public void setEnabled(boolean enable) {
		mEnabled = enable;
		notifyChanged();
	}

	public void setSwitchChange(SwitchChange switchChange) {
		mSwitchChange = switchChange;
	}

	public static abstract interface SwitchChange {
		public abstract void onSwitchChange(String paramString,
				boolean paramBoolean);
	}
}
