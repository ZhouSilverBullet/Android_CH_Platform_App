package com.chsdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Map;

public class SpUtil {
	private static final String SP_NAME = "chsdk_sp";
	private static final String SP_NAME_PUSH = "chsdk_sp_push";
	
	private static SharedPreferences getSp(Context context) {
		return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
	}
	
	private static SharedPreferences getPushSp(Context context) {
		return context.getSharedPreferences(SP_NAME_PUSH, Context.MODE_PRIVATE);
	}
	
	public static void deletePush(Context context, String key) {
		SharedPreferences sp = getPushSp(context);
		if (sp == null)
			return;

		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		applyEditor(editor);
	}
	
	public static void savePushMsg(Context context, String id, String user, String time, String type) {
		if (TextUtils.isEmpty(id) || TextUtils.isEmpty(time))
			return;
		
		SharedPreferences sp = getPushSp(context);
		if (sp == null)
			return;
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(id + "_" + user, time + "_" + type + "_" + System.currentTimeMillis());
		applyEditor(editor);
	}

	public static void saveNotUserPushMsg(Context context, String id, String time, String type) {
		if (TextUtils.isEmpty(id) || TextUtils.isEmpty(time))
			return;

		SharedPreferences sp = getPushSp(context);
		if (sp == null)
			return;
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(id + "_", time + "_" + type + "_" + System.currentTimeMillis());
		applyEditor(editor);
	}
	
	public static Map<String, ?> getPushContent(Context context) {
		SharedPreferences sp = getPushSp(context);
		if (sp == null)
			return null;
		return sp.getAll();
	}

	public static void putLong(Context context, String key, long value) {
		SharedPreferences sp = getSp(context);
		if (sp == null)
			return;
		SharedPreferences.Editor editor = sp.edit();
		editor.putLong(key, value);
		applyEditor(editor);
	}

	public static long getLong(Context context, String key, long defaultValue) {
		SharedPreferences sp = getSp(context);
		if (sp == null)
			return defaultValue;
		return sp.getLong(key, defaultValue);
	}
	
	public static void putInt(Context context, String key, int value) {
		SharedPreferences sp = getSp(context);
		if (sp == null)
			return;
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(key, value);
		applyEditor(editor);
	}

	public static int getInt(Context context, String key, int defaultValue) {
		SharedPreferences sp = getSp(context);
		if (sp == null)
			return defaultValue;
		return sp.getInt(key, defaultValue);
	}

	public static void putString(Context context, String key, String value) {
		SharedPreferences sp = getSp(context);
		if (sp == null)
			return;
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, value);
		applyEditor(editor);
	}

	public static String getString(Context context, String key, String defaultValue) {
		SharedPreferences sp = getSp(context);
		if (sp == null)
			return "";
		return sp.getString(key, defaultValue);
	}

	public static void putBoolean(Context context, String key, boolean value) {
		SharedPreferences sp = getSp(context);
		if (sp == null)
			return;

		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(key, value);
		applyEditor(editor);
	}

	public static boolean getBoolean(Context context, String key, boolean defaultValue) {
		SharedPreferences sp = getSp(context);
		if (sp == null)
			return false;
		return sp.getBoolean(key, defaultValue);
	}

	public static Map<String, ?> getAll(Context context) {
		SharedPreferences sp = getSp(context);
		if (sp == null)
			return null;

		return sp.getAll();
	}

	public static void remove(Context context, String key) {
		SharedPreferences sp = getSp(context);
		if (sp == null)
			return;

		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		applyEditor(editor);
	}

	private static void applyEditor(SharedPreferences.Editor editor) {
//		if (Build.VERSION.SDK_INT >= 9) {
//			editor.apply();
//		} else {
//			editor.commit();
//		}
		editor.commit();
	}

	public static boolean hasClickedInIntervalTime(Context context, String key, int intervalH) {
		long lastTime = getLong(context, key, 0);
		if (lastTime <= 0 || lastTime > System.currentTimeMillis()) {
			return false;
		} else {
			return isInLimitInterval(lastTime, intervalH);
		}
	}

	private static boolean isInLimitInterval(long lastTime, int intervalH) {
		return System.currentTimeMillis() - lastTime < intervalH * 60 * 60 * 1000L;
	}
}
