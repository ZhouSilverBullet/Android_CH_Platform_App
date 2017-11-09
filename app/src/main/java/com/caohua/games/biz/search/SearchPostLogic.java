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
 * Created by admin on 2017/10/26.
 */

public class SearchPostLogic extends BaseLogic {
    public void searchPost(final String gameName, final int size, final DataLogicListner<List<SearchPostEntry>> listener) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                put(PARAMS_USER_ID, userId == null ? "0" : userId);
                put("sw", gameName);
                put(PARAMS_PAGE_SIZE, 10 + "");
                put(PARAMS_NUMBER, size + "");
            }
        };

        RequestExe.post(HOST_APP + "search/searchForum", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    String responseString = map.get(HttpConsts.RESULT_PARAMS_DATA);
                    List<SearchPostEntry> entryList = fromJsonArray(responseString, SearchPostEntry.class);
                    if (listener != null) {
                        listener.success(entryList);
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
