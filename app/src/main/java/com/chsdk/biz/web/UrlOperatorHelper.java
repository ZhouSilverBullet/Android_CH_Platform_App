package com.chsdk.biz.web;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.webkit.WebView;

import com.caohua.games.app.AppContext;
import com.caohua.games.ui.account.AccountSettingActivity;
import com.caohua.games.ui.giftcenter.GiftDetailActivity;
import com.chsdk.configure.DataStorage;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZengLei on 2017/1/6.
 */

/**
 * 处理网页跳转拦截事件
 */
public class UrlOperatorHelper {
    public static final String ACCOUNT_SAFE_SETTING = "sdk.caohua.com/ucenter/safeSetting";
    public static final String ACTION_CLOSE_ACTIVITY = "sdk.caohua.com/sdk/closeWindow";
    public static final String USER_CENTER_FAQ = "sdk.api.caohua.com/SDK/SDKHelpInfo.aspx?ClassID=";
    public static final String OPEN_WEB_DETAIL = "m.caohua.com/game/detail?id="; //游戏详情
    public static final String OPEN_WEB_MORE_COMMENT = "m.caohua.com/comment/detail";
    public static final String OPEN_USER_CENTER_COMMENT = "sdk.caohua.com/ucenter/myComment";
    public static final String OPEN_ARTICLE_DETAIL = "m.caohua.com/article/detail";
    public static final String OPEN_ARTICLE_FORUM = "wap.caohua.com/forum/articleDetail";
    public static final String OPEN_COMMENT_FORUM = "wap.caohua.com/forum/commentDetail";
    public static final String OPEN_FORUM_FACE = "sdk.caohua.com/face/";
    public static final String OPEN_ACT_ON_JAVA = "newactivity.caohua.com/";

    private Activity activity;
    private List<IOverrideUrlOperator> list;

    public UrlOperatorHelper(Activity activity) {
        this.activity = activity;
    }

    public void add(IOverrideUrlOperator operator) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(operator);
    }

    public UrlOperatorResult handle(WebView view, String url, int type) {
        if (list == null)
            return null;

        for (IOverrideUrlOperator operator : list) {
            UrlOperatorResult result = operator.handldUrl(view, url, type);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public void create() {
        IOverrideUrlOperator operator = createA(activity);
        add(operator);

        operator = createB(activity);
        add(operator);

        operator = createC(activity);
        add(operator);

        operator = createD(activity);
        add(operator);

        operator = createE(activity);
        add(operator);

        operator = createF(activity);
        add(operator);

        operator = createG(activity);
        add(operator);

        operator = createI(activity);
        add(operator);

        operator = createForumMore(activity);
        add(operator);
    }

    /**
     * 拦截关闭当前窗口
     */
    public static IOverrideUrlOperator createA(final Activity activity) {
        IOverrideUrlOperator operator = new IOverrideUrlOperator() {
            @Override
            public UrlOperatorResult handldUrl(WebView view, String url, int type) {
                if (url.contains(ACTION_CLOSE_ACTIVITY)) {
                    if (url.endsWith("?from=app")) {
                        // app修改了密码
                        DataStorage.setAppLogin(activity, false);
                        AccountSettingActivity.showLoginDialog = true;
                        CHToast.show(activity, "密码已更改,请重新登录");
                    }
                    activity.finish();
                    return UrlOperatorResult.getDefault();
                }
                return null;
            }
        };
        return operator;
    }

    /**
     * 拦截跳转到礼包详情
     */
    public static IOverrideUrlOperator createB(final Activity activity) {
        IOverrideUrlOperator operator = new IOverrideUrlOperator() {
            @Override
            public UrlOperatorResult handldUrl(WebView view, String url, int type) {
                String giftInterceptUrl = DataStorage.getGiftInterceptUrl(activity);
                if (!TextUtils.isEmpty(giftInterceptUrl) && url.startsWith(giftInterceptUrl)) {
                    if (openGiftDetail(url)) {
                        return UrlOperatorResult.getNew();
                    } else {
                        return UrlOperatorResult.getDefault();
                    }
                }
                return null;
            }

            private boolean openGiftDetail(String url) {
                if (!AppContext.getAppContext().isLogin()) {
                    AppContext.getAppContext().login(activity, null);
                    return false;
                }

                String[] split = url.split("id=");
                if (split.length < 2) {
                    CHToast.show(activity, "请求的地址无效!");
                    return false;
                }

                String path = split[1];
                GiftDetailActivity.start(activity, path, "", GiftDetailActivity.TYPE_NORMAL);
                return true;
//                Intent intent = new Intent(activity, GetGiftDetailsActivity.class);
//                intent.putExtra("path", path);
//                intent.putExtra("type", 100);
//                List<ResolveInfo> resolveInfo = activity.getPackageManager().queryIntentActivities(intent,
//                        PackageManager.MATCH_DEFAULT_ONLY);
//                if (resolveInfo != null && resolveInfo.size() > 0) {
//                    activity.startActivity(intent);
//                    return true;
//                } else {
//                    CHToast.show(activity, "打开礼包详情页面出错");
//                    return false;
//                }
            }
        };
        return operator;
    }

    /**
     * 拦截新闻或游戏详情页中的游戏推荐点击
     */
    public static IOverrideUrlOperator createC(final Activity activity) {
        IOverrideUrlOperator operator = createH(activity);
        return operator;
    }

    /**
     * 拦截游戏详情页面跳转到非下载地址1
     */
    public static IOverrideUrlOperator createD(final Activity activity) {
        IOverrideUrlOperator operator = new IOverrideUrlOperator() {
            @Override
            public UrlOperatorResult handldUrl(WebView view, String url, int type) {
                if (type == WebActivity.TYPE_GAME_DETAIL_BY_ID && !url.endsWith(".apk")) {
                    if (url.toLowerCase().contains("error")) {
                        view.loadUrl(url);
                        return UrlOperatorResult.getDefault();
                    } else {
                        WebActivity.startWebPage(activity, url);
//                        if (view.getUrl().toLowerCase().contains("error")) {
//                            activity.finish();
//                        }
                        return UrlOperatorResult.getNew();
                    }
                }
                return null;
            }
        };
        return operator;
    }

    /**
     * 拦截游戏详情页面跳转到非下载地址2
     */
    public static IOverrideUrlOperator createE(final Activity activity) {
        IOverrideUrlOperator operator = new IOverrideUrlOperator() {
            @Override
            public UrlOperatorResult handldUrl(WebView view, String url, int type) {
                if (type == WebActivity.TYPE_GAME_DETAIL && !url.endsWith(".apk")) {
                    if (url.toLowerCase().contains("error")) {
                        view.loadUrl(url);
                        return UrlOperatorResult.getDefault();
                    } else {
                        WebActivity.startWebPage(activity, url);
//                        if (view.getUrl().toLowerCase().contains("error")) {
//                            activity.finish();
//                        }
                        return UrlOperatorResult.getNew();
                    }
                }
                return null;
            }
        };
        return operator;
    }

    /**
     * 拦截设置界面的跳转
     */
    public static IOverrideUrlOperator createF(final Activity activity) {
        IOverrideUrlOperator operator = new IOverrideUrlOperator() {
            @Override
            public UrlOperatorResult handldUrl(WebView view, String url, int type) {
                if (!TextUtils.isEmpty(view.getUrl()) && view.getUrl().contains(ACCOUNT_SAFE_SETTING)) {
                    WebActivity.startWebPage(activity, url);
                    return UrlOperatorResult.getNew();
                }
                return null;
            }
        };
        return operator;
    }

    /**
     * 拦截用户FAQ跳转
     */
    public static IOverrideUrlOperator createG(final Activity activity) {
        IOverrideUrlOperator operator = new IOverrideUrlOperator() {
            @Override
            public UrlOperatorResult handldUrl(WebView view, String url, int type) {
                if (type == WebActivity.TYPE_APP_LINK && url.contains(USER_CENTER_FAQ)) {
                    WebActivity.startWebPage(activity, url);
                    return UrlOperatorResult.getNew();
                }
                return null;
            }
        };
        return operator;
    }

    /**
     * 判断普通的网页链接是否为游戏详情页(如:banner图配置游戏详情页)
     */
    public static IOverrideUrlOperator createH(final Context context) {
        IOverrideUrlOperator operator = new IOverrideUrlOperator() {
            @Override
            public UrlOperatorResult handldUrl(WebView view, String url, int type) {
                if (!TextUtils.isEmpty(url) && url.contains(".apk&igd=")) {
                    String[] split = url.split("&igd=");
                    if (split != null && split.length > 1) {
                        WebActivity.startGameDetailById(context, split[1]);
                        return UrlOperatorResult.getNew();
                    } else {
                        CHToast.show(context, "当前游戏详情页无法打开");
                        return UrlOperatorResult.getDefault();
                    }
                }
                return null;
            }
        };
        return operator;
    }

    /**
     * 拦截 我的评论 跳转
     *
     * @param context
     * @return
     */
    public static IOverrideUrlOperator createI(final Context context) {
        IOverrideUrlOperator operator = new IOverrideUrlOperator() {
            @Override
            public UrlOperatorResult handldUrl(WebView view, String url, int type) {
                if (type == WebActivity.TYPE_MY_COMMENT) {
                    WebActivity.startWebPage(context, url);
                    return UrlOperatorResult.getNew();
                }
                return null;
            }
        };
        return operator;
    }

    /**
     * 拦截 论坛更多评论 跳转
     *
     * @param context
     * @return
     */
    public static IOverrideUrlOperator createForumMore(final Context context) {
        IOverrideUrlOperator operator = new IOverrideUrlOperator() {
            @Override
            public UrlOperatorResult handldUrl(WebView view, String url, int type) {
                if (url.contains(OPEN_COMMENT_FORUM)) {
                    WebActivity.startWebPage(context, url);
                    return UrlOperatorResult.getNew();
                }
                return null;
            }
        };
        return operator;
    }

    public interface IOverrideUrlOperator {
        UrlOperatorResult handldUrl(WebView view, String url, int type);
    }

    public static class UrlOperatorResult {
        public boolean startNewActivtiy;

        public static UrlOperatorResult getDefault() {
            UrlOperatorResult result = new UrlOperatorResult();
            result.startNewActivtiy = false;
            return result;
        }

        public static UrlOperatorResult getNew() {
            UrlOperatorResult result = new UrlOperatorResult();
            result.startNewActivtiy = true;
            return result;
        }
    }
}
