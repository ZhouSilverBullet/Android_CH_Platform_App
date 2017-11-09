package com.caohua.games.biz.task;

import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.widget.CHToast;

import java.util.HashMap;

/**
 * Created by zhouzhou on 2017/3/20.
 */

public class DoneTaskLogic extends BaseLogic {
    private static final String DONE_TASK_URL = "task/doneTask";
    public static final String TASK_GAME_SHARE = 2008 + "";
    public static final String TASK_ARTICLE_SHARE = 2009 + "";
    public static final String TASK_DOWNLOAD = 2003 + "";
    public static final String TASK_OPEN = 2002 + "";
    public static final String TASK_INSTALLATION = 1007 + "";
    public static final String TASK_ENTER_GAME = 1014 + "";

    private String taskNumber;
    private String pkg;

    public DoneTaskLogic(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    public DoneTaskLogic(String taskNumber, String pkg) {
        this.taskNumber = taskNumber;
        this.pkg = pkg;
    }

    public void getDoneTask() {
        final AppContext appContext = AppContext.getAppContext();
        final LoginUserInfo user = appContext.getUser();
        if (user == null) {
            return;
        }
        BaseModel baseModel = new BaseModel() {

            @Override
            public void putDataInMap() {
                put(BaseModel.PARAMS_USER_ID, user.userId);
                put(BaseModel.PARAMS_TOKEN, user.token);
                put(BaseModel.PARAMS_TASK_NUMBER, taskNumber);
                if (!TextUtils.isEmpty(pkg)) {
                    put(BaseModel.PARAMS_PACKAGE_NAME, pkg);
                }
            }
        };
        RequestExe.post(HOST_APP + DONE_TASK_URL, baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
//                CHToast.show(appContext, "下载完成");
            }

            @Override
            public void failed(String error, int errorCode) {
                if (errorCode == 206) {
                    handleTokenError(error);
                }
            }
        });
    }

    private void handleTokenError(final String error) {
        TokenRefreshLogic.refresh(new LogicListener() {
            @Override
            public void failed(String errorMsg) {
            }

            @Override
            public void success(String... result) {
                getDoneTask();
            }
        });
    }
}
