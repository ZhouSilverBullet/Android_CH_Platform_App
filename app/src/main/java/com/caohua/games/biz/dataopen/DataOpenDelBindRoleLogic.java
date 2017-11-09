package com.caohua.games.biz.dataopen;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by admin on 2017/9/29.
 */

public class DataOpenDelBindRoleLogic extends BaseLogic {
    public void openDelBindRole(final String managerGameId, final String id, final DataLogicListner<String> listener) {
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_USER_ID, userId == null ? "0" : getOpenUID());
                put(PARAMS_TOKEN, token == null ? "" : token);
                put(PARAMS_MANAGER_GAME_ID, managerGameId);
                put("i", id);
            }
        };

        RequestExe.post(HOST_APP + "push/delBindRole", baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listener != null) {
                    listener.success("");
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (listener != null) {
                    listener.failed(error, errorCode);
                }
            }
        });
    }
}
