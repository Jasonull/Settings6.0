package com.android.settings.fingerprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.R;
public class QuickStartAdapter extends BaseAdapter {
    private Context mContext;
    private static final String TAG = "QuickStartAdapter";
    private List<AppInfo> mlistAppInfo = null;
    private List<String> mPackageNames = null;
    private int mCurSelectPosition = -1;
    private boolean mIsDefault = true;
    public QuickStartAdapter(Context context) {
		// TODO Auto-generated constructor stub
        mContext = context;
        getSystemApks();
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
            AppInfo appInfoNone = new AppInfo();
            appInfoNone.setAppLabel(mContext.getResources().getString(R.string.ftp_quick_start_none));
            appInfoNone.setPkgName("");
            appInfoNone.setClassName("");
            mlistAppInfo.add(appInfoNone);
            mPackageNames.add("");
            for (ResolveInfo reInfo : resolveInfos) {
                String pkgName = reInfo.activityInfo.packageName;
                String className = reInfo.activityInfo.name;
                String appLabel = (String) reInfo.loadLabel(pm);
                Drawable icon = reInfo.loadIcon(pm);
                AppInfo appInfo = new AppInfo();
                appInfo.setAppLabel(appLabel);
                appInfo.setPkgName(pkgName);
                appInfo.setClassName(className);
                appInfo.setAppIcon(icon);

                Log.v(TAG, "packagename = " + pkgName);
                mlistAppInfo.add(appInfo);
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
            convertView = View.inflate(mContext, R.layout.quickstart_list_item, null);
            viewHold = new ViewHold();
            viewHold.item_icon = (ImageView) convertView.findViewById(R.id.item_icon);
            viewHold.item_text = (TextView) convertView.findViewById(R.id.item_text);
            viewHold.item_state = (ImageView) convertView.findViewById(R.id.appselect_item_state);
            convertView.setTag(viewHold); 
        }else{
            viewHold = (ViewHold) convertView.getTag();
        }
        viewHold.item_icon.setImageDrawable(mlistAppInfo.get(position).getAppIcon());
        viewHold.item_text.setText(mlistAppInfo.get(position).getAppLabel());
        if (mIsDefault) {
        	String info = getTitleFromDatabase("fingerprint_quickapplication" + mCurSelectPosition);
        	if(info == null && position == 0){
        		viewHold.item_state.setVisibility(View.VISIBLE);
        	}else if (isAppSelect(mlistAppInfo.get(position).getPkgName(),mlistAppInfo.get(position).getClassName(), info)) {
				viewHold.item_state.setVisibility(View.VISIBLE);
			} else {
				viewHold.item_state.setVisibility(View.INVISIBLE);
			}
		} else {
			if (mCurSelectPosition == position) {
				viewHold.item_state.setVisibility(View.VISIBLE);
			} else {
				viewHold.item_state.setVisibility(View.INVISIBLE);
			}
		}
        return convertView;
	}
	private boolean isAppSelect(String pkg, String cls, String info) {
		if(pkg == null || cls == null){
			return false;
		}
		if(info == null){
			return false;
		}
		if(pkg.equals("") && cls.equals("") && info.equals("")){
			return true;
		}
		String componentName = pkg + "/" + cls;
		if(componentName.equals(info)){
			return true;
		}
		return false;
	}
	
	private String getTitleFromDatabase(String key){
		String title = Settings.System.getString(mContext.getContentResolver(), key);
		return title;
	}
	
	public void setCurSelectPosition(int curSelectPosition) {
		mCurSelectPosition = curSelectPosition;
	}

	public int getCurSelectPosition() {
		return mCurSelectPosition;
	}
	
	public void setDefault(boolean mode) {
		mIsDefault = mode;
	}
	
	public static class ViewHold {
		ImageView item_icon;
		TextView item_text;
		ImageView item_state;
	}
}
