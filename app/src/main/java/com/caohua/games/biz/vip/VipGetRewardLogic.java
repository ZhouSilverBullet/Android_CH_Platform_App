package com.caohua.games.biz.vip;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by admin on 2017/8/30.
 */

public class VipGetRewardLogic extends BaseLogic {
    public void getReward(final int getType, final DataLogicListner<String> listener) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String token = session.getToken();
                String userId = session.getUserId();
                put(PARAMS_USER_ID, userId == null ? "" : userId);
                put(PARAMS_TOKEN, token == null ? "" : token);
                put("gt", getType + "");
            }
        };

        RequestExe.post(HOST_APP + "vip/getReward", model, new IRequestListener() {
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
