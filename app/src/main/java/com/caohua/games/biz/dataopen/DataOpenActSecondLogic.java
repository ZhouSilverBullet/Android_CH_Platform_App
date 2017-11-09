package com.caohua.games.biz.dataopen;

import com.caohua.games.biz.dataopen.entry.DataOpenActEntry;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2017/9/27.
 */

public class DataOpenActSecondLogic extends BaseLogic {
    public void getDataOpenAct(final String managerGameId, final DataLogicListner<List<DataOpenActEntry>> listener) {
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

        /**
         * 这个的地址可能不一样
         */
        RequestExe.post(HOST_APP + "push/activeList", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listener != null) {
                    if (map != null) {
                        String data = map.get("data");
                        List<DataOpenActEntry> entry = fromJsonArray(data, DataOpenActEntry.class);
                        listener.success(entry);
                        return;
                    }
                    listener.failed(HttpConsts.ERROR_CODE_PARAMS_VALID, 100);
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
