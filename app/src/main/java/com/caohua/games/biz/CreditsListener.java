package com.caohua.games.biz;

import android.webkit.WebView;

/**
 * Created by zhouzhou on 2017/2/27.
 */

public interface CreditsListener {
    /**
     * 当点击分享按钮被点击
     *
     * @param shareUrl       分享的地址
     * @param shareThumbnail 分享的缩略图
     * @param shareTitle     分享的标题
     * @param shareSubtitle  分享的副标题
     */
    public void onShareClick(WebView webView, String shareUrl, String shareThumbnail, String shareTitle, String shareSubtitle);

    /**
     * 当点击登录
     *
     * @param webView    用于登录成功后返回到当前的webview并刷新。
     * @param currentUrl 当前页面的url
     */
    public void onLoginClick(WebView webView, String currentUrl);

    /**
     * 当点复制券码时
     *
     * @param webView webview对象。
     * @param code    复制的券码
     */
    public void onCopyCode(WebView webView, String code);

    /**
     * 通知本地，刷新积分
     *
     * @param mWebView
     * @param credits
     */
    public void onLocalRefresh(WebView mWebView, String credits);
}
