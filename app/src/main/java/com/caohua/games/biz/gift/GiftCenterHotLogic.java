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
 * Created by admin on 2017/10/28.
 */

public class GiftCenterHotLogic extends BaseLogic {

    public void centerHot(final int size, final BaseLogic.DataLogicListner<GiftCenterHotEntry> listener) {
        if (!AppContext.getAppContext().isNetworkConnected()) {
            if (listener != null) {
                listener.failed(HttpConsts.ERROR_NO_NETWORK, -100);
                return;
            }
            return;
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

        RequestExe.post(HOST_APP + "gift/hotGift", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    GiftCenterHotEntry entry = new GiftCenterHotEntry();
                    String game = map.get("list");
                    String myList = map.get("my_list");
                    List<GiftCenterHotEntry.ListBean> listBeen = fromJsonArray(game, GiftCenterHotEntry.ListBean.class);
                    List<GiftCenterHotEntry.MyListBean> myGameList = fromJsonArray(myList, GiftCenterHotEntry.MyListBean.class);
                    entry.setList(listBeen);
                    entry.setMy_list(myGameList);
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
