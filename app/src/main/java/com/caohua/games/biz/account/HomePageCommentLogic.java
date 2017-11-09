package com.caohua.games.biz.account;

import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ZengLei on 2017/5/24.
 */

public class HomePageCommentLogic extends BaseLogic {

    public void getData(final String userId, final String requestCount, final String loadedCount, final DataLogicListner listner) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(PARAMS_TOKEN, session.getToken());
                put(PARAMS_USER_ID, session.getUserId());
                put("tui", userId);
                put(PARAMS_NUMBER, loadedCount);
                put(PARAMS_PAGE_SIZE, requestCount);
            }
        };
        RequestExe.post(HOST_APP + "forum/userComment", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                List<HomePageCommentEntry> entry = null;
                if (map != null) {
                    String gameListJson = map.get(HttpConsts.RESULT_PARAMS_DATA);
                    entry = fromJsonArray(gameListJson, HomePageCommentEntry.class);
                }
                if (listner != null) {
                    listner.success(entry);
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
