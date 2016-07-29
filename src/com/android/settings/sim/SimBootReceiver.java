/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.sim;

import com.android.settings.R;
import com.android.settings.Settings.SimSettingsActivity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.telephony.SubscriptionManager.OnSubscriptionsChangedListener;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.os.SystemProperties;
import com.android.internal.telephony.TelephonyIntents;

import com.mediatek.settings.cdma.CdmaUtils;
import com.mediatek.settings.ext.ISettingsMiscExt;
import com.android.settings.Utils;
/// M: Add for CT 6M.
import com.mediatek.settings.FeatureOption;
import com.mediatek.settings.UtilsExt;
import com.mediatek.settings.sim.Log;

import java.util.List;

public class SimBootReceiver extends BroadcastReceiver implements DialogInterface.OnKeyListener {
    private static final String TAG = "SimBootReceiver";
    private static final int SLOT_EMPTY = -1;
    private static final int NOTIFICATION_ID = 1;
    private static final String SHARED_PREFERENCES_NAME = "sim_state";
    private static final String SLOT_PREFIX = "sim_slot_";
    private static final int INVALID_SLOT = -2; // Used when upgrading from K to LMR1

    private SharedPreferences mSharedPreferences = null;
    private TelephonyManager mTelephonyManager;
    private Context mContext;
    private SubscriptionManager mSubscriptionManager;

    /* added by hct lijunyi, 2015-06-06, for HCT_SIM_SETTINGS */
    int mState = SubscriptionManager.EXTRA_VALUE_NOCHANGE;
    boolean mLauncher = false, mSimUpdate = false;

    final String LAUNCHER_BOOT_COMPLETE = "com.android.launcher3.action.FIRST_LOAD_COMPLETE";
    /* end of added by hct lijunyi, 2015-06-06, for HCT_SIM_SETTINGS */

    // / @}
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()... action: " + intent.getAction());
        int detectedType = intent.getIntExtra(
                SubscriptionManager.INTENT_KEY_DETECT_STATUS, 0);
        if (detectedType == SubscriptionManager.EXTRA_VALUE_NOCHANGE) {
            return;
        }
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        mContext = context;
        mSubscriptionManager = SubscriptionManager.from(mContext);
        mSharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        // mSubscriptionManager.addOnSubscriptionsChangedListener(mSubscriptionListener);

        /* added by hct lijunyi, 2015-06-06, for HCT_SIM_SETTINGS */
        String action = intent.getAction();
        Resources res = context.getResources();

        boolean mShowSimSettings = res.getBoolean(R.bool.hct_config_show_sim_setting);
        boolean mFirstLoad = SystemProperties.getBoolean("persist.sys.hct_first_load", true);

        Log.d(TAG, "mShowSimSettings = " + mShowSimSettings + "; mFirstLoad = " + mFirstLoad);
        Log.d(TAG, "11 mLauncher = " + mLauncher + "; mSimUpdate = " + mSimUpdate);

        if (mShowSimSettings) {
            if (LAUNCHER_BOOT_COMPLETE.equals(action)) {
                mLauncher = true;
                mSimUpdate = mSharedPreferences.getBoolean("mSimUpdate", false);

                mSharedPreferences.edit().putBoolean("mLauncher", mLauncher).commit();
            } 

            if (TelephonyIntents.ACTION_SUBINFO_RECORD_UPDATED.equals(action)) {
                mSimUpdate = true;
                mLauncher = mSharedPreferences.getBoolean("mLauncher", false);
                mState = intent.getExtras().getInt(SubscriptionManager.INTENT_KEY_DETECT_STATUS, 0);

                mSharedPreferences.edit().putBoolean("mSimUpdate", mSimUpdate).commit();
            }

            Log.d(TAG, "mFirstLoad 22 mLauncher = " + mLauncher + "; mSimUpdate = " + mSimUpdate);

            if (1 == mTelephonyManager.getSimCount() && TelephonyManager.SIM_STATE_READY == mTelephonyManager.getSimState()) {
                String imsi = mTelephonyManager.getSubscriberId();
                boolean noDialog = networkNoDialog(imsi);

                if (mFirstLoad) {
                    if (mLauncher && mSimUpdate) {
                        SystemProperties.set("persist.sys.hct_first_load", "0");

                        if (noDialog) {
                        } else {
                            createDialog(context, res.getString(R.string.hct_sim_apn_title), res.getString(R.string.hct_sim_apn_content), res.getString(R.string.hct_sim_apn_btn_change),
                                    res.getString(R.string.hct_sim_apn_btn_close), btnChange, btnClose);
                        }

                        createDialog(context, res.getString(R.string.hct_sim_data_title), res.getString(R.string.hct_sim_data_content), res.getString(R.string.hct_sim_data_btn_disable),
                                res.getString(R.string.hct_sim_data_btn_enable), btnDisable, btnEnable);
                    }
                } else {
                    Log.d(TAG, "mState = " + mState);

                    if (SubscriptionManager.EXTRA_VALUE_NEW_SIM == mState || SubscriptionManager.EXTRA_VALUE_REMOVE_SIM == mState) {
                        if (noDialog) {
                        } else {
                            createDialog(context, res.getString(R.string.hct_sim_apn_title), res.getString(R.string.hct_sim_apn_content), res.getString(R.string.hct_sim_apn_btn_change),
                                    res.getString(R.string.hct_sim_apn_btn_close), btnChange, btnClose);
                        }
                    }
                }
            }
        }
        /* end of added by hct lijunyi, 2015-06-06, for HCT_SIM_SETTINGS */

        detectChangeAndNotify();
    }

    private void detectChangeAndNotify() {
        final int numSlots = mTelephonyManager.getSimCount();
        final boolean isInProvisioning = Settings.Global.getInt(mContext.getContentResolver(),
                Settings.Global.DEVICE_PROVISIONED, 0) == 0;
        boolean notificationSent = false;
        int numSIMsDetected = 0;
        int lastSIMSlotDetected = -1;
        Log.d(TAG,"detectChangeAndNotify numSlots = " + numSlots + 
                " isInProvisioning = " + isInProvisioning);
        // Do not create notifications on single SIM devices or when provisiong.
        if (numSlots < 2 || isInProvisioning) {
            return;
        }

        // We wait until SubscriptionManager returns a valid list of Subscription informations
        // by checking if the list is empty.
        // This is not completely correct, but works for most cases.
        // See Bug: 18377252
        List<SubscriptionInfo> sil = mSubscriptionManager.getActiveSubscriptionInfoList();
        if (sil == null || sil.size() < 1) {
            Log.d(TAG,"do nothing since no cards inserted");
            return;
        }

        /// M: for [C2K 2 SIM Warning]
        boolean newSimInserted = false;

        for (int i = 0; i < numSlots; i++) {
            final SubscriptionInfo sir = Utils.findRecordBySlotId(mContext, i);
            Log.d(TAG,"sir = " + sir);
            final String key = SLOT_PREFIX+i;
            final int lastSubId = getLastSubId(key);
            if (sir != null) {
                numSIMsDetected++;
                final int currentSubId = sir.getSubscriptionId();
                if (lastSubId == INVALID_SLOT) {
                    createNotification(mContext); //chenyichong add to fix bug.
                    setLastSubId(key, currentSubId);
                    notificationSent = true; //chenyichong add to fix bug.

                    /// M: for [C2K 2 SIM Warning]
                    newSimInserted = true;
                } else if (lastSubId != currentSubId) {
                    createNotification(mContext);
                    setLastSubId(key, currentSubId);
                    notificationSent = true;
                    /// M: for [C2K 2 SIM Warning]
                    newSimInserted = true;
                }
                lastSIMSlotDetected = i;
                Log.d(TAG,"key = " + key + " lastSubId = " + lastSubId + 
                        " currentSubId = " + currentSubId + 
                        " lastSIMSlotDetected = " + lastSIMSlotDetected);
            } else if (lastSubId != SLOT_EMPTY) {
                createNotification(mContext);
                setLastSubId(key, SLOT_EMPTY);
                notificationSent = true;
            }
        }
        Log.d(TAG, "notificationSent = " + notificationSent + " numSIMsDetected = "
                + numSIMsDetected + " newSimInserted = " + newSimInserted);
        if (notificationSent) {

//chenyichong add begin, fix bug: #31792, if sim changed, and now simslot > 1, we need disable the other simslot's data function.
            Log.d("Bob.chen", "detectChangeAndNotify. numSIMsDetected = " + numSIMsDetected);
            if (numSIMsDetected > 1) {
                disableDataForOtherSubscriptions(mContext, sil);
            }
//chenyichong add end.

            Intent intent = new Intent(mContext, SimDialogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (numSIMsDetected == 1) {
                Log.d("Bob.chen", "detectChangeAndNotify. 111111. lastSIMSlotDetected = " + lastSIMSlotDetected);
                intent.putExtra(SimDialogActivity.DIALOG_TYPE_KEY, SimDialogActivity.PREFERRED_PICK);
                intent.putExtra(SimDialogActivity.PREFERRED_SIM, lastSIMSlotDetected);
            } else if (!isDefaultDataSubInserted()) {
                Log.d("Bob.chen", "detectChangeAndNotify. 222222. ");
                    //chenyichong modify for all project, we disabled for home and back key when DATA_PICK
                    //intent.putExtra(SimDialogActivity.DIALOG_TYPE_KEY, SimDialogActivity.DATA_PICK);
                    intent.putExtra(SimDialogActivity.DIALOG_TYPE_KEY, SimDialogActivity.DATA_PICK_NO_RET);
            }
            if (isEnableShowSimDialog()) {
                mContext.startActivity(intent);
            }
        }
        /// M: for [C2K 2 SIM Warning] @{
        if (newSimInserted) {
            CdmaUtils.startCdmaWaringDialog(mContext, numSIMsDetected);
        }
        /// @}
    }

    private int getLastSubId(String strSlotId) {
        return mSharedPreferences.getInt(strSlotId, INVALID_SLOT);
    }

    private void setLastSubId(String strSlotId, int value) {
        Editor editor = mSharedPreferences.edit();
        editor.putInt(strSlotId, value);
        editor.commit();
    }

    private void createNotification(Context context){
        final Resources resources = context.getResources();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_sim_card_alert_white_48dp)
                .setColor(resources.getColor(R.color.sim_noitification))
                .setContentTitle(resources.getString(R.string.sim_notification_title))
                .setContentText(resources.getString(R.string.sim_notification_summary));
        /// M: only for OP09 UIM/SIM changes.
        changeNotificationString(context, builder);
        Intent resultIntent = new Intent(context, SimSettingsActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static void cancelNotification(Context context) {
        NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private final OnSubscriptionsChangedListener mSubscriptionListener =
            new OnSubscriptionsChangedListener() {
        @Override
        public void onSubscriptionsChanged() {
            detectChangeAndNotify();
        }
    };

    private boolean isDefaultDataSubInserted() {
        boolean isInserted = false;
        int defaultDataSub = SubscriptionManager.getDefaultDataSubId();
        if (SubscriptionManager.isValidSubscriptionId(defaultDataSub)) {
            final int numSlots = mTelephonyManager.getSimCount();
            for (int i = 0; i < numSlots; ++i) {
                final SubscriptionInfo sir = Utils.findRecordBySlotId(mContext, i);
                if (sir != null) {
                    if (sir.getSubscriptionId() == defaultDataSub) {
                        isInserted = true;
                        break;
                    }
                }
            }
        }
        Log.d(TAG, "defaultDataSub: " + defaultDataSub + ", isInsert: " + isInserted);
        return isInserted;
    }

    /**
     * only for OP09 UIM/SIM changes.
     *
     * @param context the context.
     * @param builder the notification builder.
     */
    private void changeNotificationString(
                    Context context,
                    NotificationCompat.Builder builder) {
        Resources resources = context.getResources();
        String title = resources.getString(R.string.sim_notification_title);
        String text = resources.getString(R.string.sim_notification_summary);

        ISettingsMiscExt miscExt = UtilsExt.getMiscPlugin(context);
        title = miscExt.customizeSimDisplayString(
                            title,
                            SubscriptionManager.INVALID_SUBSCRIPTION_ID);
        text = miscExt.customizeSimDisplayString(
                            text,
                            SubscriptionManager.INVALID_SUBSCRIPTION_ID);
        builder.setContentTitle(title);
        builder.setContentText(text);
    }


//chenyichong add begin.
    private void setMobileDataEnabled(final Context context, int subId, boolean enabled) {
        Log.d("Bob.chen", "SimBootReceiver. setMobileDataEnabled(). subId = " + subId + ". enabled = " + enabled);
        TelephonyManager.from(context).setDataEnabled(subId, enabled);
        //mMobileDataEnabled.put(String.valueOf(subId), enabled);
        //updatePolicy(false);
    }

    private void disableDataForOtherSubscriptions(final Context context, final List<SubscriptionInfo>  mSubInfoList) {
        int defaultDataSub = SubscriptionManager.getDefaultDataSubId();
        Log.d("Bob.chen", "SimBootReceiver. disableDataForOtherSubscriptions(). defaultDataSub = " + defaultDataSub + ". mSubInfoList = " + mSubInfoList);
        if (mSubInfoList != null) {
            for (SubscriptionInfo subInfo : mSubInfoList) {
                Log.d("Bob.chen", "SimBootReceiver. subInfo.getSubscriptionId()  = " + subInfo.getSubscriptionId() );
                if (subInfo.getSubscriptionId() != defaultDataSub) {
                    setMobileDataEnabled(context, subInfo.getSubscriptionId(), false);
                }
            }
        }
    }

//chenyichong add end.


    /* added by hct lijunyi, 2015-06-06, for HCT_SIM_SETTINGS */
    DialogInterface.OnClickListener btnDisable = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub
            int subId = SubscriptionManager.getDefaultDataSubId();
            mTelephonyManager.setDataEnabled(subId, false);
        }
    };

    DialogInterface.OnClickListener btnEnable = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub
            int subId = SubscriptionManager.getDefaultDataSubId();
            mTelephonyManager.setDataEnabled(subId, true);
        }
    };

    DialogInterface.OnClickListener btnChange = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub
            Intent intent = new Intent(Settings.ACTION_APN_SETTINGS);
            intent.putExtra(":settings:show_fragment_as_subsetting", true);
            intent.putExtra("sub_id", SubscriptionManager.getDefaultDataSubId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    };

    DialogInterface.OnClickListener btnClose = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub
            dialog.dismiss();
        }
    };

    void createDialog(Context context, String title, String content, CharSequence strLeft, CharSequence strRight, DialogInterface.OnClickListener btnLeft, DialogInterface.OnClickListener btnRight) {
        AlertDialog.Builder builder = new Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        AlertDialog dialog = builder.create();

        dialog.setTitle(title);
        dialog.setMessage(content);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, strLeft, btnLeft);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, strRight, btnRight);

        dialog.setOnKeyListener(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
        dialog.show();
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return true;
    }

    boolean networkNoDialog(String imsi) {
        boolean noDialog = false;
        
        if (null != imsi && (imsi.startsWith("20416") || imsi.startsWith("20420") || imsi.startsWith("23430"))) {
            noDialog = true;
        }

        return noDialog;
    }
    /* end of added by hct lijunyi, 2015-06-06, for HCT_SIM_SETTINGS */

    private boolean isEnableShowSimDialog() {
        /// M: modify for CT 6M. Disable data pick dialog and preferred sim dialog. @ {
        boolean isEnable = false;
        if (!FeatureOption.MTK_CT6M_SUPPORT) {
            ISettingsMiscExt plugin = UtilsExt.getMiscPlugin(mContext);
            isEnable = plugin.isFeatureEnable();
        }
        /// @ }
        Log.d(TAG,"isEnableShowSimDialog isEnable = " + isEnable);
        return isEnable;
    }

}
