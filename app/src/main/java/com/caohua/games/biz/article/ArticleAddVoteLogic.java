package com.caohua.games.biz.article;

import com.chsdk.biz.BaseLogic;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by ZengLei on 2017/5/24.
 */

public class ArticleAddVoteLogic extends BaseLogic {
    /**
     *  点赞
     * @param articelId  帖子ID（帖子点赞）
     * @param commentId 评论ID（评论点赞）
     * @param voteType 点赞类型 1.帖子 2.评论/回复
     * @param listener
     */
    public void addVote(final String articelId, final String commentId, final String voteType,
                        final CommentLogicListener listener) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(PARAMS_TOKEN, session.getToken());
                put(PARAMS_USER_ID, session.getUserId());
                put("ci", commentId);
                put("vt", voteType);
                put("ai", articelId);
            }
        };
        RequestExe.post(HOST_APP + "forum/addVote", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listener != null) {
                    listener.success("1");
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
