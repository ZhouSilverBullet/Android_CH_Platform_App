package com.chsdk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月15日
 *          <p>
 */
public class NetworkUtils {
	
	public static boolean isNetworkConnected(Context ctx) {
		if (null == ctx) {
			return false;
		}

		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) ctx
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (null == connectivityManager) {
				return false;
			}
			NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			return activeNetworkInfo != null && activeNetworkInfo.isAvailable();
		} catch (Exception e) {
		}
		return false;
	}
	
	private static boolean IsNetworkAvailable(Context context) {
		ConnectivityManager conmgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conmgr == null) {
			return false;
		}

		// 修改解决判断网络时的崩溃
		// mobile 3G Data Network
		try {
			NetworkInfo net3g = conmgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (net3g != null) {
				NetworkInfo.State mobile = net3g.getState();// 显示3G网络连接状态
				if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING)
					return true;
			}
		} catch (Exception e) {
		}

		try {
			NetworkInfo netwifi = conmgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (netwifi != null) {
				NetworkInfo.State wifi = netwifi.getState(); // wifi
				// 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
				if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)
					return true;
			}
		} catch (Exception e) {
		}

		return false;
	}

	public static String getNetworkType(Context context) {
		String networkType = "";
		try {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				NetworkInfo info = cm.getActiveNetworkInfo();
				if (info != null) {
					int type = info.getType();
					if (type == ConnectivityManager.TYPE_WIFI) {
						networkType = "wifi";
					} else {
						TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
						if (tm != null) {
							switch (tm.getNetworkType()) {
							case TelephonyManager.NETWORK_TYPE_1xRTT:
							case TelephonyManager.NETWORK_TYPE_EDGE:
							case TelephonyManager.NETWORK_TYPE_GPRS:
							case TelephonyManager.NETWORK_TYPE_CDMA:
							case TelephonyManager.NETWORK_TYPE_IDEN:
								networkType = "2g";
								break;
							case TelephonyManager.NETWORK_TYPE_EHRPD:
							case TelephonyManager.NETWORK_TYPE_EVDO_0:
							case TelephonyManager.NETWORK_TYPE_EVDO_A:
							case TelephonyManager.NETWORK_TYPE_EVDO_B:
							case TelephonyManager.NETWORK_TYPE_HSPAP:
							case TelephonyManager.NETWORK_TYPE_HSPA:
							case TelephonyManager.NETWORK_TYPE_UMTS:
								networkType = "3g";
								break;
							case TelephonyManager.NETWORK_TYPE_HSDPA:
							case TelephonyManager.NETWORK_TYPE_HSUPA:
								networkType = "3.5g";
								break;
							case TelephonyManager.NETWORK_TYPE_LTE:
								networkType = "4g";
								break;
							case TelephonyManager.NETWORK_TYPE_UNKNOWN:
								break;
							default:
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
		}

		return networkType;
	}

	private static boolean IsMobileNetworkAvailable(Context context) {
		if (IsNetworkAvailable(context)) {
			if (IsWifiNetworkAvailable(context))
				return false;

			return true;
		}

		return false;
	}

	private static boolean IsWifiNetworkAvailable(Context context) {
		// Monitor network connections (Wi-Fi, GPRS, UMTS, etc.)
		// mobile 3G Data Network
		ConnectivityManager conmgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conmgr == null) {
			return false;
		}

		NetworkInfo info = null;
		try {
			info = conmgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (info == null) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

		NetworkInfo.State wifi = info.getState(); // 显示wifi连接状态
		if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)
			return true;

		return false;
	}

	public static boolean pingBaidu() {
		Runtime runtime = Runtime.getRuntime();
		try {
			// -c 执行 次数, -w 等待时间(单位:秒)
			Process pingProcess = runtime.exec("/system/bin/ping -c 1 -w 1 www.baidu.com");
			int exitCode = pingProcess.waitFor();
			pingProcess.destroy();
			return (exitCode == 0);
		} catch (Exception e) {
			LogUtil.errorLog(e.getMessage());
		}
		return false;
	}
}