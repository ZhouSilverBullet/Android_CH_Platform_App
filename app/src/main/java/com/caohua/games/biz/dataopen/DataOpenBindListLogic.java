package com.caohua.games.biz.dataopen;

import com.caohua.games.biz.dataopen.entry.DataOpenBindListEntry;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2017/9/20.
 */

public class DataOpenBindListLogic extends BaseLogic {
    public void openBindLogic(final String messageGameId, final DataLogicListner<List<DataOpenBindListEntry>> listener) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_TOKEN, token == null ? "" : token);
                put(PARAMS_USER_ID, userId == null ? "0" : getOpenUID());
                put("mg", messageGameId);
            }
        };

        RequestExe.post(HOST_APP + "push/bindList", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listener != null) {
                    if (map != null) {
                        String data = map.get("data");
                        List<DataOpenBindListEntry> list = fromJsonArray(data, DataOpenBindListEntry.class);
                        listener.success(list);
                        return;
                    }

                    listener.success(new ArrayList<DataOpenBindListEntry>());
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
