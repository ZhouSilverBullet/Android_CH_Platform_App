package com.chsdk.model.pay;

import com.chsdk.biz.pay.WalletInfoLogic;
import com.chsdk.model.BaseModel;
import com.chsdk.utils.DeviceUtil;

/** 
* @author  ZengLei <p>
* @version 2016年8月25日 <p>
*/
public class WalletModel extends BaseModel{
	
	private String userId;
	private String walletType;
	private String token;

	public WalletModel(int type) {
		userId = session.getUserId();
		walletType = getWalletType(type);
		token = session.getToken();
	}
	
	private String getWalletType(int type) {
		if (type == WalletInfoLogic.TYPE_ALL) {
			return "20,24,51,61";
		} else if (type == WalletInfoLogic.TYPE_CHB) {
			return "20";
		} else if (type == WalletInfoLogic.TYPE_BY) {
			return "24";
		} else if (type == WalletInfoLogic.TYPE_CHD) {
			return "51";
		}
		return "";
	}
	
	@Override
	public void putDataInMap() {
		put(PARAMS_USER_ID, userId);
		put(PARAMS_USER_WALLET, walletType);
		put(PARAMS_TOKEN, token);
	}
}
