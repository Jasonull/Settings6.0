
package com.android.settings.fingerprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.R;
public class AppLockAdapter extends BaseAdapter {
    private Context mContext;

    private static final String TAG = "privatelock_tag";

    private List<AppInfo> mlistAppInfo = null;

    private List<String> mPackageNames = null;

    private AppLockDBlite mAppLockDBlite;

    public AppLockAdapter(Context context) {
        mContext = context;
        getSystemApks();
        mAppLockDBlite = AppLockDBlite.getInstance(context);
    }

    private boolean isNotNeedProtect(String packageName) {
        String array[] = mContext.getResources().getStringArray(R.array.not_need_protect);
        boolean flag = false;
        for (int i = 0; i < array.length; i++) {
            if (packageName.equals(array[i])) {
                flag = true;
            }
        }

        return flag;
    }


    private void getSystemApks() {
        PackageManager pm = mContext.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));
        if (mlistAppInfo == null) {
            mlistAppInfo = new ArrayList<AppInfo>();
        }
        if (mPackageNames == null) {
            mPackageNames = new ArrayList<String>();
        }
        if (mlistAppInfo != null) {
            mlistAppInfo.clear();
            mPackageNames.clear();
            for (ResolveInfo reInfo : resolveInfos) {
                String pkgName = reInfo.activityInfo.packageName;
                String appLabel = (String) reInfo.loadLabel(pm);
                Drawable icon = reInfo.loadIcon(pm);
                AppInfo appInfo = new AppInfo();
                appInfo.setAppLabel(appLabel);
                appInfo.setPkgName(pkgName);
                appInfo.setAppIcon(icon);

                Log.v(TAG, "packagename = " + pkgName);
                if (mPackageNames.indexOf(pkgName) == -1 && !isNotNeedProtect(pkgName)
                        && !pkgName.equals("com.android.applock")) {
                    mlistAppInfo.add(appInfo);
                }
                mPackageNames.add(pkgName);
            }
        }
    }

    public AppInfo getAppInfo(int postition) {
        return mlistAppInfo.get(postition);
    }

    public List<String> getList() {
        return mPackageNames;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mlistAppInfo.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHold viewHold = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.applock_list_item, null);
            viewHold = new ViewHold();
            viewHold.item_icon = (ImageView) convertView.findViewById(R.id.item_icon);
            viewHold.item_text = (TextView) convertView.findViewById(R.id.item_text);
            viewHold.item_lock = (ImageView) convertView.findViewById(R.id.lock);
            convertView.setTag(viewHold); 
        }else{
            viewHold = (ViewHold) convertView.getTag();
        }
        viewHold.item_icon.setImageDrawable(mlistAppInfo.get(position).getAppIcon());
        viewHold.item_text.setText(mlistAppInfo.get(position).getAppLabel());
        if (mAppLockDBlite.isDateExits(mlistAppInfo.get(position).getPkgName())) {
            viewHold.item_lock.setImageResource(R.drawable.applock_ic_lock);
        }else{
            viewHold.item_lock.setImageResource(R.drawable.applock_ic_unlock);
        }
        return convertView;
    }

    public static class AppInfo {
        private String appLabel = null;

        private Drawable appIcon;

        private String pkgName = null;

        public AppInfo() {
        }

        public String getAppLabel() {
            return appLabel;
        }

        public void setAppLabel(String appName) {
            this.appLabel = appName;
        }

        public Drawable getAppIcon() {
            return appIcon;
        }

        public void setAppIcon(Drawable appIcon) {
            this.appIcon = appIcon;
        }

        public String getPkgName() {
            return pkgName;
        }

        public void setPkgName(String pkgName) {
            this.pkgName = pkgName;
        }
    }

    public static class ViewHold {
        ImageView item_icon;

        TextView item_text;

        ImageView item_lock;
    }

}
