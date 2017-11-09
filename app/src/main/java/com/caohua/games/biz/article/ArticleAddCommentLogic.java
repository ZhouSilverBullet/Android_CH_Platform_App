package com.caohua.games.biz.article;

import android.text.TextUtils;

import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by ZengLei on 2017/5/24.
 */

public class ArticleAddCommentLogic extends BaseLogic {

    /**
     * 评论贴子
     * @param articelId 帖子ID
     * @param content 文字内容
     * @param listener
     */
    public void addComment(final String articelId, final String content,
                        final LogicListener listener) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(PARAMS_TOKEN, session.getToken());
                put(PARAMS_USER_ID, session.getUserId());
                put("tt", content);
                put("ai", articelId);
            }
        };

        RequestExe.post(HOST_APP + "forum/addComment", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    String commentId = map.get("comment_id");
                    if (!TextUtils.isEmpty(commentId)) {
                        if (listener != null) {
                            listener.success(commentId);
                        }
                        return;
                    }
                }
                if (listener != null) {
                    listener.failed(HttpConsts.ERROR_CODE_PARAMS_VALID);
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
