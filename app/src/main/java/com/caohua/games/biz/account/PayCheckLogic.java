package com.caohua.games.biz.account;

import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;

import java.util.HashMap;

/**
 * Created by zhouzhou on 2017/5/3.
 */

public class PayCheckLogic extends BaseLogic {
    public static final String PAY_CHECK_URL = "https://passport-sdk.caohua.com/account/identityInfo";

    public void getCheck(final CommentLogicListener logicListener) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_USER_ID, userId == null ? "0" : userId);
                put(PARAMS_TOKEN, token == null ? "" : token);
                put(PARAMS_USER_ID, userId);
                put(PARAMS_TOKEN, token);
            }
        };

        RequestExe.post(PAY_CHECK_URL, model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (logicListener != null) {
                    if (map != null) {
                        String identity = map.get("auth_identity");
                        if (!TextUtils.isEmpty(identity)) {
                            int anInt = Integer.parseInt(identity);
                            if (anInt == 1) {
                                logicListener.success("");
                            } else {
                                logicListener.failed("", 101);
                            }
                        } else {
                            logicListener.failed("", 101);
                        }
                    } else {
                        logicListener.failed("", 101);
                    }
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (logicListener != null) {
                    logicListener.failed(error, errorCode);
                }
            }
        });
    }
}
