package com.chsdk.configure;

import android.content.Context;
import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.chsdk.api.CHSdk;
import com.chsdk.biz.DeviceActiveLogic;
import com.chsdk.biz.InitLogic;
import com.chsdk.biz.login.LoginBgLogic;
import com.chsdk.model.RequestSyncResult;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.CHAsyncTask;
import com.chsdk.utils.DeviceUtil;
import com.chsdk.utils.FileUtil;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.ManualTimer;

/** 
* @author  ZengLei <p>
* @version 2016年8月15日 <p>
*/
public class InitTask extends CHAsyncTask<Void, Void, RequestSyncResult> {
	private static final String TAG = InitTask.class.getSimpleName();
	private static final int SHOW_SPLASH_MIN_DURATION_S = 800; // Splash展示的最短时间
	
	private Context context;
	private Long startTime;
	public InitTask() {
		context = SdkSession.getInstance().getAppContext();
	}
	
	@Override
	protected void onPostExecute(RequestSyncResult entry) {
		LogUtil.debugLog(TAG, "InitTask end onPostExecute, cancel=" + canceled);

		if (taskListener != null) {
			if (canceled) {
				taskListener.canceled() ;	
			} else {
				taskListener.finished(startTime, entry);
			}
		}
	}

	@Override
	protected RequestSyncResult doInBackground(Void... params) {
		LogUtil.debugLog(TAG, "InitTask begin");
		
		if (canceled)
			return null;

		AppContext.EXTERNAL_STORAGE_MOUNT = FileUtil.getStorageStatus();
		startTime = System.currentTimeMillis();
		ManualTimer timer = new ManualTimer(SHOW_SPLASH_MIN_DURATION_S);
		timer.start();

		getDeviceNo();

		boolean status = handleLoginStatus();
		if (status) {
			// 验证账号合法性
			boolean invalid = new LoginBgLogic().loginNormal();
			if (invalid) {
				CHSdk.accountInvalid = true;
				DataStorage.setAppLogin(context, false);
			}
		}

		RequestSyncResult entry = postInitSdkParams();
		
		timer.end();
		return entry;
	}

	private RequestSyncResult postInitSdkParams() {
		InitLogic logic = new InitLogic();
		return logic.initSdk();
	}

	protected void getDeviceNo() {
		int appId = SdkSession.getInstance().getAppId();
		String deviceNo = FileUtil.readDeviceFile(context, appId);
		if (TextUtils.isEmpty(deviceNo)) {
			String deviceForSp = DataStorage.getDeviceNo(context);
			if (TextUtils.isEmpty(deviceForSp)) {
				deviceNo = DeviceUtil.getDeviceNo(context);
				if (!TextUtils.isEmpty(deviceNo)) {
					DataStorage.saveDeviceNo(context, deviceNo);
				}
			} else {
				deviceNo = deviceForSp;
			}
			saveDeviceoNo(deviceNo, appId);
		}
		
		SdkSession.getInstance().setDeviceNo(deviceNo);
		
		LogUtil.debugLog(TAG, "deviceNo:" + deviceNo);
	}
	
	protected void saveDeviceoNo(String deviceNo, int appId) {
		DeviceActiveLogic logic = new DeviceActiveLogic();
		RequestSyncResult result = logic.active(deviceNo);
		if (result != null) {
			if (result.postStatus) {
				FileUtil.saveDeviceFilePath(context, deviceNo, appId);
			} else {
				LogUtil.debugLog(TAG, "DeviceActiveLogic fail:" + result.msg);
			}
		}
	}

	protected boolean handleLoginStatus() {
		LoginUserInfo userInfo = UserDBHelper.getLastLoginUser(context);
		if (userInfo != null && DataStorage.isAppLogin(context)) {
			// 登录状态下，十五天未使用app，则弹出登录框
			long lastTime = DataStorage.getLastAppStartTime(context);
			if (lastTime > 0 && System.currentTimeMillis() - lastTime >= 15 * 24 * 60 * 60 * 1000) {
				SdkSession.getInstance().showLoginDialog = true;
				DataStorage.setAppLogin(context, false);
			} else {
				SdkSession.getInstance().setUserInfo(userInfo);
				DataStorage.setAppLogin(context, true);
				return true;
			}
		} else {
			DataStorage.setAppLogin(context, false);
		}
		DataStorage.setAppStartTime(context);
		return false;
	}
}
