package com.chsdk.model.login;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.caohua.games.app.AppContext;
import com.chsdk.model.BaseModel;
import com.chsdk.utils.DeviceUtil;
import com.chsdk.utils.VerifyFormatUtil;
import com.umeng.analytics.AnalyticsConfig;

/** 
* @author  ZengLei <p>
* @version 2016年8月18日 <p>
*/
public class LoginModel extends BaseModel{
	private String userName; //用户账号
	private String userType;
	private String passwd;
	private String autoToken;
	private String longitude;
	private String latitude;
	private boolean autoLogin;
	private String deviceNo;
	private String deviceId;
	private String simNum;
	private String androidId;
	
	public LoginModel(boolean autoLogin) {
		this.autoLogin = autoLogin;
		deviceNo = session.getDeviceNo();
		setArgs();
		double[] longLatitude = DeviceUtil.getLongitudeAndLatiude(session.getAppContext());
		if (longLatitude != null) {
			longitude = String.valueOf(longLatitude[0]);
			latitude = String.valueOf(longLatitude[1]);
		}
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
		userType = String.valueOf(VerifyFormatUtil.isPhoneNum(userName) ? 2 : 1);
	}
	
	public void setAutoToken(String autoToken) {
		this.autoToken = autoToken;
	}
	
	@Override
	public void putDataInMap() {
		put(PARAMS_USER_NAME, userName);
		put(PARAMS_USER_TYPE, userType);
		put(PARAMS_LONGITUDE, longitude);
		put(PARAMS_LATITUDE, latitude);
		if (autoLogin) {
			put(PARAMS_AUTO_TOKEN, autoToken);
		} else {
			put(PARAMS_PASSWORD, passwd);
		}
		put(PARAMS_DEVICE_NO, deviceNo);
		put(PARAMS_DEVICE_ID, deviceId);
		put(PARAMS_SERIAL_NUMBER, simNum);
		put(PARAMS_ANDROID_ID, androidId);
		put(PARAMS_CHANNEL_USER_ID, "639");
	}

	private void setArgs() {
		try {
			final Context context = session.getAppContext();
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			deviceId = tm.getDeviceId() + ""; // imei
			simNum = tm.getSimSerialNumber() + "";
			androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID) + "";
		} catch (Exception e) {
		}
	}

}
