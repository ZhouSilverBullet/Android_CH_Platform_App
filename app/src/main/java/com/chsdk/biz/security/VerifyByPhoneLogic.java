package com.chsdk.biz.security;

import java.util.HashMap;

import com.chsdk.biz.BaseLogic;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.security.VerifyByPhoneModel;

/** 
* @author  ZengLei <p>
* @version 2016年9月6日 <p>
*/
public class VerifyByPhoneLogic extends BaseLogic {
	private static String VERIFY_BY_PHONE = "passwd/mbVerifyMcode";
	private VerifyByPhoneModel model;
	private LogicListener listener;
	
	public VerifyByPhoneLogic(VerifyByPhoneModel model, LogicListener listener) {
		this.model = model;
		this.listener = listener;
	}
	
	public void verify() {
		String url = HOST_PASSPORT + VERIFY_BY_PHONE;
		
		RequestExe.post(url, model, new IRequestListener() {
			
			@Override
			public void success(HashMap<String, String> map) {
				if (listener != null) {
					listener.success();	
				}
			}
			
			@Override
			public void failed(String error, int errorCode) {
				if (listener != null) {
					listener.failed(error);
				}
			}
		});
	}
}
