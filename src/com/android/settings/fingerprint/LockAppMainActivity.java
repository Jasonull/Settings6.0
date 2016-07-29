
package com.android.settings.fingerprint;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.app.Activity;
import android.content.Intent;
import android.widget.AdapterView.OnItemClickListener;
import com.android.settings.R;
public class LockAppMainActivity extends Activity implements OnItemClickListener {

    private ListView mListView;

    private AppLockDBlite mAppLockDBlite;

    private AppLockAdapter appLockAdapter;

    private String mPkgName;

    private Handler mHandler = new Handler();

    private Button mBackBtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applock_activity_main);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setOnItemClickListener(this);

        mAppLockDBlite = AppLockDBlite.getInstance(this);
        appLockAdapter = new AppLockAdapter(LockAppMainActivity.this);
        mListView.setAdapter(appLockAdapter);

        mBackBtn = (Button) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (mAppLockDBlite.isDateExits(mPkgName)) {
                mAppLockDBlite.delete(mPkgName);
                // Special for downloads apk
                if (mPkgName.equals("com.android.providers.downloads.ui")) {
                    mAppLockDBlite.delete("com.android.documentsui");
                }
            } else {
                mAppLockDBlite.add(mPkgName, 0);
                // Special for downloads apk
                if (mPkgName.equals("com.android.providers.downloads.ui")) {
                    mAppLockDBlite.add("com.android.documentsui", 0);
                }
            }
            appLockAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        mPkgName = appLockAdapter.getAppInfo(arg2).getPkgName();
        mHandler.post(runnable);
    }

}
