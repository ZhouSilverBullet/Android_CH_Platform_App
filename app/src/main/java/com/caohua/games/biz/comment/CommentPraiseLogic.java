package com.caohua.games.biz.comment;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;

import java.util.HashMap;

/**
 * Created by admin on 2017/1/12.
 */

public class CommentPraiseLogic extends BaseLogic {
    private static final String COMMENT_TIMES = "comment/praise";
    private final TimesEntry mTimesEntry;
    private final String mCommentID;
    private AppLogicListner listner;

    public CommentPraiseLogic(TimesEntry timesEntry, String commentID) {
        mTimesEntry = timesEntry;
        mCommentID = commentID;
    }

    public void getCommentPraise(final BaseLogic.AppLogicListner listner) {
        this.listner = listner;
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                LoginUserInfo user = AppContext.getAppContext().getUser();
                if (user != null) {
                    put(PARAMS_USER_ID, user.userId);
                    put(PARAMS_TOKEN, user.token);
                }
                put(PARAMS_COMMENT_ID, mCommentID == null ? "0" : mCommentID);  //自己评论是0
                if (mTimesEntry == null) {
                    put(PARAMS_COMMENT_TYPE, "0");
                    put(PARAMS_COMMENT_GAME_TYPE, "0");
                    put(PARAMS_COMMENT_TYPE_ID, "0");
                } else {
                    put(PARAMS_COMMENT_TYPE, mTimesEntry.getCommentType());
                    put(PARAMS_COMMENT_GAME_TYPE, mTimesEntry.getType());
                    put(PARAMS_COMMENT_TYPE_ID, mTimesEntry.getId());
                }
            }
        };
        RequestExe.post(HOST_APP + COMMENT_TIMES, baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listner != null) {
                    listner.success("");
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (null != listner) {
                    if (errorCode == 206) {
                        handleTokenError(error);
                        return;
                    }
                    listner.failed(error);
                }
            }
        });
    }

    private void handleTokenError(final String error) {
        TokenRefreshLogic.refresh(new LogicListener() {
            @Override
            public void failed(String errorMsg) {
                if (listner != null) {
                    listner.failed(error);
                }
            }

            @Override
            public void success(String... result) {
                getCommentPraise(listner);
            }
        });
    }
}
