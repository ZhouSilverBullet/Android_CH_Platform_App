package com.chsdk.configure;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.chsdk.model.game.UpdateEntry;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.DeviceUtil;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月15日
 *          <p>
 */
public class SdkSession {
	private static SdkSession sdkSession;
	private static final String appKey = "530F17CA7ABBB2581B5CF02E0455CECE"; //应用公钥或游戏公钥
	private static final int platformId = 1; //默认1
	private static final int appId = 133; // gameId
	private String autoUserName; // 自动生成的账号, 从进入到退出游戏只生成一次, 该账号登录成功则置空
	private String deviceNo;
	private String isBoundPhone;
	private LoginUserInfo user;
	private String versionCode;
	private UpdateEntry updateEntry;
	public boolean showLoginDialog; // 登录状态下,15天未启动app,则弹出登录框

	private SdkSession() {
	}

	public static SdkSession getInstance() {
		if (sdkSession == null) {
			synchronized (SdkSession.class) {
				if (sdkSession == null) {
					sdkSession = new SdkSession();
				}
			}
		}
		return sdkSession;
	}

	public Context getAppContext() {
		return AppContext.getAppContext();
	}

	public String getSdkVersion() {
		if (!TextUtils.isEmpty(versionCode)) {
			return versionCode;
		}

		try {
			PackageManager pm = getAppContext().getPackageManager();
			PackageInfo pi = pm.getPackageInfo(getAppContext().getPackageName(), 0);
			versionCode = String.valueOf(pi.versionCode);
			return versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			return "20101";
		}
	}
	
	public String getAppKey() {
		return appKey;
	}
	
	public int getPlatformId() {
		return platformId;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}
	
	public String getDeviceNo() {
		if (TextUtils.isEmpty(deviceNo)) {
			deviceNo = DeviceUtil.getDeviceNo(getAppContext());
		}
		return deviceNo;
	}

	public int getAppId() {
		return appId;
	}

	public void setAutoUserName(String autoUserName) {
		this.autoUserName = autoUserName;
	}
	
	public String getAutoUserName() {
		return autoUserName;
	}
	
	public static void clear() {
		synchronized (SdkSession.class) {
			if (sdkSession != null) {
			}
			sdkSession = null;
		}
	}

	public String getUserId() {
		if (user != null || getUserInfo() != null) {
			return user.userId;
		}
		return null;
	}

	public String getUserName() {
		if (user != null || getUserInfo() != null) {
			return user.userName;
		}
		return null;
	}

	public String getToken() {
		if (user != null || getUserInfo() != null) {
			return user.token;
		}
		return null;
	}

	public void setIsBoundPhone(String isBind) {
		isBoundPhone = isBind;
	}
	
	public boolean isBoundPhone() {
		return "1".equals(isBoundPhone);
	}

	public void setUserInfo(LoginUserInfo info) {
		user = info;
	}

	public LoginUserInfo getUserInfo() {
		if (AppContext.getAppContext().isLogin()) {
			if (user == null) {
				user = UserDBHelper.getLastLoginUser(AppContext.getAppContext());
			}
		}
		return user;
	}

	public void setUpdateEntry(UpdateEntry entry) {
		this.updateEntry = entry;
	}

	public UpdateEntry getUpdateEntry() {
		return updateEntry;
	}
}
