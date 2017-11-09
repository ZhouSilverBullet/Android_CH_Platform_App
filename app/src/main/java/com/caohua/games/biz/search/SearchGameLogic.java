package com.caohua.games.biz.search;

import android.content.Context;

import com.chsdk.biz.app.AppLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by CXK on 2016/11/9.
 */

public class SearchGameLogic<T extends Serializable> extends AppLogic {
    private static final String ASSIGN_INFO_PATH = "search/searchCount";

    public SearchGameLogic(Context context) {
        super(context, null);
    }

    public void getAssignGame(final String gameName, final DataLogicListner<SearchCountEntry> listner) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                put(PARAMS_USER_ID, userId == null ? "0" : userId);
                put("sw", gameName);
            }
        };

        RequestExe.post(HOST_APP + ASSIGN_INFO_PATH, model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    SearchCountEntry searchCountEntry = new SearchCountEntry();
                    searchCountEntry.game = map.get("game");
                    searchCountEntry.news = map.get("news");
                    searchCountEntry.forum = map.get("forum");
                    if (listner != null) {
                        listner.success(searchCountEntry);
                    }
                    return;
                }
                if (listner != null) {
                    listner.failed(HttpConsts.ERROR_CODE_PARAMS_VALID, -100);
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (listner != null) {
                    listner.failed(error, errorCode);
                }
            }
        });
//        RequestExe.post(HOST_APP + ASSIGN_INFO_PATH, model, new IRequestListener() {
//            @Override
//            public void success(HashMap<String, String> map) {
//                if (map != null) {
//                    String responseString = map.get(HttpConsts.RESULT_PARAMS_DATA);
//                    List<T> bean = fromJsonArray(responseString, SearchGameEntry.class);
//                    if (listner != null) {
//                        listner.success(bean);
//                    }
//                    return;
//                }
//                if (listner != null) {
//                    listner.failed(HttpConsts.ERROR_CODE_PARAMS_VALID);
//                }
//            }
//
//            @Override
//            public void failed(String error, int errorCode) {
//                if (listner != null) {
//                    listner.failed(error);
//                }
//            }
//        });
    }

}
