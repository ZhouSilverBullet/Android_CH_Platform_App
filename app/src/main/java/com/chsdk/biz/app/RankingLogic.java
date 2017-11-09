package com.chsdk.biz.app;

import android.content.Context;

import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.utils.NetworkUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/10/16.
 */

public class RankingLogic<T extends Serializable> extends AppLogic {

    public RankingLogic(Context context, Params params) {
        super(context, params);
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
                    List<List<T>> list = new ArrayList<List<T>>();
                    String responseString = map.get(HttpConsts.RESULT_PARAMS_RANKING_MULTIPLE);
                    List<T> bean = fromJsonArray(responseString, params.cls);
                    if (bean != null) {
                        list.add(bean);
                    }

                    responseString = map.get(HttpConsts.RESULT_PARAMS_RANKING_NEWLY);
                    bean = fromJsonArray(responseString, params.cls);
                    if (bean != null) {
                        list.add(bean);
                    }

                    responseString = map.get(HttpConsts.RESULT_PARAMS_RANKING_RECOMMEND);
                    bean = fromJsonArray(responseString, params.cls);
                    if (bean != null) {
                        list.add(bean);
                    }

                    responseString = map.get(HttpConsts.RESULT_PARAMS_RANKING_ONLINE);
                    bean = fromJsonArray(responseString, params.cls);
                    if (bean != null) {
                        list.add(bean);
                    }

                    if (params.listner != null) {
                        params.listner.success(list);
                    }
                    saveData(context, list, params.saveFileName, params.append);
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
}
