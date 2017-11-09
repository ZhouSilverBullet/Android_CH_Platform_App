package com.caohua.games.biz.coupon;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequest2Listener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.google.gson.Gson;

/**
 * Created by admin on 2017/8/22.
 */

public class ColorEggSearchLogic extends BaseLogic {
    public void eggSearch(final String value, final DataLogicListner<EggSearchEntry> listener) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_USER_ID, userId == null ? "0" : userId);
                put(PARAMS_TOKEN, token == null ? "" : token);
                put(PARAMS_SEARCH_EGG, value);
            }
        };


        RequestExe.post(HOST_APP + "coloregg/coloreggSearch", model, new IRequest2Listener() {
            @Override
            public void success(String result) {
                if (listener != null) {
                    if (result != null) {
                        try {
                            EggSearchEntry entry = new Gson().fromJson(result, EggSearchEntry.class);
                            listener.success(entry);
                        } catch (Exception e) {
                            listener.failed(HttpConsts.ERROR_CODE_PARAMS_VALID, -100);
                        }
                        return;
                    } else {
                        listener.failed(HttpConsts.ERROR_CODE_PARAMS_VALID, -100);
                    }
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
