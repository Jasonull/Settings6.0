package com.android.settings.fingerprint;

import android.content.Context;
import android.provider.Settings;
import com.android.settings.R;
public class FingerprintData {
	public static final int ENROLL = 0;
	public static final int EDITOR = 1;
	public static final int FINGERPRINT_1 = 1;
	public static final int FINGERPRINT_2 = 2;
	public static final int FINGERPRINT_3 = 3;
	public static final int FINGERPRINT_4 = 4;
	public static final int FINGERPRINT_5 = 5;
	public static final int FINGERPRINT_MAX = 5;
	public static final int DATA_EXSIT = 1;
	public static final int DATA_NOT_EXSIT = 0;
	public static int mMaxFinger = FINGERPRINT_MAX;
	
	////egistec start
	public final static String ITEM_LOCK = "";
	public final static String ITEM_CAMERA = "com.android.fastcamera|com.android.fastcamera.MainActivity";
	public final static String ITEM_WECHART = "com.tencent.mm|com.tencent.mm.ui.LauncherUI";
	////egistec end
	
	public static int isDataExsitWithIndex(Context context, String index){
		return Settings.System.getInt(context.getContentResolver(), index, 0);
	}
	
	public static int getIdtWithIndex(Context context, String index){
		return Settings.System.getInt(context.getContentResolver(), index, -1);
	}
  
	public static String getDataWithIndex(Context context, String index){
		return Settings.System.getString(context.getContentResolver(), index);
	}
  
	public static void saveToDatabase(Context context, String Key, int Value){
		Settings.System.putInt(context.getContentResolver(), Key, Value);
	}
  
	public static void setTitleDatabase(Context context, String Key, String Value){
		Settings.System.putString(context.getContentResolver(), Key, Value);
	}

	public static void setMaxFingerCount(int max){
		mMaxFinger = max;
	}
	
	public static int getMaxFingerCount(){
		return mMaxFinger;
	}
	
	public static int fingerCount(Context context){
		int count = 0;
		if(isDataExsitWithIndex(context, "fingerprint_1") != DATA_NOT_EXSIT) count ++;
		if(isDataExsitWithIndex(context, "fingerprint_2") != DATA_NOT_EXSIT) count ++;
		if(isDataExsitWithIndex(context, "fingerprint_3") != DATA_NOT_EXSIT) count ++;
		if(isDataExsitWithIndex(context, "fingerprint_4") != DATA_NOT_EXSIT) count ++;
		if(isDataExsitWithIndex(context, "fingerprint_5") != DATA_NOT_EXSIT) count ++;
		if(count >= mMaxFinger){
			count = mMaxFinger;
		}
		return count;
	}
 	
	public static int fingerId(Context context){
		if(isDataExsitWithIndex(context, "fingerprint_1") == DATA_NOT_EXSIT){
			return FINGERPRINT_1;
		}else if(isDataExsitWithIndex(context, "fingerprint_2") == DATA_NOT_EXSIT){
			return FINGERPRINT_2;
		}else if(isDataExsitWithIndex(context, "fingerprint_3") == DATA_NOT_EXSIT){
			return FINGERPRINT_3;
		}else if(isDataExsitWithIndex(context, "fingerprint_4") == DATA_NOT_EXSIT){
			return FINGERPRINT_4;
		}else if(isDataExsitWithIndex(context, "fingerprint_5") == DATA_NOT_EXSIT){
			return FINGERPRINT_5;
		}else{
			return mMaxFinger;
		}
	}
	
	public static String fingerTitle(Context context, int index) {
		String name = null;
		switch (index) {
		case FINGERPRINT_1:
			name = getDataWithIndex(context, "fingerprint_title1");
			if (name == null || "".equals(name)) {
				name = context.getString(R.string.fingerprint_title_1);
			}
			break;
		case FINGERPRINT_2:
			name = getDataWithIndex(context, "fingerprint_title2");
			if (name == null || "".equals(name)) {
				name = context.getString(R.string.fingerprint_title_2);
			}
			break;
		case FINGERPRINT_3:
			 name = getDataWithIndex(context, "fingerprint_title3");
			if (name == null || "".equals(name)) {
				name = context.getString(R.string.fingerprint_title_3);
			}
			break;
		case FINGERPRINT_4:
			 name = getDataWithIndex(context, "fingerprint_title4");
			if (name == null || "".equals(name)) {
				name = context.getString(R.string.fingerprint_title_4);
			}
			break;
		case FINGERPRINT_5:
			 name = getDataWithIndex(context, "fingerprint_title5");
			if (name == null || "".equals(name)) {
				name = context.getString(R.string.fingerprint_title_5);
			}
			break;
		default:
			break;
		}
		return name;
	}
	
	public static void setFingerTitle(Context context, int index, String title){
		switch (index) {
		case FINGERPRINT_1:
			setTitleDatabase(context, "fingerprint_title1", title);
			break;
		case FINGERPRINT_2:
			setTitleDatabase(context, "fingerprint_title2", title);
			break;
		case FINGERPRINT_3:
			setTitleDatabase(context, "fingerprint_title3", title);
			break;
		case FINGERPRINT_4:
			setTitleDatabase(context, "fingerprint_title4", title);
			break;
		case FINGERPRINT_5:
			setTitleDatabase(context, "fingerprint_title5", title);
			break;
		default:
			break;
		}
	}
	
	public static String fingerSummary(Context context, int index) {
		String summary = "";
		switch (index) {
		case FINGERPRINT_1:
			summary = getDataWithIndex(context, "fingerprint_summary1");
			if(!("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_et300", "0")))){
				if (summary == null || "".equals(summary)) {
					summary = context.getString(R.string.fingerprint_summary);
				}
			}
			break;
		case FINGERPRINT_2:
			summary = getDataWithIndex(context, "fingerprint_summary2");
			if(!("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_et300", "0")))){
				if (summary == null || "".equals(summary)) {
					summary = context.getString(R.string.fingerprint_summary);
				}
			}
			break;
		case FINGERPRINT_3:
			summary = getDataWithIndex(context, "fingerprint_summary3");
			if(!("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_et300", "0")))){
				if (summary == null || "".equals(summary)) {
					summary = context.getString(R.string.fingerprint_summary);
				}
			}
			break;
		case FINGERPRINT_4:
			summary = getDataWithIndex(context, "fingerprint_summary4");
			if(!("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_et300", "0")))){
				if (summary == null || "".equals(summary)) {
					summary = context.getString(R.string.fingerprint_summary);
				}
			}
			break;
		case FINGERPRINT_5:
			summary = getDataWithIndex(context, "fingerprint_summary5");
			if(!("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_et300", "0")))){
				if (summary == null || "".equals(summary)) {
					summary = context.getString(R.string.fingerprint_summary);
				}
			}
			break;
		default:
			break;
		}
		if("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_et300", "0"))){
			if (summary == null) {
				summary = "";
			}
		}
		return summary;
	}
	
	public static void setFingerSummary(Context context, int index, String summary){
		switch (index) {
		case FINGERPRINT_1:
			setTitleDatabase(context, "fingerprint_summary1", summary);
			break;
		case FINGERPRINT_2:
			setTitleDatabase(context, "fingerprint_summary2", summary);
			break;
		case FINGERPRINT_3:
			setTitleDatabase(context, "fingerprint_summary3", summary);
			break;
		case FINGERPRINT_4:
			setTitleDatabase(context, "fingerprint_summary4", summary);
			break;
		case FINGERPRINT_5:
			setTitleDatabase(context, "fingerprint_summary5", summary);
			break;
		default:
			break;
		}
	}
	
	public static void setFingerQuickApplication(Context context, int index, String info){
		switch (index) {
		case FINGERPRINT_1:
			setTitleDatabase(context, "fingerprint_quickapplication1", info);
			break;
		case FINGERPRINT_2:
			setTitleDatabase(context, "fingerprint_quickapplication2", info);
			break;
		case FINGERPRINT_3:
			setTitleDatabase(context, "fingerprint_quickapplication3", info);
			break;
		case FINGERPRINT_4:
			setTitleDatabase(context, "fingerprint_quickapplication4", info);
			break;
		case FINGERPRINT_5:
			setTitleDatabase(context, "fingerprint_quickapplication5", info);
			break;
		default:
			break;
		}
	}
	
	public static String getFingerQuickApplication(Context context, int index){
		String info = "";
		switch (index) {
		case FINGERPRINT_1:
			info = getDataWithIndex(context, "fingerprint_quickapplication1");
			break;
		case FINGERPRINT_2:
			info = getDataWithIndex(context, "fingerprint_quickapplication2");
			break;
		case FINGERPRINT_3:
			info = getDataWithIndex(context, "fingerprint_quickapplication3");
			break;
		case FINGERPRINT_4:
			info = getDataWithIndex(context, "fingerprint_quickapplication4");
			break;
		case FINGERPRINT_5:
			info = getDataWithIndex(context, "fingerprint_quickapplication5");
			break;
		default:
			break;
		}
		if("1".equals(android.os.SystemProperties.get("ro.hct_fingerprint_et300", "0"))){
			if (info == null) {
				info = "";
			}
		}
		return info;
	}
	
	public static void setFingerprintToFinger(Context context, int index, int fingerNum){
		switch (index) {
		case FINGERPRINT_1:
			saveToDatabase(context, "fingerprint_finger1", fingerNum);
			break;
		case FINGERPRINT_2:
			saveToDatabase(context, "fingerprint_finger2", fingerNum);
			break;
		case FINGERPRINT_3:
			saveToDatabase(context, "fingerprint_finger3", fingerNum);
			break;
		case FINGERPRINT_4:
			saveToDatabase(context, "fingerprint_finger4", fingerNum);
			break;
		case FINGERPRINT_5:
			saveToDatabase(context, "fingerprint_finger5", fingerNum);
			break;
		default:
			break;
		}
	}
	
	public static int getFingerprintToFinger(Context context, int index){
		int value = -1;
		switch (index) {
		case FINGERPRINT_1:
			value = getIdtWithIndex(context, "fingerprint_finger1");
			break;
		case FINGERPRINT_2:
			value = getIdtWithIndex(context, "fingerprint_finger2");
			break;
		case FINGERPRINT_3:
			value = getIdtWithIndex(context, "fingerprint_finger3");
			break;
		case FINGERPRINT_4:
			value = getIdtWithIndex(context, "fingerprint_finger4");
			break;
		case FINGERPRINT_5:
			value = getIdtWithIndex(context, "fingerprint_finger5");
			break;
		default:
			break;
		}
		return value;
	}
	
	public static void setFingerprintOnlyUnlock(Context context, int index, int fingerNum){
		switch (index) {
		case FINGERPRINT_1:
			saveToDatabase(context, "fingerprint_unlock1", fingerNum);
			break;
		case FINGERPRINT_2:
			saveToDatabase(context, "fingerprint_unlock2", fingerNum);
			break;
		case FINGERPRINT_3:
			saveToDatabase(context, "fingerprint_unlock3", fingerNum);
			break;
		case FINGERPRINT_4:
			saveToDatabase(context, "fingerprint_unlock4", fingerNum);
			break;
		case FINGERPRINT_5:
			saveToDatabase(context, "fingerprint_unlock5", fingerNum);
			break;
		default:
			break;
		}
	}
	
	public static int getFingerprintOnlyUnlock(Context context, int index){
		int value = -1;
		switch (index) {
		case FINGERPRINT_1:
			value = getIdtWithIndex(context, "fingerprint_unlock1");
			break;
		case FINGERPRINT_2:
			value = getIdtWithIndex(context, "fingerprint_unlock2");
			break;
		case FINGERPRINT_3:
			value = getIdtWithIndex(context, "fingerprint_unlock3");
			break;
		case FINGERPRINT_4:
			value = getIdtWithIndex(context, "fingerprint_unlock4");
			break;
		case FINGERPRINT_5:
			value = getIdtWithIndex(context, "fingerprint_unlock5");
			break;
		default:
			break;
		}
		return value;
	}	
	
}
