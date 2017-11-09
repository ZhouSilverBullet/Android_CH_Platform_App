package com.chsdk.biz.security;

import java.util.HashMap;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.security.MBEmailModel;
import com.chsdk.ui.widget.CHToast;

/** 
* @author  ZengLei <p>
* @version 2016年9月7日 <p>
*/
public class MBEmailLogic extends BaseLogic {
	private static final String MB_EMAIL_PATH = "passwd/mbEmailCode";
	private String userName, email;
	
	public MBEmailLogic(String userName, String email) {
		this.userName = userName;
		this.email = email;
	}
	
	public void sendEmail() {
		String url = HOST_PASSPORT + MB_EMAIL_PATH;
		
		MBEmailModel model = new MBEmailModel(userName, email);
		
		RequestExe.post(url, model, new IRequestListener() {
			
			@Override
			public void success(HashMap<String, String> map) {
				CHToast.show(SdkSession.getInstance().getAppContext(), "邮件发送成功");
			}
			
			@Override
			public void failed(String error, int errorCode) {
				CHToast.show(SdkSession.getInstance().getAppContext(), "邮件发送失败:" + error);
			}
		});
	}
}
