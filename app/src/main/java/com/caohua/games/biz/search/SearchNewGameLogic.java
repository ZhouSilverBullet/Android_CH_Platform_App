package com.caohua.games.biz.search;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2017/10/25.
 */

public class SearchNewGameLogic extends BaseLogic {
    public void newGame(final String gameName, final int size, final DataLogicListner<List<SearchGameEntry>> listener) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                put(PARAMS_USER_ID, userId == null ? "0" : userId);
                put("sw", gameName);
                put(PARAMS_PAGE_SIZE, 20 + "");
                put(PARAMS_NUMBER, size + "");
            }
        };

        RequestExe.post(HOST_APP + "search/searchGame", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    String responseString = map.get(HttpConsts.RESULT_PARAMS_DATA);
                    List<SearchGameEntry> bean = fromJsonArray(responseString, SearchGameEntry.class);
                    if (listener != null) {
                        listener.success(bean);
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
