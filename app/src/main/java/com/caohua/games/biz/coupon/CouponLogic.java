package com.caohua.games.biz.coupon;

import android.text.TextUtils;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequest2Listener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.google.gson.Gson;

/**
 * Created by zhouzhou on 2017/8/2.
 */

public class CouponLogic extends BaseLogic {

    public void getCouponEntry(final int number, final AppLogicListner listener) {

        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_USER_ID, userId == null ? "0" : userId);
                put(PARAMS_TOKEN, token == null ? "" : token);
                put(PARAMS_PAGE_SIZE, 20 + "");
                put(PARAMS_NUMBER, number + "");
            }
        };

        RequestExe.post(HOST_APP + "coupon/couponCenter", baseModel, new IRequest2Listener() {
            @Override
            public void success(String result) {
                if (listener != null) {
                    try {
                        if (!TextUtils.isEmpty(result)) {
                            CouponEntry entry = new Gson().fromJson(result, CouponEntry.class);
                            listener.success(entry);
                            return;
                        }
                    } catch (Exception e) {
                    }
                    failed(HttpConsts.ERROR_CODE_PARAMS_VALID, -100);
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (listener != null) {
                    listener.failed(error);
                }
            }
        });
    }
}
