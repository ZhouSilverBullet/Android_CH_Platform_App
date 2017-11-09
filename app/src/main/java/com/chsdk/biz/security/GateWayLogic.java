package com.chsdk.biz.security;

import java.util.HashMap;

import com.chsdk.biz.BaseLogic;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.security.GateWayModel;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年9月6日
 *          <p>
 */
public class GateWayLogic extends BaseLogic {
	public static final String PATH = "passwd/gateWay";
	private String userName;
	private LogicListener listener;

	public GateWayLogic(String userName, LogicListener listener) {
		this.userName = userName;
		this.listener = listener;
	}

	public void post() {
		String url = HOST_PASSPORT + PATH;

		GateWayModel model = new GateWayModel(userName);

		RequestExe.localHtmlPost(url, model.getDataMap(), new IRequestListener() {

			@Override
			public void success(HashMap<String, String> map) {
				if (listener != null) {
					listener.success(map.get("json"));
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
