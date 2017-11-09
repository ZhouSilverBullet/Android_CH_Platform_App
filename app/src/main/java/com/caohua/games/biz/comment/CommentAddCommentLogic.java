package com.caohua.games.biz.comment;

import android.os.Build;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.LogUtil;

import java.util.HashMap;

/**
 * Created by admin on 2017/1/12.
 */

public class CommentAddCommentLogic extends BaseLogic {
    private static final String COMMENT_TIMES = "comment/addComment";
    private final CommentEntry mCommentEntry;
    private String mCity;
    private CommentLogicListener listener;

    public CommentAddCommentLogic(String city, CommentEntry commentEntry) {
        mCity = city;
        mCommentEntry = commentEntry;
    }

    public void getAddComment(final CommentLogicListener listener) {
        this.listener = listener;
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                LoginUserInfo user = AppContext.getAppContext().getUser();
                if (user != null) {
                    put(PARAMS_USER_ID, user.userId);
                    put(PARAMS_TOKEN, user.token);
                } else {
                    put(PARAMS_USER_ID, "0");
                    put(PARAMS_TOKEN, "0");
                }
                String deviceNo = SdkSession.getInstance().getDeviceNo();
                if (mCommentEntry != null) {
                    put(PARAMS_COMMENT_ID, mCommentEntry.getCommentID());  //cid
                    put(PARAMS_COMMENT_TYPE, mCommentEntry.getCommentType()); //ct
                    put(PARAMS_COMMENT_GAME_TYPE, mCommentEntry.getCommentGameType()); //cgt
                    put(PARAMS_COMMENT_TYPE_ID, mCommentEntry.getCommentTypeId()); //ctid
                    put(PARAMS_COMMENT_IS_VERIFY, mCommentEntry.getCommentIsVerify());
                    put(PARAMS_COMMENT_CONTENT, mCommentEntry.getCommentContent());
                } else {
                    //我认为不可能为空吧
                }
                put(PARAMS_DEVICE_NO, deviceNo);
                put(PARAMS_COMMENT_COMMENT_ADDRESS, mCity);
                put(PARAMS_COMMENT_MOBILE, Build.MODEL);
            }
        };
        RequestExe.post(HOST_APP + COMMENT_TIMES, baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                String comment_id = map.get("comment_id");
                if (comment_id != null && listener != null) {
                    listener.success(comment_id);
                } else {
                    if (listener != null) {
                        LogUtil.errorLog("CommentAddCommentLogic", "comment_id 为空！");
                        listener.failed("评论失败，请稍后重试", 800);
                    }
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (null != listener) {
                    if (errorCode == 206) {
                        handleTokenError(error, errorCode);
                        return;
                    }
                    listener.failed(error, errorCode);
                }
            }
        });
    }

    private void handleTokenError(final String error, final int errorCode) {
        TokenRefreshLogic.refresh(new LogicListener() {
            @Override
            public void failed(String errorMsg) {
                if (listener != null) {
                    listener.failed(error, errorCode);
                }
            }

            @Override
            public void success(String... result) {
                getAddComment(listener);
            }
        });
    }
}
