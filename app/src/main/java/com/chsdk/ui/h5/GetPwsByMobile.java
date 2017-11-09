package com.chsdk.ui.h5;

import org.json.JSONException;
import org.json.JSONObject;

import com.chsdk.biz.BaseLogic.LogicListener;
import com.chsdk.biz.SendSmsTask;
import com.chsdk.biz.security.VerifyByPhoneLogic;
import com.chsdk.model.security.VerifyByPhoneModel;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.NetworkUtils;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/** 
* @author  ZengLei <p>
* @version 2016年9月6日 <p>
*/
public class GetPwsByMobile extends BaseH5 {

	public GetPwsByMobile(Activity activity, WebView webView) {
		super(activity, webView);
	}

	@JavascriptInterface
	public void setStorage(String json) {
		super.setStorage(json);
	}
	
	@JavascriptInterface
	public String getStorage(String key) {
		return super.getStorage(key);
	}
	
	@JavascriptInterface
	public void mbMobileCode(String json) {
		try {
			JSONObject object = new JSONObject(json);
			String phone = object.optString("username");
			sendSms(phone);
		} catch (JSONException e) {
		}
	}
	
	@JavascriptInterface
	public void mbVerifyMcode(String json) {
		try {
			JSONObject object = new JSONObject(json);
			String phone = object.optString("username");
			String code = object.optString("auth_code");
			String passwd = object.optString("passwd");
			mbVerifyMcode(phone, code, passwd);
		} catch (JSONException e) {
		}
	}
	
	private void mbVerifyMcode(String phone, String code, String passwd) {
		VerifyByPhoneModel model = new VerifyByPhoneModel(phone, code, passwd);
		VerifyByPhoneLogic logic = new VerifyByPhoneLogic(model, new LogicListener() {
			
			@Override
			public void success(String... result) {
//				handleResult(getSuccessJson());
				CHToast.show(context, "密码重置成功");
				finishActivity();
			}
			
			@Override
			public void failed(String errorMsg) {
				handleResult(getErrorJson(errorMsg));
			}
		});
		logic.verify();
	}
	
	private void sendSms(String phone) {
		if (!NetworkUtils.isNetworkConnected(context)) {
			handleNetworkError();
			return;
		}
		
		SendSmsTask task = new SendSmsTask(activity, phone, SendSmsTask.TYPE_MB_MOBILE, new LogicListener() {
			
			@Override
			public void success(String... result) {
			}
			
			@Override
			public void failed(String errorMsg) {
			}
		});
		task.execute();
	}
}
