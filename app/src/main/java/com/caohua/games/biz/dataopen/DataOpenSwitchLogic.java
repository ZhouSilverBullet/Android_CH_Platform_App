package com.caohua.games.biz.dataopen;

import com.caohua.games.app.AppContext;
import com.caohua.games.biz.dataopen.entry.DataOpenSwitchEntry;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2017/9/20.
 */

public class DataOpenSwitchLogic extends BaseLogic {
    public void dataOpenSwitch(final String managerGameId, final DataLogicListner<List<DataOpenSwitchEntry>> listener) {
        if (!AppContext.getAppContext().isNetworkConnected()) {
            if (listener != null) {
                listener.failed(HttpConsts.ERROR_NO_NETWORK, -100);
            }
            return;
        }
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

        RequestExe.post(HOST_APP + "push/switchList", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listener != null) {
                    if (map != null) {
                        String data = map.get("data");
                        List<DataOpenSwitchEntry> entry = fromJsonArray(data, DataOpenSwitchEntry.class);
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
