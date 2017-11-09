package com.chsdk.biz;

import com.chsdk.http.HttpConsts;
import com.chsdk.http.RequestExe;
import com.chsdk.model.DeviceActiveModel;
import com.chsdk.model.RequestSyncResult;
import com.chsdk.utils.JsonUtil;
import com.chsdk.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/** 
* @author  ZengLei <p>
* @version 2016年8月31日 <p>
*/
public class DeviceActiveLogic extends BaseLogic {
	private static final String TAG = DeviceActiveLogic.class.getSimpleName();
	private static final String DEVICE_ACTIVE_PATH = "stat/deviceActive";

	public RequestSyncResult active(String deviceNo) {
		String url = HOST_PASSPORT + DEVICE_ACTIVE_PATH;
		DeviceActiveModel model = new DeviceActiveModel(deviceNo);
		
		String result = null;
		try {
			result = RequestExe.postSync(url, model.getDataMap());
		} catch (Exception e) {
			String msg = RequestExe.getOkHttpExceptionMsg(e, null);
			RequestSyncResult entry = new RequestSyncResult();
			entry.postStatus = false;
			entry.msg = msg;
			return entry;
		}
		
		try {
			JSONObject jsonObject = new JSONObject(result);
			if (jsonObject != null) {
				int code = JsonUtil.getStatusCode(jsonObject);
				RequestSyncResult entry = new RequestSyncResult();
				if (code == HttpConsts.CODE_SUCCESS) {
					entry.postStatus = true;
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
