package com.caohua.games.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.CreditsListener;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.minegame.LogoutEntry;
import com.caohua.games.ui.CreditActivity;
import com.caohua.games.ui.StoreSecondActivity;
import com.caohua.games.ui.find.FindContentActivity;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.web.UrlOperatorHelper;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.app.LinkModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.h5.Cookie;
import com.chsdk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by zhouzhou on 2017/2/20.
 */

public class StoreFragment extends NormalFragment {
    private WebView mWebView;
    protected String url;
    protected Boolean ifRefresh = false;
    protected Boolean delayRefresh = false;

    protected TextView mTitle;
    protected ImageView mBackView;
    private CreditsListener creditsListener;
    private ProgressBar imgProgress;
    private Animation rotateAnim;
    private View noNetworkView;


    @Override
    protected void initChildView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mTitle = findView(R.id.store_text_title);
        emptyView = findView(R.id.ch_store_empty);
        noNetworkView = findView(R.id.ch_store_no_network);
        mTitle.setText("商城");
        mBackView = findView(R.id.store_image_back);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    ((FindContentActivity) getActivity()).openWelcomeActivity(getActivity(), 2);
                    getActivity().finish();
                }
            }
        });
        imgProgress = findView(R.id.ch_store_progress_img);
        rotateAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.ch_anim_rotate_cycle);
        imgProgress.startAnimation(rotateAnim);
        mWebView = findView(R.id.ch_fragment_store_web_view);
        mWebView.addJavascriptInterface(new Object() {
            //用于跳转用户登录页面事件。
            @JavascriptInterface
            public void login() {
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!AppContext.getAppContext().isLogin() && getActivity() != null) {
                            AppContext.getAppContext().login(false, getActivity(), new TransmitDataInterface() {
                                @Override
                                public void transmit(Object o) {
                                    if (o instanceof LoginUserInfo) {
                                        LoginUserInfo info = (LoginUserInfo) o;
                                        LogUtil.errorLog(info.nickName + "---" + info.userName + "-------" + info.userId);
                                        requestLogic(mWebView, true);
                                    }
                                }
                            });
                        }
                    }
                });
            }

            //复制券码
            @JavascriptInterface
            public void copyCode(final String code) {
            }

            //客户端本地触发刷新积分。
            @JavascriptInterface
            public void localRefresh(final String credits) {

            }

            @JavascriptInterface
            public void getSource(String html) {
                if (html.contains("206") && html.contains("\"code\": 206,\"msg\": \"[206]")) {
                    LogUtil.errorLog("StoreFragment html=", html);
                    LoginUserInfo info = new LoginUserInfo();
                    info.userId = "0";
                    SdkSession.getInstance().setUserInfo(info);
                    DataStorage.setAppLogin(mWebView.getContext(), false);
                    mWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            mWebView.loadUrl("file:///android_asset/CaoHuaSDK/Html/empty.html");
                            emptyView.setVisibility(View.VISIBLE);
                            emptyView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    EventBus.getDefault().post(new LogoutEntry());
                                    emptyView.setVisibility(View.GONE);
                                    requestLogic(mWebView, false);
                                }
                            });
                        }
                    });
                }
            }

        }, "duiba_app");
//        webView.loadUrl("http://www.duiba.com.cn/autoLogin/autologin?appKey=sj07bsn2xec5snpo&uid=not_login&timestamp=1487922750327&sign=f5348709805fcb5a624255576fdd9242");
//        mWebView.loadUrl("http://www.duiba.com.cn/test/demoRedirectSAdfjosfdjdsa");
        if (getUserVisibleHint()) {
            LinkModel model = new LinkModel();
            String params = "data=" + RequestExe.getBodyParameter(model.getDataMap());
            mWebView.postUrl(BaseLogic.HOST_APP + "shop/index", params.getBytes());
        }
        ShopWebViewClient webViewClient = new ShopWebViewClient();
        mWebView.setWebViewClient(webViewClient);
        WebChromeClient webChromeClient = new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                imgProgress.setVisibility(View.VISIBLE);
                imgProgress.startAnimation(rotateAnim);

                if (newProgress == 100) {
                    imgProgress.setVisibility(View.GONE);
                    imgProgress.clearAnimation();
                }
            }
        };
        mWebView.setWebChromeClient(webChromeClient);
        WebSettings settings = mWebView.getSettings();

        // User settings
        settings.setJavaScriptEnabled(true);    //设置webview支持javascript
        settings.setLoadsImagesAutomatically(true);    //支持自动加载图片
        settings.setUseWideViewPort(true);    //设置webview推荐使用的窗口，使html界面自适应屏幕
        settings.setLoadWithOverviewMode(true);
        settings.setSaveFormData(true);    //设置webview保存表单数据
        settings.setSavePassword(true);    //设置webview保存密码
        settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);    //设置中等像素密度，medium=160dpi
        settings.setSupportZoom(true);    //支持缩放
        settings.setAllowFileAccess(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setAppCacheMaxSize(1024 * 1024 * 8);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDatabaseEnabled(true);
        CookieManager.getInstance().setAcceptCookie(true);

        if (Build.VERSION.SDK_INT > 8) {
            settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        }

        // Technical settings
        settings.setSupportMultipleWindows(true);
        mWebView.setLongClickable(true);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setDrawingCacheEnabled(true);
    }

    private void requestLogic(WebView webView, final boolean isUrl) {
        if (webView == null) {
            return;
        }

        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_TOKEN, token);
                put(PARAMS_USER_ID, userId);
                if (isUrl) {
                    try {
                        put(PARAMS_DIRECT_URL, URLEncoder.encode(mWebView.getUrl(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        put(PARAMS_DIRECT_URL, "");
                        e.printStackTrace();
                    }
                }
            }
        };
        String params = "data=" + RequestExe.getBodyParameter(model.getDataMap());
        mWebView.postUrl(BaseLogic.HOST_APP + "shop/index", params.getBytes());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ch_fragment_store;
    }

    private LinearLayout emptyView;

    private class ShopWebViewClient extends WebViewClient {
        @Override
        public void onReceivedError(final WebView view, int errorCode, String description, String failingUrl) {
            if (view == null) {
                return;
            }
            view.loadUrl("file:///android_asset/CaoHuaSDK/Html/empty.html");
            if (!AppContext.getAppContext().isNetworkConnected()) {
                noNetworkView.setVisibility(View.VISIBLE);
                noNetworkView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        noNetworkView.setVisibility(View.GONE);
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                requestLogic(view, false);
                            }
                        });
                    }
                });
            } else {
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        emptyView.setVisibility(View.GONE);
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                requestLogic(view, false);
                            }
                        });
                    }
                });
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.loadUrl("javascript:window.duiba_app.getSource('<head>'+" +
                    "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogUtil.errorLog("StoreFragment url: " + url);
            Uri uri = Uri.parse(url);
            // 处理电话链接，启动本地通话应用。
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
                return true;
            }
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                return false;
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
                WebActivity.startWebPage(getActivity(), url);
                return true;
            }
            if (url.contains("dbnewopen")) { // 新开页面
                Intent intent = new Intent();
                intent.setClass(getActivity(), StoreSecondActivity.class);
                url = url.replace("dbnewopen", "none");
                intent.putExtra("url", url);
                startActivityForResult(intent, 100);
            } else {
                if (url.endsWith(".apk") || url.contains(".apk?")) { // 支持应用链接下载
                    Intent viewIntent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(viewIntent);
                    return true;
                }
//                if (url.contains("autologin") && activityStack.size() > 1) { // 未登录用户登录后返回，所有历史页面置为待刷新
//                    // 将所有已开Activity设置为onResume时刷新页面。
//                    setAllActivityDelayRefresh();
//                }
                view.loadUrl(url);
            }
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == 100) {
            if (intent.getStringExtra("url") != null) {
                this.url = intent.getStringExtra("url");
                mWebView.loadUrl(this.url);
                ifRefresh = false;
            }
        }
    }

    @Subscribe
    public void loginRefreshWebView(CreditActivity activity) {
        if (activity != null && mWebView != null) {
            requestLogic(mWebView, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ifRefresh) {
            this.url = getActivity().getIntent().getStringExtra("url");
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
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mWebView != null) {
            mWebView.setVisibility(View.GONE);
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.loadUrl("about:blank");
            mWebView.freeMemory();
            mWebView.destroy();
            mWebView = null;
            Cookie.clear();
        }
        super.onDestroy();
    }
}
