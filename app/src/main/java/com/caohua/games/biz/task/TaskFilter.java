package com.caohua.games.biz.task;

/**
 * Created by zhouzhou on 2017/3/22.
 */

public interface TaskFilter {
    String METHOD_RESOURCE = "resource"; //跳源生界面
    String VALUE_RESOURCE_MY_GAME = "myGame"; //我的游戏
    String VALUE_RESOURCE_RECHARGE = "recharge";  //充值界面
    String VALUE_RESOURCE_MULTIPLEGAME = "multipleGame"; //综合游戏排行
    String VALUE_RESOURCE_MINE_FRAGMENT = "mineFragment";  //我的头像 ，我的
    String VALUE_RESOURCE_ACCOUNT_SETTING = "AccountSetting";  //账号设置
    String VALUE_RESOURCE_SEARCH_ACTIVITY = "SearchActivity";   //搜索界面
    String VALUE_RESOURCE_HOME_PAGER_ACTIVITY = "homePagerActivity"; //首页
    String VALUE_RESOURCE_BBS_ACTIVITY = "forum"; //首页
    String VALUE_RESOURCE_LIST_ACTIVITY = "list"; //首页

    String METHOD_URL = "url";  //网页
    String VALUE_URL_PHONE_BINDING = "phoneBinding"; //手机绑定
    String VALUE_URL_PHONE_BINDING_URL = "https://passport-sdk.caohua.com/user/bindMobile";

    String VALUE_URL_REAL_NAME_REGISTRATION = "RealNameRegistration"; // 实名注册
    String VALUE_URL_REAL_NAME_REGISTRATION_URL = "https://passport-sdk.caohua.com/user/authIdentity"; // 实名注册

    String VALUE_URL_CHANGE_PASSWORD = "https://passport-sdk.caohua.com/account/modifyPasswd";
    String VALUE_URL_GAME_DETAIL = "gameDetail"; //下载游戏的游戏详情页面
    String VALUE_URL_GAME_DETAIL_URL = "m.caohua.com/game/detail"; //下载游戏的游戏详情页面
    String VALUE_URL_OPEN_TASK_DETAIL = "m.caohua.com/article";

    String METHOD_INTERF = "interf"; //商城
}
