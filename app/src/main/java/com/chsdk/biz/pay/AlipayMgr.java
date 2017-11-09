package com.chsdk.biz.pay;

import java.net.URLEncoder;

import com.alipay.sdk.app.PayTask;
import com.chsdk.model.pay.GameMoneyInfo;
import com.chsdk.utils.CryptionUtil;

import android.app.Activity;
import android.text.TextUtils;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月26日
 *          <p>
 */
public class AlipayMgr extends PayAction {
	public static final String ALIPAY_CODE_SUCCESS = "9000";
	public static final String ALIPAY_CODE_WAIT = "8000";
	
	private Activity activity;
	private LogicListener listener;
	private String partner, seller, rsaPrivate, callBackUrl;
	private GameMoneyInfo info;
	private String sdkOrderNo;

	public AlipayMgr(Activity activity, GameMoneyInfo info, LogicListener listener, String...result) {
		this.activity = activity;
		this.info = info;
		this.listener = listener;
		
		if (result != null && result.length == 5) {
			sdkOrderNo = result[0];
			partner = result[1];
			seller = result[2];
			rsaPrivate = result[3];
			callBackUrl = result[4];
		}
	}

	public void pay() {
		String subject = String.valueOf(info.gameMoney) + info.gameMoneyName;
		String orderInfo = getOrderInfo(subject, subject, String.valueOf(info.money), sdkOrderNo);
		String sign = CryptionUtil.rsaSign(orderInfo, rsaPrivate);
		try {
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (Exception e) {
			if (listener != null) {
				listener.failed("调用支付宝失败(130)");
			}
			return;
		}
		
		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&sign_type=\"RSA\"";
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				PayTask alipay = new PayTask(activity);
				String result = alipay.pay(payInfo, true);
				PayResult payResult = new PayResult(result);
				final String resultStatus = payResult.getResultStatus();
				activity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						resultCallback(resultStatus);
					}
				});
			}
		}).start();
	}
	
	private void resultCallback(String result) {
		if (ALIPAY_CODE_SUCCESS.equals(result)) {
			if (listener != null) {
				listener.success("支付宝支付成功");
			}
		} else if (ALIPAY_CODE_WAIT.equals(result)) {
			if (listener != null) {
				listener.success("支付宝支付结果确认中");
			}
		} else {
			// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
			if (listener != null) {
				if (!TextUtils.isEmpty(result))
					listener.failed("支付宝支付失败(" + result + ")");
				else {
					listener.failed("支付宝支付失败");
				}
			}
		}
	}

	/**
	 * 
	 * @param subject 商品名称
	 * @param body 商品详情
	 * @param price 商品金额 ：元
	 * @param orderNo 订单号
	 * @return
	 */
	private String getOrderInfo(String subject, String body, String price, String orderNo) {
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"").append(partner).append("\"&seller_id=\"").append(seller)
		.append("\"&out_trade_no=\"").append(orderNo).append("\"&subject=\"").append(subject)
		.append("\"&body=\"").append(body).append("\"&total_fee=\"").append(price)
		.append("\"&notify_url=\"").append(callBackUrl).append("\"&service=\"mobile.securitypay.pay")
		.append("\"&payment_type=\"1\"&_input_charset=\"utf-8\"&it_b_pay=\"30m\"&return_url=\"m.alipay.com\"");
		return sb.toString();
	}
	
	class PayResult {
		private String resultStatus;
		private String result;
		private String memo;

		public PayResult(String rawResult) {

			if (TextUtils.isEmpty(rawResult))
				return;

			String[] resultParams = rawResult.split(";");
			for (String resultParam : resultParams) {
				if (resultParam.startsWith("resultStatus")) {
					resultStatus = gatValue(resultParam, "resultStatus");
				}
				if (resultParam.startsWith("result")) {
					result = gatValue(resultParam, "result");
				}
				if (resultParam.startsWith("memo")) {
					memo = gatValue(resultParam, "memo");
				}
			}
		}

		@Override
		public String toString() {
			return "resultStatus={" + resultStatus + "};memo={" + memo
					+ "};result={" + result + "}";
		}

		private String gatValue(String content, String key) {
			String prefix = key + "={";
			return content.substring(content.indexOf(prefix) + prefix.length(),
					content.lastIndexOf("}"));
		}

		/**
		 * @return the resultStatus
		 */
		public String getResultStatus() {
			return resultStatus;
		}

		/**
		 * @return the memo
		 */
		public String getMemo() {
			return memo;
		}

		/**
		 * @return the result
		 */
		public String getResult() {
			return result;
		}
	}
}
