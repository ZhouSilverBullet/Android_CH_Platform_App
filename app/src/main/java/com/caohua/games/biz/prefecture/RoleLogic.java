package com.caohua.games.biz.prefecture;

import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.http.IRequest2Listener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.JsonUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhouzhou on 2017/5/31.
 */

public class RoleLogic extends BaseLogic {
    private String subjectId;

    public RoleLogic(String subjectId) {
        this.subjectId = subjectId;
    }

    public void role(final GameCenterLogic.AppGameCenterListener listener) {
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(BaseModel.PARAMS_SUBJECT_GAME_ID, subjectId);
                LoginUserInfo user = AppContext.getAppContext().getUser();
                if (user != null) {
                    put(BaseModel.PARAMS_USER_ID, user.userId);
                    put(BaseModel.PARAMS_TOKEN, user.token);
                }
            }
        };
        RequestExe.post(HOST_APP + "subject/role", baseModel, new IRequest2Listener() {
            @Override
            public void success(String result) {
                if (listener != null) {
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            RoleEntry roleEntry = new Gson().fromJson(result, RoleEntry.class);
                            listener.success(roleEntry, 0);
                        } catch (Exception e) {
                            resolveError(result, listener);
                        }
                    }
                }
            }

            @Override
            public void failed(String error, int errorCode) {

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
        listener.failed(msg + "(" + code + ")");
    }
}
