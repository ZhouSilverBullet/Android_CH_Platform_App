package com.caohua.games.biz.vip;

import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequest2Listener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.google.gson.Gson;

/**
 * Created by admin on 2017/8/29.
 */

public class VipRechargeAwardLogic extends BaseLogic {
    public void getRechargeAward(final DataLogicListner<VipRechargeAwardEntry> listener) {
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
                String token = session.getToken();
                String userId = session.getUserId();
                put(PARAMS_USER_ID, userId == null ? "" : userId);
                put(PARAMS_TOKEN, token == null ? "" : token);
            }
        };

        RequestExe.post(HOST_APP + "vip/rechargeDetail", model, new IRequest2Listener() {
            @Override
            public void success(String result) {
                if (listener != null) {
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            VipRechargeAwardEntry entry = new Gson().fromJson(result, VipRechargeAwardEntry.class);
                            listener.success(entry);
                        } catch (Exception e) {
                            listener.failed(HttpConsts.ERROR_CODE_PARAMS_VALID, -100);
                        }
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
}
