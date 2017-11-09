package com.caohua.games.biz.dataopen;

import android.text.TextUtils;

import com.caohua.games.app.AppContext;
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
 * Created by admin on 2017/9/21.
 */

public class DataOpenActLogic extends BaseLogic {
    //pager 为all时，后台认为是列表
    public void dataOpenAct(final String managerGameId, final String pager,final DataLogicListner<List<DataOpenActEntry>> listener) {
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
                if (!TextUtils.isEmpty(pager)) {
                    put("p", pager);
                }
            }
        };

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
