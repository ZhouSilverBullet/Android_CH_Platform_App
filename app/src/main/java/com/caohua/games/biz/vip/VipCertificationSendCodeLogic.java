package com.caohua.games.biz.vip;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.LogUtil;

import java.util.HashMap;

/**
 * Created by admin on 2017/8/29.
 */

public class VipCertificationSendCodeLogic extends BaseLogic {
    public void sendCode(final String phone, final DataLogicListner<String> listener) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String token = session.getToken();
                String userId = session.getUserId();
                put(PARAMS_USER_ID, userId == null ? "" : userId);
                put(PARAMS_TOKEN, token == null ? "" : token);
                put(PARAMS_MSG_ID, phone);
            }
        };

        RequestExe.post(HOST_APP + "vip/sendAuthCode", model, new IRequestListener() {
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
