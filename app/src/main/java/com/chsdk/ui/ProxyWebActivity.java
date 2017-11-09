package com.chsdk.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.download.ViewDownloadMgr;
import com.caohua.games.ui.account.AccountSettingActivity;
import com.caohua.games.ui.emoji.FaceRelativeLayout;
import com.caohua.games.ui.gift.GetGiftDetailsActivity;
import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.biz.web.UrlOperatorHelper;
import com.chsdk.configure.DataStorage;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.app.LinkModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.h5.BaseH5;
import com.chsdk.ui.h5.Cookie;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.VideoEnabledWebChromeClient;
import com.chsdk.ui.widget.VideoEnabledWebView;
import com.chsdk.utils.FileUtil;
import com.chsdk.utils.JsonUtil;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.greenrobot.eventbus.EventBus;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.DownloadParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProxyWebActivity extends BaseWebActivity {
    private static final String TAG = ProxyWebActivity.class.getSimpleName();
    private static final String KEY_TYPE = "type";
    private static final String KEY_URL = "url";
    private static final String KEY_FILE_NAME = "file_name";
    private static final String ACTION_CLOSE_ACTIVITY = "http://passport.sdk.caohua.com/sdk/closeWindow";

    private static final int TYPE_OPEN_WEB = 1;
    private static final int TYPE_OPEN_LOCAL = 2;
    private static final int TYPE_APP_H5 = 5;
    private static final int TYPE_GAME_DETAIL = 7;
    private static final int TYPE_GAME_DETAIL_BY_ID = 8;

    private static final String IGD_ID = "igd_id";

    private DownloadEntry downloadEntry;

    private VideoEnabledWebView mWebView;
    private VideoEnabledWebChromeClient webChromeClient;
    private ProgressBar mProgressBar;
    private TextView tvTitle;
    private ImageView btnBack;
    private ImageView imgProgress;
    private RelativeLayout nonVideoLayout;
    private int type;
    private String url, localFileName;
    private BaseH5 h5;
    private boolean isOnPause = false;
    private Animation rotateAnim;
//    private DownloadProgressButton downloadButton;
//    private LinearLayout downloadLayout;
    private String igdId;
    private ViewDownloadMgr viewDownloadMgr;
    private FaceRelativeLayout faceRLayout;
    private EditText commentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        initData(intent);

        if (type == TYPE_GAME_DETAIL_BY_ID && igdId != null) {
            setContentView(R.layout.ch_activity_proxy_web);
            imgProgress = getView(R.id.ch_web_progress_img);
            rotateAnim = AnimationUtils.loadAnimation(this, R.anim.ch_anim_rotate_cycle);
            imgProgress.startAnimation(rotateAnim);
            getDownloadEntry(igdId);
        }else {
            if (TextUtils.isEmpty(url)) {
                finish();
                return;
            }
            setContentView(R.layout.ch_activity_proxy_web);
            initView();
            setView();
        }
    }

    private void initData(Intent intent) {
        type = intent.getIntExtra(KEY_TYPE, 0);
        url = intent.getStringExtra(KEY_URL);
        igdId = intent.getStringExtra(IGD_ID);

        if (type == TYPE_GAME_DETAIL) {
            downloadEntry = (DownloadEntry) intent.getSerializableExtra("downloadEntry");
            url = downloadEntry.getDetail_url();
        }

        if (type == TYPE_OPEN_LOCAL) {
            localFileName = intent.getStringExtra(KEY_FILE_NAME);
        }

        LogUtil.debugLog(TAG, "initData_loadUrl_" + url + ", type_" + type);
    }

    private void initView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        tvTitle = getView(R.id.ch_web_title_tv);
        mProgressBar = getView(R.id.ch_web_progress);
        nonVideoLayout = getView(R.id.ch_web_layout);
        btnBack = getView(R.id.ch_web_title_back);
//        downloadButton = getView(R.id.ch_web_download_button);
//        downloadLayout = getView(R.id.ch_web_download_layout);
        faceRLayout = getView(R.id.ch_emoji_rl_bottom);
        if(type == TYPE_GAME_DETAIL) {
            //如果是要的 就 new 一个 ShareContentEntry
//            faceRLayout.setShareContentEntry(shareContentEntry);
            faceRLayout.setVisibility(View.VISIBLE);
        }
//        faceRLayout.setVisibility(View.VISIBLE);

        btnBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgProgress = getView(R.id.ch_web_progress_img);

        mWebView = new VideoEnabledWebView(this);
//		mWebView.setBackgroundColor(getResources().getColor(R.color.ch_gray));
        nonVideoLayout.addView(mWebView, 0,
                new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        ViewGroup videoLayout = getView(R.id.ch_web_video_layout);
        View loadingView = getLayoutInflater().inflate(R.layout.ch_view_loading_video, null);

        Listener listner = new Listener();
        mWebView.setDownloadListener(listner);
        webChromeClient = new CustomWebView(nonVideoLayout, videoLayout, loadingView, mWebView);
        webChromeClient.setOnToggledFullscreen(listner);
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(new CustomWebViewClient());
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.setScrollContainer(false);

        setWebView(mWebView.getSettings());
        // nexus 5上加载时, 标题栏会模糊变花
        // above API 11
        // mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        if (type == TYPE_GAME_DETAIL) {
//            String downloadUrl = downloadEntry.downloadUrl;
//            LogUtil.errorLog(downloadUrl);
//            viewDownloadMgr = new ViewDownloadMgr(downloadButton);
//            viewDownloadMgr.setData(downloadEntry);
//            downloadLayout.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("JavascriptInterface")
    private void setView() {
        rotateAnim = AnimationUtils.loadAnimation(this, R.anim.ch_anim_rotate_cycle);
        imgProgress.startAnimation(rotateAnim);

        if (type == TYPE_APP_H5) {
            LinkModel model = new LinkModel();
            String params = "data=" + RequestExe.getBodyParameter(model.getDataMap());
            mWebView.postUrl(url, params.getBytes());
        } else {
            if (type == TYPE_OPEN_LOCAL) {
                h5 = BaseH5.createInstance(localFileName, this, mWebView);
                if (h5 != null) {
                    mWebView.addJavascriptInterface(h5, localFileName);
                }
            }
            mWebView.loadUrl(url);
        }
    }

    private void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
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

    private void setLoadProgress(int progress) {
        if (!mProgressBar.isShown()) {
            mProgressBar.setVisibility(View.VISIBLE);
            imgProgress.setVisibility(View.VISIBLE);
            imgProgress.startAnimation(rotateAnim);
        }

        mProgressBar.setProgress(progress);

        if (progress == 100) {
            mProgressBar.setVisibility(View.INVISIBLE);
            imgProgress.setVisibility(View.GONE);
            imgProgress.clearAnimation();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView(WebSettings settings) {
        settings.setAllowFileAccess(true);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAppCacheMaxSize(1024 * 1024 * 8);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String databasePath = mWebView.getContext().getDir("databases", Context.MODE_PRIVATE).getPath();
            settings.setDatabasePath(databasePath);
        }
    }

    @Override
    public void onBackPressed() {
        LogUtil.errorLog(TAG," -- "+ faceRLayout.getShowContent());
        if (faceRLayout.getShowContent()) {
            faceRLayout.hideFaceLayout();
            return;
        }

        if (webChromeClient != null && !webChromeClient.onBackPressed()) {
            if (mWebView.canGoBack() && !mWebView.getUrl().contains("Error.html")) {
                mWebView.goBack();
                return;
            }
        }
        super.onBackPressed();
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
    public void onResume() {
        super.onResume();

        if (AccountSettingActivity.showLoginDialog) {
            finish();
            return;
        }

        if (!NetworkUtils.isNetworkConnected(this)) {
            CHToast.show(this, "请检查您当前的网络");
            return;
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

                if (!TextUtils.isEmpty(url) && url.contains(UrlOperatorHelper.ACCOUNT_SAFE_SETTING)) {
                    LinkModel model = new LinkModel();
                    String params = "data=" + RequestExe.getBodyParameter(model.getDataMap());
                    mWebView.postUrl(url, params.getBytes());
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            if (!TextUtils.isEmpty(localFileName)) {
                mWebView.removeJavascriptInterface(localFileName);
            }
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

        EventBus.getDefault().unregister(this);

        if (faceRLayout != null) {
            faceRLayout.release();
        }

        if (viewDownloadMgr != null) {
            viewDownloadMgr.release();
        }
    }

    public static void openWebView(Context context, String url) {
        if (TextUtils.isEmpty(url))
            return;

        if (handleDownloadUrl(context, url)) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(KEY_TYPE, TYPE_OPEN_WEB);
        intent.putExtra(KEY_URL, url);
        intent.setClass(context, ProxyWebActivity.class);
        context.startActivity(intent);
    }

    /**
     *  通过openWebView打开的可能是下载游戏
     */
    private static boolean handleDownloadUrl(Context context, String url) {
        if (url.contains(".apk&igd=")) {
            String[] split = url.split("&igd=");
            if (split.length>1 && !TextUtils.isEmpty(split[1])) {
                openGameDetailById(context, split[1]);
            }else {
                CHToast.show(context, "当前游戏无法下载");
            }
            return true;
        }
        return false;
    }

    public static void loadLocalHtml(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName))
            return;

        Intent intent = new Intent();
        intent.putExtra(KEY_TYPE, TYPE_OPEN_LOCAL);
        intent.putExtra(KEY_URL, "file:///android_asset/CaoHuaSDK/Html/" + fileName + ".html");
        intent.putExtra(KEY_FILE_NAME, fileName);
        intent.setClass(context, ProxyWebActivity.class);
        context.startActivity(intent);
    }

    public static void openAppLink(Context context, String url) {
        Intent intent = new Intent();
        intent.putExtra(KEY_TYPE, TYPE_APP_H5);
        intent.putExtra(KEY_URL, url);
        intent.setClass(context, ProxyWebActivity.class);
        context.startActivity(intent);
    }

    public static void openGameDetail(Context context, DownloadEntry downloadEntry) {
        Intent intent = new Intent();
        intent.putExtra(KEY_TYPE, TYPE_GAME_DETAIL);
        intent.putExtra("downloadEntry", downloadEntry);
        intent.setClass(context, ProxyWebActivity.class);
        context.startActivity(intent);
    }

    public static void openGameDetailById(Context context, String id) {
        Intent intent = new Intent();
        intent.putExtra(KEY_TYPE, TYPE_GAME_DETAIL_BY_ID);
        intent.putExtra(IGD_ID,id);
        intent.setClass(context, ProxyWebActivity.class);
        context.startActivity(intent);
    }

    class Listener implements DownloadListener, VideoEnabledWebChromeClient.ToggledFullscreenCallback {

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

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {

            if (type == TYPE_GAME_DETAIL || type == TYPE_GAME_DETAIL_BY_ID) {
                if (downloadEntry != null ) {
                    if (TextUtils.isEmpty(downloadEntry.downloadUrl)) {
                        downloadEntry.downloadUrl = "无法下载";
                    }
                    String downloadUrl = downloadEntry.downloadUrl;
                    LogUtil.errorLog(downloadUrl);
                    if (viewDownloadMgr != null) {
                        viewDownloadMgr.release();
                    }
//                    viewDownloadMgr = new ViewDownloadMgr(downloadButton);
//                    viewDownloadMgr.setData(downloadEntry);
//                    viewDownloadMgr.startDownload();
                    DownloadParams params = new DownloadParams();
                    params.url = downloadEntry.downloadUrl;
                    params.title = downloadEntry.title;
                    params.pkg = downloadEntry.pkg;
                    params.iconUrl = downloadEntry.iconUrl;
                    FileDownloader.start(params);
                }
            } else {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    Intent chooserIntent = Intent.createChooser(intent, "Select Application");
                    if (chooserIntent != null) {
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            LogUtil.errorLog("onDownloadStart:" + e.getMessage());
                        }

                    }
                }
            }
        }
    }

    private boolean checkAppPkg(String url) {

        if (!AppContext.getAppContext().isLogin()) {
            AppContext.getAppContext().login(ProxyWebActivity.this, new TransmitDataInterface() {
                @Override
                public void transmit(Object o) {
                    if (o instanceof LoginUserInfo) {
                        LoginUserInfo info = (LoginUserInfo) o;
                        LogUtil.errorLog(info.nickName + "---" + info.userName + "-------" + info.userId);
                    }
                }
            });
            return false;
        }

        String[] split = url.split("id=");
        if (split.length < 2) {
            CHToast.show(this,"请求的地址无效!");
            return false;
        }
        String path = split[1];
        Intent intent = new Intent(this, GetGiftDetailsActivity.class);
//        intent.setPackage("com.caohua.games.apps");
//        intent.setClassName("com.caohua.games.apps", "com.caohua.games.ui.gift.GetGiftDetailsActivity");
        intent.putExtra("path", path);
        intent.putExtra("type", 100);
        List<ResolveInfo> resolveInfo =
                getPackageManager().queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo.size() > 0) {
            startActivity(intent);
            return true;
        } else {
            LogUtil.errorLog("请确认GetGiftDetailsActivity路径是否正确");
            throw new RuntimeException("请确认GetGiftDetailsActivity路径是否正确");
        }
    }

    class CustomWebViewClient extends WebViewClient {

        @SuppressLint("JavascriptInterface")
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogUtil.debugLog(TAG, "onPageStarted_" + url);
            if (!TextUtils.isEmpty(url) && url.startsWith("file:///")) {
                String tmpFileName = FileUtil.getFileName(url);
                if (!TextUtils.isEmpty(tmpFileName) && !tmpFileName.equals(localFileName)) {
                    if (!TextUtils.isEmpty(localFileName)) {
                        mWebView.removeJavascriptInterface(localFileName);
                    }

                    LogUtil.debugLog(TAG,
                            "onPageStarted_localFileName_" + localFileName + ", tmpFileName_" + tmpFileName);

                    BaseH5 h5 = BaseH5.createInstance(tmpFileName, ProxyWebActivity.this, mWebView);
                    if (h5 != null) {
                        mWebView.addJavascriptInterface(h5, tmpFileName);
                        mWebView.loadUrl(url);
                        localFileName = tmpFileName;
                    }
                }
            }
        }

        @SuppressLint("JavascriptInterface")
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            LogUtil.debugLog(TAG, "shouldOverrideUrlLoading_" + url);
            String giftInterceptUrl = DataStorage.getGiftInterceptUrl(ProxyWebActivity.this);

            if (!NetworkUtils.isNetworkConnected(ProxyWebActivity.this)) {
//                downloadLayout.setVisibility(View.GONE);
                CHToast.show(ProxyWebActivity.this, "请检查您当前的网络");
            }

            if (!TextUtils.isEmpty(giftInterceptUrl) && url.startsWith(giftInterceptUrl)) {
                checkAppPkg(url);
            } else if (url.startsWith("file:///")) {
                String tmpFileName = FileUtil.getFileName(url);
                if (!TextUtils.isEmpty(tmpFileName) && !tmpFileName.equals(localFileName)) {
                    if (!TextUtils.isEmpty(localFileName)) {
                        mWebView.removeJavascriptInterface(localFileName);
                    }

                    BaseH5 h5 = BaseH5.createInstance(tmpFileName, ProxyWebActivity.this, mWebView);
                    if (h5 != null) {
                        mWebView.addJavascriptInterface(h5, tmpFileName);
                    }
                    localFileName = tmpFileName;
                    view.loadUrl(url);
                }
            }else if (url.contains(".apk&igd=")) {
                String[] split = url.split("&igd=");
                if (split.length>1 && !TextUtils.isEmpty(split[1])) {
                    openGameDetailById(ProxyWebActivity.this,split[1]);
                }else {
                    CHToast.show(getApplicationContext(), "当前游戏无法下载");
                }
            }else if (type == TYPE_GAME_DETAIL_BY_ID && !url.endsWith(".apk")) {

                ProxyWebActivity.openWebView(ProxyWebActivity.this, url);

            }else if (type == TYPE_GAME_DETAIL && !url.endsWith(".apk")) {
                if (url.toLowerCase().contains("error")) {
                    view.loadUrl(url);
//                    downloadLayout.setVisibility(View.GONE);
                } else {
                    ProxyWebActivity.openWebView(ProxyWebActivity.this, url);
                }
//						downloadLayout.setVisibility(View.INVISIBLE);
//						view.loadUrl(url);
            } else if (url.startsWith("http")) {
                if (url.contains(ACTION_CLOSE_ACTIVITY)) {
                    if (url.endsWith("?from=app")) {
                        // app修改了密码
                        DataStorage.setAppLogin(ProxyWebActivity.this, false);
                        AccountSettingActivity.showLoginDialog = true;
                        CHToast.show(ProxyWebActivity.this, "密码已更改,请重新登录");
                    }
                    finish();
                } else if (UrlOperatorHelper.ACCOUNT_SAFE_SETTING.equals(ProxyWebActivity.this.url)) {
                    ProxyWebActivity.openWebView(ProxyWebActivity.this, url);
                } else if (url.contains(UrlOperatorHelper.USER_CENTER_FAQ) && type == TYPE_APP_H5) {
                    ProxyWebActivity.openWebView(ProxyWebActivity.this, url);
                } else {
                    view.loadUrl(url);
                }
            } else {
                Intent baseIntent = new Intent(Intent.ACTION_VIEW);
                baseIntent.setData(Uri.parse(url));
                if (baseIntent.resolveActivity(getPackageManager()) != null) {
                    Intent chooserIntent = Intent.createChooser(baseIntent, "Select Application");
                    if (chooserIntent != null) {
                        startActivity(chooserIntent);
                    }
                } else {
                    CHToast.show(ProxyWebActivity.this,"您的设备上未安装相关程序");
                    super.shouldOverrideUrlLoading(view, url);
                }
            }
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();// 接受证书
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            downloadLayout.setVisibility(View.GONE);
             super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            String title = view.getTitle();
            setTitle(title);
        }
    }

    class CustomWebView extends VideoEnabledWebChromeClient {

        public CustomWebView(View activityNonVideoView, ViewGroup activityVideoView, View loadingView,
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
                setTitle(title);
            }
        }
    }

    private void getDownloadEntry(final String id) {
        BaseModel baseModel = new BaseModel(){
            @Override
            public void putDataInMap() {
                put("igd",id);
            }
        };
        RequestExe.post("http://app.sdk.caohua.com/game/getGameById", baseModel, new IRequestListener() {
            @Override
            public void success(final HashMap<String, String> map) {
                imgProgress.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        imgProgress.setVisibility(View.GONE);
                        imgProgress.clearAnimation();
                        if (map != null) {
                            String data = map.get(HttpConsts.POST_PARAMS_DATA);
                            List<DownloadEntry> list = JsonUtil.fromJsonArray(data, DownloadEntry.class);
                            if (list != null && list.size() > 0) {
                                downloadEntry = list.get(0);
                                if (downloadEntry != null) {
                                    if (TextUtils.isEmpty(downloadEntry.getDetail_url())) {
                                        CHToast.show(getApplicationContext(), "当前游戏下载失败，未获取到游戏详情");
                                        finish();
                                        return;
                                    }

                                    if (TextUtils.isEmpty(downloadEntry.getDownloadUrl())) {
                                        CHToast.show(getApplicationContext(), "当前游戏下载失败，未获取到游戏下载地址");
                                        finish();
                                        return;
                                    }

                                    initView();

                                    mWebView.loadUrl(downloadEntry.getDetail_url());
//                                    downloadLayout.setVisibility(View.VISIBLE);
//                                    viewDownloadMgr = new ViewDownloadMgr(downloadButton);
//                                    viewDownloadMgr.setData(downloadEntry);
//                                    viewDownloadMgr.startDownload();
                                    return;
                                }
                            }
                        }
                        CHToast.show(getApplicationContext(), "当前游戏无法下载");
                        finish();
                    }
                }, 1000);
            }

            @Override
            public void failed(final String error, int errorCode) {
                imgProgress.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imgProgress.setVisibility(View.GONE);
                        imgProgress.clearAnimation();
                        CHToast.show(getApplicationContext(),"当前游戏无法下载:" + error);
                        finish();
                    }
                }, 1000);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (faceRLayout.isShouldHideInput(v, ev)) {
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtil.errorLog("---------------------> 结束进度");
    }
}
