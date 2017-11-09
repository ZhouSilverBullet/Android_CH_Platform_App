package com.caohua.games.biz.prefecture;

import android.content.Context;
import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.chsdk.api.AppOperator;
import com.chsdk.api.CacheManager;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequest2Listener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.JsonUtil;
import com.chsdk.utils.NetworkUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhouzhou on 2017/5/9.
 */

public class PrefectureLogic extends BaseLogic {
    public static final String PREFECTURE_URL = "subject/index";
    private Context context;
    private int subjectId;

    public PrefectureLogic(Context context, int subjectId) {
        this.context = context;
        this.subjectId = subjectId;
    }

    public void getPrefectureData(final GameCenterLogic.AppGameCenterListener listener) {
        if (!NetworkUtils.isNetworkConnected(context)) {
            if (listener != null) {
                listener.failed(HttpConsts.ERROR_NO_NETWORK);
            }
            return;
        }
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(BaseModel.PARAMS_SUBJECT_GAME_ID, subjectId + "");
            }
        };

        RequestExe.post(HOST_APP + PREFECTURE_URL, baseModel, new IRequest2Listener() {
            @Override
            public void success(String result) {
                if (listener != null) {
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            final PrefectureEntry topEntry = new Gson().fromJson(result, PrefectureEntry.class);
                            AppOperator.runOnThread(new Runnable() {
                                @Override
                                public void run() {
                                    CacheManager.saveObject(AppContext.getAppContext(), topEntry, "PrefectureEntry");
                                }
                            });
                            listener.success(topEntry, 0);
                        } catch (Exception e) {
                            resolveError(result, listener);
                        }

                    } else {
                        listener.failed("");
                    }
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (listener != null) {
                    listener.failed(error + "(" + errorCode + ")");
                }
            }
        });
    }

    private void resolveError(String result, GameCenterLogic.AppGameCenterListener listener) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e1) {
        }
        int code = JsonUtil.getStatusCode(jsonObject);
        String msg = JsonUtil.getServerMsg(jsonObject);
        if (code == 0 || code == 200) {
            listener.success(null, -1);
            return;
        }
        listener.failed(msg + "(" + code + ")");
    }

}
