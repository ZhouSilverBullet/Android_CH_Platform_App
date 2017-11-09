package com.chsdk.biz.login;

import android.content.Context;
import android.text.TextUtils;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.NetworkUtils;
import com.chsdk.utils.PicUtil;

import java.util.HashMap;

/**
 * @author ZengLei <p>  
 */
public class AdLogoutLogic extends BaseLogic {
	private static final String TAG = AdLogoutLogic.class.getSimpleName();
	private static final String AD_PATH = "ucenter/logoutImg";
	private static final long UPDATE_INTERVAL = 2 * 60 * 60 * 1000;

	public void getAd() {
		final Context context = SdkSession.getInstance().getAppContext();
		if (!NetworkUtils.isNetworkConnected(context)) {
			return;
		}
		
		long lastTime = DataStorage.getAdPicLastUpdateTime(context);
		if (System.currentTimeMillis() - lastTime > 0 &&
				System.currentTimeMillis() - lastTime < UPDATE_INTERVAL) {
			return;
		}
		
		String url = HOST_APP + AD_PATH;
		RequestExe.post(url, new BaseModel(), new IRequestListener() {
			
			@Override
			public void success(HashMap<String, String> map) {
				if (map != null) {
					String url = map.get(HttpConsts.RESULT_PARAMS_IMG_URL);
					String link = map.get(HttpConsts.RESULT_PARAMS_IMG_LINK);
					if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(link)) {
						DataStorage.saveAdPicUpdateTime(context);
						DataStorage.saveAdPicLink(context, link);
						DataStorage.saveAdPicUrl(context, url);
						PicUtil.downloadPic(context, url, null);
					}
				} else {
					LogUtil.errorLog(TAG, HttpConsts.ERROR_CODE_PARAMS_VALID);
					DataStorage.saveAdPicUrl(context, "");
				}
			}
			
			@Override
			public void failed(String error, int errorCode) {
				LogUtil.debugLog(TAG, error);
				DataStorage.saveAdPicUrl(context, "");
			}
		});
	}
}
