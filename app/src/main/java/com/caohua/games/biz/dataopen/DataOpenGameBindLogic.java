package com.caohua.games.biz.dataopen;

import com.caohua.games.biz.dataopen.entry.DataOpenGameBindEntry;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by admin on 2017/9/27.
 */

public class DataOpenGameBindLogic extends BaseLogic {
    public void getDataOpenGame(final String managerGameId, final DataLogicListner<DataOpenGameBindEntry> listener) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_USER_ID, userId == null ? "0" : getOpenUID());
                put(PARAMS_TOKEN, token == null ? "" : token);
                put(PARAMS_MANAGER_GAME_ID, managerGameId);
            }
        };

        RequestExe.post(HOST_APP + "push/getNewUserBind", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listener != null) {
                    if (map != null) {
                        DataOpenGameBindEntry entry = new DataOpenGameBindEntry();
                        entry.role_name = map.get("role_name");
                        entry.cp_sname = map.get("cp_sname");
                        entry.id = map.get("id");
                        entry.role_level = map.get("role_level");
                        entry.face = map.get("face");
                        listener.success(entry);
                        return;
                    }
                    listener.failed(HttpConsts.ERROR_CODE_PARAMS_VALID, -100);
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
