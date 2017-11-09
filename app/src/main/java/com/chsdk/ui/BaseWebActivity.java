package com.chsdk.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.minegame.TipsEntry;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.StoreSecondActivity;
import com.caohua.games.ui.account.AccountSettingActivity;
import com.caohua.games.ui.widget.BlankLoginView;
import com.caohua.games.ui.widget.TwoBallRotationProgressBar;
import com.caohua.games.views.main.WelcomeActivity;
import com.chsdk.biz.web.UrlOperatorHelper;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.app.LinkModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.h5.Cookie;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.RiffEffectImageButton;
import com.chsdk.ui.widget.VideoEnabledWebChromeClient;
import com.chsdk.ui.widget.VideoEnabledWebView;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.NetworkUtils;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月16日
 *          <p>
 */
public class BaseWebActivity extends BaseActivity {
    private static final String TAG = BaseWebActivity.class.getSimpleName();
    protected static final String KEY_TYPE = "type";
    protected static final String KEY_URL = "url";
    protected static final String KEY_TIPS_ENTRY = "tips_entry";

    protected VideoEnabledWebView mWebView;
    private VideoEnabledWebChromeClient webChromeClient;
    protected TextView tvTitle;
    private ImageView btnBack;
    private TwoBallRotationProgressBar imgProgress;
    private RelativeLayout nonVideoLayout;
    private EventListener eventListener;
    private boolean isOnPause;

    protected int type;
    protected String url;
    protected Intent intent;
    protected RiffEffectImageButton imageCollect;

    protected boolean actIsRefresh;
    protected BlankLoginView blankLoginView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        initData();
        initVariables(intent);
        if (TextUtils.isEmpty(url)) {
            finish();
            return;
        }

        initView();
        startLoad();
    }

    protected void initVariables(Intent intent) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (AccountSettingActivity.showLoginDialog) {
            finish();
            return;
        }

        if (!NetworkUtils.isNetworkConnected(this)) {
            CHToast.show(this, "请检查您当前的网络");
        }

        if (isOnPause) {
            isOnPause = false;
            if (mWebView != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mWebView.onResume();
                } else {
                    try {
                        mWebView.getClass().getMethod("onResume").invoke(mWebView, (Object[]) null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

//                if (!TextUtils.isEmpty(url) && url.contains(UrlOperatorHelper.ACCOUNT_SAFE_SETTING)) {
//                    postLoadUrl();
//                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isOnPause = true;
        if (mWebView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mWebView.onPause();
            } else {
                try {
                    mWebView.getClass().getMethod("onPause").invoke(mWebView, (Object[]) null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.setVisibility(View.GONE);
            nonVideoLayout.removeView(mWebView);
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.loadUrl("about:blank");
//			mWebView.removeAllViews();
            mWebView.freeMemory();
            mWebView.destroy();
            // mWebView.pauseTimers();
            mWebView = null;
            Cookie.clear();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (webChromeClient != null && !webChromeClient.onBackPressed()) {
                if (mWebView != null && mWebView.canGoBack() && !mWebView.getUrl().contains("Error.html")) {
                    mWebView.goBack();
                    return;
                }
            }
        } catch (Exception e) {
            LogUtil.errorLog("BaseWebActivity onBackPressed error:" + e.getMessage());
        }

        super.onBackPressed();
    }


    /**
     *  公用方法, 供子类重写
     */

    /**
     * 网页开始加载时调用
     */
    protected void handlePageStarted(String url) {
    }

    /**
     * 拦截网页跳转地址,不处理时返回false
     */
    protected boolean handleUrlLoading(WebView view, String url) {
        return false;
    }

    /**
     * 处理网页下载行为
     */
    protected void handleDownload(String url) {
    }

    /**
     * 展示进度条
     */
    protected void showProgress() {
        if (imgProgress == null) {
            imgProgress = getView(R.id.ch_web_progress_img);
        }
        imgProgress.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏进度条
     */
    protected void hideProgress() {
        if (imgProgress != null) {
            imgProgress.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化数据,如Intent传值获取
     */
    protected void initChildData() {
    }

    /**
     * 初始化控件
     */
    protected void initChildView() {
    }

    /**
     * 加载方式：post or get, 默认get
     */
    protected boolean loadUrlByPost() {
        return false;
    }

    /**
     * 主线程延迟执行
     */
    protected void postUIAction(long time, Runnable action) {
        if (imgProgress != null) {
            imgProgress.postDelayed(action, time);
        }
    }

    /**
     * 初始加载
     */
    protected void startLoad() {
        showProgress();
        if (!loadUrlByPost()) {
            mWebView.loadUrl(url);
        } else {
            postLoadUrl();
        }
    }

    /**
     * 打开本机应用
     */
    protected boolean openActionUri(String url) {
        Intent baseIntent = new Intent(Intent.ACTION_VIEW);
        baseIntent.setData(Uri.parse(url));
        if (baseIntent.resolveActivity(getPackageManager()) != null) {
            Intent chooserIntent = Intent.createChooser(baseIntent, "Select Application");
            if (chooserIntent != null) {
                try {
                    startActivity(chooserIntent);
                    return true;
                } catch (Exception e) {
                    LogUtil.errorLog("openActionUri error:" + e.getMessage());
                }
            }
        }
        CHToast.show(BaseWebActivity.this, "您的设备上未安装相关程序");
        return false;
    }

    /**
     * 私有方法
     */

    private void initData() {
        type = intent.getIntExtra(KEY_TYPE, 0);
        url = intent.getStringExtra(KEY_URL);
        initChildData();
    }

    private void initView() {
        setContentView(R.layout.ch_activity_proxy_web);

        tvTitle = getView(R.id.ch_web_title_tv);
        nonVideoLayout = getView(R.id.ch_web_layout);
        btnBack = getView(R.id.ch_web_title_back);
        blankLoginView = getView(R.id.ch_web_blank_login);
        btnBack.setOnClickListener(getEventListener());
        if (imgProgress == null) {
            imgProgress = getView(R.id.ch_web_progress_img);
        }
        setWebView();
        initChildView();
    }

    private void setWebView() {
        mWebView = new VideoEnabledWebView(this);
        nonVideoLayout.addView(mWebView, 0,
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ViewGroup videoLayout = getView(R.id.ch_web_video_layout);
        View loadingView = getLayoutInflater().inflate(R.layout.ch_view_loading_video, null);
        mWebView.setDownloadListener(getEventListener());
        webChromeClient = new CustomChromeClient(nonVideoLayout, videoLayout, loadingView, mWebView);
        webChromeClient.setOnToggledFullscreen(getEventListener());
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(new CustomWebViewClient());
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setScrollContainer(false);
        setWebViewSettings(mWebView.getSettings());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebViewSettings(WebSettings settings) {
        settings.setAllowFileAccess(true);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setBlockNetworkImage(true);
        settings.setAppCacheMaxSize(1024 * 1024 * 8);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String databasePath = mWebView.getContext().getDir("databases", Context.MODE_PRIVATE).getPath();
            settings.setDatabasePath(databasePath);
        }
    }

    private void setLoadProgress(int progress) {
        if (imgProgress == null)
            return;

        if (progress == 100) {
            imgProgress.setVisibility(View.GONE);
        } else {
            imgProgress.setVisibility(View.VISIBLE);
        }
    }

    private void setWebTitle(String title) {
        if (TextUtils.isEmpty(title) || tvTitle == null) {
            return;
        }

        if (url.contains(UrlOperatorHelper.OPEN_ARTICLE_DETAIL)) {
            tvTitle.setText("文章详情");
            return;
        }
        if (!isUrl(title)) {
            if ("shopmall".equals(title.trim().toLowerCase())) {
                title = "草花商城";
            }
            tvTitle.setText(title);
        }
    }

    private boolean isUrl(String title) {
        return title.contains(".com") || title.contains(".cn")
                || title.contains("www.") || title.contains("wap.")
                || title.contains("caohua.") || title.contains("http") || title.contains("https");
    }

    private void postLoadUrl() {
        LinkModel model = new LinkModel();
        TipsEntry entry = (TipsEntry) intent.getSerializableExtra(KEY_TIPS_ENTRY);
        if (entry != null) {
            model.getDataMap().put("consume", entry.getConsume() + "");
            model.getDataMap().put("recharge", entry.getRecharge() + "");
            model.getDataMap().put("msg", entry.getMsg() + "");
        }
        LoginUserInfo user = AppContext.getAppContext().getUser();
        if (AppContext.getAppContext().isLogin() && user != null) {
            model.getDataMap().put(BaseModel.PARAMS_USER_ID, user.userId);
        }
        String params = "data=" + RequestExe.getBodyParameter(model.getDataMap());
        mWebView.postUrl(url, params.getBytes());
    }

    private EventListener getEventListener() {
        if (eventListener == null) {
            eventListener = new EventListener();
        }
        return eventListener;
    }


    /**
     * 私有类
     */

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtil.debugLog(TAG, "onPageStarted:" + url);
            handlePageStarted(url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();// 接受证书
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return;
            }
            if (!AppContext.getAppContext().isNetworkConnected()) { // 或者： if(request.getUrl().toString() .equals(getUrl()))
                // 在这里显示自定义错误页
                view.loadUrl("file:///android_asset/CaoHuaSDK/Html/NoNetwork.html");
                return;
            }
            view.loadUrl("file:///android_asset/CaoHuaSDK/Html/Error.html");
            // 在这里显示自定义错误页
        }

        // 新版本，只会在Android6.0及以上调用
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (request.isForMainFrame() && !AppContext.getAppContext().isNetworkConnected()) { // 或者： if(request.getUrl().toString() .equals(getUrl()))
                // 在这里显示自定义错误页
                view.loadUrl("file:///android_asset/CaoHuaSDK/Html/NoNetwork.html");
                return;
            }
            if (request.isForMainFrame()) { // 或者： if(request.getUrl().toString() .equals(getUrl()))
                // 在这里显示自定义错误页
                view.loadUrl("file:///android_asset/CaoHuaSDK/Html/Error.html");
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (AppContext.getAppContext().isLogin() && !actIsRefresh && url.contains(UrlOperatorHelper.OPEN_ACT_ON_JAVA)) {
                h5InjectLoginParams();
            }
            String title = view.getTitle();
            if (mWebView != null) {
                mWebView.getSettings().setBlockNetworkImage(false);
            }
            setWebTitle(title);
            parseHTML(view);
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            LogUtil.debugLog(TAG, "shouldOverrideUrlLoading:" + url);
            if (!NetworkUtils.isNetworkConnected(BaseWebActivity.this)) {
                CHToast.show(BaseWebActivity.this, "请检查您当前的网络");
            }
            if (url.contains(StoreSecondActivity.SHOP_URL)) {
                Intent webIntent = new Intent(BaseWebActivity.this, StoreSecondActivity.class);
                webIntent.putExtra("url", url);
                webIntent.putExtra("type", 1);
                BaseWebActivity.this.startActivity(webIntent);
                return true;
            }

            if (url.contains("gm.caohua.com")) {
                Intent baseIntent = new Intent(Intent.ACTION_VIEW);
                baseIntent.setData(Uri.parse(url));
                if (baseIntent.resolveActivity(getPackageManager()) != null) {
                    Intent chooserIntent = Intent.createChooser(baseIntent, "Select Application");
                    if (chooserIntent != null) {
                        startActivity(chooserIntent);
                        finish();
                        return true;
                    }
                }
            }

            if (url.contains("wap.caohua.com")) {
                return false;
            }
            boolean handled = handleUrlLoading(view, url);
            if (!handled) {
                if (url.startsWith("http")) {
                    view.loadUrl(url);
                } else {
                    openActionUri(url);
                }
            }
            return true;
        }
    }

    protected void h5InjectLoginParams() {
        SdkSession session = SdkSession.getInstance();
        String userName = session.getUserName();
        String autoToken = AppContext.getAppContext().getUser().autoToken;
        String deviceNo = session.getDeviceNo() == null ? "" : session.getDeviceNo();
        String deviceId = "";
        String simNum = "";
        String androidId = "";
        String lg = "";
        String la = "";
        try {
            final Context context = session.getAppContext();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId() + ""; // imei
            simNum = tm.getSimSerialNumber() + "";
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID) + "";
        } catch (Exception e) {
        }

        mWebView.loadUrl("javascript:autoLoginParams('" + userName + "','" + autoToken + "','"
                + deviceNo + "','" + deviceId + "','"
                + simNum + "','" + androidId + "','"
                + lg + "','" + la + "')");
        LogUtil.errorLog("javascript:autoLoginParams('" + userName + "','" + autoToken + "','"
                + deviceNo + "','" + deviceId + "','"
                + simNum + "','" + androidId + "','"
                + lg + "','" + la + "')");
    }

    /**
     * 解析图片地址
     *
     * @param view
     */
    protected void parseHTML(WebView view) {

    }

    private class CustomChromeClient extends VideoEnabledWebChromeClient {

        public CustomChromeClient(View activityNonVideoView, ViewGroup activityVideoView, View loadingView,
                                  VideoEnabledWebView webView) {
            super(activityNonVideoView, activityVideoView, loadingView, webView);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            setLoadProgress(newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String sTitle) {
            super.onReceivedTitle(view, sTitle);
            if (sTitle != null && sTitle.length() > 0) {
                String title = view.getTitle();
                setWebTitle(title);
            }
        }
    }

    private class EventListener implements View.OnClickListener, DownloadListener,
            VideoEnabledWebChromeClient.ToggledFullscreenCallback {

        @Override
        public void onClick(View v) {
            if (v == btnBack) {
                if (!AppContext.getAppContext().isRun()) {
                    Intent intent = new Intent(BaseWebActivity.this, WelcomeActivity.class);
                    BaseWebActivity.this.startActivity(intent);
                }
                finish();
            }
        }

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            handleDownload(url);
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        @Override
        public void toggledFullscreen(boolean fullscreen) {
            if (fullscreen) {
                WindowManager.LayoutParams attrs = getWindow().getAttributes();
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                getWindow().setAttributes(attrs);
                if (android.os.Build.VERSION.SDK_INT >= 14) {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                }
            } else {
                WindowManager.LayoutParams attrs = getWindow().getAttributes();
                attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                getWindow().setAttributes(attrs);
                if (android.os.Build.VERSION.SDK_INT >= 14) {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }
        }
    }
}
