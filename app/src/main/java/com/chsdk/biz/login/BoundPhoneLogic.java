package com.chsdk.biz.login;

import java.util.HashMap;

import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.login.BoundPhoneModel;

/** 
* @author  ZengLei <p>
* @version 2016年9月1日 <p>
*/
public class BoundPhoneLogic extends BaseLogic {
	private static String BOUND_PHONE_PATH = "user/bindVerifyCode";
	private String code;
	private LogicListener listener;
	
	public BoundPhoneLogic(String code, LogicListener listener) {
		this.code = code;
		this.listener = listener;
	}
	
	public void boundPhone() {
		String url = HOST_PASSPORT + BOUND_PHONE_PATH;
		
		BoundPhoneModel model = new BoundPhoneModel(code);
		
		RequestExe.post(url, model, new IRequestListener() {
			
			@Override
			public void success(HashMap<String, String> map) {
				if (listener != null) {
					listener.success();
				}
			}
			
			@Override
			public void failed(final String error, int errorCode) {
				if (BaseLogic.isTokenInvialid(error)) {
					TokenRefreshLogic.refresh(new LogicListener() {

						@Override
						public void failed(String errorMsg) {
							if (listener != null) {
								listener.failed(error);
							}
						}

						@Override
						public void success(String... result) {
							boundPhone();
						}
					});
					return;
				}
				
				if (listener != null) {
					listener.failed(error);
				}
			}
		});
	}
}
