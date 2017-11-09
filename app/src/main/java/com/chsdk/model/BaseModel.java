package com.chsdk.model;

import android.text.TextUtils;

import com.chsdk.configure.SdkSession;
import com.chsdk.utils.DeviceUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZengLei <p>
 * @version 2016年8月15日 <p>
 */
public class BaseModel {
    public static final String PARAMS_USER_ADRESS = "a"; //用户地址
    public static final String PARAMS_AUTH_CODE = "ac"; //用户输入的验证码
    public static final String PARAMS_ANDROID_ID = "ai";
    public static final String PARAMS_AUTO_TOKEN = "at"; //自动登录token
    public static final String PARAMS_USER_BIRTHDAY = "b"; //生日格式
    public static final String PARAMS_BIND_PHONE = "bm"; //绑定手机号
    public static final String PARAMS_INIT_CHANNEL_ID = "ci"; //channel_id 版本号验证
    public static final String PARAMS_CENTER_ID = "ci"; //领卷中心领取id
    public static final String PARAMS_CP_ORDER_NO = "co"; //CP 游戏订单号
    public static final String PARAMS_CP_ROLE_ID = "cr"; //CP 角色ID
    public static final String PARAMS_CHANNEL_USER_ID = "cu"; //渠道商id
    public static final String PARAMS_DEVICE_NO = "dn"; //设备ID
    public static final String PARAMS_DEVICE_ID = "di"; // imei
    public static final String PARAMS_DIRECT_URL = "du";
    public static final String PARAMS_EMAIL = "e";
    public static final String PARAMS_FORUM_ID = "fi"; //论坛id
    public static final String PARAMS_GAME_ID = "g"; //游戏ID
    public static final String PARAMS_GAME_EXTRA = "ge"; //CP 游戏透彻信息
    public static final String PARAMS_GAME_VERSION = "gv"; // 游戏版本
    public static final String PARAMS_GAME_ASSIGN = "gn"; //用户输入的游戏名
    public static final String PARAMS_GAIN_USER_MONEY = "gum"; //获得充值草花币个数
    public static final String PARAMS_GAIN_GIVE_MONEY = "ggm"; //获得赠送草花币个数
    public static final String PARAMS_H5_FILE = "hf";
    public static final String PARAMS_H5_HASH = "hh";
    public static final String PARAMS_INVITE_CODE = "ic";  //好友码
    public static final String PARAMS_IS_GOOD = "ig";  //论坛精华
    public static final String PARAMS_IS_TOP = "it"; //论坛是否置顶
    public static final String PARAMS_LONGITUDE = "lg"; // 经度
    public static final String PARAMS_LATITUDE = "la"; // 纬度
    public static final String PARAMS_LIST_ID = "lid"; // 游戏listid
    public static final String PARAMS_MSG_ID = "mi";
    public static final String PARAMS_MANAGER_GAME_ID = "mg";
    public static final String PARAMS_MOBILE_TYPE = "mt"; // 手机机型
    public static final String PARAMS_NUMBER = "n"; //当前数据总数
    public static final String PARAMS_NEED_CHB = "nc"; // 需支付的草花币
    public static final String PARAMS_OPERATOR = "no"; // 网络运营商
    public static final String PARAMS_NEED_SILVER = "ns"; // 需支付的绑银
    public static final String PARAMS_NETWORK_TYPE = "nw"; // 网络类型
    public static final String PARAMS_NIKE_NAME = "nn"; //用户昵称
    public static final String PARAMS_ORDER_AMT = "oa"; //订单金额 (单位：分)
    public static final String PARAMS_PAGE_SIZE = "p"; //请求数据的条数
    public static final String PARAMS_PAY_MONEY = "pa"; // 需充值金额
    public static final String PARAMS_PLATFORM_ID = "pf"; //平台ID
    public static final String PARAMS_PACKAGE_LIST = "pl"; //包命list集合
    public static final String PARAMS_PACKAGE_NAME = "pn"; //包名
    public static final String PARAMS_CP_SID = "ps"; //CP 游戏区服标示
    public static final String PARAMS_PAY_TYPE = "pt"; //1:草花币2:支付宝3:银联4:微信
    public static final String PARAMS_PASSWORD = "pw"; //密码
    public static final String PARAMS_USER_QQ = "q"; //用户QQ号
    public static final String PARAMS_QUESTION_ANSWER = "qa";
    public static final String PARAMS_ROEL_ID = "ri"; // 草花角色ID
    public static final String PARAMS_ROLE_LEVEL = "rl"; //角色等级
    public static final String PARAMS_ROLE_NAME = "rn"; //角色名
    public static final String PARAMS_RECEVICE_USER_NAME = "ru";  //受益者账号
    public static final String PARAMS_USER_SEX = "s"; //用户性别
    public static final String PARAMS_SERVER_ID = "si"; //草花平台区服ID（接口八返回）
    public static final String PARAMS_SUBJECT_ID = "sbi"; //专题id
    public static final String PARAMS_SUBJECT_GAME_ID = "sg"; //专区的游戏id
    public static final String PARAMS_SOURCE_ID = "sid"; //当前渠道ID
    public static final String PARAMS_SEARCH_EGG = "se"; //搜索彩蛋
    public static final String PARAMS_SIGNATURE = "sn"; //签名
    public static final String PARAMS_SDK_ORDER = "so"; //草花订单号
    public static final String PARAMS_SERIAL_NUMBER = "ssn"; // sim卡序列号
    public static final String PARAMS_STEP = "st"; // st
    public static final String PARAMS_TOKEN = "t"; //token 令牌
    public static final String PARAMS_TAB_ID = "tbi"; //专区tab的id
    public static final String PARAMS_TASK_ID = "ti";
    public static final String PARAMS_TASK_NUMBER = "tn";
    public static final String PARAMS_TIMESTAMP = "ts"; //时间戳
    public static final String PARAMS_TO_USER_ID = "tui"; //查看的用户ID
    public static final String PARAMS_USER_ID = "ui"; //草花用户ID
    public static final String PARAMS_USER_NAME = "un"; //用户账号
    public static final String PARAMS_USER_TYPE = "ut"; // 1普通； 2手机账号
    public static final String PARAMS_USER_WALLET = "uw"; // 钱包类型 20ch,24by 传多个用英文逗号拼接
    public static final String PARAMS_SDK_VERSION = "v"; //SDK版本

    //礼物中心
    public static final String PARAMS_GIFT_ID = "gid";
    public static final String PARAMS_GIFT_ITEM = "item";

    // 评论
    public static final String PARAMS_COMMENT_TYPE = "ct";  //1:游戏 2:文章 3:评论
    public static final String PARAMS_COMMENT_ID = "cid";  //评论id
    public static final String PARAMS_COMMENT_GAME_TYPE = "cgt";  //1：草花 2：非草花
    public static final String PARAMS_COMMENT_TYPE_ID = "ctid"; //游戏id或者文章id，评论id
    public static final String PARAMS_COMMENT_CONTENT = "cc"; // 评论内容
    public static final String PARAMS_COMMENT_IS_VERIFY = "iv";  // 0：未验证 1.已经通过验证
    public static final String PARAMS_COMMENT_COMMENT_ADDRESS = "ca"; // 地址
    public static final String PARAMS_COMMENT_MOBILE = "cm"; // 手机型号

    protected SdkSession session;
    protected Map<String, String> dataMap;

    protected String gameId;
    protected String timestamp;
    protected String sdkVersion;

    public BaseModel() {
        session = SdkSession.getInstance();
        dataMap = new HashMap<String, String>();

        gameId = String.valueOf(session.getAppId());
        timestamp = String.valueOf(DeviceUtil.getUnixTimestamp());
        sdkVersion = session.getSdkVersion();
    }

    protected void put(String key, String value) {
        dataMap.put(key, TextUtils.isEmpty(value) ? "" : value);
    }

    public Map<String, String> getDataMap() {
        put(PARAMS_GAME_ID, gameId);
        put(PARAMS_SDK_VERSION, sdkVersion);
        put(PARAMS_TIMESTAMP, timestamp);

        putDataInMap();

        return dataMap;
    }

    public void putDataInMap() {

    }
}
