package com.caohua.games.biz.dataopen;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by admin on 2017/9/20.
 */

public class SwitchStatusLogic extends BaseLogic {
    public void switchStatus(final String managerGameId, final String index, final String status, final DataLogicListner<String> listener) {

        //数据给id，然后点击开关的时候，再传回去，就不用index

        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_USER_ID, userId == null ? "0" : getOpenUID());
                put(PARAMS_TOKEN, token == null ? "" : token);
                put("mg", managerGameId);
                put("i", index + "");
                put("s", status);
            }
        };

        RequestExe.post(HOST_APP + "push/switchStatus", model, new IRequestListener() {
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
