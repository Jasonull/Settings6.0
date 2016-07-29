
package com.android.settings.gesture;

import android.os.Bundle;

import com.android.settings.HctAllAppActivity;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import android.os.SystemProperties;
import android.preference.PreferenceScreen;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.app.Activity;
import android.preference.CheckBoxPreference;
import android.app.ActionBar;
import android.view.Gravity;
import android.preference.Preference;
import com.android.settings.widget.SwitchBar;
import com.android.settings.SettingsActivity;

public class GestureListSettings extends SettingsPreferenceFragment implements
        SwitchBar.OnSwitchChangeListener {
    private static final String KEY_GESTURE_SETTINGS = "key_gesture_list_settings";
    private static final String KEY_GESTURE_UP = "key_gesture_up";
    private static final String KEY_GESTURE_DOWN = "key_gesture_down";
    private static final String KEY_GESTURE_LEFT = "key_gesture_left_right";
    private static final String KEY_GESTURE_C = "key_gesture_c";
    private static final String KEY_GESTURE_M = "key_gesture_m";
    private static final String KEY_GESTURE_E = "key_gesture_e";
    private static final String KEY_GESTURE_O = "key_gesture_o";
    private static final String KEY_GESTURE_W = "key_gesture_w";
    private static final String KEY_GESTURE_Z = "key_gesture_z";
    private static final String KEY_GESTURE_V = "key_gesture_v";
    private static final String KEY_GESTURE_S = "key_gesture_s";
    private static final String KEY_GESTURE_DOUBLE = "key_gesture_double";
    private static final String KEY_GESTURE_RIGHT = "key_gesture_right";

    private PreferenceScreen mGestureSettingsPreference;
    private HctTpGestureSwitchPreference mGestureUpPreference;
    private HctTpGestureSwitchPreference mGestureDownPreference;
    private HctTpGestureSwitchPreference mGestureLeftPreference;
    private HctTpGestureSwitchPreference mGestureRightPreference;
    private HctTpGestureSwitchPreference mGestureCPreference;
    private HctTpGestureSwitchPreference mGestureMPreference;
    private HctTpGestureSwitchPreference mGestureEPreference;
    private HctTpGestureSwitchPreference mGestureOPreference;
    private HctTpGestureSwitchPreference mGestureWPreference;
    private HctTpGestureSwitchPreference mGestureZPreference;
    private HctTpGestureSwitchPreference mGestureVPreference;
	private HctTpGestureSwitchPreference mGestureSPreference;
    private HctTpGestureSwitchPreference mGestureDoublePreference;
    private String mCurrGesturePreferenceKey;

    private boolean mGestureUpEnable = true;
    private boolean mGestureDownEnable = true;
    private boolean mGestureLeftEnable = true;
    private boolean mGestureCEnable = true;
    private boolean mGestureMEnable = true;
    private boolean mGestureEEnable = true;
    private boolean mGestureOEnable = true;
    private boolean mGestureWEnable = true;
    private boolean mGestureZEnable = true;
    private boolean mGestureVEnable = true;
	private boolean mGestureSEnable = true;
    private boolean mGestureDoubleEnable = true;
	private boolean mGestureRightEnable = true;
    private SwitchBar mSwitchBar;
    private boolean mLastEnabledState;


    private Activity mActivity;
    private final String PROPERTIES_GESTURE_POWERONOFF = "persist.sys.gestureonoff";

    private final static String PROPERTIES_GESTURE_UP = "persist.sys.gestureup";
    private final static String PROPERTIES_GESTURE_DOWN = "persist.sys.gesturedown";
    private final static String PROPERTIES_GESTURE_LEFT = "persist.sys.gestureleft";
    private final static String PROPERTIES_GESTURE_RIGHT = "persist.sys.gestureright";
    private final static String PROPERTIES_GESTURE_C = "persist.sys.gesturec";
    private final static String PROPERTIES_GESTURE_M = "persist.sys.gesturem";
    private final static String PROPERTIES_GESTURE_E = "persist.sys.gesturee";
    private final static String PROPERTIES_GESTURE_O = "persist.sys.gestureo";
    private final static String PROPERTIES_GESTURE_W = "persist.sys.gesturew";
    private final static String PROPERTIES_GESTURE_Z = "persist.sys.gesturez";
    private final static String PROPERTIES_GESTURE_V = "persist.sys.gesturev";
	private final static String PROPERTIES_GESTURE_S = "persist.sys.gestures";
    private final static String PROPERTIES_GESTURE_DOUBLE = "persist.sys.gesturedouble";

	private static final int SELECT_APP = 1;
    
	private static final String HCT_TPGESTURE_C_APPNAME ="persist.sys.tpgescname";
	private static final String HCT_TPGESTURE_C_PKGNAME = "persist.sys.tpgescpkg";
	private static final String HCT_TPGESTURE_C_CLSNAME = "persist.sys.tpgesccls";

	private static final String HCT_TPGESTURE_DOWN_APPNAME ="persist.sys.tpgesdownname";
	private static final String HCT_TPGESTURE_DOWN_PKGNAME = "persist.sys.tpgesdownpkg";
	private static final String HCT_TPGESTURE_DOWN_CLSNAME = "persist.sys.tpgesdowncls";

	private static final String HCT_TPGESTURE_M_APPNAME ="persist.sys.tpgesmname";
	private static final String HCT_TPGESTURE_M_PKGNAME = "persist.sys.tpgesmpkg";
	private static final String HCT_TPGESTURE_M_CLSNAME = "persist.sys.tpgesmcls";

	private static final String HCT_TPGESTURE_E_APPNAME ="persist.sys.tpgesename";
	private static final String HCT_TPGESTURE_E_PKGNAME = "persist.sys.tpgesepkg";
	private static final String HCT_TPGESTURE_E_CLSNAME = "persist.sys.tpgesecls";
    
	private static final String HCT_TPGESTURE_O_APPNAME ="persist.sys.tpgesoname";
	private static final String HCT_TPGESTURE_O_PKGNAME = "persist.sys.tpgesopkg";
	private static final String HCT_TPGESTURE_O_CLSNAME = "persist.sys.tpgesocls";

	private static final String HCT_TPGESTURE_W_APPNAME ="persist.sys.tpgeswname";
	private static final String HCT_TPGESTURE_W_PKGNAME = "persist.sys.tpgeswpkg";
	private static final String HCT_TPGESTURE_W_CLSNAME = "persist.sys.tpgeswcls";

	private static final String HCT_TPGESTURE_Z_APPNAME ="persist.sys.tpgeszname";
	private static final String HCT_TPGESTURE_Z_PKGNAME = "persist.sys.tpgeszpkg";
	private static final String HCT_TPGESTURE_Z_CLSNAME = "persist.sys.tpgeszcls";

	private static final String HCT_TPGESTURE_V_APPNAME ="persist.sys.tpgesvname";
	private static final String HCT_TPGESTURE_V_PKGNAME = "persist.sys.tpgesvpkg";
	private static final String HCT_TPGESTURE_V_CLSNAME = "persist.sys.tpgesvcls";

	private static final String HCT_TPGESTURE_S_APPNAME ="persist.sys.tpgessname";
	private static final String HCT_TPGESTURE_S_PKGNAME = "persist.sys.tpgesspkg";
	private static final String HCT_TPGESTURE_S_CLSNAME = "persist.sys.tpgesscls";

	private static final String HCT_TPGESTURE_UP_PKGNAME = "persist.sys.tpgesuppkg";
	private static final String HCT_TPGESTURE_UP_CLSNAME = "persist.sys.tpgesupcls";
	private static final String HCT_TPGESTURE_LEFT_PKGNAME = "persist.sys.tpgesleftpkg";
	private static final String HCT_TPGESTURE_LEFT_CLSNAME = "persist.sys.tpgesleftcls";
	private static final String HCT_TPGESTURE_RIGHT_PKGNAME = "persist.sys.tpgesrightpkg";
	private static final String HCT_TPGESTURE_RIGHT_CLSNAME = "persist.sys.tpgesrightcls";
    private boolean debug = false;
    private boolean changeup = false;
    private boolean changeleft = false;
    private boolean changeright = false;
    private boolean changedown = false;
    private boolean changem = false;
    private boolean removev = false;
private boolean removes = false;
	private boolean removez = false;

    private String[] tpgestureDownApp;
    private String[] tpgestureUpApp;
    private String[] tpgestureLeftApp;
    private String[] tpgestureRightApp;
    private String[] tpgestureCApp;
    private String[] tpgestureEApp;
    private String[] tpgestureOApp;
    private String[] tpgestureWApp;
    private String[] tpgestureZApp;
    private String[] tpgestureVApp;
	private String[] tpgestureSApp;
    private String[] tpgestureMApp;
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
	if (getResources().getBoolean(R.bool.hct_tp_gesture_no_sensor))
	{
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
	}
        addPreferencesFromResource(R.xml.hct_gesture_unlock_setting_selectapp);
        initScreen();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final SettingsActivity activity = (SettingsActivity) getActivity();
        mSwitchBar = activity.getSwitchBar();
        mSwitchBar.addOnSwitchChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        mLastEnabledState = SystemProperties.getBoolean(
                PROPERTIES_GESTURE_POWERONOFF, true);

        boolean isChecked = mLastEnabledState;
        mSwitchBar.setChecked(isChecked);
        getPreferenceScreen().setEnabled(isChecked);
        mSwitchBar.show();
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        if (switchView != mSwitchBar.getSwitch()) {
            return;
        }
        
        if (isChecked != mLastEnabledState) {
            getPreferenceScreen().setEnabled(isChecked);
            SystemProperties.set(PROPERTIES_GESTURE_POWERONOFF, isChecked ? "true" : "false");
            mLastEnabledState = isChecked;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        mSwitchBar.removeOnSwitchChangeListener(this);
        mSwitchBar.hide();
    }

    private void initScreen() {

        final PreferenceScreen parent = getPreferenceScreen();
        mGestureDoublePreference = (HctTpGestureSwitchPreference) parent
                .findPreference(KEY_GESTURE_DOUBLE);
        mGestureUpPreference = (HctTpGestureSwitchPreference) parent
                .findPreference(KEY_GESTURE_UP);
        mGestureDownPreference = (HctTpGestureSwitchPreference) parent
                .findPreference(KEY_GESTURE_DOWN);
        mGestureLeftPreference = (HctTpGestureSwitchPreference) parent
                .findPreference(KEY_GESTURE_LEFT);
		mGestureRightPreference = (HctTpGestureSwitchPreference) parent
                .findPreference(KEY_GESTURE_RIGHT);
        mGestureCPreference = (HctTpGestureSwitchPreference) parent
                .findPreference(KEY_GESTURE_C);
        mGestureMPreference = (HctTpGestureSwitchPreference) parent
                .findPreference(KEY_GESTURE_M);
        mGestureEPreference = (HctTpGestureSwitchPreference) parent
                .findPreference(KEY_GESTURE_E);
        mGestureOPreference = (HctTpGestureSwitchPreference) parent
                .findPreference(KEY_GESTURE_O);
        mGestureWPreference = (HctTpGestureSwitchPreference) parent
                .findPreference(KEY_GESTURE_W);
        mGestureZPreference = (HctTpGestureSwitchPreference) parent
                .findPreference(KEY_GESTURE_Z);
        mGestureVPreference = (HctTpGestureSwitchPreference) parent
                .findPreference(KEY_GESTURE_V);
mGestureSPreference = (HctTpGestureSwitchPreference) parent
                .findPreference(KEY_GESTURE_S);
        tpgestureCApp = getResources().getStringArray(com.android.internal.R.array.config_tpgesture_cchar_entry);
        tpgestureEApp = getResources().getStringArray(com.android.internal.R.array.config_tpgesture_echar_entry);
        tpgestureOApp = getResources().getStringArray(com.android.internal.R.array.config_tpgesture_ochar_entry);
        tpgestureWApp = getResources().getStringArray(com.android.internal.R.array.config_tpgesture_wchar_entry);
        tpgestureZApp = getResources().getStringArray(com.android.internal.R.array.config_tpgesture_zchar_entry);
        tpgestureVApp = getResources().getStringArray(com.android.internal.R.array.config_tpgesture_vchar_entry);
	tpgestureSApp = getResources().getStringArray(com.android.internal.R.array.config_tpgesture_schar_entry);
        tpgestureMApp = getResources().getStringArray(com.android.internal.R.array.config_tpgesture_mchar_entry);
		tpgestureUpApp = getResources().getStringArray(com.android.internal.R.array.config_tpgesture_upchar_entry);
		tpgestureLeftApp = getResources().getStringArray(com.android.internal.R.array.config_tpgesture_leftchar_entry);
        tpgestureRightApp = getResources().getStringArray(com.android.internal.R.array.config_tpgesture_rightchar_entry);
		tpgestureDownApp = getResources().getStringArray(com.android.internal.R.array.config_tpgesture_downchar_entry);
		changeup = getResources().getBoolean(com.android.internal.R.bool.hct_tp_gesture_change_up);
		changeleft = getResources().getBoolean(com.android.internal.R.bool.hct_tp_gesture_change_left);
		changeright = getResources().getBoolean(com.android.internal.R.bool.hct_tp_gesture_change_right);
		changem = getResources().getBoolean(com.android.internal.R.bool.hct_tp_gesture_change_m);
		changedown = getResources().getBoolean(R.bool.hct_tp_gesture_change_down);	
		removev = !getResources().getBoolean(com.android.internal.R.bool.hct_tp_gesture_vchar_enable);
		removes = !getResources().getBoolean(com.android.internal.R.bool.hct_tp_gesture_schar_enable);
		removez = !getResources().getBoolean(com.android.internal.R.bool.hct_tp_gesture_zchar_enable);
        CharSequence c_summary = getApplicationName(SystemProperties.get(HCT_TPGESTURE_C_PKGNAME, tpgestureCApp[0])
            , SystemProperties.get(HCT_TPGESTURE_C_CLSNAME, tpgestureCApp[1]));
        CharSequence e_summary = getApplicationName(SystemProperties.get(HCT_TPGESTURE_E_PKGNAME, tpgestureEApp[0])
            , SystemProperties.get(HCT_TPGESTURE_E_CLSNAME, tpgestureEApp[1]));
        CharSequence o_summary = getApplicationName(SystemProperties.get(HCT_TPGESTURE_O_PKGNAME, tpgestureOApp[0])
            , SystemProperties.get(HCT_TPGESTURE_O_CLSNAME, tpgestureOApp[1]));
        CharSequence w_summary = getApplicationName(SystemProperties.get(HCT_TPGESTURE_W_PKGNAME, tpgestureWApp[0])
            , SystemProperties.get(HCT_TPGESTURE_W_CLSNAME, tpgestureWApp[1]));
        CharSequence z_summary = getApplicationName(SystemProperties.get(HCT_TPGESTURE_Z_PKGNAME, tpgestureZApp[0])
            , SystemProperties.get(HCT_TPGESTURE_Z_CLSNAME, tpgestureZApp[1]));
        CharSequence v_summary = getApplicationName(SystemProperties.get(HCT_TPGESTURE_V_PKGNAME, tpgestureVApp[0])
            , SystemProperties.get(HCT_TPGESTURE_V_CLSNAME, tpgestureVApp[1]));
	CharSequence s_summary = getApplicationName(SystemProperties.get(HCT_TPGESTURE_S_PKGNAME, tpgestureSApp[0])
            , SystemProperties.get(HCT_TPGESTURE_S_CLSNAME, tpgestureSApp[1]));
        CharSequence m_summary = getApplicationName(SystemProperties.get(HCT_TPGESTURE_M_PKGNAME, tpgestureMApp[0])
            , SystemProperties.get(HCT_TPGESTURE_M_CLSNAME, tpgestureMApp[1]));
		CharSequence up_summary = getApplicationName(SystemProperties.get(HCT_TPGESTURE_UP_PKGNAME, tpgestureUpApp[0])
            , SystemProperties.get(HCT_TPGESTURE_UP_CLSNAME, tpgestureUpApp[1]));
        CharSequence left_summary = getApplicationName(SystemProperties.get(HCT_TPGESTURE_LEFT_PKGNAME, tpgestureLeftApp[0])
            , SystemProperties.get(HCT_TPGESTURE_LEFT_CLSNAME, tpgestureLeftApp[1]));
        CharSequence right_summary = getApplicationName(SystemProperties.get(HCT_TPGESTURE_RIGHT_PKGNAME, tpgestureRightApp[0])
            , SystemProperties.get(HCT_TPGESTURE_RIGHT_CLSNAME, tpgestureRightApp[1]));
		CharSequence down_summary = getApplicationName(SystemProperties.get(HCT_TPGESTURE_DOWN_PKGNAME, tpgestureDownApp[0])
            , SystemProperties.get(HCT_TPGESTURE_DOWN_CLSNAME, tpgestureDownApp[1]));
        
        mGestureCPreference.setTitle(R.string.hct_gesture_c_title);
        mGestureCPreference.setSummary(getResources().getString(R.string.hct_gesture_action) 
            + " " + c_summary);
        mGestureEPreference.setTitle(R.string.hct_gesture_e_title);
        mGestureEPreference.setSummary(getResources().getString(R.string.hct_gesture_action) 
            + " " + e_summary);
        mGestureOPreference.setTitle(R.string.hct_gesture_o_title);
        mGestureOPreference.setSummary(getResources().getString(R.string.hct_gesture_action) 
            + " " + o_summary);
        mGestureWPreference.setTitle(R.string.hct_gesture_w_title);
        mGestureWPreference.setSummary(getResources().getString(R.string.hct_gesture_action) 
            + " " + w_summary);
        mGestureZPreference.setTitle(R.string.hct_gesture_z_title);
        mGestureZPreference.setSummary(getResources().getString(R.string.hct_gesture_action) 
            + " " + z_summary);
        mGestureVPreference.setTitle(R.string.hct_gesture_v_title);
        mGestureVPreference.setSummary(getResources().getString(R.string.hct_gesture_action) 
            + " " + v_summary);
	mGestureSPreference.setTitle(R.string.hct_gesture_s_title);
        mGestureSPreference.setSummary(getResources().getString(R.string.hct_gesture_action) 
            + " " + s_summary);
		if(changeup)
		{
			mGestureUpPreference.setTitle(getResources().getString(R.string.hct_gesture_string_changeup));
			mGestureUpPreference.setSummary(getResources().getString(R.string.hct_gesture_action) 
            	+ " " + up_summary);
		}
		if(changeleft){
			mGestureLeftPreference.setTitle(getResources().getString(R.string.hct_gesture_string_changeleft));
			mGestureLeftPreference.setSummary(getResources().getString(R.string.hct_gesture_action) 
            	+ " " + left_summary);
		}
		if(changedown){
			mGestureDownPreference.setTitle(getResources().getString(R.string.hct_gesture_string_changedown));
			mGestureDownPreference.setSummary(getResources().getString(R.string.hct_gesture_action) 
            	+ " " + down_summary);
		}
		if(changem){
			mGestureMPreference.setTitle(getResources().getString(R.string.hct_gesture_string_changem));
			mGestureMPreference.setSummary(getResources().getString(R.string.hct_gesture_action) 
            	+ " " + m_summary);
		}
		if(changeright){
			mGestureRightPreference.setTitle(getResources().getString(R.string.hct_gesture_string_changeright));
			mGestureRightPreference.setSummary(getResources().getString(R.string.hct_gesture_action) 
            	+ " " + right_summary);
		}else{
			getPreferenceScreen().removePreference(mGestureRightPreference);
		}
		if(removev)
		getPreferenceScreen().removePreference(mGestureVPreference);
		if(removes)
		getPreferenceScreen().removePreference(mGestureSPreference);
		if(removez)
		getPreferenceScreen().removePreference(mGestureZPreference);
        mGestureDoublePreference.setPropertiesString(PROPERTIES_GESTURE_DOUBLE);
        mGestureUpPreference.setPropertiesString(PROPERTIES_GESTURE_UP);
        mGestureDownPreference.setPropertiesString(PROPERTIES_GESTURE_DOWN);
        mGestureLeftPreference.setPropertiesString(PROPERTIES_GESTURE_LEFT);
        mGestureRightPreference.setPropertiesString(PROPERTIES_GESTURE_RIGHT);
        mGestureCPreference.setPropertiesString(PROPERTIES_GESTURE_C);
        mGestureMPreference.setPropertiesString(PROPERTIES_GESTURE_M);
        mGestureEPreference.setPropertiesString(PROPERTIES_GESTURE_E);
        mGestureOPreference.setPropertiesString(PROPERTIES_GESTURE_O);
        mGestureWPreference.setPropertiesString(PROPERTIES_GESTURE_W);
        mGestureZPreference.setPropertiesString(PROPERTIES_GESTURE_Z);
        mGestureVPreference.setPropertiesString(PROPERTIES_GESTURE_V);
	mGestureSPreference.setPropertiesString(PROPERTIES_GESTURE_S);
        mGestureDoubleEnable = SystemProperties.getBoolean(
                PROPERTIES_GESTURE_DOUBLE, true);
        mGestureUpEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_UP,
                true);
        mGestureDownEnable = SystemProperties.getBoolean(
                PROPERTIES_GESTURE_DOWN, true);
        mGestureLeftEnable = SystemProperties.getBoolean(
                PROPERTIES_GESTURE_LEFT, true);
        mGestureCEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_C,
                true);
        mGestureMEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_M,
                true);
        mGestureEEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_E,
                true);
        mGestureOEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_O,
                true);
        mGestureWEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_W,
                true);
        mGestureZEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_Z,
                true);
		mGestureVEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_V,
                true);
	mGestureSEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_S,
                true);
		mGestureRightEnable = SystemProperties.getBoolean(PROPERTIES_GESTURE_RIGHT,
                true);
        mGestureDoublePreference.setCurrentStatus(mGestureDoubleEnable);
        mGestureUpPreference.setCurrentStatus(mGestureUpEnable);
        mGestureDownPreference.setCurrentStatus(mGestureDownEnable);
        mGestureLeftPreference.setCurrentStatus(mGestureLeftEnable);
        mGestureRightPreference.setCurrentStatus(mGestureRightEnable);
        mGestureCPreference.setCurrentStatus(mGestureCEnable);
        mGestureMPreference.setCurrentStatus(mGestureMEnable);
        mGestureEPreference.setCurrentStatus(mGestureEEnable);
        mGestureOPreference.setCurrentStatus(mGestureOEnable);
        mGestureWPreference.setCurrentStatus(mGestureWEnable);
        mGestureZPreference.setCurrentStatus(mGestureZEnable);
        mGestureVPreference.setCurrentStatus(mGestureVEnable);
	mGestureSPreference.setCurrentStatus(mGestureSEnable);
	}

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference pref) {
        Log.d("gestureunlock", "onPreferenceTreeClick,pref="+pref.getKey());
        mCurrGesturePreferenceKey = pref.getKey();
        if (mCurrGesturePreferenceKey.equals(KEY_GESTURE_C)
            /*|| mCurrGesturePreferenceKey.equals(KEY_GESTURE_M)*/
            || mCurrGesturePreferenceKey.equals(KEY_GESTURE_E)
            || mCurrGesturePreferenceKey.equals(KEY_GESTURE_O)
            || mCurrGesturePreferenceKey.equals(KEY_GESTURE_W)
            || mCurrGesturePreferenceKey.equals(KEY_GESTURE_Z)
            || mCurrGesturePreferenceKey.equals(KEY_GESTURE_V)
|| mCurrGesturePreferenceKey.equals(KEY_GESTURE_S)
        ) {
			Intent intent = new Intent(getActivity(), HctAllAppActivity.class);
            startActivityForResult(intent, SELECT_APP);
		}else if ((mCurrGesturePreferenceKey.equals(KEY_GESTURE_UP) && changeup)
				   ||(mCurrGesturePreferenceKey.equals(KEY_GESTURE_LEFT) && changeleft)
				   ||(mCurrGesturePreferenceKey.equals(KEY_GESTURE_RIGHT) && changeright)
				   ||(mCurrGesturePreferenceKey.equals(KEY_GESTURE_DOWN) && changedown)
				   ||(mCurrGesturePreferenceKey.equals(KEY_GESTURE_M) && changem)){
			Intent intent = new Intent(getActivity(), HctAllAppActivity.class);
            startActivityForResult(intent, SELECT_APP);
		}
        
		return true;
        
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v("gestureunlock", "onActivityResult,resultCode=" + resultCode + ",data="
				+ data +",requestCode="+requestCode);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
    			case SELECT_APP:
    				selectAppForResult(data);
    				break;
			}
		}
	}

	public void selectAppForResult(Intent data) {
		Bundle mBuddle = data.getExtras();
		if (mBuddle != null) {
			String appname = mBuddle.getString("appname");
			String pkgname = mBuddle.getString("pkgname");

			Log.v("gestureunlock", "selectAppForResult-appname=" + appname
					+ ",pkgname=" + pkgname);

			if (appname != null && pkgname != null) {
                String[] mPkgAndClsName = new String[2];
                mPkgAndClsName = pkgname.split("\\|");
                HctTpGestureSwitchPreference mGurrPreference;
                mGurrPreference = (HctTpGestureSwitchPreference) findPreference(mCurrGesturePreferenceKey);
                CharSequence summary = getApplicationName(mPkgAndClsName[0], mPkgAndClsName[1]);
                mGurrPreference.setSummary(getResources().getString(R.string.hct_gesture_action) + " " + summary);        
                if (mCurrGesturePreferenceKey.equals(KEY_GESTURE_C)) {
                        SystemProperties.set(HCT_TPGESTURE_C_PKGNAME, mPkgAndClsName[0]);
                        SystemProperties.set(HCT_TPGESTURE_C_CLSNAME, mPkgAndClsName[1]);
                } else if (mCurrGesturePreferenceKey.equals(KEY_GESTURE_E)) {
                        SystemProperties.set(HCT_TPGESTURE_E_PKGNAME, mPkgAndClsName[0]);
                        SystemProperties.set(HCT_TPGESTURE_E_CLSNAME, mPkgAndClsName[1]);
                } else if (mCurrGesturePreferenceKey.equals(KEY_GESTURE_O)) {
                        SystemProperties.set(HCT_TPGESTURE_O_PKGNAME, mPkgAndClsName[0]);
                        SystemProperties.set(HCT_TPGESTURE_O_CLSNAME, mPkgAndClsName[1]);
                } else if (mCurrGesturePreferenceKey.equals(KEY_GESTURE_W)) {
                        SystemProperties.set(HCT_TPGESTURE_W_PKGNAME, mPkgAndClsName[0]);
                        SystemProperties.set(HCT_TPGESTURE_W_CLSNAME, mPkgAndClsName[1]);
                } else if (mCurrGesturePreferenceKey.equals(KEY_GESTURE_Z)) {
                        SystemProperties.set(HCT_TPGESTURE_Z_PKGNAME, mPkgAndClsName[0]);
                        SystemProperties.set(HCT_TPGESTURE_Z_CLSNAME, mPkgAndClsName[1]);
                }else if (mCurrGesturePreferenceKey.equals(KEY_GESTURE_V)) {
                        SystemProperties.set(HCT_TPGESTURE_V_PKGNAME, mPkgAndClsName[0]);
                        SystemProperties.set(HCT_TPGESTURE_V_CLSNAME, mPkgAndClsName[1]);
                }else if (mCurrGesturePreferenceKey.equals(KEY_GESTURE_S)) {
                        SystemProperties.set(HCT_TPGESTURE_S_PKGNAME, mPkgAndClsName[0]);
                        SystemProperties.set(HCT_TPGESTURE_S_CLSNAME, mPkgAndClsName[1]);
                } else if (mCurrGesturePreferenceKey.equals(KEY_GESTURE_UP)) {
                        SystemProperties.set(HCT_TPGESTURE_UP_PKGNAME, mPkgAndClsName[0]);
                        SystemProperties.set(HCT_TPGESTURE_UP_CLSNAME, mPkgAndClsName[1]);
                }else if (mCurrGesturePreferenceKey.equals(KEY_GESTURE_LEFT)) {
                        SystemProperties.set(HCT_TPGESTURE_LEFT_PKGNAME, mPkgAndClsName[0]);
                        SystemProperties.set(HCT_TPGESTURE_LEFT_CLSNAME, mPkgAndClsName[1]);
                }else if (mCurrGesturePreferenceKey.equals(KEY_GESTURE_RIGHT)) {
                        SystemProperties.set(HCT_TPGESTURE_RIGHT_PKGNAME, mPkgAndClsName[0]);
                        SystemProperties.set(HCT_TPGESTURE_RIGHT_CLSNAME, mPkgAndClsName[1]);
                }else if (mCurrGesturePreferenceKey.equals(KEY_GESTURE_DOWN)) {
                        SystemProperties.set(HCT_TPGESTURE_DOWN_PKGNAME, mPkgAndClsName[0]);
                        SystemProperties.set(HCT_TPGESTURE_DOWN_CLSNAME, mPkgAndClsName[1]);
                }else if (mCurrGesturePreferenceKey.equals(KEY_GESTURE_M)) {
                        SystemProperties.set(HCT_TPGESTURE_M_PKGNAME, mPkgAndClsName[0]);
                        SystemProperties.set(HCT_TPGESTURE_M_CLSNAME, mPkgAndClsName[1]);
                }
			}
		}
	}

    public static void setPropertiesValue(final String Properties, boolean enable) {
        
        boolean oldValue = SystemProperties.getBoolean(Properties, true);
        if (oldValue != enable) {
            SystemProperties.set(Properties, enable ? "1" : "0");
        }
    }

    private CharSequence getApplicationName(String pkgname, String clsname) { 
        //String applicationName = null;
        //PackageManager packageManager = null;
        CharSequence cs;
        Log.v("gestureunlock", "getApplicationName-pkgname=" + pkgname
                + ",clsname=" + clsname);
        if (pkgname == null || clsname == null) {
            cs = (CharSequence)getResources().getString(R.string.hct_gesture_choose_app);
            return cs; 
        }
        Intent i = new Intent();
        i.setClassName(pkgname, clsname);
        if (getPackageManager().resolveActivity(i, 0) != null) {
            cs = getPackageManager().resolveActivity(i, 0).loadLabel(
                    getPackageManager());
        } else {
            cs = (CharSequence)getResources().getString(R.string.hct_gesture_choose_app);
        }
        Log.v("gestureunlock", "updateSleepKeyLongPref11-cs=" + cs);
        return cs; 
    } 


}
