package com.chsdk.biz;

import android.text.TextUtils;

import com.chsdk.http.HttpConsts;
import com.chsdk.http.RequestExe;
import com.chsdk.model.MsgAuthCodeModel;
import com.chsdk.model.RequestSyncResult;
import com.chsdk.model.login.BoundPhoneCodeModel;
import com.chsdk.model.security.MBPhoneModel;
import com.chsdk.utils.JsonUtil;
import com.chsdk.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/** 
* @author  ZengLei <p>
* @version 2016年8月20日 <p>
*/
public class SendSmsLogic extends BaseLogic {
	private static final String TAG = SendSmsLogic.class.getSimpleName();
	private static final String SEND_SMS_PATH = "register/authCode";
	private static final String BOUND_PHONE_PATH = "user/bindAuthCode";
	private static final String MB_PHONE_PATH = "passwd/mbMobileCode";
	
	private String phoneNum;
	private int type;
	
	public SendSmsLogic(String phoneNum, int type) {
		this.phoneNum = phoneNum;
		this.type = type;
	}
	
	public RequestSyncResult sendSms() {
		String url = null;
		
		Map<String, String> map = null;
		if (type == SendSmsTask.TYPE_REGISTER) {
			url = HOST_PASSPORT + SEND_SMS_PATH;
			MsgAuthCodeModel model = new MsgAuthCodeModel();
			model.setUserName(phoneNum);
			map = model.getDataMap();
		} else if (type == SendSmsTask.TYPE_BOUND_PHONE) {
			url = HOST_PASSPORT + BOUND_PHONE_PATH;
			BoundPhoneCodeModel model = new BoundPhoneCodeModel(phoneNum);
			map = model.getDataMap();
		} else if (type == SendSmsTask.TYPE_MB_MOBILE) {
			url = HOST_PASSPORT + MB_PHONE_PATH;
			MBPhoneModel model = new MBPhoneModel(phoneNum);
			map = model.getDataMap();
		}
		RequestSyncResult syncResult;
		String result = null;
		try {
			result = RequestExe.postSync(url, map);
		} catch (Exception e) {
			syncResult = new RequestSyncResult();
			syncResult.postStatus = false;
			syncResult.msg = RequestExe.getOkHttpExceptionMsg(e, null);
			return syncResult;
		}
		
		if (!TextUtils.isEmpty(result)) {
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject != null) {
					int code = JsonUtil.getStatusCode(jsonObject);
					syncResult = new RequestSyncResult();
					if (code == HttpConsts.CODE_SUCCESS) {
						syncResult.postStatus = true;
						syncResult.msg = JsonUtil.getUserid((JSONObject) JsonUtil.getJsonData(jsonObject));
					} else {
						syncResult.postStatus = false;
						syncResult.msg = JsonUtil.getServerMsg(jsonObject);
					}
					return syncResult;
				}
			} catch (JSONException e) {
				LogUtil.errorLog(TAG, "postSync: JSONException_" + e.getMessage() + ", result_" + result);
			}
		}
		return null;
	}
}
