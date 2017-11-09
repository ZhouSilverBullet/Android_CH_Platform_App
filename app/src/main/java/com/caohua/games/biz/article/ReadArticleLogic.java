package com.caohua.games.biz.article;

import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by ZengLei on 2017/5/24.
 */

public class ReadArticleLogic extends BaseLogic {

    public void read(final String articelId, final DataLogicListner listner) {
        BaseModel model = new BaseModel(){
            @Override
            public void putDataInMap() {
                put(PARAMS_TOKEN, session.getToken());
                put(PARAMS_USER_ID, session.getUserId());
                put("ai", articelId);
            }
        };
        RequestExe.post(HOST_APP + "forum/readArticle", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    ReadArticleEntry entry = new ReadArticleEntry();
                    entry.comment_total = map.get("comment_total");
                    entry.upvote_total = map.get("upvote_total");
                    entry.is_collect = map.get("is_collect");
                    entry.is_top = map.get("is_top");
                    entry.priv_top = map.get("priv_top");
                    entry.is_good = map.get("is_good");
                    entry.priv_good = map.get("priv_good");
                    entry.is_lock = map.get("is_lock");
                    entry.priv_lock = map.get("priv_lock");
                    entry.is_hide = map.get("is_hide");
                    entry.priv_hide = map.get("priv_hide");
                    entry.is_vote = map.get("is_vote");
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
