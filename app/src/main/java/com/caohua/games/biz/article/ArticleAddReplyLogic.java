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

public class ArticleAddReplyLogic extends BaseLogic {

    /**
     * 回复
     * @param content 文字内容
     * @param commentId 上级评论ID
     * @param listener
     */
    public void addReply(final String content, final String commentId,
                        final LogicListener listener) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(PARAMS_TOKEN, session.getToken());
                put(PARAMS_USER_ID, session.getUserId());
                put("pi", commentId);
                put("tt", content);
            }
        };

        RequestExe.post(HOST_APP + "forum/addReply", model, new IRequestListener() {
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
