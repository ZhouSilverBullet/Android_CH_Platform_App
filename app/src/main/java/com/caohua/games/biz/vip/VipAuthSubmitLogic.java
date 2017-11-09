package com.caohua.games.biz.vip;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by admin on 2017/8/29.
 */

public class VipAuthSubmitLogic extends BaseLogic {
    public void getVipAuth(final VipAuthEntry entry, final DataLogicListner<VipAuthSubmitEntry> dataLogicListner) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String token = session.getToken();
                String userId = session.getUserId();
                put(PARAMS_USER_ID, userId == null ? "" : userId);
                put(PARAMS_TOKEN, token == null ? "" : token);
                put(PARAMS_MSG_ID, entry.phoneValue);
                put("qq", entry.qqValue);
                put("ac", entry.verificationValue);
                put("ic", entry.icValue);
                put("na", entry.nameValue);
                put("sd", entry.addressValue);
            }
        };

        RequestExe.post(HOST_APP + "vip/authSubmit", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (dataLogicListner != null) {
                    if (map != null) {
                        VipAuthSubmitEntry submitEntry = new VipAuthSubmitEntry();
                        submitEntry.is_auth = map.get("is_auth");
                        submitEntry.nickname = map.get("nickname");
                        submitEntry.vip_level = map.get("vip_level");
                        submitEntry.vip_name = map.get("vip_name");
                        submitEntry.vip_exp = map.get("vip_exp");
                        submitEntry.next_exp = map.get("next_exp");
                        submitEntry.exp_rate = map.get("exp_rate");
                        dataLogicListner.success(submitEntry);
                        return;
                    }
                    dataLogicListner.failed(HttpConsts.ERROR_CODE_PARAMS_VALID, -100);
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (dataLogicListner != null) {
                    dataLogicListner.failed(error, errorCode);
                }
            }
        });
    }
}
