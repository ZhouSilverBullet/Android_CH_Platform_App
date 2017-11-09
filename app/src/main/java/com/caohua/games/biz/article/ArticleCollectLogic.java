package com.caohua.games.biz.article;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by zhouzhou on 2017/6/6.
 */

public class ArticleCollectLogic extends BaseLogic {

    public void articleCollect(final String articleId, final String oc, final LogicListener listener) {
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(BaseModel.PARAMS_USER_ID, SdkSession.getInstance().getUserId());
                put(BaseModel.PARAMS_ANDROID_ID, articleId);
                put("oc", oc);
            }
        };
        RequestExe.post(HOST_APP + "forum/articleCollect", baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listener != null) {
                    listener.success("");
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
