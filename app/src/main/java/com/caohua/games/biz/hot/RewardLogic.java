package com.caohua.games.biz.hot;

import android.content.Context;
import android.text.TextUtils;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AppLogic;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;

import java.util.HashMap;

/**
 * Created by admin on 2016/11/30.
 */

public class RewardLogic extends AppLogic {
    public static final String REWARD_CAOHUA_B = "97";
    public static final String REWARD_CAOHUA_D = "98";

    public static final String REWARD_HOT_URL = "http://act.caohua.com/app/upgradeReward";
    private AppLogicListner listener;

    public RewardLogic(Context context) {
        super(context, null);
    }

    public void getRewardLogic(final AppLogicListner listener) {
        this.listener = listener;
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                LoginUserInfo user = AppContext.getAppContext().getUser();
                if (user != null) {
                    put(PARAMS_TOKEN, user.token);
                    put(PARAMS_USER_ID, user.userId);
                }
            }
        };
        RequestExe.post(REWARD_HOT_URL, baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (null != map && null != listener) {
                    String data = map.get(HttpConsts.RESULT_PARAMS_REWARD);
                    String num = map.get("num");
                    RewardEntry rewardEntry = new RewardEntry();
                    if (!TextUtils.isEmpty(data) && !TextUtils.isEmpty(num)) {
                        rewardEntry.setNum(num);
                        rewardEntry.setReward(data);
                        listener.success(rewardEntry);
                    } else {
                        listener.failed("");
                    }
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (null != listener) {
                    if (errorCode == 206) {
                        handleTokenError(error);
                        return;
                    }
                    listener.failed(error);
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
                getRewardLogic(listener);
            }
        });
    }
}
