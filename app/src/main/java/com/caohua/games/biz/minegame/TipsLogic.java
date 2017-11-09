package com.caohua.games.biz.minegame;

import android.content.Context;
import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;

import java.util.HashMap;

/**
 * Created by zhouzhou on 2017/3/2.
 */

public class TipsLogic extends BaseLogic {
    private static final String TIPS_PATH = "ucenter/tips";
    private static final long UPDATE_INTERVAL = 10 * 1000;
    private boolean isRefresh;
    private Context mContext;
    private AppLogicListner listner;
    private static long resumeTime;

    public TipsLogic(Context mContext, boolean isRefresh) {
        this.mContext = mContext;
        this.isRefresh = isRefresh;
    }

    public void getTips(final AppLogicListner listner) {
        this.listner = listner;
        final LoginUserInfo user = AppContext.getAppContext().getUser();
        if (user == null) {
            return;
        }

        if (System.currentTimeMillis() - resumeTime > 0 &&
                System.currentTimeMillis() - resumeTime < UPDATE_INTERVAL && !isRefresh) {
            return;
        }

        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(PARAMS_USER_ID, user.userId);
                put(PARAMS_TOKEN, user.token);
            }
        };
        RequestExe.post(HOST_APP + TIPS_PATH, baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                resumeTime = System.currentTimeMillis();
                if (listner != null && map != null) {
                    TipsEntry entry = new TipsEntry();
                    String comment = map.get("comment");
                    String get_gift = map.get("get_gift");
                    String consume = map.get("consume");
                    String recharge = map.get("recharge");
                    String msg = map.get("msg");
                    entry.setComment(getInt(comment));
                    entry.setGet_gift(getInt(get_gift));
                    entry.setConsume(getInt(consume));
                    entry.setRecharge(getInt(recharge));
                    entry.setMsg(getInt(msg));
                    listner.success(entry);
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (errorCode == 206) {
                    handleTokenError(error);
                }
            }
        });
    }

    private int getInt(String value) {
        if (!TextUtils.isEmpty(value)) {
            return Integer.parseInt(value);
        }
        return 0;
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
                getTips(listner);
            }
        });
    }
}
