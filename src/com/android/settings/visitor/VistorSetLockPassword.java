
package com.android.settings.visitor;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.settings.R;

import android.widget.TextView.OnEditorActionListener;

public class VistorSetLockPassword extends Activity implements OnClickListener, TextWatcher {
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

    private static final char[] ACCEPTED_CHARS = "1234567890".toCharArray();

    private EditText mPasswordEntry;

    private String mFirstPsw = null;

    private boolean mIsFirstSet = false;
	  ActivityManager mAm;//add by jiashixian

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visitor_lock_passerwd);
        mPasswordEntry = (EditText) findViewById(R.id.visitor_lock_entry);
        mPasswordEntry.addTextChangedListener(this);
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
        mAm = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);//add by jiashixian
        setupKeyBordView();
        mIsFirstSet = getIntent().getBooleanExtra("visitor_mode_first_set", false);
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
        mSure.setEnabled(false);
        mSure.setText(R.string.lockapp_continue);
        mSure.setOnClickListener(this);
    }

    private void handleNext() {
        mFirstPsw = mPasswordEntry.getText().toString();
        mSure.setText(android.R.string.ok);
        mSure.setEnabled(false);
        mPasswordEntry.setText("");
        mPasswordEntry.setHint(R.string.lockapp_psw_confirm);
    }

    private boolean checkPsw(String entrypsw) {
        return mFirstPsw.equals(entrypsw);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.keyboard_del:
                mPasswordEntry.dispatchKeyEvent(new KeyEvent(0, 67));
                if (TextUtils.isEmpty(mPasswordEntry.getText().toString())) {
                    mPasswordEntry.setHint(R.string.enter_psw_16);
                }
                break;

            case R.id.button_sure:
                if (mSure.getText().toString()
                        .equals(getResources().getString(R.string.lockapp_continue))) {
                    handleNext();
                } else {
                    if (checkPsw(mPasswordEntry.getText().toString())) {
                        String lockpsw = VisitorModeUntil.passwordsToHash(mFirstPsw);
                       android.os.SystemProperties.set("persist.sys.visitor_mode_psw", lockpsw);
                        android.os.SystemProperties.set("persist.sys.visitor_mode", "true");
                        mAm.killBackgroundProcesses("com.android.gallery3d"); //add by jiashixian
                        mAm.killBackgroundProcesses("com.android.dialer");//add by jiashixian
                      //  android.os.SystemProperties.set("persist.sys.lock_app", "true");
//                        if (mIsFirstSet) {
//                            Intent i = new Intent();
//                            i.setClassName("com.android.applock",
//                                    "com.android.applock.LockAppMainActivity");
//                            startActivity(i);
//                        }
                        finish();
                    } else {
                        mPasswordEntry.setHint(R.string.lockapp_psw_not_match);
                        mPasswordEntry.setText("");
                    }
                }
                break;
            case R.id.keyboard_clearall:
                mPasswordEntry.setText("");
                mPasswordEntry.setHint(R.string.enter_psw_16);
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
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub
        if (mPasswordEntry.getText().toString().length() > 0) {
            mSure.setEnabled(true);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO Auto-generated method stub
    }
}
