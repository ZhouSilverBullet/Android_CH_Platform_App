package com.caohua.games.biz.bbs;

import android.content.Context;

import com.caohua.games.app.AppContext;
import com.caohua.games.biz.prefecture.GameCenterLogic;
import com.chsdk.api.AppOperator;
import com.chsdk.api.CacheManager;
import com.chsdk.biz.app.AppLogic;
import com.chsdk.http.IRequest2Listener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.JsonUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhouzhou on 2017/5/25.
 */

public class BBSTopLogic extends AppLogic {

    private static final String BBS_TOP = "forum/forumIndex";
    private int page;
    private int forumId;

    public BBSTopLogic(Context context, int forumId, int page) {
        super(context, null);
        this.forumId = forumId;
        this.page = page;
    }

    public void getTop(final GameCenterLogic.AppGameCenterListener listener) {
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                LoginUserInfo user = AppContext.getAppContext().getUser();
                if (user != null) {
                    put(BaseModel.PARAMS_USER_ID, user.userId);
                }
                put(BaseModel.PARAMS_FORUM_ID, forumId + "");
            }
        };
        RequestExe.post(HOST_APP + BBS_TOP, baseModel, new IRequest2Listener() {
            @Override
            public void success(String result) {
                if (result != null) {
                    if (listener != null) {
                        try {
                            final BBSTopEntry entry = new Gson().fromJson(result, BBSTopEntry.class);
                            listener.success(entry, page);
                            if (page == 0) {
                                AppOperator.runOnThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CacheManager.saveObject(AppContext.getAppContext(), entry, "BBSTopEntry");
                                    }
                                });
                            }
                        } catch (Exception e) {
                            resolveError(result, listener);
                        }
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
