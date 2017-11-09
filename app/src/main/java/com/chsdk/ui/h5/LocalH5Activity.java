package com.chsdk.ui.h5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.WebView;

import com.caohua.games.ui.account.AboutActivity;
import com.chsdk.ui.BaseWebActivity;
import com.chsdk.ui.WebActivity;
import com.chsdk.utils.FileUtil;
import com.chsdk.utils.LogUtil;

/**
 * Created by ZengLei on 2017/1/6.
 */

public class LocalH5Activity extends BaseWebActivity {
    private static final String KEY_FILE_NAME = "file_name";
    private static final String GM_CAOHUA_COM = "http://gm.caohua.com";
    private String localFileName;
    private BaseH5 h5;

    protected void initChildData() {
        localFileName = intent.getStringExtra(KEY_FILE_NAME);
    }

    @SuppressLint("JavascriptInterface")
    protected void initChildView() {
        h5 = BaseH5.createInstance(localFileName, this, mWebView);
        if (h5 != null) {
            mWebView.addJavascriptInterface(h5, localFileName);
        }
    }

    @Override
    public void onDestroy() {
        if (!TextUtils.isEmpty(localFileName) && mWebView != null) {
            mWebView.removeJavascriptInterface(localFileName);
        }
        super.onDestroy();
    }

    @SuppressLint("JavascriptInterface")
    protected void handlePageStarted(String url) {
        if (!TextUtils.isEmpty(url) && url.startsWith("file:///")) {
            String tmpFileName = FileUtil.getFileName(url);
            if (!TextUtils.isEmpty(tmpFileName) && !tmpFileName.equals(localFileName)) {
                if (!TextUtils.isEmpty(localFileName)) {
                    mWebView.removeJavascriptInterface(localFileName);
                }

                BaseH5 h5 = BaseH5.createInstance(tmpFileName, this, mWebView);
                if (h5 != null) {
                    mWebView.addJavascriptInterface(h5, tmpFileName);
                    mWebView.loadUrl(url);
                    localFileName = tmpFileName;
                }
            }
        }
    }

    @SuppressLint("JavascriptInterface")
    protected boolean handleUrlLoading(WebView view, String url) {
        if (url.startsWith("file:///")) {
            String tmpFileName = FileUtil.getFileName(url);
            if (!TextUtils.isEmpty(tmpFileName) && !tmpFileName.equals(localFileName)) {
                if (!TextUtils.isEmpty(localFileName)) {
                    mWebView.removeJavascriptInterface(localFileName);
                }

                BaseH5 h5 = BaseH5.createInstance(tmpFileName, this, mWebView);
                if (h5 != null) {
                    mWebView.addJavascriptInterface(h5, tmpFileName);
                }
                localFileName = tmpFileName;
                view.loadUrl(url);
                return true;
            }
        } else if(url.startsWith(GM_CAOHUA_COM)) {
            WebActivity.startWebPage(this, url);
            return true;
        }

        return false;
    }

    public static void start(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName))
            return;

        Intent intent = new Intent();
        intent.putExtra(KEY_URL, "file:///android_asset/CaoHuaSDK/Html/" + fileName + ".html");
        intent.putExtra(KEY_FILE_NAME, fileName);
        intent.setClass(context, LocalH5Activity.class);
        context.startActivity(intent);
    }
}
