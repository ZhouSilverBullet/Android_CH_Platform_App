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
 * Created by admin on 2017/9/25.
 */

public class VipRebateLogic extends BaseLogic {
    public void getRebate(final String rbi, final DataLogicListner<VipRebateEntry> listener) {
        if (!AppContext.getAppContext().isNetworkConnected()) {
            if (listener != null) {
                listener.failed(HttpConsts.ERROR_NO_NETWORK, -100);
            }
            return;
        }
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String token = session.getToken();
                String userId = session.getUserId();
                put(PARAMS_USER_ID, userId == null ? "" : userId);
                put(PARAMS_TOKEN, token == null ? "" : token);
                put("rbi", rbi);
            }
        };

        RequestExe.post(HOST_APP + "vip/rebateDetail", baseModel, new IRequest2Listener() {
            @Override
            public void success(String result) {
                if (listener != null) {
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            VipRebateEntry entry = new Gson().fromJson(result, VipRebateEntry.class);
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
