package com.android.settings.fingerprint;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import com.android.settings.R;

public class FingerprintQuickStartSelectApplication extends Activity implements
		OnItemClickListener {
	private static final String TAG = "QuickStartSelectApplication";
	private ListView mListView;
	private FingerprintQuickStartAdapter quickStartAdapter;
	private String mPkgName;
	private String mClassName;
	private String mLable;
	private int mFingerIndex = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quickstartprefixlayout);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mFingerIndex = bundle.getInt("index");
		mListView = (ListView) findViewById(R.id.prefix_list);
		mListView.setOnItemClickListener(this);
		quickStartAdapter = new FingerprintQuickStartAdapter(this,mFingerIndex);
		mListView.setAdapter(quickStartAdapter);
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
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		quickStartAdapter.setDefault(false);
		quickStartAdapter.setCurSelectPosition(position);
		mPkgName = quickStartAdapter.getAppInfo(position).getPkgName();
		mClassName = quickStartAdapter.getAppInfo(position).getClassName();
		mLable = quickStartAdapter.getAppInfo(position).getAppLabel();
		FingerprintData.setFingerSummary(this, mFingerIndex, mLable);
		String info = mPkgName + "|" + mClassName;
		FingerprintData.setFingerQuickApplication(this, mFingerIndex, info);
		quickStartAdapter.notifyDataSetChanged();
		finish();
	}

}
