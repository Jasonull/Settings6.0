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
public class QuickStartAppliaction extends Activity implements OnItemClickListener{
	private static final String TAG = "QuickStartAppliaction";
	private ListView mListView;
	private QuickStartAdapter quickStartAdapter;
	private String mPkgName;
	private String mClassName;
	private String mLable;
	private int mFingerIndex = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quickstart_activity_main);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		mFingerIndex = bundle.getInt("index");
        mListView = (ListView) findViewById(R.id.list);
        mListView.setOnItemClickListener(this);
        quickStartAdapter = new QuickStartAdapter(QuickStartAppliaction.this);
        quickStartAdapter.setCurSelectPosition(mFingerIndex);
        mListView.setAdapter(quickStartAdapter);
		setTitle(getString(R.string.fingerprint_summary));
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		InitTitleView();
	}
	
	private void InitTitleView() {
		int color = this.GetCurrentTheme(getPackageName());
		getWindow().setStatusBarColor(getResources().getColor(color));
		ColorDrawable cd = new ColorDrawable(getResources().getColor(color));
		if(getActionBar() != null)
			getActionBar().setBackgroundDrawable(cd);
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
		String summary = getString(R.string.fingerprint_quickstart_summary) + mLable;
		FingerprintData.setFingerSummary(this, mFingerIndex, summary);
		String info = "";
		if(mPkgName != null && !mPkgName.equals("") && mClassName != null && !mClassName.equals("")){
			 info = mPkgName + "/" + mClassName;
		}
		FingerprintData.setFingerQuickApplication(this, mFingerIndex, info);
		quickStartAdapter.notifyDataSetChanged();
		finish();
	}
}
