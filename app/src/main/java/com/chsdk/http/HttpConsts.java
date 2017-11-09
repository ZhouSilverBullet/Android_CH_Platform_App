package com.chsdk.http;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月15日
 *          <p>
 */
public class HttpConsts {
	public static final String POST_PARAMS_DATA = "data";
	
	// 请求返回的参数
	public static final String RESULT_PARAMS_ACCOUNT_FACE = "accountface";
	public static final String RESULT_PARAMS_ADDRESS = "address";
	public static final String RESULT_PARAMS_GAME_UPDATE_URL = "apk_url";
	public static final String RESULT_PARAMS_AUTO_TOKEN = "auto_token"; //自动登录token
	public static final String RESULT_PARAMS_BIRTH = "birthday";
	public static final String RESULT_PARAMS_CODE = "code";
	public static final String RESULT_PARAMS_COUPON = "coupon";
	public static final String RESULT_PARAMS_CHB = "chb"; // 草花币
	public static final String RESULT_PARAMS_CHD = "chd"; 
	public static final String RESULT_PARAMS_DATA = "data";
	public static final String RESULT_PARAMS_EMAIL = "email";
	public static final String RESULT_PARAMS_FORUM_NAME = "forum_name";
	public static final String RESULT_PARAMS_H5_URL = "h5_url";
	public static final String RESULT_PARAMS_IMG_URL = "img_url";
	public static final String RESULT_PARAMS_IMG_LINK = "img_link";
	public static final String RESULT_PARAMS_IS_BIND_PHONE = "is_bind_mobile"; //是否绑定手机
	public static final String RESULT_PARAMS_MSG_LINK = "msg_link";
	public static final String RESULT_PARAMS_MSG_ID = "msg_id";
	public static final String RESULT_PARAMS_MSG_TYPE = "msg_type"; // 1.文字 2.图片
	public static final String RESULT_PARAMS_MSG_SHOW_TYPE = "msg_show_type"; // 1.点击后不再展示 2.点击后当天（日期）不展示 3.每次登陆都展示
	public static final String RESULT_PARAMS_MSG_CONTENT = "msg_content";
	public static final String RESULT_PARAMS_MSG_EXPIRE_TIME = "msg_expire_time";
	public static final String RESULT_PARAMS_MOBILE = "mobile";
	public static final String RESULT_PARAMS_MSG = "msg";
	public static final String RESULT_PARAMS_NICK_NAME = "nickname";
	public static final String RESULT_PARAMS_NOTIFY_URL = "notify_url";
	public static final String RESULT_PARAMS_PARTNER = "partner"; // 支付宝商户ID
	public static final String RESULT_PARAMS_PAY_SORT = "pay_sort";
	public static final String RESULT_PARAMS_PHONE_PWD = "pp";
	public static final String RESULT_PARAMS_PSW = "psw";
	public static final String RESULT_PARAMS_QID = "qid";
	public static final String RESULT_PARAMS_QQ = "qq";
	public static final String RESULT_PARAMS_ROLE_ID = "role_id";
	public static final String RESULT_PARAMS_RSA_PRIVATE = "rsa_private"; // 支付宝私钥
	public static final String RESULT_PARAMS_SDK_ORDER_NO = "sdk_orderno"; //sdk 订单号
	public static final String RESULT_PARAMS_SELLER = "seller"; // 支付宝收款账号
	public static final String RESULT_PARAMS_SERVER_ID = "server_id"; //草花平台区服ID
	public static final String RESULT_PARAMS_SEX = "sex";
	public static final String RESULT_PARAMS_SILVER = "silver"; // 绑银
	public static final String RESULT_PARAMS_TOKEN_ID = "token_id"; // wechat(wft) token
	public static final String RESULT_PARAMS_TN = "tn"; // 银联由订单号生成的tn
	public static final String RESULT_PARAMS_TOKEN = "token"; //token 令牌
	public static final String RESULT_PARAMS_USER_ID = "userid";
	public static final String RESULT_PARAMS_USER_NAME = "username";
	public static final String RESULT_PARAMS_USER_FLAG = "user_flag";
	public static final String RESULT_PARAMS_VERSION_CODE = "version_code";

	// 排行榜
	public static final String RESULT_PARAMS_RANKING_MULTIPLE = "multiple";
	public static final String RESULT_PARAMS_RANKING_NEWLY = "newly";
	public static final String RESULT_PARAMS_RANKING_RECOMMEND = "recommend";
	public static final String RESULT_PARAMS_RANKING_ONLINE = "online";

	//礼物
	public static final String RESULT_PARAMS_GIFT_CORDNO = "cardno";
	public static final String RESULT_PARAMS_REWARD = "reward";
	// 请求返回的状态 定义
	public static final int CODE_SUCCESS = 200;

	// 自定义错误字符串
	public static final String ERROR_RESPONSE_NULL = "error_response_null";
	public static final String ERROR_RESPONSE_RESULT_NULL = "error_response_result_null";
	public static final String ERROR_JSON_EXCEPTION = "error_json_exception";
	
	// 操作符定义
	public static final String STR_EQUAL_OPERATION = "=";
	public static final String STR_SPLICE_SYMBOL = "&";
	
	public static final String ERROR_CODE_PARAMS_VALID = "未知错误,接收参数失败(120)";
	public static final String ERROR_NO_NETWORK = "当前没有网络连接(121)";

	public static final String ERROR_TOKEN_INVALID = "token刷新失败,请重新登录";

}
