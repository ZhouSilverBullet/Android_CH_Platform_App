package com.chsdk.biz.pay;

import java.util.Locale;

import com.chsdk.ui.widget.CHToast;
import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月26日
 *          <p>
 */
public class WechatPayMgr extends PayAction {
	public static void pay(Activity activity, String tokenId) {
		RequestMsg msg = new RequestMsg();
		msg.setTokenId(tokenId);
		msg.setTradeType(MainApplication.PAY_WX_WAP);
		PayPlugin.unifiedH5Pay(activity, msg);
	}

	public static void handleResult(Activity activity, Bundle bundle, String show) {
		String wftResultCode = bundle.getString("resultCode");
		if (TextUtils.isEmpty(wftResultCode)) {
			return;
		}
		wftResultCode = wftResultCode.toLowerCase(Locale.ENGLISH).trim();
		if ("success".equals(wftResultCode)) {
			if (TextUtils.isEmpty(show)) {
				CHToast.show(activity, "支付成功");
			} else {
				CHToast.show(activity, "支付成功" + "," + show);
			}
		} else if ("notpay".equals(wftResultCode)) {
			CHToast.show(activity, "未完成支付");
		} else {
			CHToast.show(activity, "未完成支付:" + wftResultCode);
		}
	}
}
