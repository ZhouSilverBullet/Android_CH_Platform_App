package com.caohua.games.biz.bbs;

import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.caohua.games.biz.prefecture.GameCenterLogic;
import com.chsdk.api.AppOperator;
import com.chsdk.api.CacheManager;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequest2Listener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.utils.JsonUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhouzhou on 2017/6/20.
 */

public class BBSListLogic extends BaseLogic {

    public void getBBSListLogic(final String page, final AppLogicListner listener) {

        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(BaseModel.PARAMS_USER_ID, SdkSession.getInstance().getUserId());
            }
        };

        RequestExe.post(HOST_APP + "forum/forumHome", model, new IRequest2Listener() {
            @Override
            public void success(String result) {
                if (listener != null) {
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            final ForumListEntry forumListEntry = new Gson().fromJson(result, ForumListEntry.class);
                            listener.success(forumListEntry);
                            if ("0".equals(page)) {
                                AppOperator.runOnThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CacheManager.saveObject(AppContext.getAppContext(), forumListEntry, "ForumListEntry");
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

    private void resolveError(String result, AppLogicListner listener) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e1) {
        }
        int code = JsonUtil.getStatusCode(jsonObject);
        String msg = JsonUtil.getServerMsg(jsonObject);
        if (code == 0 || code == 200) {
            listener.success(null);
            return;
        }
        listener.failed(msg + "(" + code + ")");
    }
}
