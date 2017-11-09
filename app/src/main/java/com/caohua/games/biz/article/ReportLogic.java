package com.caohua.games.biz.article;

import com.chsdk.biz.BaseLogic;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by ZengLei on 2017/5/25.
 */

public class ReportLogic extends BaseLogic {

    public void report(final String articleId, final String content, final LogicListener listener) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(PARAMS_USER_ID, session.getUserId());
                put(PARAMS_TOKEN, session.getToken());
                put("ai", articleId);
                put("ra", content);
            }
        };
        RequestExe.post(HOST_APP + "forum/reportArticle", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listener != null) {
                    listener.success();
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (listener != null) {
                    listener.failed(error);
                }
            }
        });
    }
}
