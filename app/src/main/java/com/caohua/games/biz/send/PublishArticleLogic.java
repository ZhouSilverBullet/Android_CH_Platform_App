package com.caohua.games.biz.send;

import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

public class PublishArticleLogic extends BaseLogic {

    public static void post(final ArticleEntry args, final DataLogicListner listner) {
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(PARAMS_USER_ID, session.getUserId());
                put(PARAMS_FORUM_ID, args.forumId);
                put(PARAMS_TOKEN, session.getToken());
                put(PARAMS_AUTO_TOKEN, args.title);
                put("tt", args.content);
                put("ti", args.tag);
                put("ci", args.getPics());
            }
        };

        RequestExe.post(HOST_APP + "forum/pubArticle", baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    if (listner != null) {
                        listner.success(null);
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
