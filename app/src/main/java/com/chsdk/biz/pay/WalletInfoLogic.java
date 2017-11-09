package com.chsdk.biz.pay;

import java.util.HashMap;

import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.pay.WalletModel;
import com.chsdk.utils.LogUtil;

import android.text.TextUtils;

/**
* @author  ZengLei <p>
* @version 2016年8月25日 <p>
*/
public class WalletInfoLogic extends BaseLogic {
	private static final String WALLET_INFO_PATH = "user/walletInfo";

	public static final int TYPE_ALL = 1;
	public static final int TYPE_CHB = 2;
	public static final int TYPE_BY = 3;
	public static final int TYPE_CHD = 4;
	private static final long UPDATE_INTERVAL = 5 * 1000;
	private static long resumeTime;
    private boolean isRefresh;

	private int type;
	private AppLogicListner listener;

	public WalletInfoLogic(int type, boolean isRefresh, AppLogicListner listener) {
		this.type = type;
		this.listener = listener;
        this.isRefresh = isRefresh;
    }

	public void getBalance() {

		if (System.currentTimeMillis() - resumeTime > 0 &&
				System.currentTimeMillis() - resumeTime < UPDATE_INTERVAL && !isRefresh) {
			if (listener != null) {
				listener.failed("");
			}
			return;
		}

		String url = HOST_PAY + WALLET_INFO_PATH;
		WalletModel model = new WalletModel(type);
		RequestExe.post(url, model, new IRequestListener() {
			@Override
			public void success(HashMap<String, String> map) {
				resumeTime = System.currentTimeMillis();
				if (map != null) {
					String chb = map.get(HttpConsts.RESULT_PARAMS_CHB);
					String silver = map.get(HttpConsts.RESULT_PARAMS_SILVER);
					String chd = map.get(HttpConsts.RESULT_PARAMS_CHD);
					String coupon = map.get(HttpConsts.RESULT_PARAMS_COUPON);
					WalletEntry walletEntry = new WalletEntry();
					walletEntry.chb = getValue(chb);
					walletEntry.chd = getValue(chd);
					walletEntry.silver = getValue(silver);
					walletEntry.coupon = getValue(coupon);
					if (listener != null) {
						listener.success(walletEntry);
						return;
					}
				}

				if (listener != null) {
					listener.failed(HttpConsts.ERROR_CODE_PARAMS_VALID);
				}
			}

			@Override
			public void failed(String error, int errorCode) {
				resumeTime = System.currentTimeMillis();
				if (BaseLogic.isTokenInvialid(error)) {
					handleTokenError(error);
					return;
				}

				if (listener != null) {
					listener.failed(error);
				}
			}
		});
	}

	private String getValue(String value) {
		if (TextUtils.isEmpty(value)) {
			return "0";
		}
		return value;
	}
	private void handleTokenError(final String error){
		TokenRefreshLogic.refresh(new LogicListener() {

			@Override
			public void success(String... result) {
				getBalance();
			}

			@Override
			public void failed(String errorMsg) {
				if (listener != null) {
					listener.failed(error);
				}
			}
		});
	}
}
