package com.caohua.games.biz.bbs;

import android.content.Context;
import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.caohua.games.biz.prefecture.GameCenterEntry;
import com.caohua.games.biz.prefecture.GameCenterLogic;
import com.chsdk.api.AppOperator;
import com.chsdk.api.CacheManager;
import com.chsdk.biz.app.AppLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequest2Listener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.JsonUtil;
import com.chsdk.utils.NetworkUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhouzhou on 2017/5/26.
 */

public class BBSContentLogic extends AppLogic {
    private static final String BBS_CONTENT = "forum/articleList";
    private int isSort;
    private BBSContentParam param;

    public BBSContentLogic(int isSort, Context context, BBSContentParam param) {
        super(context, null);
        this.param = param;
        this.isSort = isSort;
    }

    public void getContent(final GameCenterLogic.AppGameCenterListener listener) {
        if (!NetworkUtils.isNetworkConnected(context)) {
            if (listener != null) {
                listener.failed(HttpConsts.ERROR_NO_NETWORK);
            }
            return;
        }

        final BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                LoginUserInfo user = AppContext.getAppContext().getUser();
                if (user != null) {
                    put(PARAMS_USER_ID, user.userId);
                }
                put(PARAMS_FORUM_ID, param.getForum_id());
                put(PARAMS_TASK_ID, param.getTag_id());
                put(PARAMS_IS_GOOD, param.getIs_good());
                put(PARAMS_IS_TOP, param.getIs_top());
                put(PARAMS_NUMBER, param.getPage_start() + "");
                put(PARAMS_PAGE_SIZE, 20 + "");
                put("iss", isSort + "");
            }
        };

        RequestExe.post(HOST_APP + BBS_CONTENT, baseModel, new IRequest2Listener() {
            @Override
            public void success(String result) {
                if (listener != null) {
                    if (!TextUtils.isEmpty(result)) {
                        try {
                            final BBSContentEntry contentEntry = new Gson().fromJson(result, BBSContentEntry.class);
                            listener.success(contentEntry, param.getPage_start());
                            if ("0".equals(param.getTag_id())) {
                                AppOperator.runOnThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CacheManager.saveObject(AppContext.getAppContext(), contentEntry, "BBSContentEntry");
                                    }
                                });
                            }
                        } catch (Exception e) {
                            resolveError(result, listener);
                        }
                    } else {
                        listener.failed("");
                    }
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

    private void resolveError(String result, GameCenterLogic.AppGameCenterListener listener) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
        } catch (JSONException e1) {
        }
        int code = JsonUtil.getStatusCode(jsonObject);
        String msg = JsonUtil.getServerMsg(jsonObject);
        if (code == 0 || code == 200) {
            listener.success(null, -1);
            return;
        }
        listener.failed(msg + "(" + code + ")");
    }
}
