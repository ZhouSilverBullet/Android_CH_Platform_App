package com.caohua.games.biz.gift;

import android.text.TextUtils;

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
 * Created by admin on 2017/10/31.
 */

public class GameGiftLogic extends BaseLogic {
    public void gameGift(final String giftGameId, final DataLogicListner<GameGiftEntry> listener) {
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
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_USER_ID, userId == null ? "0" : userId);
                put(PARAMS_TOKEN, token == null ? "" : token);
                put("ggid", giftGameId);
            }
        };

        RequestExe.post(HOST_APP + "gift/gameList", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    GameGiftEntry entry = new GameGiftEntry();
                    String list = map.get("list");
                    String game = map.get("game");
                    List<GameGiftEntry.ListBean> listBeen = fromJsonArray(list, GameGiftEntry.ListBean.class);
                    GameGiftEntry.GameBean gameBean = null;
                    if (!TextUtils.isEmpty(game)) {
                        gameBean = new Gson().fromJson(game, GameGiftEntry.GameBean.class);
                    }
                    entry.setList(listBeen);
                    entry.setGame(gameBean);
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
