package com.caohua.games.biz.gift;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2017/10/27.
 */

public class GiftCenterClassifyLogic extends BaseLogic {

    public void centerClassify(final BaseLogic.DataLogicListner<GiftCenterClassifyEntry> listener) {
        if (!AppContext.getAppContext().isNetworkConnected()) {
            if (listener != null) {
                listener.failed(HttpConsts.ERROR_NO_NETWORK, -100);
                return;
            }
        }
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_USER_ID, userId == null ? "0" : userId);
                put(PARAMS_TOKEN, token == null ? "" : token);
            }
        };

        RequestExe.post(HOST_APP + "gift/gameGift", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    GiftCenterClassifyEntry entry = new GiftCenterClassifyEntry();
                    String gift = map.get("gift");
                    String nav = map.get("nav");
                    List<GiftCenterClassifyEntry.GiftBean> giftBeanList = fromJsonArray(gift, GiftCenterClassifyEntry.GiftBean.class);
                    List<String> list = fromJsonArray(nav, String.class);
                    entry.setGift(giftBeanList);
                    entry.setNav(list);
                    if (listener != null) {
                        listener.success(entry);
                    }
                    return;
                }
                if (listener != null) {
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
