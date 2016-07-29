
    package com.android.settings.threecolorled;
    
    import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;
    import android.os.Bundle;
    
    import com.android.settings.R;
    import com.android.settings.SettingsPreferenceFragment;
   
    import android.os.SystemProperties;
   import android.preference.PreferenceScreen;
  import android.content.Intent;
   import android.util.Log;
   import android.R.anim;
   import android.R.bool;
   import android.app.Activity;
   import android.preference.CheckBoxPreference;
   import android.preference.ListPreference;
   import android.preference.Preference;
   import android.preference.Preference.OnPreferenceChangeListener;
  import android.preference.PreferenceGroup;
  import android.preference.PreferenceScreen;
  import android.provider.Settings;
   import android.provider.Settings.System;
   
   
  public class ThreeColorLedSettings extends SettingsPreferenceFragment implements
           Preference.OnPreferenceChangeListener,Preference.OnPreferenceClickListener{
            private static final String TAG = "ThreeColorSettings";
       private static final String KEY_THREECOLOR_MMS = "sms_notify_color";
       private static final String KEY_THREECOLOR_MISSCALL = "misscall_notify_color";
       private static final String KEY_THREECOLOR_LOW_BATTERY = "batterylow_notify_color";
       private static final String KEY_THREECOLOR_FULL_BATTERY = "batteryfull_notify_color";
       private static final String KEY_THREECOLOR_NOTICE_CLOLOR = "notice_color_light";
       private static final String KEY_THREECOLOR_NOTICE_CLOLOR_POWER = "notice_color_light_power";
   
       private static final String PROPERTIES_THREECOLOR_MMS = "persist.sys.nreadmms";
       private static final String PROPERTIES_THREECOLOR_MISSCALL = "persist.sys.misscall";
       private static final String PROPERTIES_THREECOLOR_LOWBATTERY = "persist.sys.lbattery";
      private static final String PROPERTIES_THREECOLOR_FULLBATTERY = "persist.sys.fbattery";
       private ListPreference mmsPreference;
       private ListPreference misscallPreference;
      private ListPreference lBatteryPreference;
      private ListPreference fBatteryPreference;
      private CheckBoxPreference noticecolorlight;
      private CheckBoxPreference noticecolorlightpower;
      private static final String MMS_NOTIFY_ACTION = "hct.intent.action.ACTION_NOTIFY_MMS";
       private static final String MISS_CALL_NOTIFY_ACTION = "hct.intent.action.ACTION_NOTIFY_MISSCALL";
      private static final String LOW_NOTIFY_ACTION = "hct.intent.action.ACTION_NOTIFY_LBATTERY";
       private static final String FULL_NOTIFY_ACTION = "hct.intent.action.ACTION_NOTIFY_FBATTERY";
       private String fullbatterycolor;
       private String lowbatterycolor;
       private String mmscolor;
       private String callColor;
     private static final String PROPERTIES_LED_NOTIFY = "persist.sys.lednotify";
      private static final String PROPERTIES_LED_LOW_BATTERY = "persist.sys.ledchargingoff";
      private boolean mNotifyEnable = true;
     private boolean mLowbatteryEnable = true;
       private static Activity mActivity;
   private static final String CHANGE_BATTERY_LIGHTS_STATUS = "hct.intent.action.ACTION_CHANGE_LIGHTS_STATUS";
    private static final String CHANGE_NOTIFICATION_LIGHTS_STATUS = "hct.intent.action.ACTION_NOTIFICATION_LIGHTS_STATUS";
  

   
      
       private String getColorString(String newString)
       {
         if ("green".equals(newString))
         return getActivity().getResources().getString(R.string.notifycolor_green);
         if ("blue".equals(newString))
           return getActivity().getResources().getString(R.string.notifycolor_blue);
         if ("red".equals(newString))
        return getActivity().getResources().getString(R.string.notifycolor_red);
        return "";
       }
       
       private void updateMmsDescription()
       {
         ListPreference preference = mmsPreference;
         if (preference == null)
           return;
         StringBuffer localStringBuffer = new StringBuffer(preference.getContext().getResources().getString(R.string.sms_color_summary));
        localStringBuffer.append(" " + getColorString(mmscolor));
        // Log.i("jsx", "updateMmsDescription : " + localStringBuffer);
        preference.setSummary(localStringBuffer.toString());
      }
    
     
      private void updateMissCallDescription()
       {
         ListPreference preference = misscallPreference;
         if (preference == null)
           return;
         StringBuffer localStringBuffer = new StringBuffer(preference.getContext().getResources().getString(R.string.call_color_summary));
         localStringBuffer.append(" " + getColorString(callColor));
       // Log.i("jsx", "updateMissCallDescription : " + localStringBuffer);
       preference.setSummary(localStringBuffer.toString());
       }
  
      private void updateBartteryLowDescription()
      {
        ListPreference preference = lBatteryPreference;
       if (preference == null)
          return;
       StringBuffer localStringBuffer = new StringBuffer(preference.getContext().getResources().getString(R.string.batterylow_color_summary));
         localStringBuffer.append(" " + getColorString(lowbatterycolor));
        Log.i("jsx", "updateBartteryLowDescription : " + localStringBuffer);
         preference.setSummary(localStringBuffer.toString());
      }
       
       private void updateBartteryFullDescription()
       {
         ListPreference preference = fBatteryPreference;
        if (preference == null)
          return;
        StringBuffer localStringBuffer = new StringBuffer(preference.getContext().getResources().getString(R.string.batteryfull_color_summary));
        localStringBuffer.append(" " + getColorString(fullbatterycolor));
        Log.i("jsx", "updateBartteryFullDescription : " + localStringBuffer);
        preference.setSummary(localStringBuffer.toString());
     }
   
    
      @Override
      public void onCreate(Bundle icicle) {
          super.onCreate(icicle);
          addPreferencesFromResource(R.xml.hct_threecolor_setting);
          
          mmsPreference = (ListPreference) findPreference(KEY_THREECOLOR_MMS);
          misscallPreference = (ListPreference) findPreference(KEY_THREECOLOR_MISSCALL);
          lBatteryPreference = (ListPreference) findPreference(KEY_THREECOLOR_LOW_BATTERY);
          fBatteryPreference = (ListPreference) findPreference(KEY_THREECOLOR_FULL_BATTERY);
          
          noticecolorlight = (CheckBoxPreference)findPreference(KEY_THREECOLOR_NOTICE_CLOLOR);
          noticecolorlightpower = (CheckBoxPreference)findPreference(KEY_THREECOLOR_NOTICE_CLOLOR_POWER);
           mNotifyEnable = SystemProperties
                .getBoolean(PROPERTIES_LED_NOTIFY, true);
            mLowbatteryEnable = SystemProperties.getBoolean(
                PROPERTIES_LED_LOW_BATTERY, true);
         noticecolorlightpower.setChecked(mLowbatteryEnable);
          noticecolorlight.setChecked(mNotifyEnable);
          mmscolor = SystemProperties.get(PROPERTIES_THREECOLOR_MMS, "red");   
        Log.v("jsx","hahahhaha" + mmscolor);
          mmsPreference.setValue(mmscolor);
          updateMmsDescription();
          mmsPreference.setOnPreferenceChangeListener(this);
       
          callColor = SystemProperties.get(PROPERTIES_THREECOLOR_MISSCALL, "red");      
          misscallPreference.setValue(callColor);
         updateMissCallDescription();
         misscallPreference.setOnPreferenceChangeListener(this);
          
         lowbatterycolor = SystemProperties.get(PROPERTIES_THREECOLOR_LOWBATTERY, "red");     
          lBatteryPreference.setValue(lowbatterycolor);
          updateBartteryLowDescription();
         lBatteryPreference.setOnPreferenceChangeListener(this);
         fullbatterycolor = SystemProperties.get(PROPERTIES_THREECOLOR_LOWBATTERY, "green");         
         fBatteryPreference.setValue(fullbatterycolor);
          fBatteryPreference.setValue(String.valueOf(fullbatterycolor));
          updateBartteryFullDescription();
          fBatteryPreference.setOnPreferenceChangeListener(this);
        
           noticecolorlight.setOnPreferenceClickListener(this);
           noticecolorlightpower.setOnPreferenceClickListener(this); 
           mmsPreference.setEnabled(noticecolorlight.isChecked());
           misscallPreference.setEnabled(noticecolorlight.isChecked());
           lBatteryPreference.setEnabled(noticecolorlightpower.isChecked());
           fBatteryPreference.setEnabled(noticecolorlightpower.isChecked());
        
     
      }
  
      
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
            final String key = preference.getKey();
            if (KEY_THREECOLOR_MMS.equals(key)) {
                   SystemProperties.set(PROPERTIES_THREECOLOR_MMS, newValue.toString());
                    mmscolor = newValue.toString();
                updateMmsDescription();
               Intent intent = new Intent(MMS_NOTIFY_ACTION);
                getActivity().sendBroadcast(intent);
            //   Log.i("jsx", "mms sendBroadcast");
            }
            else if (KEY_THREECOLOR_MISSCALL.equals(key)) {
                   SystemProperties.set(PROPERTIES_THREECOLOR_MISSCALL, newValue.toString());
                callColor = newValue.toString();
                updateMissCallDescription();
               Intent intent = new Intent(MISS_CALL_NOTIFY_ACTION);
                getActivity().sendBroadcast(intent);
               // Log.i("jsx","PROPERTIES_THREECOLOR_MISSCALL  " + SystemProperties.get(PROPERTIES_THREECOLOR_MISSCALL, "red"));
              //  Log.i("jsx","newValue.toString  " + newValue.toString());
              //  Log.i("jsx", "misscall sendBroadcast");
           }
            else if (KEY_THREECOLOR_LOW_BATTERY.equals(key)) {
                    SystemProperties.set(PROPERTIES_THREECOLOR_LOWBATTERY, newValue.toString());
                lowbatterycolor = newValue.toString();
                updateBartteryLowDescription();
                Intent intent = new Intent(LOW_NOTIFY_ACTION);
               getActivity().sendBroadcast(intent);
              //  Log.i("jsx", "LOWBATTERY sendBroadcast");
            }
          else if (KEY_THREECOLOR_FULL_BATTERY.equals(key)) {
                   SystemProperties.set(PROPERTIES_THREECOLOR_FULLBATTERY, newValue.toString());
               fullbatterycolor = newValue.toString();
                updateBartteryFullDescription();
                Intent intent = new Intent(FULL_NOTIFY_ACTION);
                getActivity().sendBroadcast(intent);
           //     Log.i("jsx", "LOWBATTERY sendBroadcast");  
            }/**else if (KEY_THREECOLOR_NOTICE_CLOLOR.equals(key)){
         Log.d("haocheng",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>1");
                SystemProperties.set(PROPERTIES_LED_NOTIFY,
                    noticecolorlight.isChecked() ? "true" : "false");
                 Intent intent = new Intent(CHANGE_NOTIFICATION_LIGHTS_STATUS);
                 getActivity().sendBroadcast(intent);
                     }else if (KEY_THREECOLOR_NOTICE_CLOLOR_POWER.equals(key)){
                  Log.d("haocheng",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2");
         SystemProperties.set(PROPERTIES_LED_LOW_BATTERY,
                    noticecolorlightpower.isChecked() ? "true" : "false");
      
                 Intent intent = new Intent(CHANGE_BATTERY_LIGHTS_STATUS);
                 getActivity().sendBroadcast(intent);


                  }*/
          
  
         return true;
      }
  
      @Override
      public boolean onPreferenceClick (Preference preference){
        if(noticecolorlight==preference){
           mmsPreference.setEnabled(noticecolorlight.isChecked());
           misscallPreference.setEnabled(noticecolorlight.isChecked());
         
                SystemProperties.set(PROPERTIES_LED_NOTIFY,
                    noticecolorlight.isChecked() ? "true" : "false");
                 Intent intent = new Intent(CHANGE_NOTIFICATION_LIGHTS_STATUS);
                 getActivity().sendBroadcast(intent);
                Log.d("haocheng",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>1");
           }else if (noticecolorlightpower==preference){
              lBatteryPreference.setEnabled(noticecolorlightpower.isChecked());
              fBatteryPreference.setEnabled(noticecolorlightpower.isChecked());
              
         SystemProperties.set(PROPERTIES_LED_LOW_BATTERY,
                    noticecolorlightpower.isChecked() ? "true" : "false");
      
                 Intent intent = new Intent(CHANGE_BATTERY_LIGHTS_STATUS);
                 getActivity().sendBroadcast(intent);
              Log.d("haocheng",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>2");
             }
          return false;
      }


      
  }


