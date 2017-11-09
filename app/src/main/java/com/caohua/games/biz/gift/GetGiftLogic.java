package com.caohua.games.biz.gift;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by admin on 2017/10/31.
 */

public class GetGiftLogic extends BaseLogic {
    public void getGift(int type, String dataToH5, final String giftId, final String item, final DataLogicListner<String> listener) {
        if (type == 99) {
            getGiftToH5(dataToH5, listener);
            return;
        }
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {

                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_USER_ID, userId == null ? "0" : userId);
                put(PARAMS_TOKEN, token == null ? "" : token);
                put(PARAMS_GIFT_ID, giftId);
                put(PARAMS_GIFT_ITEM, item);
            }
        };

        RequestExe.post(HOST_APP + "gift/showCardno", baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    String cardno = map.get(HttpConsts.RESULT_PARAMS_GIFT_CORDNO);
                    if (listener != null) {
                        listener.success(cardno);
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

    public void getGiftToH5(String data, final DataLogicListner<String> listener) {
        RequestExe.postToData(HOST_APP + "gift/showCardno", data, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    String cardno = map.get(HttpConsts.RESULT_PARAMS_GIFT_CORDNO);
                    if (listener != null) {
                        listener.success(cardno);
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
