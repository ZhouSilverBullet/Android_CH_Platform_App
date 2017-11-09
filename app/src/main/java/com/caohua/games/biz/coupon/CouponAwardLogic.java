package com.caohua.games.biz.coupon;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by zhouzhou on 2017/8/3.
 */

public class CouponAwardLogic extends BaseLogic {
    public void centerGet(final String centerId, final DataLogicListner<Integer> listener) {

        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_USER_ID, userId);
                put(PARAMS_TOKEN, token);
                put(PARAMS_CENTER_ID, centerId);
            }
        };

        RequestExe.post(HOST_APP + "coupon/centerGet", baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listener != null) {
                    if (map != null) {
                        int use_rate = getInt(map.get("use_rate"));
                        listener.success(use_rate);
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

    private int getInt(String value) {
        int i = 0;
        try {
            i = Integer.parseInt(value);
        } catch (Exception e) {
        }
        return i;
    }
}
