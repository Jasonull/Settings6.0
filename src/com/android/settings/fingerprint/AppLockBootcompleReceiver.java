
package com.android.settings.fingerprint;

import com.android.settings.R;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import com.android.internal.widget.LockPatternUtils;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

public class AppLockBootcompleReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
    	boolean applockStart = Settings.System.getInt(context.getContentResolver(), FingerPrintSettings.KEY_FINGERPRINT_UNLOCK_APPLICATION, 0) == 1 ? true : false;
        if (applockStart) {
        	if (intent.getAction().equals("com.applock.activity.ONRESUME")) {
                String intentpkg = intent.getStringExtra("applock_pkgname");
                String intentcls = intent.getStringExtra("applock_clsname");
                KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                LockPatternUtils mLockPatternUtils = new LockPatternUtils(context);
                AppLockDBlite mAppLockDBlite = AppLockDBlite.getInstance(context);
                Log.e("AppLockBootcompleReceiver","-------->intentpkg = " + intentpkg + " intentcls = " + intentcls);
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                String className = am.getRunningTasks(1).get(0).topActivity.getClassName();
                Log.e("AppLockBootcompleReceiver","-------->className = " + className);
                if(mAppLockDBlite.getLockFlag(intentpkg) == 0 && mAppLockDBlite.isDateExits(intentpkg)
                        && !isNotNeedProtect(intentcls,context) && !mKeyguardManager.inKeyguardRestrictedInputMode()
                        && (mLockPatternUtils.getKeyguardStoredPasswordQuality() == DevicePolicyManager.PASSWORD_QUALITY_NUMERIC
                           || mLockPatternUtils.getKeyguardStoredPasswordQuality() == DevicePolicyManager.PASSWORD_QUALITY_NUMERIC_COMPLEX)
                    && !className.equals("com.android.settings.fingerprint.AppLockUnLockActivity")){
                    Intent i = new Intent();
                    i.setClassName("com.android.settings", "com.android.settings.fingerprint.AppLockUnLockActivity");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("app_lock_id", mAppLockDBlite.getExitAppID(intentpkg));
                    i.putExtra("app_lock_pkgname",intentpkg);
                    context.startActivity(i);
                }
            }
        }
    }
    
    private boolean isNotNeedProtect(String className,Context context) {
        String array[] = context.getResources().getStringArray(R.array.not_need_protect_classname);
        boolean flag = false;
        for (int i = 0; i < array.length; i++) {
            if (className.equals(array[i])) {
                flag = true;
            }
        }
        return flag;
    }
}
