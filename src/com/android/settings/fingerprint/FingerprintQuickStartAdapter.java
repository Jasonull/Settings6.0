package com.android.settings.fingerprint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.R;

public class FingerprintQuickStartAdapter extends BaseAdapter {
	private Context mContext;
	private static final String TAG = "FingerprintQuickStartAdapter";
	private List<AppInfo> mlistAppInfo = null;
	private List<String> mPackageNames = null;
	private int mFingerId = -1;
	private int mCurSelectPosition = -1;
	private boolean mIsDefault = true;
	private String mHasSelectCmp = "";

	public FingerprintQuickStartAdapter(Context context, int fingerID) {
		mContext = context;
		mFingerId = fingerID;
		mHasSelectCmp = FingerprintData.getFingerQuickApplication(context,
				fingerID);
		getSystemApks();
	}

	private boolean isNotNeedProtect(String packageName) {
		String array[] = mContext.getResources().getStringArray(
				R.array.not_need_quick_start);
		boolean flag = false;
		for (int i = 0; i < array.length; i++) {
			if (packageName.equals(array[i])) {
				flag = true;
			}
		}
		return flag;
	}

	private ResolveInfo getResolveInfo(String cmpName) {
		ResolveInfo resolveInfo = null;
		if (cmpName == null || cmpName.equals("") || cmpName.indexOf("|") == -1
				|| cmpName.equals(FingerprintData.ITEM_CAMERA)
				|| cmpName.equals(FingerprintData.ITEM_WECHART))
			return null;
		String[] keyWords = new String[2];
		keyWords = cmpName.split("\\|");
		PackageManager pm = mContext.getPackageManager();
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.addCategory(Intent.CATEGORY_LAUNCHER);
		i.setComponent(new ComponentName(keyWords[0], keyWords[1]));
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(i, 0);
		if(!resolveInfos.isEmpty())
		    resolveInfo = resolveInfos.get(0);
		return resolveInfo;
	}

	private void getSystemApks() {
		PackageManager pm = mContext.getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> resolveInfos = pm
				.queryIntentActivities(mainIntent, 0);
		Collections.sort(resolveInfos,
				new ResolveInfo.DisplayNameComparator(pm));
		if (mlistAppInfo == null) {
			mlistAppInfo = new ArrayList<AppInfo>();
		}
		if (mPackageNames == null) {
			mPackageNames = new ArrayList<String>();
		}
		if (mlistAppInfo != null) {
			mlistAppInfo.clear();
			mPackageNames.clear();

			ResolveInfo select = getResolveInfo(mHasSelectCmp);
			if (select != null) {
				String pkgName = select.activityInfo.packageName;
				String className = select.activityInfo.name;
				String appLabel = (String) select.loadLabel(pm);
				Drawable icon = select.loadIcon(pm);
				AppInfo appInfo = new AppInfo();
				appInfo.setAppLabel(appLabel);
				appInfo.setPkgName(pkgName);
				appInfo.setClassName(className);
				appInfo.setAppIcon(icon);
				mlistAppInfo.add(appInfo);
				mPackageNames.add(pkgName);
			}

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
				if (mPackageNames.indexOf(pkgName) == -1
						&& !isNotNeedProtect(pkgName)) {
					mlistAppInfo.add(appInfo);
				}
				mPackageNames.add(pkgName);
			}
		}
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

	public AppInfo getAppInfo(int position) {
		return mlistAppInfo.get(position);
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
			convertView = View.inflate(mContext,
					R.layout.fingerprint_quickapp_item, null);
			viewHold = new ViewHold();
			viewHold.item_icon = (ImageView) convertView
					.findViewById(R.id.appselect_item_icon);
			viewHold.item_text = (TextView) convertView
					.findViewById(R.id.appselect_item_title);
			viewHold.item_state = (ImageView) convertView
					.findViewById(R.id.appselect_item_state);
			convertView.setTag(viewHold);
		} else {
			viewHold = (ViewHold) convertView.getTag();
		}
		viewHold.item_icon.setImageDrawable(mlistAppInfo.get(position)
				.getAppIcon());
		viewHold.item_text.setText(mlistAppInfo.get(position).getAppLabel());
		if (mIsDefault) {
			if ((mFingerId != -1)
					&& (isAppSelect(mlistAppInfo.get(position).getPkgName(),
							mlistAppInfo.get(position).getClassName()) == mFingerId)) {
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

	private int isAppSelect(String pkg, String cls) {
		if (pkg == null || cls == null || pkg.equals("") || cls.equals(""))
			return -1;
		String componentName = pkg + "|" + cls;
		for (int i = 1; i < 6; i++) {
			String localcomponentName = FingerprintData
					.getFingerQuickApplication(mContext, i);
			if (localcomponentName != null) {
				if (localcomponentName.equals(componentName))
					return i;
			}
		}
		return -1;
	}

	public static class ViewHold {
		ImageView item_icon;
		TextView item_text;
		ImageView item_state;
	}

}
