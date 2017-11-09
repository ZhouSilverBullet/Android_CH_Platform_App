package com.chsdk.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月15日
 *          <p>
 */
public class DeviceUtil {
	public static String getDeviceNo(Context context) {
        String tmDevice = "";
        String tmSerial = "";
        String androidId = "";
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            tmDevice = tm.getDeviceId() + ""; // imei
            tmSerial = tm.getSimSerialNumber() + "";
            androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID) + "";
        } catch (Exception e) {
            LogUtil.errorLog("DeviceUtil getDeviceNo: " + e);
        }
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return CryptionUtil.encodeMd5(deviceUuid.toString());
    }

	// 移动设备网络代码(英语：Mobile Network Code，MNC,)
	// 是与移动设备国家代码(Mobile Country Code，MCC)(也称为"MCC/MNC")相结合,
	// 例如46000，前三位是MCC，后两位是MNC 获取手机服务商信息
	// 返回值: 1代表中国移动,2代表中国联通,3代表中国电信,0代表未知
	public static int getOperators(Context context) {
		String model = Build.MODEL; // 型号 MX4
		String brand = Build.BRAND; // OS定制商 Meizu
		String manufacturer = Build.MANUFACTURER; // 硬件制造商 Meizu

		TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = telMgr.getSubscriberId(); // 460021131322685
												// ,http://www.soimsi.com/mobile,
												// 广东珠海 - 中国移动
		String operation = telMgr.getSimOperator(); // 46002
		String operationName = telMgr.getSimOperatorName(); // CMCC
		String imei = telMgr.getDeviceId(); // 865479023690407
											// ,http://www.imeidb.com/#lookup,
											// 魅族MX4
		String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID); // 5425389eed1c83d8

		int operatorsName = 0;
		if (TextUtils.isEmpty(imsi)) {
			return operatorsName;
		}

		// IMSI号前面3位460是国家，紧接着后面2位00 运营商代码
		if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007")) {
			operatorsName = 1;
		} else if (imsi.startsWith("46001") || imsi.startsWith("46006")) {
			operatorsName = 2;
		} else if (imsi.startsWith("46003") || imsi.startsWith("46005")) {
			operatorsName = 3;
		}

		return operatorsName;
	}

	public static double[] getLongitudeAndLatiude(Context context) {
		LocationManager locationMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		List<String> list = locationMgr.getProviders(true);
		if (list != null && list.size() > 0) {
			for (String name : list) {
				try {
					Location location = locationMgr.getLastKnownLocation(name);
					if (location != null) {
						double longitude = location.getLongitude();
						double latitude = location.getLatitude();

						if (longitude != 0 && latitude != 0) {
							return new double[] { longitude, latitude };
						}
					}
				} catch (Exception e) {
				}
			}
		}
		return new double[] { 0, 0 };
	}

	public static long getUnixTimestamp() {
		try {
			return System.currentTimeMillis() / 1000L;
		} catch (Exception e) {
			return 0;
		}
	}

	public static void hideSoftInput(Context context, View viewToken) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(viewToken.getWindowToken(), 0);
	}

	public static boolean isSameDay(long lastTime) {
		if (lastTime > System.currentTimeMillis()) {
			return false;
		}

		Calendar nowDate = Calendar.getInstance();
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTimeInMillis(lastTime);

		boolean isSameYear = nowDate.get(Calendar.YEAR) == lastDate.get(Calendar.YEAR);
		boolean isSameMonth = nowDate.get(Calendar.MONTH) == lastDate.get(Calendar.MONTH);
		boolean isSameDate = nowDate.get(Calendar.DAY_OF_MONTH) == lastDate.get(Calendar.DAY_OF_MONTH);
		return isSameYear && isSameMonth && isSameDate;
	}
}
