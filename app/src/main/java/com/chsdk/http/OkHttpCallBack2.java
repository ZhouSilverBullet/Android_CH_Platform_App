package com.chsdk.http;

import android.os.Handler;
import android.text.TextUtils;

import com.chsdk.utils.JsonUtil;
import com.chsdk.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 2017年5月9日
 */
class OkHttpCallBack2 implements Callback {
    private static final String TAG = OkHttpCallBack2.class.getSimpleName();
    private IRequest2Listener listener;

    public OkHttpCallBack2(IRequest2Listener listener) {
        this.listener = listener;
    }

    private Handler getOkMainHandler() {
        return RequestExe.getOkMainThreadHandler();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (response == null) {
            LogUtil.debugLog(TAG, "onSuccess: responseInfo null");
            getOkMainHandler().post(new Runnable() {
                @Override
                public void run() {
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

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                } catch (JSONException e) {
                    LogUtil.debugLog(TAG, "onSuccess: JSONException:" + e.getMessage() + ", result_" + result);
                    onFailure("未知错误(112)");
                }

                if (jsonObject != null && listener != null) {
                    int code = JsonUtil.getStatusCode(jsonObject);
                    String msg = JsonUtil.getServerMsg(jsonObject);
                    if (code != HttpConsts.CODE_SUCCESS) {
                        onFailure(msg, code);
                    } else {
                        listener.success(result);
                    }
                }
            }
        });

    }

    protected void onFailure(String errorMsg) {
        onFailure(errorMsg, 0);
    }

    protected void onFailure(String errorMsg, int errorCode) {
        LogUtil.debugLog(TAG, "onFailure:" + errorMsg);
        if (listener != null) {
            listener.failed(errorMsg, errorCode);
        }
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        LogUtil.errorLog(TAG + " onFailure IOException :  " + e);
        getOkMainHandler().post(new Runnable() {
            @Override
            public void run() {
                String msg = RequestExe.getOkHttpExceptionMsg(e, "未知错误(-100)");
                onFailure(msg);
            }
        });
    }

}
