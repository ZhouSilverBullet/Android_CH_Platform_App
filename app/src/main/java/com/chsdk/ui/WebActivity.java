package com.chsdk.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.article.ReadArticleEntry;
import com.caohua.games.biz.article.ReadArticleLogic;
import com.caohua.games.biz.bbs.ForumShareEntry;
import com.caohua.games.biz.comment.CommentScrollEntry;
import com.caohua.games.biz.comment.LoginUpdateEntry;
import com.caohua.games.biz.comment.TimesEntry;
import com.caohua.games.biz.comment.TimesPraiseEntry;
import com.caohua.games.biz.comment.UpdateCommentEntry;
import com.caohua.games.biz.comment.UpdatePraiseEntry;
import com.caohua.games.biz.minegame.DealTipsLogic;
import com.caohua.games.biz.minegame.LogoutEntry;
import com.caohua.games.biz.minegame.TipsEntry;
import com.caohua.games.biz.share.CHShareLayout;
import com.caohua.games.ui.HomePagerActivity;
import com.caohua.games.ui.account.AccountHomePageActivity;
import com.caohua.games.ui.bbs.MorePopView;
import com.caohua.games.ui.emoji.FaceRelativeLayout;
import com.caohua.games.ui.imagewatch.ImageWatchActivity;
import com.caohua.games.ui.widget.BlankLoginView;
import com.caohua.games.views.main.WelcomeActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.WebParamEntry;
import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.biz.web.GameDetailLogic;
import com.chsdk.biz.web.UrlOperatorHelper;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.UserEntry;
import com.chsdk.model.app.LinkModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.RiffEffectImageButton;
import com.chsdk.utils.LogUtil;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.DownloadParams;

import pub.devrel.easypermissions.EasyPermissions;

public class WebActivity extends BaseWebActivity {

    public static final int TYPE_OPEN_WEB = 1;
    public static final int TYPE_APP_LINK = 5;
    public static final int TYPE_GAME_DETAIL = 7;
    public static final int TYPE_GAME_DETAIL_BY_ID = 8;
    public static final int TYPE_GAME_DETAIL_BY_OUT_H5 = 11;
    public static final int TYPE_MY_COMMENT = 9;
    public static final int TYPE_TIPS = 10;
    protected static final int TYPE_GET_PARAMS = 12;


    private static final String IGD_ID = "igd_id";
    private static final String DOWNLOAD_ENTRY = "download_entry";
    private static final String ARTICLE_ID = "article_id";
    private static final String FORUM_SHARE_ENTRY = "forum_share_entry";

    private String TAG = getClass().getSimpleName();
    private String commentUrlTarget;

    private FaceRelativeLayout faceRLayout;
    private DownloadEntry downloadEntry;
    private UrlOperatorHelper urlOperatorHelper;
    private TimesEntry mTimesEntry;
    private View imageMore;
    private View imageFaq;
    private ForumShareEntry forumShareEntry;
    private String forumShareArticleId;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (faceRLayout != null && faceRLayout.isShouldHideInput(v, ev)) {
                faceRLayout.hideSoftKey();
                faceRLayout.hideContent();
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (faceRLayout != null && faceRLayout.getShowContent()) {
            faceRLayout.hideFaceLayout();
            return;
        }
        if (!AppContext.getAppContext().isRun()) {
            Intent i = new Intent(this, WelcomeActivity.class);
            startActivity(i);
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.removeJavascriptInterface("Comment");
        }
        super.onDestroy();
        forumShareEntry = null;
        forumShareArticleId = null;
        if (faceRLayout != null) {
            faceRLayout.setCommentDetail(false);
            faceRLayout.release();
        }
    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1f; //0.0-1.0
        getWindow().setAttributes(lp);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void startLoad() {
        if (type == TYPE_GAME_DETAIL_BY_ID) {
            getDownloadEntry();
        } else {
            if (loadUrlByPost() || type == TYPE_GET_PARAMS) {
                if (!blankLoginView.isLogin()) {
                    tvTitle.setText("请登录");
                    blankLoginView.show(new BlankLoginView.BlankLoginListener() {
                        @Override
                        public void onBlankLogin(LoginUserInfo info) {
                            tvTitle.setText("");
                            startLoad();
                        }
                    });
                    return;
                }
            }

            if (type == TYPE_GET_PARAMS) {
                LinkModel model = new LinkModel();
                if (intent != null) {
                    WebParamEntry webParam = (WebParamEntry) intent.getSerializableExtra("web_param");
                    if (webParam != null && webParam.getMap() != null && webParam.getMap().size() > 0) {
                        model.getDataMap().putAll(webParam.getMap());
                    }
                }
                String params = "data=" + RequestExe.getBodyParameter(model.getDataMap());
                url = url + "?" + params;
            }
            super.startLoad();
        }
    }

    @Override
    protected void handleDownload(String url) {
        if (type == TYPE_GAME_DETAIL || type == TYPE_GAME_DETAIL_BY_ID || type == TYPE_GAME_DETAIL_BY_OUT_H5) {
            if (downloadEntry != null) {
                if (TextUtils.isEmpty(downloadEntry.downloadUrl)) {
                    CHToast.show(this, "下载出错,下载地址为空");
                    return;
                }
                hasWifiDownload();
            }
        } else {
            openActionUri(url);
        }
    }

    private void download() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "请授予手机读写权限", HomePagerActivity.PERMISSION_FOR_WRITE_STORAGE, perms);
            return;
        }
        CHToast.show(this, "正在下载中，可在广场右上角下载管理中查看进度");
        DownloadParams params = new DownloadParams();
        params.url = downloadEntry.downloadUrl;
        params.title = downloadEntry.title;
        params.pkg = downloadEntry.pkg;
        params.iconUrl = downloadEntry.iconUrl;
        FileDownloader.start(params);
    }

    private boolean hasWifiDownload() {
        boolean wifiDownload = DataStorage.getWifiDownload(this);
        if (wifiDownload && getNetworkType() != ConnectivityManager.TYPE_WIFI) {
            final CHAlertDialog chAlertDialog = new CHAlertDialog((Activity) this);
            chAlertDialog.show();
            chAlertDialog.setContent("你确定在非wifi状态下载吗?");
            chAlertDialog.setCancelButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chAlertDialog.dismiss();
                }
            });
            chAlertDialog.setOkButton("确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CHToast.show(WebActivity.this, "建议您在wifi条件下载");
                    download();
                    chAlertDialog.dismiss();
                }
            });
        } else {
            download();
        }
        return false;
    }

    private int getNetworkType() {
        ConnectivityManager connectMgr = (ConnectivityManager) AppContext.getAppContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectMgr != null) {
            NetworkInfo info = connectMgr.getActiveNetworkInfo();
            if (info != null) {
                return info.getType();
            } else {
                return -1;
            }
        }
        return -1;
    }

    @Override
    protected void handlePageStarted(String url) {
        showCommentDialog(url, true, false);
    }

    @Override
    protected boolean handleUrlLoading(WebView view, String url) {
        if (urlOperatorHelper != null) {
            UrlOperatorHelper.UrlOperatorResult result = urlOperatorHelper.handle(view, url, type);
            showCommentDialog(url, false, result != null ? result.startNewActivtiy : false);
            return result != null;
        }
        showCommentDialog(url, false, false);
        return false;
    }

    @Override
    protected void initVariables(Intent intent) {
        super.initVariables(intent);
        Uri data = intent.getData();
        if (data != null) {
            String query = data.getQuery();
            if (!TextUtils.isEmpty(query)) {
                //我的成长  http://passport.sdk.caohua.com/grow/index
                if (query.contains("passport-sdk.caohua.com/grow/index")) { //我的成长
                    if (AppContext.getAppContext().isLogin()) {
                        BaseModel baseModel = new BaseModel() {
                            @Override
                            public void putDataInMap() {
                                put(PARAMS_USER_ID, SdkSession.getInstance().getUserId());
                                put(PARAMS_TOKEN, SdkSession.getInstance().getToken());
                            }
                        };
                        String parameter = "data=" + RequestExe.getBodyParameter(baseModel.getDataMap());
                        url = "https://passport-sdk.caohua.com/grow/index?" + parameter;
                    } else {
                        CHToast.show(AppContext.getAppContext(), "请先登录，才能进入我的成长界面");
                        if (!AppContext.getAppContext().isRun()) {
                            intent = new Intent(WebActivity.this, HomePagerActivity.class);
                            intent.putExtra(WelcomeActivity.WELCOME_OPEN_HOME, 3);
                            startActivity(intent);
                        }
                        finish();
                    }
                } else if (query.contains("m.caohua.com/gift/index")) {  //礼包列表
                    url = "https://m.caohua.com/gift/index?fr=app";
                } else if (query.contains("m.caohua.com/article/detail")) {  //文章启动
                    try {
                        url = query.split("detail_url=")[1];
                    } catch (Exception e) {
                        CHToast.show(AppContext.getAppContext(), "地址访问可能不存在");
                        if (!AppContext.getAppContext().isRun()) {
                            intent = new Intent(WebActivity.this, HomePagerActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    }
                } else if (query.contains("wap.caohua.com/forum/articleDetail")) {  //论坛文章
                    forumShareArticleId = data.getQueryParameter("article_id");
                    url = data.getQueryParameter("detail_url");
                    String title = data.getQueryParameter("title");
                    String game_icon = data.getQueryParameter("game_icon");
                    String forum_name = data.getQueryParameter("forum_name");
                    forumShareEntry = new ForumShareEntry();
                    forumShareEntry.setTitle(title);
                    forumShareEntry.setGameIcon(game_icon);
                    forumShareEntry.setGameName(forum_name);
                    String fr = data.getQueryParameter("fr");
                    if (!TextUtils.isEmpty(fr)) {
                        url = url + "fr=" + fr;
                    }
                } else if (query.contains("m.caohua.com/game/detail")) { //game_details
                    // 链接? 后面一定是?id=2xx 这样
                    String iconUrl = data.getQueryParameter("game_icon"); //  game_icon
                    String downloadUrl = data.getQueryParameter("game_url"); //  game_url
                    String pkg = data.getQueryParameter("package_name");  // package_name
                    String game_name = data.getQueryParameter("game_name"); // game_name
                    String detail_url = data.getQueryParameter("detail_url"); //detail_url
                    String type = data.getQueryParameter("type");
                    this.type = TYPE_GAME_DETAIL_BY_OUT_H5;
                    if (!TextUtils.isEmpty(pkg)) {  //pkg不为空的话
                        downloadEntry = new DownloadEntry();
                        String fr = data.getQueryParameter("fr");
                        if (!TextUtils.isEmpty(fr)) {
                            url = detail_url + "&type=" + type + "&fr=" + fr;
                        } else {
                            url = detail_url;
                        }
                        downloadEntry.setDetail_url(url);
                        downloadEntry.setTitle(game_name);
                        downloadEntry.setPkg(pkg);
                        downloadEntry.setDownloadUrl(downloadUrl);
                        downloadEntry.setIconUrl(iconUrl);
                    } else {
                        if (!AppContext.getAppContext().isRun()) {
                            CHToast.show(AppContext.getAppContext(), "地址访问可能不存在");
                            Intent i = new Intent(this, WelcomeActivity.class);
                            startActivity(i);
                        }
                    }
                } else {
                    //最后，如果没有匹配，就直接进入app
                    if (!AppContext.getAppContext().isRun()) {
                        Intent i = new Intent(this, WelcomeActivity.class);
                        startActivity(i);
                    }
                }
            }
        } else {
            String webUrl = intent.getStringExtra("web_url");
            if (!TextUtils.isEmpty(webUrl)) {
                url = webUrl;
            }
        }
    }

    @Override
    protected void initChildData() {
        if (type == TYPE_GAME_DETAIL) {
            downloadEntry = (DownloadEntry) intent.getSerializableExtra(DOWNLOAD_ENTRY);
            url = downloadEntry.getDetail_url();
        }

        if (type == TYPE_GAME_DETAIL_BY_ID) {
            // 伪造url,否则父类判空会finish
            url = String.valueOf(TYPE_GAME_DETAIL_BY_ID);
        }
        urlOperatorHelper = new UrlOperatorHelper(this);
        urlOperatorHelper.create();
    }

    @Override
    protected boolean loadUrlByPost() {
        return type == TYPE_APP_LINK || type == TYPE_MY_COMMENT || type == TYPE_TIPS;
    }

    @Override
    protected void initChildView() {
        mWebView.addJavascriptInterface(new CommentJSInvoke(), "Comment");
        mTimesEntry = (TimesEntry) intent.getSerializableExtra("timesEntry");
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = ((WebView) v).getHitTestResult();
                if (result == null) {
                    return true;
                }
                int type = result.getType();
                String extra = result.getExtra();
                if (type == WebView.HitTestResult.UNKNOWN_TYPE)
                    return false;
                if (type == WebView.HitTestResult.SRC_ANCHOR_TYPE) { // 超链接
                    if (mWebView != null && !TextUtils.isEmpty(extra)) {
                        Context context = mWebView.getContext();
                        ClipboardManager copyManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        copyManager.setText(extra);
                        CHToast.show(context, "复制成功");
                    }
                }
                return true;
            }
        });
    }

    /**
     * 私有方法
     */

    private void showCommentDialog(String url, boolean pageStart, boolean newActivity) {
        if (TextUtils.isEmpty(url))
            return;

        // 拦截跳转时，若是新开一个页面，则不隐藏当前页面的评论框
        if (!pageStart && newActivity) {
            return;
        }

        LogUtil.errorLog(TAG, "URL = " + url);
        if (url.contains("sdk.caohua.com/ucenter/contactKefu")) {
            if (imageFaq == null) {
                imageFaq = getView(R.id.ch_web_image_faq);
                imageFaq.setVisibility(View.VISIBLE);
            }
            imageFaq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebActivity.startAppLink(WebActivity.this, "https://app-sdk.caohua.com/ucenter/faq");
                }
            });
        }

        if (url.contains("sdk.caohua.com/coupon/myCoupon")) {

            if (imageFaq == null) {
                imageFaq = getView(R.id.ch_web_image_faq);
                imageFaq.setVisibility(View.VISIBLE);
            }
            imageFaq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WebActivity.startAppLink(WebActivity.this, "https://app-sdk.caohua.com/coupon/couponHelp");
                }
            });
        }

        // 游戏详情页和新闻页具有评论功能
        if (url.contains(UrlOperatorHelper.OPEN_WEB_DETAIL) ||
                url.contains("wap.caohua.com") ||
                url.contains(UrlOperatorHelper.OPEN_WEB_MORE_COMMENT) ||
                url.contains(UrlOperatorHelper.OPEN_USER_CENTER_COMMENT) ||
                url.contains(UrlOperatorHelper.OPEN_ARTICLE_DETAIL) ||
                url.contains(UrlOperatorHelper.OPEN_ARTICLE_FORUM) ||
                url.contains(UrlOperatorHelper.OPEN_COMMENT_FORUM)) {
            if (faceRLayout == null) {
                faceRLayout = getView(R.id.ch_emoji_rl_bottom);
            }
            LogUtil.errorLog("url -- " + url);
            if (url.contains(UrlOperatorHelper.OPEN_USER_CENTER_COMMENT)) {
                faceRLayout.setCommentDetail(true);
                faceRLayout.setVisibility(View.GONE);
                commentUrlTarget = "OPEN_USER_CENTER_COMMENT";
                faceRLayout.setCommentUrlTarget(commentUrlTarget);
                return;
            }
            if (url.contains(UrlOperatorHelper.OPEN_WEB_DETAIL)) {
                mTimesEntry = parseCommentURL(url, "1");
                faceRLayout.setVisibility(View.VISIBLE);
                commentUrlTarget = "OPEN_WEB_DETAIL";
            }

            if (url.contains(UrlOperatorHelper.OPEN_ARTICLE_DETAIL)) {
                mTimesEntry = parseCommentURL(url, "2"); // 固定的
                if (mTimesEntry != null) {
                    mTimesEntry.setType("0");  // 固定的
                }
                faceRLayout.setVisibility(View.VISIBLE);
                commentUrlTarget = "OPEN_ARTICLE_DETAIL";
                if (imageCollect == null) {
                    imageCollect = ((RiffEffectImageButton) findViewById(R.id.ch_web_image_collect));
                    imageCollect.setVisibility(View.VISIBLE);
                }
            }

            if (url.contains(UrlOperatorHelper.OPEN_WEB_MORE_COMMENT)) {
                if (mTimesEntry == null) {
                    mTimesEntry = parsePushCommentURL(url); // 固定的
                }

                faceRLayout.setCommentDetail(true);
                faceRLayout.setVisibility(View.GONE);
                commentUrlTarget = "OPEN_WEB_MORE_COMMENT";
            }

            //论坛
            if (url.contains(UrlOperatorHelper.OPEN_ARTICLE_FORUM)) {
                faceRLayout.setVisibility(View.VISIBLE);
                if (imageMore == null) {
                    imageMore = getView(R.id.ch_web_image_more);
                }
                imageMore.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(forumShareArticleId)) {
                    forumShareArticleId = intent.getStringExtra(ARTICLE_ID);
                }

                if (TextUtils.isEmpty(forumShareArticleId)) {
                    forumShareArticleId = getArticleIdToUrl(url);
                }
                if (forumShareEntry == null) {
                    forumShareEntry = (ForumShareEntry) intent.getSerializableExtra(FORUM_SHARE_ENTRY);
                }
                if (forumShareEntry == null) {
                    forumShareEntry = new ForumShareEntry();
                    forumShareEntry.setGameIcon("");
                    forumShareEntry.setGameName("草花手游");
                    forumShareEntry.setTitle("草花手游");
                }
                int position = intent.getIntExtra("position", -1);
                forumShareEntry.setShareUrl(mWebView.getUrl());
                logicForumCommentPraise(forumShareArticleId);
                commentUrlTarget = "OPEN_ARTICLE_FORUM";
                faceRLayout.setCommentUrlTarget(commentUrlTarget);
                faceRLayout.setForumShareEntry(forumShareEntry, position);
            }

            if (url.contains(UrlOperatorHelper.OPEN_COMMENT_FORUM)) {
                faceRLayout.setCommentDetail(true);
                faceRLayout.setVisibility(View.GONE);
                commentUrlTarget = "OPEN_COMMENT_FORUM";
                String[] split = url.split("id=");
                String commentId = "";
                if (split.length >= 2) {
                    commentId = split[1];
                }
                faceRLayout.setCommentID(commentId);
//                logicForumCommentPraise(articleId);
            }

            faceRLayout.timesComment(mTimesEntry, imageCollect);
            faceRLayout.setTimesEntry(mTimesEntry);
            faceRLayout.setCommentUrlTarget(commentUrlTarget);
        } else {
            // 详情页或新闻页点击下载apk时,不隐藏评论框
            if (!url.contains(".apk") && faceRLayout != null) {
                faceRLayout.setVisibility(View.GONE);
            }
        }
    }

    private void logicForumCommentPraise(final String articleId) {
        ReadArticleLogic logic = new ReadArticleLogic();
        logic.read(articleId, new BaseLogic.DataLogicListner() {
            @Override
            public void failed(String errorMsg, int errorCode) {
//                CHToast.show(WebActivity.this, errorMsg);
            }

            @Override
            public void success(Object entryResult) {
                if (isFinishing()) {
                    return;
                }
                if (entryResult instanceof ReadArticleEntry) {
                    final ReadArticleEntry articleEntry = (ReadArticleEntry) entryResult;
                    faceRLayout.forumCommentAndPraise(articleEntry, articleId);
                    imageMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new MorePopView(WebActivity.this, v, articleEntry, mWebView.getUrl(), articleId);
                        }
                    });
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateJSPraise(final UpdatePraiseEntry entry) {
        if (mWebView != null && entry != null) {
            if (entry.getCommentTarget() != null && commentUrlTarget.equals(entry.getCommentTarget())) {
                LogUtil.errorLog(TAG, "javascript:editStatus = " + entry.getCommentID());
                mWebView.loadUrl("javascript:editStatus('" + entry.getCommentID() + "')");
                mWebView.loadUrl("javascript:dianZanOk('" + entry.getCommentID() + "')");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateJSComment(UpdateCommentEntry entry) {
        if (mWebView != null && entry != null) {
            if (entry.getCommentTarget() != null && commentUrlTarget.equals(entry.getCommentTarget())) {
                LogUtil.errorLog(TAG, "javascript:handleComment= " + entry.getCommentID());
                mWebView.loadUrl("javascript:handleComment('" + entry.getCommentID() + "')");
                mWebView.loadUrl("javascript:commentOk('" + entry.getCommentID() + "')");
            }
        }
    }

    @Subscribe
    public void updateJSCommentScroll(CommentScrollEntry entry) {
        if (mWebView != null && entry != null) {
            LogUtil.errorLog(TAG, "CommentScrollEntry    = ");
            if (entry.getCommentTarget() != null && commentUrlTarget.equals(entry.getCommentTarget())) {
                mWebView.loadUrl("javascript:scrollToComment()");
                mWebView.loadUrl("javascript:gotoComment()");
            }

        }
    }

    @Subscribe
    public void updateLogin(LoginUpdateEntry entry) {
        if (mWebView != null && entry != null) {
            mWebView.loadUrl("javascript:reloadComment('" + AppContext.getAppContext().getUser().userId + "')");
        }
    }

    /**
     * https://m.caohua.com/comment/detail?id=1121262&ui=50002448&ct=1&ctid=291&fr=app
     *
     * @param url
     * @return
     */

    private TimesEntry parsePushCommentURL(String url) {
        String[] split = url.split("\\?");
        if (split.length > 1 && !TextUtils.isEmpty(split[1])) { //id=1121262&ui=50002448&ct=1&ctid=291&fr=app
            String[] s = split[1].split("=");  //id, 1121262&ui, 50002448&ct,1&ctid,291&fr,app
            if (s.length >= 6 && !TextUtils.isEmpty(s[1]) && !TextUtils.isEmpty(s[3]) && !TextUtils.isEmpty(s[4])) {
                TimesEntry timesEntry = new TimesEntry();
                timesEntry.setId(s[4].split("&")[0]);
                timesEntry.setType("1");
                timesEntry.setCommentType(s[3].split("&")[0]);
                return timesEntry;
            } else {
                LogUtil.errorLog(TAG, "timeEntry解析出错");
            }
        } else {
            LogUtil.errorLog(TAG, "timeEntry解析出错");
        }
        return null;
    }

    /**
     * 解析url 出 id 和 type
     *
     * @param url
     * @return
     */
    private TimesEntry parseCommentURL(String url, String commentType) {
        String[] split = url.split("\\?");
        if (split.length > 1 && !TextUtils.isEmpty(split[1])) {
            String[] s = split[1].split("=");
            if (s.length > 2 && !TextUtils.isEmpty(s[1]) && !TextUtils.isEmpty(s[2])) {
                TimesEntry timesEntry = new TimesEntry();
                timesEntry.setId(s[1].split("&")[0]);
                timesEntry.setType(s[2].split("&")[0]);
                timesEntry.setCommentType(commentType);
                return timesEntry;
            } else {
                LogUtil.errorLog(TAG, "timeEntry解析出错");
            }
        } else {
            LogUtil.errorLog(TAG, "timeEntry解析出错");
        }
        return null;
    }


    private void getDownloadEntry() {
        final String id = intent.getStringExtra(IGD_ID);
        showProgress();
        GameDetailLogic gameDetail = new GameDetailLogic(id, new GameDetailLogic.ActivityStatusListener() {
            @Override
            public void closeActivity() {
                finish();
            }

            @Override
            public void stopProgress() {
                hideProgress();
            }

            @Override
            public void setEntry(DownloadEntry entry) {
                downloadEntry = entry;
                url = downloadEntry.getDetail_url();
                if (mWebView != null) {
                    mWebView.loadUrl(url);
                }
            }

            @Override
            public void postAction(long time, Runnable action) {
                postUIAction(time, action);
            }
        });
        gameDetail.getEntry();
    }


    /**
     * 公开的静态方法
     */

    public static void startAppLink(Context context, String url) {
        Intent intent = new Intent();
        intent.putExtra(KEY_TYPE, TYPE_APP_LINK);
        intent.putExtra(KEY_URL, url);
        intent.setClass(context, WebActivity.class);
        context.startActivity(intent);
    }

    public static void startMessageCenter(Context context, String url, TipsEntry entry) {
        Intent intent = new Intent();
        intent.putExtra(KEY_TYPE, TYPE_APP_LINK);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_TIPS_ENTRY, entry);
        intent.setClass(context, WebActivity.class);
        context.startActivity(intent);
    }

    public static void startMyComment(Context context, String url) {
        Intent intent = new Intent();
        intent.putExtra(KEY_TYPE, TYPE_MY_COMMENT);
        intent.putExtra(KEY_URL, url);
        intent.setClass(context, WebActivity.class);
        context.startActivity(intent);
    }

    public static void startWebPage(Context context, String url) {
        UrlOperatorHelper.IOverrideUrlOperator operator = UrlOperatorHelper.createH(context);
        UrlOperatorHelper.UrlOperatorResult result = operator.handldUrl(null, url, TYPE_OPEN_WEB);
        if (result != null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(KEY_TYPE, TYPE_OPEN_WEB);
        intent.putExtra(KEY_URL, url);
        intent.setClass(context, WebActivity.class);
        context.startActivity(intent);
    }

    /**
     * 和linkList相反
     *
     * @param context
     * @param url
     */
    public static void startWebPageGetParam(Context context, String url, WebParamEntry entry) {
        UrlOperatorHelper.IOverrideUrlOperator operator = UrlOperatorHelper.createH(context);
        UrlOperatorHelper.UrlOperatorResult result = operator.handldUrl(null, url, TYPE_GET_PARAMS);
        if (result != null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("web_param", entry);
        intent.putExtra(KEY_TYPE, TYPE_GET_PARAMS);
        intent.putExtra(KEY_URL, url);
        intent.setClass(context, WebActivity.class);
        context.startActivity(intent);
    }

    public static void startWelcomeWebPage(Context context, String url) {
        UrlOperatorHelper.IOverrideUrlOperator operator = UrlOperatorHelper.createH(context);
        UrlOperatorHelper.UrlOperatorResult result = operator.handldUrl(null, url, TYPE_OPEN_WEB);
        if (result != null) {
            return;
        }
        if (AppContext.getAppContext().isRun()) {
            Intent intent = new Intent();
            intent.putExtra(KEY_TYPE, TYPE_OPEN_WEB);
            intent.putExtra(KEY_URL, url);
            intent.setClass(context, WebActivity.class);
            context.startActivity(intent);
        } else {
            Intent intent1 = new Intent(context, HomePagerActivity.class);
            Intent intent2 = new Intent();
            intent2.putExtra(KEY_TYPE, TYPE_OPEN_WEB);
            intent2.putExtra(KEY_URL, url);
            intent2.setClass(context, WebActivity.class);
            context.startActivities(new Intent[]{intent1, intent2});
        }
    }

    public static void startForForumPage(Context context, String url, String article_id, ForumShareEntry entry, int position) {
        UrlOperatorHelper.IOverrideUrlOperator operator = UrlOperatorHelper.createH(context);
        UrlOperatorHelper.UrlOperatorResult result = operator.handldUrl(null, url, TYPE_OPEN_WEB);
        if (result != null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(KEY_TYPE, TYPE_OPEN_WEB);
        intent.putExtra(ARTICLE_ID, article_id);
        intent.putExtra(FORUM_SHARE_ENTRY, entry);
        intent.putExtra(KEY_URL, url);
        intent.putExtra("position", position);
        intent.setClass(context, WebActivity.class);
        context.startActivity(intent);
    }

    public static void startWelcomeForForumPage(Context context, String url, String article_id, ForumShareEntry entry, int position) {
        UrlOperatorHelper.IOverrideUrlOperator operator = UrlOperatorHelper.createH(context);
        UrlOperatorHelper.UrlOperatorResult result = operator.handldUrl(null, url, TYPE_OPEN_WEB);
        if (result != null) {
            return;
        }
        Intent intent1 = new Intent(context, HomePagerActivity.class);
        Intent intent2 = new Intent();
        intent2.putExtra(KEY_TYPE, TYPE_OPEN_WEB);
        intent2.putExtra(ARTICLE_ID, article_id);
        intent2.putExtra(FORUM_SHARE_ENTRY, entry);
        intent2.putExtra(KEY_URL, url);
        intent2.putExtra("position", position);
        intent2.setClass(context, WebActivity.class);
        context.startActivities(new Intent[]{intent1, intent2});
    }

    public static void startCommentDetail(Context context, String url, TimesEntry timesEntry) {
        UrlOperatorHelper.IOverrideUrlOperator operator = UrlOperatorHelper.createH(context);
        UrlOperatorHelper.UrlOperatorResult result = operator.handldUrl(null, url, TYPE_OPEN_WEB);
        if (result != null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("timesEntry", timesEntry);
        intent.putExtra(KEY_TYPE, TYPE_OPEN_WEB);
        intent.putExtra(KEY_URL, url);
        intent.setClass(context, WebActivity.class);
        context.startActivity(intent);
    }

    public static void startGameDetail(Context context, DownloadEntry downloadEntry) {
        if (downloadEntry == null) {
            CHToast.show(context, "当前游戏详情页无法打开");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(KEY_TYPE, TYPE_GAME_DETAIL);
        intent.putExtra(DOWNLOAD_ENTRY, downloadEntry);
        intent.setClass(context, WebActivity.class);
        context.startActivity(intent);
    }

    public static void startWelcomeGameDetail(Context context, DownloadEntry downloadEntry) {
        if (downloadEntry == null) {
            CHToast.show(context, "当前游戏详情页无法打开");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(KEY_TYPE, TYPE_GAME_DETAIL);
        intent.putExtra(DOWNLOAD_ENTRY, downloadEntry);
        intent.setClass(context, WebActivity.class);
        context.startActivity(intent);
    }

    public static void startGameDetailById(Context context, String id) {
        if (TextUtils.isEmpty(id)) {
            CHToast.show(context, "当前游戏详情页无法打开");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(KEY_TYPE, TYPE_GAME_DETAIL_BY_ID);
        intent.putExtra(IGD_ID, id);
        intent.setClass(context, WebActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void parseHTML(WebView view) {
//        String url = mWebView.getUrl();
//        if (!TextUtils.isEmpty(url) && (url.contains(UrlOperatorHelper.OPEN_ARTICLE_FORUM) ||
//                url.contains(UrlOperatorHelper.OPEN_COMMENT_FORUM))) {
//
//        }
    }

    public String getArticleIdToUrl(String url) {
        String article_id = "";
        if (mWebView != null) {
            try {
                String[] split = url.split("id=");
                article_id = split[1];
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return article_id;
    }

    public class CommentJSInvoke {

        @JavascriptInterface
        public String getLoginInfo() {
            String login = "0";
            if (AppContext.getAppContext().isLogin()) {
                login = AppContext.getAppContext().getUser().userId;
            }
            return login;
        }

        @JavascriptInterface
        public String getApp() {
            return "app";
        }

        @JavascriptInterface
        public void logout() {
            if (mWebView != null) {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new LogoutEntry());
                        DataStorage.setAppLogin(WebActivity.this, false);
                        if (mWebView != null) {
                            mWebView.loadUrl("javascript:reloadPage()");
                        }
                    }
                });
            }
        }

        @JavascriptInterface
        public void login() {
            AppContext.getAppContext().login(WebActivity.this, new TransmitDataInterface() {
                @Override
                public void transmit(Object o) {
                    if (o instanceof LoginUserInfo) {
                        final LoginUserInfo info = (LoginUserInfo) o;
                        LogUtil.errorLog(TAG, info.nickName + "---" + info.userName + "-------" + info.userId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mWebView.loadUrl("javascript:reloadComment('" + info.userId + "')");
                            }
                        });
                    }
                }
            });
        }

        @JavascriptInterface
        public void actLogin() {
            AppContext.getAppContext().login(WebActivity.this, new TransmitDataInterface() {
                @Override
                public void transmit(Object o) {
                    if (o instanceof LoginUserInfo) {
                        final LoginUserInfo info = (LoginUserInfo) o;
                        LogUtil.errorLog(TAG, info.nickName + "---" + info.userName + "-------" + info.userId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mWebView.loadUrl("javascript:reloadPage()");
                            }
                        });
                    }
                }
            });
        }
        //Comment.setArgs(cid)//评论
        //Comment.praise(cid)//点赞

        @JavascriptInterface
        public void setArgs(final String cid) {
            if (faceRLayout != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        faceRLayout.setVisibility(View.VISIBLE);
                        faceRLayout.setCommentID(cid);
                        LogUtil.errorLog(TAG, "--cid----" + cid);
                        faceRLayout.openSoft();
                    }
                });
            }
        }

        @JavascriptInterface
        public void setArgs(final String cid, final String ct, final String ctid, final String cgt) {
            if (faceRLayout != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        faceRLayout.setVisibility(View.VISIBLE);
                        faceRLayout.setCommentID(cid);
                        LogUtil.errorLog(TAG, "setArgs -- cid----" + cid);
                        faceRLayout.openSoft();
                        faceRLayout.setCommentEntryForJs(cid, ct, ctid, cgt);
                    }
                });
            }
        }

        @JavascriptInterface
        public void praise(final String cid, final String ct, final String ctid, final String cgt) {
            if (faceRLayout != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTimesEntry = new TimesEntry();
                        mTimesEntry.setType(cgt);
                        mTimesEntry.setCommentType(ct);
                        mTimesEntry.setId(ctid);
                        faceRLayout.praiseLike(mTimesEntry, cid);
                    }
                });
            }
        }

        @JavascriptInterface
        public void praise(String cid) {
            if (faceRLayout != null) {
                faceRLayout.praiseLike(mTimesEntry, cid);
            }
        }

        @JavascriptInterface
        public void jumpToMore(final String cid) {
            if (!TextUtils.isEmpty(cid)) {
                LogUtil.errorLog(TAG, "moreUrl = " + cid);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WebActivity.startCommentDetail(WebActivity.this, cid, mTimesEntry);
                    }
                });
            }
        }

        @JavascriptInterface
        public void dealTips(final int step) {
            if (step != 0 && mWebView != null) {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        new DealTipsLogic(new TipsEntry(), step).getDealTips(null);
                    }
                });
            }
        }

        @JavascriptInterface
        public void shareAct(final String url, final String content, final String title) {
            if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(content) && !TextUtils.isEmpty(title) && mWebView != null) {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        TimesPraiseEntry entry = new TimesPraiseEntry();
                        entry.setArticle_url(url);
                        entry.setArticle_title(title);
                        entry.setContent(content);
                        new CHShareLayout(WebActivity.this, entry).show();
                    }
                });
            }
        }

        @JavascriptInterface
        public void goToSubject(final int gameId) {
            if (gameId != 0 && mWebView != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HomePagerActivity.start(WebActivity.this, gameId);
                    }
                });
            }
        }

        @JavascriptInterface
        public String getAppVersion() {
            return SdkSession.getInstance().getSdkVersion();
        }

        @JavascriptInterface
        public void praiseForumComment(int commentId) {
            if (commentId != 0) {
                if (faceRLayout != null) {
                    faceRLayout.praiseForumLike(0, commentId, FaceRelativeLayout.PRAISE_FORUM_LIKE_FOR_H5);
                }
            }
        }

        @JavascriptInterface
        public void addForumCommmet(final int commentId) {
            if (faceRLayout != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        faceRLayout.setVisibility(View.VISIBLE);
                        faceRLayout.setCommentID(commentId + "");
                        faceRLayout.forumCommentAndPraise(new ReadArticleEntry(), "");
                        LogUtil.errorLog(TAG, "--cid----" + commentId);
                        faceRLayout.openSoft();
                    }
                });

            }
        }

        @JavascriptInterface
        public void praiseForumArticle(int articleId) {
            if (faceRLayout != null) {
                faceRLayout.praiseForumLike(articleId, 0);
            }
        }

        @JavascriptInterface
        public void copyForumContent(String content) {
            if (!TextUtils.isEmpty(content)) {
                ClipboardManager copyManager = (ClipboardManager) WebActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                copyManager.setText(content);
                CHToast.show(WebActivity.this, "复制成功");
            }
        }

        @JavascriptInterface
        public void openImage(final String url, final int userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(url) && userId != 0) {
                        AccountHomePageActivity.start(WebActivity.this, userId + "", "");
                        return;
                    }
                    Intent intent = new Intent(WebActivity.this, ImageWatchActivity.class);
                    intent.putExtra(ImageWatchActivity.IMAGE_URL, url);
                    startActivity(intent);
                }
            });
        }

        @JavascriptInterface
        public void h5ActLogin() {
            AppContext.getAppContext().login(WebActivity.this, new TransmitDataInterface() {
                @Override
                public void transmit(Object o) {
                    if (o instanceof LoginUserInfo) {
                        mWebView.post(new Runnable() {
                            @Override
                            public void run() {
                                h5InjectLoginParams();
                            }
                        });
                        final LoginUserInfo info = (LoginUserInfo) o;
                        LogUtil.errorLog(TAG, info.nickName + "---" + info.userName + "-------" + info.userId);
                    }
                }
            });
        }

        @JavascriptInterface
        public void isRefreshActLogin(final int status) {
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    LogUtil.errorLog(TAG, "刷新活动的时候是否重新登录" + status + "");
                    actIsRefresh = true;
                }
            });
        }

        /**
         * php 活动自动登陆
         *
         * @return
         */
        @JavascriptInterface
        public String getActLoginParams() {
            if (AppContext.getAppContext().isLogin()) {
                String userName = SdkSession.getInstance().getUserName();
                LoginUserInfo user = AppContext.getAppContext().getUser();
                String autoToken = user.autoToken;
                UserEntry userEntry = new UserEntry();
                userEntry.setAt(autoToken);
                userEntry.setAu(userName);
                String toJson = new Gson().toJson(userEntry);
                LogUtil.errorLog("WebActivity getActLoginParams(): " + toJson);
                return toJson;
            }
            return "";
        }

        @JavascriptInterface
        public void downloadForApk(String gameName, String gameIcon, String downloadUrl, String pkgName) {
            downloadEntry = new DownloadEntry();
            downloadEntry.setTitle(gameName);
            downloadEntry.setIconUrl(gameIcon);
            downloadEntry.setDownloadUrl(downloadUrl);
            downloadEntry.setPkg(pkgName);
        }
    }
}
