package com.chsdk.biz.pay;

import org.json.JSONException;
import org.json.JSONObject;

import com.chsdk.ui.widget.CHToast;
import com.unionpay.UPPayAssistEx;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;

/**
* @author  ZengLei <p>
* @version 2016年8月26日 <p>
*/
public class UnionPayMgr extends PayAction {
	private static final String UNION_MODE = "00"; // 设置测试模式:01为测试 00为正式环境

	private static UnionPayMgr mgr;

	private UnionPayMgr() {

	}

	public static UnionPayMgr getInstance() {
		if (mgr == null) {
			synchronized (UnionPayMgr.class) {
				if (mgr == null) {
					mgr = new UnionPayMgr();
				}
			}
		}
		return mgr;
	}

	public void pay(Activity activity, String tn) {
		UPPayAssistEx.startPay(activity, null, null, tn, UNION_MODE);
	}

	public void handleResult(Activity activity, Bundle bundle, String show) {
		String payStatus = bundle.getString("pay_result");
		String appendShow;
		if (TextUtils.isEmpty(show)) {
			appendShow = "";
		} else {
			appendShow = "," + show;
		}
		if ("success".equalsIgnoreCase(payStatus)) {
			String result = "支付成功" + appendShow;
			String data = bundle.getString("result_data");
			if (!TextUtils.isEmpty(data)) {
				try {
					JSONObject resultJson = new JSONObject(data);
	                String sign = resultJson.getString("sign");
	                String dataOrg = resultJson.getString("data");
	                // 验签证书同后台验签证书
                    // 此处的verify，商户需送去商户后台做验签
	                boolean ret = verifyResult(dataOrg, sign);
	                if (ret) {
	                	result = "支付成功" + appendShow;
	                } else {
	                	// 验证不通过后的处理
                        // 建议通过商户后台查询支付结果
	                	result = "支付失败";
	                }
				} catch (JSONException e) {
				}
			} else {
				// 未收到签名信息
                // 建议通过商户后台查询支付结果
				result = "支付成功！";
			}
			showResult(activity, "银联支付结果:" + result);
		} else if ("fail".equals(payStatus)){
			showResult(activity, "银联支付结果:支付失败");
		} else if ("cancel".equals(payStatus)) {
			showResult(activity, "银联支付结果:支付取消");
		} else {
			// 不属于银联结果
		}
	}

	private void showResult(Activity activity, String msg) {
		if (activity == null) {
			return;
		}

        CHToast.show(activity, msg);

		destroy();
	}

	private boolean verifyResult(String data, String sign64) {
		 // 此处的verify，商户需送去商户后台做验签
		return true;
	}

	public static void destroy() {
		mgr = null;
	}
}
