package com.caohua.games.biz.dataopen;

import android.text.TextUtils;

import com.caohua.games.biz.dataopen.entry.DataOpenServerEntry;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2017/9/19.
 */

public class DataOpenServerLogic extends BaseLogic {

    public void getDataServerOpen(final String managerGameId, final DataLogicListner<List<DataOpenServerEntry>> listener) {
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

        RequestExe.post(HOST_APP + "push/getServer", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listener != null) {
                    if (map != null) {
                        String data = map.get("data");
                        if (!TextUtils.isEmpty(data)) {
                            List<DataOpenServerEntry> list = fromJsonArray(data, DataOpenServerEntry.class);
                            listener.success(list);
                            return;
                        }
                    }
                    listener.failed("服务数据空空如也，稍后再来看看吧", -100);
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
