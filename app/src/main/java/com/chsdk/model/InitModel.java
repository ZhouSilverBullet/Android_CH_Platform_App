package com.chsdk.model;

import com.caohua.games.app.AppContext;
import com.chsdk.utils.DeviceUtil;
import com.chsdk.utils.NetworkUtils;
import com.umeng.analytics.AnalyticsConfig;

/** 
* @author  ZengLei <p>
* @version 2016年8月17日 <p>
*/
public class InitModel extends BaseModel{
	private String deviceNo; // 子渠道ID
	
	public InitModel() {
		deviceNo = session.getDeviceNo();
	}

	@Override
	public void putDataInMap() {
		put(PARAMS_DEVICE_NO, deviceNo);

		put(PARAMS_INIT_CHANNEL_ID, AnalyticsConfig.getChannel(AppContext.getAppContext()));
	}
}
