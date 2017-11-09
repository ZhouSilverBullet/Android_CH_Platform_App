package com.caohua.games.biz.coupon;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by admin on 2017/8/22.
 */

public class FlyingCardDialogLogic extends BaseLogic {

    public void cardDialog(final String id, final DataLogicListner<FlyingCardDialogEntry> listener) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_USER_ID, userId == null ? "0" : userId);
                put(PARAMS_TOKEN, token == null ? "" : token);
                put("seid", id);
            }
        };

        RequestExe.post(HOST_APP + "coloregg/getReward", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listener != null) {
                    if (map != null) {
                        String successTips = map.get("success_tips");
                        String rewardDesc = map.get("reward_desc");
                        FlyingCardDialogEntry entry = new FlyingCardDialogEntry();
                        entry.success_tips = successTips;
                        entry.reward_desc = rewardDesc;
                        listener.success(entry);
                        return;
                    }
                    listener.failed(HttpConsts.ERROR_CODE_PARAMS_VALID, -100);
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (listener != null) {
                    listener.failed(error, errorCode);
                }
            }
        });
    }
}
