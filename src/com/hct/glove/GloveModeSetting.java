package com.hct.glove;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SettingsActivity;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.SearchIndexableRaw;
import com.android.settings.widget.SwitchBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;



import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class GloveModeSetting extends SettingsPreferenceFragment implements
        SwitchBar.OnSwitchChangeListener {
    private static final String TAG = "GloveModeSetting";
    private IntentFilter mIsntentFilter;
    
    private SwitchBar mSwitchBar;
    private boolean mLastEnabledState;

	//private final String GLOVE_MODE_ENABLE = "1";
	//private final String GLOVE_MODE_DISABLE = "0";
	private final String GLOVE_MODE_FILE_PATH_NAME = "/proc/class/ms-touchscreen-msg20xx/device/glove_mode";
	private static final String VFS_OPEN_GLOVE_MODE = "/proc/class/ms-touchscreen-msg20xx/device/open_glove_mode";
	private static final String VFS_CLOSE_GLOVE_MODE = "/proc/class/ms-touchscreen-msg20xx/device/close_glove_mode";

//	private static SharedPreferences _prefVFS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingsActivity activity = (SettingsActivity) getActivity();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final SettingsActivity activity = (SettingsActivity) getActivity();
        mSwitchBar = activity.getSwitchBar();
        mSwitchBar.addOnSwitchChangeListener(this);
        Log.d(TAG, "onActivityCreated, mSwitchBar = " + mSwitchBar);
    }

        
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.glove_mode_settings, container, false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        mSwitchBar.removeOnSwitchChangeListener(this);
        mSwitchBar.hide();

    }

    public void onResume() {
        super.onResume();

		boolean gloveModeOnoff = (readGloveModeStatus() == 1);
        Log.d(TAG, "onResume, gloveModeOnoff = " + gloveModeOnoff);
        mLastEnabledState = gloveModeOnoff;
        mSwitchBar.setChecked(gloveModeOnoff);
        mSwitchBar.show();

    }

    public void onPause() {
        super.onPause();

    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        if (switchView == mSwitchBar.getSwitch()) {
            if (isChecked != mLastEnabledState) {
                Log.d(TAG, "onSwitchChanged, isChecked = " + isChecked);
                //setGloveModeStatus(isChecked);
                changeGloveMode(isChecked ? 1 : 0);
                mLastEnabledState = isChecked;
            }
        }
        
    }

/*
    private boolean readGloveModeStatus() {
        File file = new File(GLOVE_MODE_FILE_PATH_NAME);
        Log.d(TAG, "readGloveModeStatus, file = " + file);
        if(!file.exists())return false;
        
        try {
            FileInputStream fis = new FileInputStream(file);
            int length = fis.available();
            byte [] buffer = new byte[length];
            fis.read(buffer);
            fis.close();
            Log.d(TAG, "readGloveModeStatus, length = " + length+",buffer="+buffer[0]);

            if (length == 0) {
                return false;
            }
            
            boolean ret = true;
            if(buffer[0]=='0')ret = false;
            return ret;
        } catch (Exception e) {
            Log.d(TAG, "readGloveModeStatus, Exception = " + e);
            e.printStackTrace();   
        }   
        return false;
    }

    
    private void setGloveModeStatus(boolean enable) {
        File file = new File(GLOVE_MODE_FILE_PATH_NAME);
        Log.d(TAG, "setGloveModeStatus, file = " + file);
        if(!file.exists())return;
        
        try {
            FileOutputStream fout = new FileOutputStream(file);   
            byte [] bytes = GLOVE_MODE_DISABLE.getBytes();   
            if(enable)bytes = GLOVE_MODE_ENABLE.getBytes();
            Log.d(TAG, "setGloveModeStatus, enable = " + enable+",bytes[0]="+bytes[0]);
            fout.write(bytes);   
            fout.close();   
        } catch (Exception e) {
            Log.d(TAG, "setGloveModeStatus, Exception = " + e);
            e.printStackTrace();   
        }
    }
*/

	private static int glove_mode = 0;
	public static int GLOVE_MODE_OFF_MODE = 0x00; 
	public static int GLOVE_MODE_ON_MODE = 0x01; 

    private int readGloveModeStatus() {
        Log.d(TAG, "Get Glove Mode Start");
        
        try {
            FileInputStream in = new FileInputStream(GLOVE_MODE_FILE_PATH_NAME);
            byte[] inputBuf = new byte[32];
            int length = in.read(inputBuf);
            in.close();
    
            Log.d(TAG, "length = " + length);
            
            if (length > 0 && length <= 32) {
                String mode = new String(inputBuf, 0, length);
                int i = 0;
                for (i = 0; i < length; i ++) {
                    Log.d(TAG, "\n inputBuf[" + i + "] = " + inputBuf[i]);
                }
            
                glove_mode = Integer.parseInt(mode, 16);
                
				Log.d(TAG, "Glove Mode = " + mode);
            }  else {
                glove_mode = 0;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        Log.d(TAG, "glove_mode = " + glove_mode);
        Log.d(TAG, "Get Firmware Mode End");
        
        return glove_mode;
    }


    public void changeGloveMode(int current_mode) {
        Log.d(TAG, "Change Glove Mode Start");
        
		try {
			FileInputStream in = null;
			byte[] inputBuf = new byte[32];
			Log.d(TAG, "current_mode = " + current_mode);
			if (current_mode == GLOVE_MODE_ON_MODE) {

				in = new FileInputStream(VFS_OPEN_GLOVE_MODE);
			}
			else if (current_mode == GLOVE_MODE_OFF_MODE) {
				in = new FileInputStream(VFS_CLOSE_GLOVE_MODE);
			}
			else {
				Log.d(TAG, "Undefined Glove Mode");
				return;	
			}
			
			int length = in.read(inputBuf);
			in.close();

			Log.d(TAG, "length = " + length);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.d(TAG, "Change Glove Mode End");
    }
    
   
}
