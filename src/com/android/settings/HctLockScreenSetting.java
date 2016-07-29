package com.android.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

public class HctLockScreenSetting extends BroadcastReceiver {
	private final String ACTION_WALLPAPER_LOCKSCREEN_IMG_PATH = "cn.sh.hct.wallpaperchooser.IMG_PATH";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String path = intent.getExtras().getString(ACTION_WALLPAPER_LOCKSCREEN_IMG_PATH, null);
		if (null == path || "".equals(path)) {
			android.os.SystemProperties.set("persist.sys.lock_flag", "false");
		} else {
			android.os.SystemProperties.set("persist.sys.lock_flag", "true");

			File file = new File(Environment.getDataDirectory() + "/lock.png");
			fileCopy(new File(path), file);
		}
	}

	private void fileCopy(File from, File to) {
		try {
			int byteread = 0;

			if (!from.exists()) {
				return;
			}

			if (to.exists()) {
				to.delete();
			}

			if (to.createNewFile()) {
				to.setWritable(true, false);
				to.setReadable(true, false);

				if (from.exists()) {
					InputStream streamFrom = new FileInputStream(from);
					FileOutputStream streamTo = new FileOutputStream(to);

					byte[] buffer = new byte[256];

					while ((byteread = streamFrom.read(buffer)) != -1) {
						streamTo.write(buffer, 0, byteread);
					}

					streamFrom.close();
					streamTo.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
