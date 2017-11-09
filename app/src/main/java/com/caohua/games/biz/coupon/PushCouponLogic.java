package com.caohua.games.biz.coupon;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by admin on 2017/8/9.
 */

public class PushCouponLogic extends BaseLogic {
    public void getCoupon(final String msgId, final DataLogicListner listener) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_USER_ID, userId == null ? "0" : userId);
                put(PARAMS_TOKEN, token == null ? "" : token);
                put(PARAMS_MSG_ID, msgId == null ? "0" : msgId);
            }
        };
        RequestExe.post(HOST_PASSPORT + "message/msgReward", model, new IRequestListener() {
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
