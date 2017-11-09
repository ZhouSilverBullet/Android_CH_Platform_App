package com.caohua.games.biz.account;

import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ZengLei on 2017/5/24.
 */

public class HomePageGameLogic extends BaseLogic {

    public void getData(final String userId, final DataLogicListner listner) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(PARAMS_TOKEN, session.getToken());
                put(PARAMS_USER_ID, session.getUserId());
                put("tui", userId);
            }
        };
        RequestExe.post(HOST_APP + "forum/userIndex", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    HomePageEntry entry = new HomePageEntry();
                    entry.nickName = map.get("nickname");
                    entry.photo = map.get("user_photo");
                    entry.typeName = map.get("type_name");
                    entry.upvoteCount = map.get("be_upvote");
                    entry.show_level = map.get("show_level");
                    entry.img_mask = map.get("img_mask");
                    entry.grow_name = map.get("grow_name");
                    String gameListJson = map.get("game_list");
                    entry.vip_level = map.get("vip_level");
                    entry.vip_name = map.get("vip_name");
                    entry.gameList = fromJsonArray(gameListJson, HomePageGameEntry.class);
                    if (listner != null) {
                        listner.success(entry);
                    }
                    return;
                }
                if (listner != null) {
                    listner.failed(HttpConsts.ERROR_CODE_PARAMS_VALID, 0);
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (listner != null) {
                    listner.failed(error, errorCode);
                }
            }
        });
    }
}
