package com.android.settings.fingerprint;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.settings.R;

public class QuickStartPrefixActivity extends Activity implements
		OnItemClickListener {

	private ListView mListView;
	private static final int ITEM_NUM = 4;
    private static final int ITEM_UNLOCK = 0;
    private static final int ITEM_CAMERA = 1;
    private static final int ITEM_WEICHAT = 2;
    private static final int ITEM_MORE = 3;
	private SimpleAdapter mSimpleAdapter;
	private int mFingerId;
	private boolean mIsEditMode;
	private boolean mIsDefaultMode = true;
	private HashMap<String, Boolean> states = new HashMap<String, Boolean>();
	private AlertDialog mAlertDialog;
	private boolean mIsSubCamera = false;
	private TextView mSubCamera;
	private TextView mMainCamera;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quickstartprefixlayout);

		Intent i = getIntent();
		mFingerId = i.getIntExtra("index", -1);
		mIsEditMode = i.getBooleanExtra("fingerEdit", false);

		mListView = (ListView) findViewById(R.id.prefix_list);
		mSimpleAdapter = new SimpleAdapter(this);
		mListView.setAdapter(mSimpleAdapter);
		mListView.setOnItemClickListener(this);
		mContext = this;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		InitTitleView();
	}
	
	private void InitTitleView() {
		int color = this.GetCurrentTheme(getPackageName());
		ColorDrawable cd = new ColorDrawable(getResources().getColor(color));
		if (getActionBar() != null)
			getActionBar().setBackgroundDrawable(cd);
		getWindow().setStatusBarColor(getResources().getColor(color));
	}
	
	class SimpleAdapter extends BaseAdapter {
		private Context mContext;
		private int mCurSelectPosition = -1;

		public SimpleAdapter(Context context) {
			// TODO Auto-generated constructor stub
			mContext = context;
		}

		public void setCurSelectPosition(int curSelectPosition) {
			mCurSelectPosition = curSelectPosition;
		}

		public int getCurSelectPosition() {
			return mCurSelectPosition;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return ITEM_NUM;
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
						R.layout.quickstartprefixlayoutitem, null);
				viewHold = new ViewHold();
				viewHold.item_icon = (ImageView) convertView
						.findViewById(R.id.prefix_item_icon);
				viewHold.item_text = (TextView) convertView
						.findViewById(R.id.prefix_item_title);
				viewHold.item_radioButton = (RadioButton) convertView
						.findViewById(R.id.prefix_item_radio);
				viewHold.item_radioButton.setClickable(false);
				convertView.setTag(viewHold);
			} else {
				viewHold = (ViewHold) convertView.getTag();
			}
			String componentName = FingerprintData.getFingerQuickApplication(
					mContext, mFingerId);
			if(componentName == null){
				componentName = "";
			}
			Log.e("fuck", "--------->componentName = " + componentName);
			switch (position) {
			case ITEM_UNLOCK:
				viewHold.item_icon
						.setImageResource(R.drawable.ftp_quick_unlock);
				viewHold.item_text
						.setText(R.string.ftp_quick_start_setting_item_unlock);
				viewHold.item_radioButton.setVisibility(View.VISIBLE);
				break;
			case ITEM_CAMERA:
				viewHold.item_icon
						.setImageResource(R.drawable.ftp_quick_camera);
				viewHold.item_text
						.setText(R.string.ftp_quick_start_setting_item_flashshot);
				viewHold.item_radioButton.setVisibility(View.VISIBLE);
				break;
			case ITEM_WEICHAT:
				viewHold.item_icon
						.setImageResource(R.drawable.ftp_quick_mm);
				viewHold.item_text
						.setText(R.string.ftp_quick_start_setting_item_wechat);
				viewHold.item_radioButton.setVisibility(View.VISIBLE);
				break;
			case ITEM_MORE:
				viewHold.item_icon
						.setImageResource(R.drawable.ftp_quick_more);
				viewHold.item_text
						.setText(R.string.ftp_quick_start_setting_item_more);
				//viewHold.item_radioButton.setVisibility(View.GONE);
				break;
			default:
				break;
			}
			if (mIsDefaultMode) {
				if ((componentName.equals(FingerprintData.ITEM_LOCK)) && (position == ITEM_UNLOCK)) {
					viewHold.item_radioButton.setChecked(true);
					mCurSelectPosition = position;
				} else if (componentName.equals(FingerprintData.ITEM_CAMERA) && (position == ITEM_CAMERA)) {
					viewHold.item_radioButton.setChecked(true);
					mCurSelectPosition = position;
				} else if (componentName.equals(FingerprintData.ITEM_WECHART)
						&& (position == ITEM_WEICHAT)) {
					viewHold.item_radioButton.setChecked(true);
					mCurSelectPosition = position;
				} else if((!componentName.equals(FingerprintData.ITEM_LOCK)) 
                            && (!componentName.equals(FingerprintData.ITEM_CAMERA))
                            && (!componentName.equals(FingerprintData.ITEM_WECHART))
                            && position == ITEM_MORE){
					viewHold.item_radioButton.setChecked(true);
				} else {
					viewHold.item_radioButton.setChecked(false);                 
                }
			} else {
				if (mCurSelectPosition == position) {
					viewHold.item_radioButton.setChecked(true);
				} else {
					viewHold.item_radioButton.setChecked(false);
				}
			}
			return convertView;
		}
	}

	public static class ViewHold {
		ImageView item_icon;
		TextView item_text;
		RadioButton item_radioButton;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (mSimpleAdapter.getCurSelectPosition() == position)
			return;
		mIsDefaultMode = false;
		switch (position) {
		case ITEM_UNLOCK:
			mSimpleAdapter.setCurSelectPosition(position);
			FingerprintData.setFingerQuickApplication(this, mFingerId,
					FingerprintData.ITEM_LOCK);
			FingerprintData.setFingerSummary(this, mFingerId, "");
			mSimpleAdapter.notifyDataSetChanged();
            finish();
			break;
		case ITEM_CAMERA:
			int cameraOccupancy = checkoutAppOccupancy(FingerprintData.ITEM_CAMERA);
			String camerafingerName = FingerprintData.fingerTitle(mContext,cameraOccupancy);
			if (cameraOccupancy == -1) {
				createDialog(position);
			} else {
				String camera_tip = getResources().getString(
						R.string.ftp_quick_start_setting_app_occupancy,
						camerafingerName);
				Toast.makeText(this, camera_tip, Toast.LENGTH_SHORT).show();
			}
			break;
		case ITEM_WEICHAT:
			int wechartOccupancy = checkoutAppOccupancy(FingerprintData.ITEM_WECHART);
			String wechartfingerName = FingerprintData.fingerTitle(mContext,wechartOccupancy);
			if (wechartOccupancy == -1) {
				mSimpleAdapter.setCurSelectPosition(position);
				FingerprintData.setFingerQuickApplication(this, mFingerId,
						FingerprintData.ITEM_WECHART);
				String valueWechat = getResources().getString(
						R.string.ftp_quick_start_setting_item_wechat);
				FingerprintData.setFingerSummary(this, mFingerId, valueWechat);
				mSimpleAdapter.notifyDataSetChanged();
                finish();
			} else {
				String wechart_tip = getResources().getString(
						R.string.ftp_quick_start_setting_app_occupancy,
						wechartfingerName);
				Toast.makeText(this, wechart_tip, Toast.LENGTH_SHORT).show();
			}
			break;
		case ITEM_MORE:
			mSimpleAdapter.setCurSelectPosition(position);
			if (!mIsEditMode) {
				FingerprintData
						.setFingerQuickApplication(this, mFingerId, null);
				mSimpleAdapter.notifyDataSetChanged();
			}
			enterQuckStartAppSele(mFingerId);
		    finish();
			break;
		}
	}

	private void enterQuckStartAppSele(int fingerIndex) {
		Intent i = new Intent();
		i.setClassName(this,
				"com.android.settings.fingerprint.FingerprintQuickStartSelectApplication");
		i.putExtra("index", fingerIndex);
		startActivity(i);
	}

	private int checkoutAppOccupancy(String componentName) {
		String mComponentName;
		for (int i = 1; i < 6; i++) {
			mComponentName = FingerprintData.getFingerQuickApplication(this,i);
			if (mComponentName != null) {
				if (mComponentName.equals(componentName))
					return i;
			}
		}
		return -1;
	}

	private void createDialog(final int position) {
		AlertDialog.Builder builder = new Builder(this);
		// builder.setMessage(getResources().getString(R.string.simple_launcher_quit_confirm));
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

						mSimpleAdapter.setCurSelectPosition(position);
						FingerprintData.setFingerQuickApplication(mContext,
								mFingerId, FingerprintData.ITEM_CAMERA);
						String valueCamera = getResources()
								.getString(
										R.string.ftp_quick_start_setting_item_flashshot);
						FingerprintData.setFingerSummary(mContext, mFingerId,
								valueCamera);
						mSimpleAdapter.notifyDataSetChanged();
						if (mIsSubCamera) {
							FingerprintData.saveToDatabase(mContext,
									"ftp_camera_id", 1);
						} else {
							FingerprintData.saveToDatabase(mContext,
									"ftp_camera_id", 0);
						}
						mAlertDialog.dismiss();
                        finish();
					}
				});

		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						mAlertDialog.dismiss();
					}
				});
		View view = View.inflate(this, R.layout.ftp_camear_select_dialog, null);
		mSubCamera = (TextView) view.findViewById(R.id.ftp_subcamera);
		mSubCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mIsSubCamera = true;
				Drawable subCameraDrawableSelect = getResources().getDrawable(
						R.drawable.ftp_subcamera_select);
				subCameraDrawableSelect.setBounds(0, 0,
						subCameraDrawableSelect.getMinimumWidth(),
						subCameraDrawableSelect.getMinimumHeight());
				mSubCamera.setCompoundDrawables(null, subCameraDrawableSelect,
						null, null);

				Drawable mainCameraDrawableNormal = getResources().getDrawable(
						R.drawable.ftp_camera_normal);
				mainCameraDrawableNormal.setBounds(0, 0,
						mainCameraDrawableNormal.getMinimumWidth(),
						mainCameraDrawableNormal.getMinimumHeight());
				mMainCamera.setCompoundDrawables(null,
						mainCameraDrawableNormal, null, null);
			}
		});
		mMainCamera = (TextView) view.findViewById(R.id.ftp_maincamera);
		mMainCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mIsSubCamera = false;
				Drawable subCameraDrawableNormal = getResources().getDrawable(
						R.drawable.ftp_subcamera_normal);
				subCameraDrawableNormal.setBounds(0, 0,
						subCameraDrawableNormal.getMinimumWidth(),
						subCameraDrawableNormal.getMinimumHeight());
				mSubCamera.setCompoundDrawables(null, subCameraDrawableNormal,
						null, null);

				Drawable mainCameraDrawableSelect = getResources().getDrawable(
						R.drawable.ftp_camera_select);
				mainCameraDrawableSelect.setBounds(0, 0,
						mainCameraDrawableSelect.getMinimumWidth(),
						mainCameraDrawableSelect.getMinimumHeight());
				mMainCamera.setCompoundDrawables(null,
						mainCameraDrawableSelect, null, null);
			}
		});

		int cameraId = FingerprintData.isDataExsitWithIndex(mContext,
				"ftp_camera_id");

		if (cameraId == 1) {
			Drawable subCameraDrawableSelect = getResources().getDrawable(
					R.drawable.ftp_subcamera_select);
			subCameraDrawableSelect.setBounds(0, 0,
					subCameraDrawableSelect.getMinimumWidth(),
					subCameraDrawableSelect.getMinimumHeight());
			mSubCamera.setCompoundDrawables(null, subCameraDrawableSelect,
					null, null);

			Drawable mainCameraDrawableNormal = getResources().getDrawable(
					R.drawable.ftp_camera_normal);
			mainCameraDrawableNormal.setBounds(0, 0,
					mainCameraDrawableNormal.getMinimumWidth(),
					mainCameraDrawableNormal.getMinimumHeight());
			mMainCamera.setCompoundDrawables(null, mainCameraDrawableNormal,
					null, null);
		}

		builder.setView(view);
		mAlertDialog = builder.create();
		mAlertDialog.show();
		mAlertDialog.setCanceledOnTouchOutside(false);
		mAlertDialog.setCancelable(false);
		mAlertDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

}
