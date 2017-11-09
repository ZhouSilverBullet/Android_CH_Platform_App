package com.chsdk.biz;

import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.chsdk.api.AppOperator;
import com.chsdk.api.CacheManager;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.RequestExe;
import com.chsdk.model.InitModel;
import com.chsdk.model.RequestSyncResult;
import com.chsdk.model.game.UpdateEntry;
import com.chsdk.utils.ApkUtil;
import com.chsdk.utils.JsonUtil;
import com.chsdk.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月17日
 *          <p>
 */
public class InitLogic extends BaseLogic {
	private static final String TAG = InitLogic.class.getSimpleName();
	private static final String INIT_SDK_PATH = "app/init";

	public InitLogic() {
	}

	public RequestSyncResult initSdk() {
		String url = HOST_APP + INIT_SDK_PATH;

		InitModel model = new InitModel();

		String result = null;
		try {
			AppContext.getAppContext().inviteCodeShow = "";
			result = RequestExe.postSync(2000, url, model.getDataMap());
		} catch (Exception e) {
			String msg = RequestExe.getOkHttpExceptionMsg(e, null);
			RequestSyncResult entry = new RequestSyncResult();
			entry.postStatus = false;
			entry.msg = msg;
			return entry;
		}
		if (TextUtils.isEmpty(result)) {
			UpdateEntry updateEntry = (UpdateEntry) CacheManager.readObject(AppContext.getAppContext(), "updateEntry");
            if (updateEntry != null && ApkUtil.getVersionCode(AppContext.getAppContext()) < updateEntry.versionCode) {
                SdkSession.getInstance().setUpdateEntry(updateEntry);
            } else {
                CacheManager.deleteObject(AppContext.getAppContext(), "updateEntry");
            }
            LogUtil.errorLog("InitLogic result:" + result);
			return null;
		}

		try {
			JSONObject jsonObject = new JSONObject(result);

			if (jsonObject != null) {
				int code = JsonUtil.getStatusCode(jsonObject);

				RequestSyncResult entry = new RequestSyncResult();
				if (code == HttpConsts.CODE_SUCCESS) {
					entry.postStatus = true;
					final JSONObject data = (JSONObject) JsonUtil.getJsonData(jsonObject);
					String gift_url = data.getString("gift_url");
					String gift_intercept_url = data.getString("gift_intercept_url");
					String paySort = data.getString(HttpConsts.RESULT_PARAMS_PAY_SORT);
					DataStorage.saveAppPaySort(SdkSession.getInstance().getAppContext(), paySort);

					AppContext.getAppContext().inviteCodeShow = data.optString("invite_switch");
					DataStorage.saveGiftMoreUrl(SdkSession.getInstance().getAppContext(),gift_url);
					DataStorage.saveGiftInterceptUrl(SdkSession.getInstance().getAppContext(),gift_intercept_url);

					String couponActive = data.optString("coupon_active");
					DataStorage.saveCouponActive(SdkSession.getInstance().getAppContext(), couponActive);
					String updateUrl = JsonUtil.getGameUpdateUrl(data);
					if (!TextUtils.isEmpty(updateUrl)) {
						final UpdateEntry updateEntry = new UpdateEntry();
						updateEntry.url = updateUrl;
						updateEntry.updateState = data.optString("update_state");
						updateEntry.versionCode = JsonUtil.getServerVersionCode(data);
						updateEntry.forceUpdate = "1".equals(data.optString("force_update"));
                        if (updateEntry.versionCode > ApkUtil.getVersionCode(AppContext.getAppContext())) { //保存
                            entry.obj = updateEntry;
                            CacheManager.saveObject(AppContext.getAppContext(), updateEntry, "updateEntry");
                        } else { //删除
                            entry.obj = null;
                            CacheManager.deleteObject(AppContext.getAppContext(), "updateEntry");
                        }
					} else {
						entry.obj = null;
					}
				} else {
					entry.postStatus = false;
					entry.msg = JsonUtil.getServerMsg(jsonObject);
				}

				return entry;
			}
		} catch (JSONException e) {
			LogUtil.errorLog(TAG, "postSync: JSONException_" + e.getMessage() + ", result_" + url);
		}

		return null;
	}
}
