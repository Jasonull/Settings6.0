package com.android.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class HctAllAppActivity extends Activity implements
		AdapterView.OnItemClickListener {
	private ListView mListView;
	private AllAppAdapter adapter;
	ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.allapp_main);
		mListView = (ListView) findViewById(R.id.allapp_lisview);
		PackageManager pm = getPackageManager();
		// List<PackageInfo> packs = pm.getInstalledPackages(0);

		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent,
				PackageManager.GET_INTENT_FILTERS);
		Collections.sort(resolveInfos,
				new ResolveInfo.DisplayNameComparator(pm));
		if (items != null) {
			items.clear();
			for (ResolveInfo reInfo : resolveInfos) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				String activityName = reInfo.activityInfo.name;
				String pkgName = reInfo.activityInfo.packageName;
				String appLabel = (String) reInfo.loadLabel(pm);
				Drawable icon = reInfo.loadIcon(pm);
                
				Intent launchIntent = new Intent();
				launchIntent.setComponent(new ComponentName(pkgName,
						activityName));

				map.put("icon", icon);
				map.put("appName", appLabel);
				map.put("packageName", pkgName + "|" + activityName);
				items.add(map);
			}
		}

		adapter = new AllAppAdapter(this, items, R.layout.allapp_list_item, new String[] {
				"icon", "appName", "packageName" }, new int[] { R.id.icon,
				R.id.appName, R.id.packageName });
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String appname = null;
		String pkgname = null;
		// Object mObject = null;
		HashMap<String, Object> data = (HashMap<String, Object>) mListView
				.getItemAtPosition(position);
		// mObject = mListView.getItemAtPosition(position);
		Log.v("gesture", "AllAppActivity-onItemClick-data=" + data);
		if (data != null) {
			Intent intent = new Intent();

			pkgname = data.get("packageName").toString();
			appname = data.get("appName").toString();
			Log.v("gesture", "onItemClick-appname----1=" + appname + ",pkgname="
					+ pkgname);
			intent.putExtra("appname", appname);
			intent.putExtra("pkgname", pkgname);
			setResult(RESULT_OK, intent);
		} else {
			setResult(RESULT_CANCELED);
		}
		
		finish();
	}
}

class AllAppAdapter extends SimpleAdapter {
	private int[] appTo;
	private String[] appFrom;
	private ViewBinder appViewBinder;
	private List<? extends Map<String, ?>> appData;
	private int appResource;
	private LayoutInflater appInflater;

	public AllAppAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		appData = data;
		appResource = resource;
		appFrom = from;
		appTo = to;
		appInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView, parent,
				appResource);
	}

	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		View v;

		if (convertView == null) {
			v = appInflater.inflate(resource, parent, false);
			final int[] to = appTo;
			final int count = to.length;
			final View[] holder = new View[count];

			for (int i = 0; i < count; i++) {
				holder[i] = v.findViewById(to[i]);
			}

			v.setTag(holder);
		} else {
			v = convertView;
		}

		bindView(position, v);
		return v;
	}

	private void bindView(int position, View view) {
		final Map dataSet = appData.get(position);

		if (dataSet == null) {
			return;
		}

		final ViewBinder binder = appViewBinder;
		final View[] holder = (View[]) view.getTag();
		final String[] from = appFrom;
		final int[] to = appTo;
		final int count = to.length;

		for (int i = 0; i < count; i++) {
			final View v = holder[i];

			if (v != null) {
				final Object data = dataSet.get(from[i]);
				String text = data == null ? "" : data.toString();

				if (text == null) {
					text = "";
				}

				boolean bound = false;

				if (binder != null) {
					bound = binder.setViewValue(v, data, text);
				}

				if (!bound) {
					if (v instanceof TextView) {
						setViewText((TextView) v, text);
					} else if (v instanceof ImageView) {
						setViewImage((ImageView) v, (Drawable) data);
					} else {
						throw new IllegalStateException(
								v.getClass().getName()
										+ " is not a "
										+ "view that can be bounds by this SimpleAdapter");
					}
				}
			}
		}
	}

	public void setViewImage(ImageView v, Drawable value) {
		v.setImageDrawable(value);
	}
}