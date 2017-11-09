package com.caohua.games.biz.hot;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;

import java.util.HashMap;

/**
 * Created by admin on 2016/11/30.
 */

public class SubHotLogic extends BaseLogic{
    public static final String SUB_HOT_URL = "http://act.caohua.com/app/appUpgrade";
    private LogicListener listener;

    public void getSubHotLogic(final LogicListener listener) {
        this.listener = listener;
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                LoginUserInfo user = AppContext.getAppContext().getUser();
                if (user != null) {
                    put(PARAMS_USER_ID, user.userId);
                    put(PARAMS_DEVICE_NO, session.getDeviceNo());
                    put(PARAMS_TOKEN, AppContext.getAppContext().getUser().token);
                }
            }
        };
        RequestExe.post(SUB_HOT_URL, baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (null != listener) {
                    listener.success("");
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (null != listener) {
                    if (errorCode == 206) {
                        handleTokenError(error);
                        return;
                    }
                    listener.failed(String.valueOf(errorCode));
                }
            }
        });
    }

    private void handleTokenError(final String error) {
        TokenRefreshLogic.refresh(new LogicListener() {
            @Override
            public void failed(String errorMsg) {
                if (listener != null) {
                    listener.failed(error);
                }
            }

            @Override
            public void success(String... result) {
                getSubHotLogic(listener);
            }
        });
    }
}
