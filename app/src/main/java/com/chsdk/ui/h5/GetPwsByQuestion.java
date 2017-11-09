package com.chsdk.ui.h5;

import org.json.JSONObject;

import com.chsdk.biz.BaseLogic.LogicListener;
import com.chsdk.biz.security.VerifyByQuestionLogic;
import com.chsdk.model.security.VerifyByQuestionModel;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.NetworkUtils;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/** 
* @author  ZengLei <p>
* @version 2016年9月7日 <p>
*/
public class GetPwsByQuestion extends BaseH5 {

	public GetPwsByQuestion(Activity activity, WebView webView) {
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
	public void verifyQuestion(String json) {
		if (!NetworkUtils.isNetworkConnected(context)) {
			handleNetworkError();
			return;
		}
		
		JSONObject object = getJsonObject(json);
		if (object != null) {
			String userName = getJSONObjectValue(object, "username");
			String answer = getJSONObjectValue(object, "question_answer");
			String passwd = getJSONObjectValue(object, "passwd");
			
			VerifyByQuestionModel model = new VerifyByQuestionModel(userName, answer, passwd);
			VerifyByQuestionLogic logic = new VerifyByQuestionLogic(model, new LogicListener() {
				
				@Override
				public void success(String... result) {
//					handleResult(getSuccessJson());
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
	}
}
