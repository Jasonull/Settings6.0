package com.android.settings.visitor;

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
import android.preference.CheckBoxPreference;

import com.android.settings.SettingsPreferenceFragment;

import android.os.SystemProperties;
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
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class VisitorSettings extends SettingsPreferenceFragment implements
DialogInterface.OnClickListener {
    private static final String TAG = "VisitorSettings";
   private CheckBoxPreference mVisitorPreference;
   private DialogInterface mVisitorDialog;
	private final static String PROPERTIES_VISITOR_ON = "persist.sys.visitor_mode";

    @Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.hct_visitor_mode);
         mVisitorPreference = (CheckBoxPreference)findPreference("key_visitor_mode");
                 
	
    }

  	@Override
	public void onResume() {
		super.onResume();
		initPreferences();
	}

  	private void initPreferences(){
  		mVisitorPreference.setChecked(android.os.SystemProperties.getBoolean(PROPERTIES_VISITOR_ON,false));
  		
  	}
    
	private void warnVisitorMode() {
		// TODO: DialogFragment?
		Log.e("jsx","hahah warnVisitorMode");
		mVisitorDialog = new AlertDialog.Builder(getActivity()).setTitle(
                getResources().getString(R.string.hct_visitor_mode_string))
				.setIcon(com.android.internal.R.drawable.ic_dialog_alert)
				.setMessage(getResources().getString(R.string.visitor_mode_warning))
				.setPositiveButton(android.R.string.yes, this) 
				 .setNegativeButton(android.R.string.no, null)
                .show();
	}
	
	
   
     public void onClick(DialogInterface dialog, int which) {
      // TODO Auto-generated method stub
  		 if (dialog == mVisitorDialog && which == DialogInterface.BUTTON_POSITIVE) {
    	  			Intent intent = new Intent().setClass(getActivity(),
    					VistorSetLockPassword.class);
    		  			(getActivity()).startActivity(intent);
    		  		
    		  		   if (mVisitorPreference != null) {
    		  			if (SystemProperties.getBoolean("persist.sys.visitor_mode", false)){
    		  			 mVisitorPreference.setChecked(true);
    		  			 }	
    		            }	
    		  			mVisitorDialog.dismiss();
    		}
  		 
     }
   
	private void confirmPassword() {
		
		Intent intent = new Intent().setClass(getActivity(),
				VistorUnlockPassword.class);
	  			(getActivity()).startActivity(intent);
	  		
	  			if (SystemProperties.getBoolean("persist.sys.visitor_mode", false)){
	  				if (mVisitorPreference != null) {
  		  			 mVisitorPreference.setChecked(true);
  		  			 }			
	  			}
	  			else
	  			{
	  				if (mVisitorPreference != null) {
	  		  			 mVisitorPreference.setChecked(false);
	  		  			 }			
	  			}
		}
     
			   

     @Override
     public void onDestroy() {
        super.onDestroy();
        if (mVisitorDialog != null) {
        	mVisitorDialog.dismiss();
        }
     }
  	
     private boolean handlePreference(PreferenceScreen preferenceScreen, Preference preference){
        
         if (mVisitorPreference == preference) {
				if (mVisitorPreference.isChecked()) {
					mVisitorPreference.setChecked(false);
					warnVisitorMode();
		        } else {
		        	confirmPassword();
		        	 }
		
             return true;
         }
         return false;
     }
    
     @Override
     public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
         boolean ret = handlePreference(preferenceScreen, preference);
         if (!ret) {
            ret = super.onPreferenceTreeClick(preferenceScreen, preference);
         }
         return ret;
    }


	
	
}
