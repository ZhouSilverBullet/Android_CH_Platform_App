package com.chsdk.biz.app;

import android.content.Context;

import com.chsdk.configure.SdkSession;
import com.umeng.analytics.MobclickAgent;

/**
 * 友盟统计
 * Created by zhouzhou on 2016/11/19.
 */

public class AnalyticsHome {

    public static final String HOME_BANNER_CLICK_ANALYTICS = "home_banner_click_analytics";  //首页轮播

    public static final String HOME_DAY_RECOMMEND_ANALYTICS = "home_day_recommend_analytics"; //首页每日推荐
    public static final String HOME_DAY_RECOMMEND_CLICK_ANALYTICS = "home_day_recommend_click_analytics"; //首页每日推荐

    public static final String HOME_HOT_ACTIVITY_ANALYTICS = "home_hot_activity_analytics"; //首页热门活动
    public static final String HOME_HOT_ACTIVITY_CLICK_ANALYTICS = "home_hot_activity_click_analytics"; //首页热门活动

    public static final String HOME_HOT_GIFT_ANALYTICS = "home_hot_gift_analytics";    //首页热门礼包
    public static final String HOME_HOT_GIFT_CLICK_ANALYTICS = "home_hot_gift_click_analytics";    //首页热门礼包
    public static final String GIFT_CLICK_ANALYTICS = "gift_click_analytics";  //礼物的领取按钮

    public static final String HOME_HOT_TOPIC_ANALYTICS = "home_hot_topic_analytics";  //首页专题
    public static final String HOME_TOPIC_CLICK_ANALYTICS = "home_hot_topic_click_analytics";  //首页专题

    public static final String HOME_GAME_RANKING_ANALYTICS = "home_game_ranking_analytics";  //首页游戏排行
    public static final String HOME_GAME_RANKING_CLICK_ANALYTICS = "home_game_ranking_click_analytics";  //首页游戏排行


    public static final String HOME_SEARCH_CLICK_ANALYTICS = "home_search_click_analytics";  //首页收索点击
    public static final String HOME_DOWNLOAD_CLICK_ANALYTICS = "home_download_click_analytics";  //首页下载按钮

    public static final String HOME_OPENING_ANALYTICS = "home_opening_analytics";  //首页开服表more点击
    public static final String HOME_OPENING_CLICK_ANALYTICS = "home_opening_click_analytics";  //首页开服表item点击

    //MobclickAgent.onEvent(getContext(),"giftMoreUrl","232332341");

    //rankingDetailFragment item
    public static final String DETAIL_FRAGMENT_RANKING_CLICK_ANALYTICS = "detail_fragment_ranking_click_analytics";  //fragment开服表item点击
    public static final String FRAGMENT_RANKING_RECYCLER_VIEW_ITEM = "fragment_ranking_recycler_view_item";  //fragment开服表item点击

    //opening_fragment
    public static final String OPENING_FRAGMENT_ITEM_VIEW_CLICK_ANALYTICS = "opening_fragment_item_view_click_analytics";

    public static final String HOT_ITEM_ACTIVITY_SECOND = "hot_item_activity_second";  //热门活动的二级界面点击

    /**
     * account
     */
    public static final String ACCOUNT_USER_CENTER_PAY = "account_user_center_pay";  //充值草花币
    public static final String ACCOUNT_USER_CENTER_SILVER = "account_user_center_silver";  //账户绑银
    public static final String ACCOUNT_USER_CENTER_GIFT = "account_user_center_gift";  //我的礼包
    public static final String ACCOUNT_USER_CENTER_MSG = "account_user_center_msg";  //消息记录
    public static final String ACCOUNT_USER_CENTER_MALL = "account_user_center_mall";  //商城
    public static final String ACCOUNT_USER_CENTER_H5 = "account_user_center_h5";  //H5小游戏
    public static final String ACCOUNT_USER_CENTER_MIME_GAME = "account_user_center_mine_game";  //H5小游戏
    public static final String ACCOUNT_USER_CENTER_SERVICE = "account_user_center_service";  //联系客服
    public static final String ACCOUNT_USER_CENTER_FAQ = "account_user_center_faq";  //帮助FAQ
    public static final String ACCOUNT_USER_CENTER_COMMENT = "account_user_center_comment";  //帮助FAQ

    public static final String QQ_SHARE_CLICK = "qq_share_click";  //分享按钮点击
    public static final String QZONE_SHARE_CLICK = "qzone_share_click";  //分享按钮点击
    public static final String SINAWEIBO_SHARE_CLICK = "sinaweibo_share_click";  //分享按钮点击
    public static final String WECHAT_SHARE_CLICK = "wechat_share_click";  //分享按钮点击
    public static final String WECHAT_MOMENTS_SHARE_CLICK = "wechat_moments_share_click";  //分享按钮点击


    public static final String QQ_SHARE_SUCCESS = "QQ_SHARE_SUCCESS";  //分享成功
    public static final String QZONE_SHARE_SUCCESS = "QZONE_SHARE_SUCCESS";  //分享成功
    public static final String SINAWEIBO_SHARE_SUCCESS = "SINAWEIBO_SHARE_SUCCESS";  //分享成功
    public static final String WECHAT_SHARE_SUCCESS = "WECHAT_SHARE_SUCCESS";  //分享成功
    public static final String WECHAT_MOMENTS_SHARE_SUCCESS = "WECHAT_MOMENTS_SUCCESS";  //分享成功

    public static final String HOME_FRAGMENT = "HOME_FRAGMENT";  //首页
    public static final String MINE_FRAGMENT = "MINE_FRAGMENT";  //我的
    public static final String TASK_FRAGMENT = "TASK_FRAGMENT";  //任务
    public static final String STORE_FRAGMENT = "STORE_FRAGMENT";  //商城

    public static final String TASK_GO_VIEW = "TASK_GO_VIEW";  //Task领取去完成
    public static final String TASK_COMPLETE = "task_complete";  //Task领取

    //V2.4.0加入
    //专区
    public static final String SUBJECT_MESSAGE = "SUBJECT_MESSAGE";
    public static final String SUBJECT_CHANGE_GAME = "SUBJECT_CHANGE_GAME"; //切换游戏
    public static final String SUBJECT_MAIN_TAB = "SUBJECT_MAIN_TAB";
    public static final String SUBJECT_PAGER_TAB = "SUBJECT_PAGER_TAB";
    public static final String SUBJECT_FRAGMENT_TAB_ITEM = "SUBJECT_FRAGMENT_TAB_ITEM";

    //论坛
    public static final String FORUM_MESSAGE = "FORUM_MESSAGE";
    public static final String FORUM_TAB = "FORUM_TAB";
    public static final String FORUM_CONTENT_ITEM = "FORUM_CONTENT_ITEM";
    public static final String FORUM_EDIT_ARTICLE = "FORUM_EDIT_ARTICLE";

    //帖子详情
    public static final String FORUM_POP_WINDOW = "FORUM_POP_WINDOW";
    public static final String FORUM_COLLECT = "FORUM_COLLECT";
    public static final String FORUM_COPY = "FORUM_COPY";
    public static final String FORUM_REPORT = "FORUM_REPORT";

    //发布界面
    public static final String FORUM_PUBLISH = "FORUM_PUBLISH";

    //发现界面
    public static final String FIND_HOT = "FIND_HOT";
    public static final String FIND_GIFT = "FIND_GIFT";
    public static final String FIND_SHOP = "FIND_SHOP";
    public static final String FIND_TASK = "FIND_TASK";
    public static final String FIND_RANKING = "FIND_RANKING";
    //文章详情
    public static final String FIND_XIANG_COLLECT = "FIND_XIANG_COLLECT";

    //游戏中心
    public static final String GAME_CENTER_ITEM_MINE = "GAME_CENTER_ITEM_MINE";
    public static final String GAME_CENTER_ITEM_RECOMMENT = "GAME_CENTER_ITEM_RECOMMENT";
    public static final String GAME_CENTER_ITEM = "GAME_CENTER_ITEM";
    public static final String GAME_CENTER_ENTER_SUBJECT = "GAME_CENTER_ENTER_SUBJECT";

    //我的新的界面
    public static final String MINE_COLLECT = "MINE_COLLECT"; // 我的收藏
    public static final String MINE_FORUM_MESSAGE = "MINE_FORUM_MESSAGE";
    public static final String MINE_PUBLISH = "MINE_PUBLISH";

    //V2.4.1加入
    public static final String MINE_GROW = "MINE_GROW"; //我的成长


    //V2.4.2加入

    /**
     * 领券中心：
     * 1.发现点击领取那中心
     * 2.点击【领取】按钮次数
     * 3.领取成功次数
     * 4.点击弹窗的下载游戏次数
     * <p>
     * 我的-钱包
     * 1.点击我的钱包次数
     * 2.点击我的优惠券次数
     */

    public static final String FIND_COUPON_CENTER = "find_coupon_center"; //发现点击领取那中心
    public static final String COUPON_CENTER_AWARD = "coupon_center_award"; //点击领取按钮次数
    public static final String COUPON_CENTER_AWARD_SUCCESS = "coupon_center_award_success"; //领取成功次数
    public static final String COUPON_CENTER_AWARD_DOWNLOAD = "coupon_center_award_download"; //点击弹窗的下载游戏次数

    public static final String MINE_WALLET = "mine_wallet"; //点击我的钱包次数
    public static final String MINE_WALLET_COUPON = "mine_wallet_coupon"; //点击我的优惠券次数


    // QZone  SinaWeibo  Wechat  WechatMoments
    public static void umOnEvent(String id, String content) {
        MobclickAgent.onEvent(SdkSession.getInstance().getAppContext(), id, content);
    }

    /**
     * 场景类型设置接口
     *
     * @param context
     */
    public static void umNormalAnalytics(Context context) {
        MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    /**
     * 登陆统计
     *
     * @param userName
     */
    public static void umOnProfileSignIn(String userName) {
        MobclickAgent.onProfileSignIn(userName);
    }

    /**
     * 登出统计
     */
    public static void umProfileSignOff() {
        MobclickAgent.onProfileSignOff();
    }
}
