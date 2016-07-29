package com.android.settings;

import android.os.Bundle;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import android.os.SystemProperties;
import android.preference.PreferenceScreen;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
//import android.widget.Switch;
import android.widget.CompoundButton;
import android.app.Activity;
import android.view.LayoutInflater;
import android.content.Context;
import android.app.ActionBar;
import android.view.Gravity;
import android.preference.PreferenceActivity;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.content.Intent;
import android.content.IntentFilter;

public class HctTelephonyMotion extends SettingsPreferenceFragment implements
		Preference.OnPreferenceClickListener/*, Preference.OnPreferenceChangeListener */{

	private CheckBoxPreference mTurnSilence;
	private CheckBoxPreference mAnswerSwing;
        private CheckBoxPreference mFlitAnswer;

	private final static String PROPERTIES_TURN_SILENCE = "persist.sys.hctturnsilence";
	private final static String PROPERTIES_ANSWER_SWING = "persist.sys.hctanswerswing";
	private final static String PROPERTIES_FLITANSWER = "persist.sys.hgdautoanswer";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.hct_system_motion);

		final PreferenceScreen parent = getPreferenceScreen();

		mTurnSilence = (CheckBoxPreference)parent.findPreference("key_hct_turn_silence");
		mAnswerSwing = (CheckBoxPreference)parent.findPreference("key_hct_answer_by_swing");
		mFlitAnswer = (CheckBoxPreference)parent.findPreference("key_hct_flit_slient_answer");

		if(mTurnSilence != null){
		mTurnSilence.setOnPreferenceClickListener(this);
		mTurnSilence.setChecked(SystemProperties.getBoolean(PROPERTIES_TURN_SILENCE, false));
		}
		if(mAnswerSwing != null){
		mAnswerSwing.setOnPreferenceClickListener(this);
		mAnswerSwing.setChecked(SystemProperties.getBoolean(PROPERTIES_ANSWER_SWING, false));
		}
		if(mFlitAnswer!=null){
		mFlitAnswer.setOnPreferenceClickListener(this);
		mFlitAnswer.setChecked(SystemProperties.getBoolean(PROPERTIES_FLITANSWER, false));
		}

		   if(getResources().getBoolean(R.bool.config_hct_remove_turnsilen))
		         {
		             parent.removePreference(mTurnSilence);
		          }

		   if(getResources().getBoolean(R.bool.config_hct_remove_answerswing))
		         {
		             parent.removePreference(mAnswerSwing);
		          }          
 
		  if(getResources().getBoolean(R.bool.config_hct_remove_mflitanswer))
		         {
		             parent.removePreference(mFlitAnswer);
		          }         

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(mTurnSilence == preference){
			SystemProperties.set(PROPERTIES_TURN_SILENCE, mTurnSilence.isChecked() ? "true" : "false");
			String act = "hct.intent.stop.silentservice";
                         if(mTurnSilence.isChecked()){
                                 act = "hct.intent.start.silentservice";
                         }
                         Intent intent = new Intent(act);
                         getActivity().sendBroadcast(intent);
		}else if(mAnswerSwing == preference){
			Log.v("HctGestureSystemSetting", "mAnswerSwing");

			SystemProperties.set(PROPERTIES_ANSWER_SWING, mAnswerSwing.isChecked() ? "true" : "false");
			String act = "hct.intent.stop.flitbyhandservice";
				
				if(mAnswerSwing.isChecked()){
					act = "hct.intent.start.flitbyhandservice";
				}
				Intent intent = new Intent(act);
				getActivity().sendBroadcast(intent);
		}else if(mFlitAnswer == preference){
			Log.v("HctGestureSystemSetting", "mFlitAnswer");
			SystemProperties.set(PROPERTIES_FLITANSWER, mFlitAnswer.isChecked() ? "true" : "false");
		}
	    return true;
	}
/*
	@Override
	public boolean onPreferenceChange(Preference arg0, Object newValue) {
	    return true;
	}
*/
}
