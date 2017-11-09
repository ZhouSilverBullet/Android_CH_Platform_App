package com.caohua.games.biz.coupon;

import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by admin on 2017/8/22.
 */

public class ColorEggSwitchLogic extends BaseLogic {

    public void eggSwitch(final AppLogicListner listener) {
        RequestExe.post(HOST_APP + "coloregg/coloreggSwitch", new BaseModel(), new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listener != null) {
                    if (map != null) {
                        String coloreggSwitch = map.get("coloregg_switch");
                        if ("y".equalsIgnoreCase(coloreggSwitch)) {
                            listener.success(coloreggSwitch);
                            return;
                        }
                    }
                    listener.failed(HttpConsts.ERROR_CODE_PARAMS_VALID);
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
}
