package com.caohua.games.biz.account;

import android.content.Context;
import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.caohua.games.biz.prefecture.GameCenterLogic;
import com.chsdk.api.AppOperator;
import com.chsdk.api.CacheManager;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequest2Listener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.utils.JsonUtil;
import com.chsdk.utils.NetworkUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhouzhou on 2017/5/28.
 */

public class TieZiLogic extends BaseLogic {
    private Context context;
    private int page;
    private String toUserId;

    public TieZiLogic(Context context, int page, String toUserId) {
        this.context = context;
        this.page = page;
        this.toUserId = toUserId;
    }

    public void tieZi(final GameCenterLogic.AppGameCenterListener listener) {
        if (!NetworkUtils.isNetworkConnected(context)) {
            if (listener != null) {
                listener.failed(HttpConsts.ERROR_NO_NETWORK);
            }
            return;
        }

        final BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(PARAMS_USER_ID, SdkSession.getInstance().getUserId());
                if (!TextUtils.isEmpty(toUserId)) {
                    put(PARAMS_TO_USER_ID, toUserId);
                } else {
                    put(PARAMS_TO_USER_ID, SdkSession.getInstance().getUserId());
                }
                put(PARAMS_NUMBER, page + "");
                put(PARAMS_PAGE_SIZE, 20 + "");
            }
        };

        RequestExe.post(HOST_APP + "forum/userArticle", baseModel, new IRequest2Listener() {
            @Override
            public void success(String result) {
                if (listener != null) {
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            final TieZiEntry tieZiEntry = new Gson().fromJson(result, TieZiEntry.class);
                            listener.success(tieZiEntry, page);
                            if (page == 0) {
                                AppOperator.runOnThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CacheManager.saveObject(AppContext.getAppContext(), tieZiEntry, "TieZiEntry");
                                    }
                                });
                            }
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
                    listener.failed(error);
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
