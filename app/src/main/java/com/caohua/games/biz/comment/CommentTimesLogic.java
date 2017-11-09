package com.caohua.games.biz.comment;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.LogUtil;

import java.util.HashMap;

/**
 * Created by admin on 2017/1/12.
 */

public class CommentTimesLogic extends BaseLogic {
    private static final String COMMENT_TIMES = "comment/times";
    private final TimesEntry mTimesEntry;
    private AppLogicListner listner;

    public CommentTimesLogic(TimesEntry timesEntry) {
        mTimesEntry = timesEntry;
    }

    public void getCommentTimes(final AppLogicListner listner) {
        this.listner = listner;
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
                if (mTimesEntry != null) {
                    put(PARAMS_COMMENT_TYPE, mTimesEntry.getCommentType());
                    put(PARAMS_COMMENT_GAME_TYPE, mTimesEntry.getType());
                    put(PARAMS_COMMENT_TYPE_ID, mTimesEntry.getId());
                } else {
                    put(PARAMS_COMMENT_TYPE, "0");
                    put(PARAMS_COMMENT_GAME_TYPE, "0");
                    put(PARAMS_COMMENT_TYPE_ID, "0");
                }
            }
        };
        RequestExe.post(HOST_APP + COMMENT_TIMES, baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null && listner != null) {
                    TimesPraiseEntry entry = new TimesPraiseEntry();
                    entry.setComment_times(map.get("comment_times"));
                    entry.setPraise_times(map.get("praise_times"));
                    entry.setIs_praise(map.get("is_praise"));
                    entry.setArticle_icon(map.get("article_icon"));
                    entry.setArticle_title(map.get("article_title"));
                    entry.setArticle_url(map.get("article_url"));
                    entry.setIs_collect(map.get("is_collect"));
                    entry.setArticle_id(map.get("article_id"));
                    LogUtil.errorLog(entry.getComment_times() + "---" + entry.getPraise_times() + "---" +
                            entry.getIs_praise() + "---" + entry.getArticle_icon() + " --- " + entry.getArticle_title()
                    );
                    listner.success(entry);
                } else {
                    if (listner != null) {
                        listner.failed("map 为空！");
                    }
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (null != listner) {
                    if (BaseLogic.isTokenInvialid(error)) {
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
                getCommentTimes(listner);
            }
        });
    }
}
