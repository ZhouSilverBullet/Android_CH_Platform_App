package com.chsdk.biz.pay;

import com.caohua.games.ui.account.PayActionActivity;
import com.chsdk.ui.widget.CHToast;
import com.ipaynow.wechatpay.plugin.api.WechatPayPlugin;
import com.ipaynow.wechatpay.plugin.manager.route.dto.ResponseParams;
import com.ipaynow.wechatpay.plugin.manager.route.impl.ReceivePayResult;

import android.content.Context;
import android.text.TextUtils;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年10月20日
 *          <p>
 */
public class IpayNowMgr {
	public static void pay(final Context context, String info, final String benefitValue) {
		WechatPayPlugin.getInstance().init(context).setCallResultReceiver(new ReceivePayResult() {

			@Override
			public void onIpaynowTransResult(ResponseParams responseparams) {
				StringBuilder temp = new StringBuilder();
				if (responseparams == null) {
					temp.append("交易状态:失败");
				} else {
					String respCode = responseparams.respCode;
					String errorCode = responseparams.errorCode;
					String respMsg = responseparams.respMsg;
					if (respCode.equals("00")) {
						if (TextUtils.isEmpty(benefitValue)) {
                            temp.append("交易状态:成功");
                        } else {
                            temp.append("交易状态:成功" + "," + benefitValue);
                        }
					} else if (respCode.equals("02")) {
						temp.append("交易状态:取消");
					} else if (respCode.equals("01")) {
						temp.append("交易状态:失败").append("\n").append("错误码:").append(errorCode).append("原因:" + respMsg);
					} else if (respCode.equals("03")) {
						temp.append("交易状态:未知").append("\n").append("错误码:").append(errorCode).append("原因:" + respMsg);
					}
				}
				CHToast.show(context, temp.toString());
			}
		}).pay(info);
	}
}
