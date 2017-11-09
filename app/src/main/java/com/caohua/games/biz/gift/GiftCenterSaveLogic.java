package com.caohua.games.biz.gift;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2017/10/27.
 */

public class GiftCenterSaveLogic extends BaseLogic {

    public void centerSave(final int size, final BaseLogic.DataLogicListner<List<GiftCenterSaveEntry>> listener) {
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
                put(PARAMS_PAGE_SIZE, 10 + "");
                put(PARAMS_NUMBER, size + "");
                put(PARAMS_TOKEN, token == null ? "" : token);
            }
        };

        RequestExe.post(HOST_APP + "gift/myGift", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    String responseString = map.get(HttpConsts.RESULT_PARAMS_DATA);
                    List<GiftCenterSaveEntry> searchArticleEntryList = fromJsonArray(responseString, GiftCenterSaveEntry.class);
                    if (listener != null) {
                        listener.success(searchArticleEntryList);
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
