
package com.android.settings.visitor;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Bundle;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.provider.Settings;
import android.widget.TextView;
import com.android.settings.R;
import android.widget.TextView.OnEditorActionListener;

public class VistorUnlockPassword extends Activity implements OnClickListener {
    private static final String TAG = "VistorUnlockPassword";

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

    private boolean mIsUnlockVisitor = false;
    
    ActivityManager mAm; //add by jiashixian


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visitor_mode_unlock);

        mPasswordEntry = (EditText) findViewById(R.id.visitor_unlock_password_entry);
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
                String pws = mPasswordEntry.getText().toString();
                if (TextUtils.isEmpty(pws)) {
                    return;
                }
                String savePsw = android.os.SystemProperties.get("persist.sys.visitor_mode_psw");
                String hashPws = VisitorModeUntil.passwordsToHash(pws);
                if (hashPws.equals(savePsw)) {
                   
                  	android.os.SystemProperties.set("persist.sys.visitor_mode", "false");
                  	mAm.killBackgroundProcesses("com.android.gallery3d"); //add by jiashixain
                  	mAm.killBackgroundProcesses("com.android.dialer");//add by jiashixian
                    finish();	
                         
                } else {
                    mPasswordEntry.setText("");
                    mPasswordEntry.setHint(R.string.lockapp_psw_wrong);
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

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        // TODO Auto-generated method stub
//       
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent i = new Intent(Intent.ACTION_MAIN, null);
//            i.addCategory(Intent.CATEGORY_HOME);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//            startActivity(i);
//            finish();
//            return false;
//        }
//        if (keyCode == KeyEvent.KEYCODE_HOME) {
//            finish();
//        }
//        return super.onKeyUp(keyCode, event);
//    }

    private void updateData() {
       
    }
}
