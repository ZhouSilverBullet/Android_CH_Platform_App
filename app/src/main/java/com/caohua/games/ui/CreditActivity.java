package com.caohua.games.ui;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.CreditsListener;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.comment.TimesPraiseEntry;
import com.caohua.games.biz.share.CHShareLayout;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.web.UrlOperatorHelper;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.h5.Cookie;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Stack;

public class CreditActivity extends BaseActivity {
    private static String ua;
    private static Stack<CreditActivity> activityStack;
    public static final String VERSION = "1.0.7";
    public static CreditsListener creditsListener;

    protected String url;
    protected String shareUrl;            //分享的url
    protected String shareThumbnail;    //分享的缩略图
    protected String shareTitle;        //分享的标题
    protected String shareSubtitle;        //分享的副标题
    protected Boolean ifRefresh = false;
    protected Boolean delayRefresh = false;

    protected WebView mWebView;
    protected TextView mTitle;
    protected ImageView mBackView;
    protected TextView mShare;

    private int RequestCode = 100;
    private ProgressBar imgProgress;
    private Animation rotateAnim;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 锁定竖屏显示
        url = getIntent().getStringExtra("url");
        type = getIntent().getIntExtra("type", 0);
        if (url == null) {
            throw new RuntimeException("url can't be blank");
        }

        // 管理匿名类栈，用于模拟原生应用的页面跳转。
        if (activityStack == null) {
            activityStack = new Stack<CreditActivity>();
        }
        activityStack.push(this);

        // 初始化页面
        setContentView(R.layout.ch_fragment_store);
        initView();

        mTitle.setTextColor(0xFFFFFFFF);
        mBackView.setClickable(true);

        // 添加后退按钮监听事件
        mBackView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                onBackClick();
            }
        });
        // 添加分享按钮的监听事件
        if (mShare != null) {
            mShare.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (creditsListener != null) {
                        creditsListener.onShareClick(mWebView, shareUrl, shareThumbnail, shareTitle, shareSubtitle);
                    }
                }
            });
        }
        creditsListener = new CreditsListener() {
            @Override
            public void onShareClick(WebView webView, String shareUrl, String shareThumbnail, String shareTitle, String shareSubtitle) {
                TimesPraiseEntry entry = new TimesPraiseEntry();
                entry.setArticle_icon("");
                entry.setArticle_title(shareTitle);
                entry.setArticle_url(shareUrl);
                new CHShareLayout(CreditActivity.this, entry).show();
            }

            @Override
            public void onLoginClick(WebView webView, String currentUrl) {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!AppContext.getAppContext().isLogin()) {
                            AppContext.getAppContext().login(false, CreditActivity.this, new TransmitDataInterface() {
                                @Override
                                public void transmit(Object o) {
                                    if (o instanceof LoginUserInfo) {
                                        LoginUserInfo info = (LoginUserInfo) o;
                                        LogUtil.errorLog(info.nickName + "---" + info.userName + "-------" + info.userId);
                                        BaseModel model = new BaseModel() {
                                            @Override
                                            public void putDataInMap() {
                                                String userId = session.getUserId();
                                                String token = session.getToken();
                                                put(PARAMS_TOKEN, token);
                                                put(PARAMS_USER_ID, userId);
                                                try {
                                                    put(PARAMS_DIRECT_URL, URLEncoder.encode(mWebView.getUrl(), "UTF-8"));
                                                } catch (UnsupportedEncodingException e) {
                                                    put(PARAMS_DIRECT_URL, "");
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        String params = "data=" + RequestExe.getBodyParameter(model.getDataMap());
                                        mWebView.postUrl(BaseLogic.HOST_APP + "shop/index", params.getBytes());
                                        EventBus.getDefault().post(CreditActivity.this);
                                    }
                                }
                            });
                        } else {
                            BaseModel model = new BaseModel() {
                                @Override
                                public void putDataInMap() {
                                    String userId = session.getUserId();
                                    String token = session.getToken();
                                    put(PARAMS_TOKEN, token);
                                    put(PARAMS_USER_ID, userId);
                                    try {
                                        put(PARAMS_DIRECT_URL, URLEncoder.encode(mWebView.getUrl(), "UTF-8"));
                                    } catch (UnsupportedEncodingException e) {
                                        put(PARAMS_DIRECT_URL, "");
                                        e.printStackTrace();
                                    }
                                }
                            };
                            String params = "data=" + RequestExe.getBodyParameter(model.getDataMap());
                            mWebView.postUrl(BaseLogic.HOST_APP + "shop/index", params.getBytes());
                            EventBus.getDefault().post(CreditActivity.this);
                        }
                    }
                });
            }

            @Override
            public void onCopyCode(WebView webView, String code) {
                if (webView != null && !TextUtils.isEmpty(code)) {
                    Context context = webView.getContext();
                    ClipboardManager copyManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    copyManager.setText(code);
                    CHToast.show(context, "复制成功");
                }
            }

            @Override
            public void onLocalRefresh(WebView mWebView, String credits) {

            }
        };

        //js调java代码接口。
        mWebView.addJavascriptInterface(new Object() {
            //用于跳转用户登录页面事件。
            @JavascriptInterface
            public void login() {
                if (creditsListener != null) {
                    mWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            creditsListener.onLoginClick(mWebView, mWebView.getUrl());
                        }
                    });
                }
            }

            //复制券码
            @JavascriptInterface
            public void copyCode(final String code) {
                if (creditsListener != null) {
                    mWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            creditsListener.onCopyCode(mWebView, code);
                        }
                    });
                }
            }

            //客户端本地触发刷新积分。
            @JavascriptInterface
            public void localRefresh(final String credits) {
                if (creditsListener != null) {
                    mWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            creditsListener.onLocalRefresh(mWebView, credits);
                        }
                    });
                }
            }

        }, "duiba_app");

        if (ua == null) {
            ua = mWebView.getSettings().getUserAgentString() + " Duiba/" + VERSION;
        }
        mWebView.getSettings().setUserAgentString(ua);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                CreditActivity.this.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                imgProgress.setVisibility(View.VISIBLE);
                imgProgress.startAnimation(rotateAnim);

                if (newProgress == 100) {
                    imgProgress.setVisibility(View.GONE);
                    imgProgress.clearAnimation();
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return shouldOverrideUrlByDuiba(view, url);
            }
        });

        if (mWebView != null && AppContext.getAppContext().isLogin() && type == 1
                && !TextUtils.isEmpty(url)) {
            String httpUrl;
            if (url.contains(StoreSecondActivity.SHOP_URL)) {
                String[] split = url.split("&dbredirect=");
                if (split.length > 1) {
                    httpUrl = split[1];
                } else {
                    httpUrl = url;
                }
            } else {
                httpUrl = url;
            }
            url = httpUrl;
            BaseModel model = new BaseModel() {
                @Override
                public void putDataInMap() {
                    String userId = session.getUserId();
                    String token = session.getToken();
                    put(PARAMS_TOKEN, token);
                    put(PARAMS_USER_ID, userId);
                    put(PARAMS_DIRECT_URL, url);
                }
            };
            String params = "data=" + RequestExe.getBodyParameter(model.getDataMap());
            mWebView.postUrl(BaseLogic.HOST_APP + "shop/index", params.getBytes());
            type = 0;
        } else if (mWebView != null && !AppContext.getAppContext().isLogin() && type == 1
                && !TextUtils.isEmpty(url)) {
            mWebView.loadUrl(url);
            type = 0;
        } else {
            mWebView.loadUrl(url);
        }
    }

    protected void onBackClick() {
        Intent intent = new Intent();
        setResult(99, intent);
        finishActivity(this);
    }

    // 初始化页面
    protected void initView() {
        mTitle = (TextView) findViewById(R.id.store_text_title);
        mShare = (TextView) findViewById(R.id.store_text_share);
        mBackView = (ImageView) findViewById(R.id.store_image_back);
        imgProgress = (ProgressBar) findViewById(R.id.ch_store_progress_img);
        initWebView();
    }


    //初始化WebView配置
    protected void initWebView() {
        mWebView = (WebView) findViewById(R.id.ch_fragment_store_web_view);
        rotateAnim = AnimationUtils.loadAnimation(this, R.anim.ch_anim_rotate_cycle);
        imgProgress.startAnimation(rotateAnim);
        WebSettings settings = mWebView.getSettings();

        // User settings
        settings.setJavaScriptEnabled(true);    //设置webview支持javascript
        settings.setLoadsImagesAutomatically(true);    //支持自动加载图片
        settings.setUseWideViewPort(true);    //设置webview推荐使用的窗口，使html界面自适应屏幕
        settings.setLoadWithOverviewMode(true);
        settings.setSaveFormData(true);    //设置webview保存表单数据
        settings.setSavePassword(true);    //设置webview保存密码
        settings.setDefaultZoom(ZoomDensity.MEDIUM);    //设置中等像素密度，medium=160dpi
        settings.setSupportZoom(true);    //支持缩放

        CookieManager.getInstance().setAcceptCookie(true);

        if (Build.VERSION.SDK_INT > 8) {
            settings.setPluginState(PluginState.ON_DEMAND);
        }

        // Technical settings
        settings.setSupportMultipleWindows(true);
        mWebView.setLongClickable(true);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setDrawingCacheEnabled(true);

        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);

        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                imgProgress.setVisibility(View.VISIBLE);
                imgProgress.startAnimation(rotateAnim);
                LogUtil.errorLog("webChromeClient newProgress: " + newProgress);
                if (newProgress == 100) {
                    imgProgress.setVisibility(View.GONE);
                    imgProgress.clearAnimation();
                }
            }
        };
        mWebView.setWebChromeClient(webChromeClient);
    }

    protected void onReceivedTitle(WebView view, String title) {
        mTitle.setText(title);
    }

    /**
     * 拦截url请求，根据url结尾执行相应的动作。 （重要）
     * 用途：模仿原生应用体验，管理页面历史栈。
     *
     * @param view
     * @param url
     * @return
     */
    protected boolean shouldOverrideUrlByDuiba(WebView view, String url) {
        Uri uri = Uri.parse(url);
        if (this.url.equals(url)) {
            view.loadUrl(url);
            return true;
        }
        // 处理电话链接，启动本地通话应用。
        if (url.startsWith("tel:")) {
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            startActivity(intent);
            return true;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return false;
        }
        // 截获页面分享请求，分享数据
        if ("/client/dbshare".equals(uri.getPath())) {
            String content = uri.getQueryParameter("content");
            if (creditsListener != null && content != null) {
                String[] dd = content.split("\\|");
                if (dd.length == 4) {
                    setShareInfo(dd[0], dd[1], dd[2], dd[3]);
                    mShare.setVisibility(View.VISIBLE);
                    mShare.setClickable(true);
                }
            }
            return true;
        }
        // 截获页面唤起登录请求。（目前暂时还是用js回调的方式，这里仅作预留。）
        if ("/client/dblogin".equals(uri.getPath())) {
            if (creditsListener != null) {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        creditsListener.onLoginClick(mWebView, mWebView.getUrl());
                    }
                });
            }
            return true;
        }
        if (url.contains(UrlOperatorHelper.OPEN_WEB_DETAIL)
                || url.contains(UrlOperatorHelper.OPEN_ARTICLE_DETAIL)) {
            WebActivity.startWebPage(CreditActivity.this, url);
            finish();
            return true;
        }
        if (url.contains("dbnewopen")) { // 新开页面
            Intent intent = new Intent();
            intent.setClass(CreditActivity.this, CreditActivity.this.getClass());
            url = url.replace("dbnewopen", "none");
            intent.putExtra("url", url);
            startActivityForResult(intent, RequestCode);
        } else if (url.contains("dbbackrefresh")) { // 后退并刷新
            url = url.replace("dbbackrefresh", "none");
            Intent intent = new Intent();
            intent.putExtra("url", url);
            setResult(RequestCode, intent);
            finishActivity(this);
        } else if (url.contains("dbbackrootrefresh")) { // 回到积分商城首页并刷新
            url = url.replace("dbbackrootrefresh", "none");
            if (activityStack.size() == 1) {
                finishActivity(this);
            } else {
                activityStack.get(0).ifRefresh = true;
                finishUpActivity();
            }
        } else if (url.contains("dbbackroot")) { // 回到积分商城首页
            url = url.replace("dbbackroot", "none");
            if (activityStack.size() == 1) {
                finishActivity(this);
            } else {
                finishUpActivity();
            }
        } else if (url.contains("dbback")) { // 后退
            url = url.replace("dbback", "none");
            finishActivity(this);
        } else {
            if (url.endsWith(".apk") || url.contains(".apk?")) { // 支持应用链接下载
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(viewIntent);
                return true;
            }
            if (url.contains("autologin") && activityStack.size() > 1) { // 未登录用户登录后返回，所有历史页面置为待刷新
                // 将所有已开Activity设置为onResume时刷新页面。
                setAllActivityDelayRefresh();
            }
            view.loadUrl(url);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == 100) {
            if (intent.getStringExtra("url") != null) {
                this.url = intent.getStringExtra("url");
                mWebView.loadUrl(this.url);
                ifRefresh = false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ifRefresh) {
            this.url = getIntent().getStringExtra("url");
            mWebView.loadUrl(this.url);
            ifRefresh = false;
        } else if (delayRefresh) {
            mWebView.reload();
            delayRefresh = false;
        } else {
            // 返回页面时，如果页面含有onDBNewOpenBack()方法,则调用该js方法。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mWebView.evaluateJavascript("if(window.onDBNewOpenBack){onDBNewOpenBack()}", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                    }
                });
            } else {
                mWebView.loadUrl("javascript:if(window.onDBNewOpenBack){onDBNewOpenBack()}");
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackClick();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.setVisibility(View.GONE);
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
        super.onDestroy();
    }

    //--------------------------------------------以下为工具方法----------------------------------------------

    /**
     * 配置分享信息
     */
    protected void setShareInfo(String shareUrl, String shareThumbnail, String shareTitle, String shareSubtitle) {
        this.shareUrl = shareUrl;
        this.shareThumbnail = shareThumbnail;
        this.shareSubtitle = shareSubtitle;
        this.shareTitle = shareTitle;
    }

    /**
     * 结束除了最底部一个以外的所有Activity
     */
    public void finishUpActivity() {
        int size = activityStack.size();
        for (int i = 0; i < size - 1; i++) {
            activityStack.pop().finish();
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            openWelcomeActivity(activity, 2);
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 设置栈内所有Activity为返回待刷新。
     * 作用：未登录用户唤起登录后，将所有栈内的Activity设置为onResume时刷新页面。
     */
    public void setAllActivityDelayRefresh() {
        int size = activityStack.size();
        for (int i = 0; i < size; i++) {
            if (activityStack.get(i) != this) {
                activityStack.get(i).delayRefresh = true;
            }
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
