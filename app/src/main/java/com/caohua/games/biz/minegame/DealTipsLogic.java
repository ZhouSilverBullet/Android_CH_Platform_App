package com.caohua.games.biz.minegame;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;

import java.util.HashMap;

/**
 * Created by zhouzhou on 2017/3/2.
 */

public class DealTipsLogic extends BaseLogic {
    private static final String DEAL_TIPS_PATH = "ucenter/dealTips";
    private final TipsEntry tipsEntry;
    private final int step;
    private AppLogicListner listner;

    public DealTipsLogic(TipsEntry tipsEntry, int step) {
        this.tipsEntry = tipsEntry;
        this.step = step;
    }

    public void getDealTips(final AppLogicListner listner) {
        this.listner = listner;
        if (tipsEntry == null) {
            return;
        }
        final LoginUserInfo user = AppContext.getAppContext().getUser();
        if (user == null) {
            return;
        }
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                put(PARAMS_USER_ID, user.userId);
                put(PARAMS_TOKEN, user.token);
                put(PARAMS_STEP, step + "");
            }
        };

        RequestExe.post(HOST_APP + DEAL_TIPS_PATH, baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listner != null && map != null) {

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
                getDealTips(listner);
            }
        });
    }
}
