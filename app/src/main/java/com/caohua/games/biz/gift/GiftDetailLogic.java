package com.caohua.games.biz.gift;

import com.caohua.games.app.AppContext;
import com.caohua.games.ui.giftcenter.GiftDetailActivity;
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

public class GiftDetailLogic extends BaseLogic {
    public void giftDetail(int type, final String gift_id, String dataToH5, final DataLogicListner<GiftDetailEntry> listener) {
        if (!AppContext.getAppContext().isNetworkConnected()) {
            if (listener != null) {
                listener.failed(HttpConsts.ERROR_NO_NETWORK, -100);
            }
            return;
        }
        if (type == GiftDetailActivity.TYPE_SPECIAL) {
            giftToH5(dataToH5, listener);
            return;
        }
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_USER_ID, userId == null ? "0" : userId);
                put(PARAMS_TOKEN, token == null ? "" : token);
                put("gid", gift_id);
            }
        };

        RequestExe.post(HOST_APP + "gift/giftDetail", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    GiftDetailEntry entry = new GiftDetailEntry();
                    entry.gift_id = map.get("gift_id");
                    entry.gift_name = map.get("gift_name");
                    entry.gift_desc = map.get("gift_desc");
                    entry.take_limit = map.get("take_limit");
                    entry.gift_time = map.get("gift_time");
                    entry.use_state = map.get("use_state");
                    entry.game_icon = map.get("game_icon");
                    entry.game_name = map.get("game_name");
                    entry.package_name = map.get("package_name");
                    entry.game_url = map.get("game_url");
                    entry.take_hb = map.get("take_hb");
                    entry.take_grow = map.get("take_grow");
                    entry.take_level = map.get("take_level");
                    entry.take_vip = map.get("take_vip");
                    entry.game_introduct = map.get("game_introduct");
                    entry.item = getIntValue(map.get("item"));
                    entry.take_certify = getIntValue(map.get("take_certify"));
                    entry.is_get = getIntValue(map.get("is_get"));

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

    private void giftToH5(final String dataToH5, final DataLogicListner<GiftDetailEntry> listener) {
        RequestExe.postToData(HOST_APP + "gift/giftDetail", dataToH5, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    GiftDetailEntry entry = new GiftDetailEntry();
                    entry.gift_id = map.get("gift_id");
                    entry.gift_name = map.get("gift_name");
                    entry.gift_desc = map.get("gift_desc");
                    entry.take_limit = map.get("take_limit");
                    entry.gift_time = map.get("gift_time");
                    entry.use_state = map.get("use_state");
                    entry.game_icon = map.get("game_icon");
                    entry.game_name = map.get("game_name");
                    entry.package_name = map.get("package_name");
                    entry.take_hb = map.get("take_hb");
                    entry.take_grow = map.get("take_grow");
                    entry.take_level = map.get("take_level");
                    entry.take_vip = map.get("take_vip");
                    entry.game_introduct = map.get("game_introduct");
                    entry.item = getIntValue(map.get("item"));
                    entry.take_certify = getIntValue(map.get("take_certify"));
                    entry.is_get = getIntValue(map.get("is_get"));

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

    private int getIntValue(String value) {
        int i = 0;
        try {
            i = Integer.parseInt(value);
        } catch (Exception e) {
        }
        return i;
    }
}
