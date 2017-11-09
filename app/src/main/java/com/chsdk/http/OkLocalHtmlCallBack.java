package com.chsdk.http;

import android.os.Handler;
import android.text.TextUtils;

import com.chsdk.utils.LogUtil;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @author ZengLei
 *         <p>
 * @version 2016年8月15日
 *          <p>
 */
class OkLocalHtmlCallBack implements Callback {
    private static final String TAG = OkLocalHtmlCallBack.class.getSimpleName();
    private IRequestListener listener;

    public OkLocalHtmlCallBack(IRequestListener listener) {
        this.listener = listener;
    }

    private Handler getOkMainHandler() {
        return RequestExe.getOkMainThreadHandler();
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        getOkMainHandler().post(new Runnable() {
            @Override
            public void run() {
                String msg = RequestExe.getOkHttpExceptionMsg(e, "未知错误(-100)");
                onFailure(msg);
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response == null) {
            getOkMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    LogUtil.debugLog(TAG, "onSuccess: responseInfo null");
                    onFailure("未知错误(110)");
                }
            });
            return;
        }

        final String result = response.body().string();
        getOkMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(result)) {
                    LogUtil.debugLog(TAG, "onSuccess: result null");
                    onFailure("未知错误(111)");
                    return;
                }

                LogUtil.debugLog(TAG, "onSuccess:" + result.replace("\n", ""));

                if (listener != null) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("json", result);
                    listener.success(map);
                }
            }
        });
    }

    protected void onFailure(String errorMsg) {
        LogUtil.debugLog(TAG, "onFailure:" + errorMsg);
        if (listener != null) {
            listener.failed(errorMsg, 0);
        }
    }


}
