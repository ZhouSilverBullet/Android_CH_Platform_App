package com.chsdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.caohua.games.biz.task.DoneTaskLogic;

import java.io.File;
import java.util.List;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月30日
 *          <p>
 */
public class ApkUtil {
	public static void installApk(Context context, String filePath) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri localUri;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			localUri = FileProvider.getUriForFile(context, "com.caohua.games.apps.fileprovider", new File(filePath));
			intent.setDataAndType(localUri, "application/vnd.android.package-archive");
		} else {
			localUri = Uri.fromFile(new File(filePath));
			intent.setDataAndType(localUri, "application/vnd.android.package-archive");
		}
		context.startActivity(intent);
	}

	public static int getVersionCode(Context context, String filePath) {
		final PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(filePath, 0);
		if (info == null)
			return 0;

		return info.versionCode;
	}

    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        int versionCode = 0;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

	public static boolean isInstalledCHApp(Context context) {
		boolean hasInstalled = false;
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> list = pm.getInstalledPackages(PackageManager.PERMISSION_GRANTED);
		for (PackageInfo p : list) {
			if (AppContext.getAppContext().getAppPackageName().equals(p.packageName)) {
				hasInstalled = true;
				break;
			}
		}
		return hasInstalled;
	}

	public static boolean checkAppInstalled(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName)) {
			return false;
		}
		try {
			context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_INSTRUMENTATION);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	public static void skipAppMessage(Activity activity, int request) {
		Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
		Uri uri = Uri.fromParts("package", AppContext.getAppContext().getPackageName(), null);
		intent.setData(uri);
		if (request == 0) {
			activity.startActivity(intent);
		} else {
			activity.startActivityForResult(intent, request);
		}
	}

	public static String getUnInstallApkPackageName(Context context, String apkPath) {
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
		if (info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			if (appInfo != null) {
				return appInfo.packageName;
			}
		}
		return null;
	}

	public static void startAPP(Context context, String appPackageName){
		try{
			Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
			context.startActivity(intent);
			new DoneTaskLogic(DoneTaskLogic.TASK_OPEN + "," + DoneTaskLogic.TASK_ENTER_GAME).getDoneTask();
		}catch(Exception e){
			LogUtil.errorLog("startAPP error:" + appPackageName + "," + e.getMessage());
		}
	}
}
