package com.caohua.games.biz.task;

import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.DeviceUtil;

import java.util.HashMap;

/**
 * Created by zhouzhou on 2017/3/20.
 */

public class DrawAwardLogic extends BaseLogic {
    private static final String DRAW_AWARD_URL = "task/drawAward";
    private String taskId;
    private CommentLogicListener listner;

    public DrawAwardLogic(String taskId) {
        this.taskId = taskId;
    }

    public void getDrawAward(final CommentLogicListener listner) {
        this.listner = listner;
        final LoginUserInfo user = AppContext.getAppContext().getUser();
        if (user == null) {
            return;
        }
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(BaseModel.PARAMS_USER_ID, user.userId);
                put(BaseModel.PARAMS_TASK_ID, taskId);
                put(BaseModel.PARAMS_TOKEN, user.token);
                String deviceNo = DeviceUtil.getDeviceNo(AppContext.getAppContext());
                put(BaseModel.PARAMS_ANDROID_ID, TextUtils.isEmpty(deviceNo) ? "" : deviceNo);
            }
        };
        RequestExe.post(HOST_APP + DRAW_AWARD_URL, baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listner != null) {
                    listner.success("");
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (listner != null) {
                    if (errorCode == 206) {
                        handleTokenError(error, errorCode);
                    }
                    listner.failed(error, errorCode);
                }
            }
        });
    }

    private void handleTokenError(final String error, final int errorCode) {
        TokenRefreshLogic.refresh(new LogicListener() {
            @Override
            public void failed(String errorMsg) {
                if (listner != null) {
                    listner.failed(error, errorCode);
                }
            }

            @Override
            public void success(String... result) {
                getDrawAward(listner);
            }
        });
    }
}
