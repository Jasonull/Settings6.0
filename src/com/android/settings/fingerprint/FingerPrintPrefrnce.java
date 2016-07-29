package com.android.settings.fingerprint;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.R;

public class FingerPrintPrefrnce extends Preference {

	public FingerPrintPrefrnce(Context context) {
		this(context, null, 0);
		// TODO Auto-generated constructor stub
	}

	public FingerPrintPrefrnce(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public FingerPrintPrefrnce(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		// TODO Auto-generated method stub
		return LayoutInflater.from(getContext()).inflate(
				R.layout.fingerprint_preferences, parent, false);
	}

}
