package com.chsdk.biz.app;

import android.content.Context;
import android.text.TextUtils;

import com.chsdk.api.AppOperator;
import com.chsdk.api.CacheManager;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.ResultBean;
import com.chsdk.utils.JsonUtil;
import com.chsdk.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/10/16.
 */

public class AppLogic<T extends Serializable> extends BaseLogic {
    protected Context context;
    protected Params params;

    public AppLogic(Context context, Params params) {
        this.context = context;
        this.params = params;
    }

    public void getData() {
        if (!NetworkUtils.isNetworkConnected(context)) {
            if (params.listner != null) {
                params.listner.failed(HttpConsts.ERROR_NO_NETWORK);
            }
            return;
        }

        String path = HOST_APP + params.url;
        RequestExe.post(path, params.model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    String responseString = map.get(HttpConsts.RESULT_PARAMS_DATA);
                    if (!TextUtils.isEmpty(responseString)) {
                        List bean = fromJsonArray(responseString, params.cls);
                        if (params.listner != null) {
                            params.listner.success(bean);
                        }
                        saveData(context, bean, params.saveFileName, params.append);
                    }
                    return;
                }
                saveData(context, null, params.saveFileName, params.append);
                if (params.listner != null) {
                    params.listner.failed(HttpConsts.ERROR_CODE_PARAMS_VALID);
                }
            }

            @Override
            public void failed(final String error, int errorCode) {
                if (params.listner != null) {
                    params.listner.failed(error);
                }
            }
        });
    }

    public static <T> List<T> getLocalData(Context context, String saveFileName) {
        ResultBean<T> pageBean = (ResultBean<T>) CacheManager.readObject(context, saveFileName);
        if (pageBean != null && pageBean.getData() != null) {
            List<T> list = pageBean.getData();
            return list;
        }
        return null;
    }

    public void saveData(final Context context, final List<? extends Serializable> entry, final String fileName, final boolean append) {
        if (append && entry == null) {
            return;
        }

        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                List data = null;
                if (append) {
                    data = getLocalData(context, fileName);
                    if (data == null) {
                        data = entry;
                    } else {
                        data.addAll(entry);
                    }
                    if (data != null && data.size() > 30) {
                        return;
                    }
                } else {
                    data = entry;
                }
                CacheManager.saveObject(context, new ResultBean(data), fileName);
            }
        });
    }

    protected <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        return JsonUtil.fromJsonArray(json, clazz);
    }

    public static class Params {
        public AppLogicListner listner;
        public String saveFileName;
        public BaseModel model;
        public String url;
        public Class cls;
        public boolean append;
    }
}
