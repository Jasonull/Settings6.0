package com.android.settings.floatview;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.os.ServiceManager;
import com.hct.android.floatview.IFloatViewService;


public class FloatViewSettings extends PreferenceFragment{
	private static final String TAG = "FloatViewSettings";

	private static final String KEY_FLOATVIEW_SETTINGS = "floatview_settings";
	private static final String KEY_FLOATVIEW_CHECKBOX = "floatview_checkbox";

	private Preference mFloatViewSettingsPreference;
	private CheckBoxPreference mFloatViewCheckBoxPreference;
	private static final String SERVERACTION = "hct.FloatView.action.START";

	
	private Context mContext;
    IFloatViewService myService = null;

    ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            myService = null;
            Log.e("Bob.chen", "FloatViewSettings. onServiceDisconnected. myService = " + myService);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            myService = IFloatViewService.Stub.asInterface(service);
            Log.e("Bob.chen", "FloatViewSettings. onServiceConnected. myService = " + myService);
        }
    };

	
	@Override
	public void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.floatview_settings);

		mFloatViewSettingsPreference = (Preference) findPreference(KEY_FLOATVIEW_SETTINGS);
		mFloatViewCheckBoxPreference = (CheckBoxPreference) findPreference(KEY_FLOATVIEW_CHECKBOX);
		
		mContext = getActivity();
		if(mContext.getResources().getBoolean(R.bool.config_hct_remove_floatview) && mFloatViewSettingsPreference != null){
			getPreferenceScreen().removePreference(mFloatViewSettingsPreference);
		}

//    boolean isSuccess = getActivity().bindService(
//            new Intent("com.hct.android.floatview.IFloatViewService"), serviceConnection,
//            Context.BIND_AUTO_CREATE);

    
    Intent intent = new Intent();
    intent.setClassName("com.hct.android.floatview", "com.hct.android.floatview.FloatViewService");
    boolean isSuccess = getActivity().bindService( intent, serviceConnection,   Context.BIND_AUTO_CREATE);
    Log.e("Bob.chen", "FloatViewSettings. isSuccess = " + isSuccess);

        
	}

	@Override
	public void onResume() {
		super.onResume();
		//getActivity().actionbarMade(getActivity(), true);
		initAllPreferences();
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	private void initAllPreferences() {
		
		final boolean enable = Settings.System.getInt(getActivity().getContentResolver(), "FloatView",  
            getActivity().getResources().getInteger(com.android.internal.R.integer.hct_floatview_function)) != 0;

		mFloatViewCheckBoxPreference.setChecked(enable);

	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// TODO Auto-generated method stub
		boolean flag = false;                    
	        
		if (mFloatViewSettingsPreference == preference) {

            Intent mIntent = new Intent();   
            ComponentName comp = new ComponentName(  
                 "com.hct.android.floatview",  
                "com.hct.android.floatview.SplashScreenActivity");
            mIntent.setComponent(comp);   
            //mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);  //chenyichong disabled for avoid blackscreen a little.
            startActivity(mIntent);
            
			flag = true;
		} else if (mFloatViewCheckBoxPreference == preference) {
			if (preference instanceof CheckBoxPreference) {
				CheckBoxPreference pref = (CheckBoxPreference) preference;
				int state = pref.isChecked() ? 1 : 0;
                                Intent intent = new Intent(SERVERACTION);
				Settings.System.putInt(getActivity().getContentResolver(),"FloatView", state);

                Log.e("Bob.chen", "onPreferenceTreeClick = " + Settings.System.getInt(getActivity().getContentResolver(), "FloatView",  1));

                            if((state == 1) ? true : false){
                                try {
                                    Log.e("Bob.chen", "FloatViewSettings. onClick. startService");

                                    Log.e("Bob.chen", "FloatViewSettings. onPreferenceTreeClick00. myService = " + myService);
                                    
                                    myService.startService();
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }


                            }
                            else{

                                try {
                                    Log.e("Bob.chen", "FloatViewSettings. onClick. stopService");

                                    Log.e("Bob.chen", "FloatViewSettings. onPreferenceTreeClick11. myService = " + myService);
                                    
                                    myService.stopSelf();
                                } catch (RemoteException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                            }



                
				if (mFloatViewCheckBoxPreference != null) {
					mFloatViewCheckBoxPreference.setChecked(pref.isChecked());
				}

			}
			flag = true;
		} 
		
		if(flag){
			return true;
		}else{
			return super.onPreferenceTreeClick(preferenceScreen, preference);
		}
	}

	
}
