package com.caohua.games.biz.gift;

import android.text.TextUtils;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.utils.LogUtil;

import java.util.HashMap;

/**
 * Created by admin on 2016/11/19.
 */

public class InitGiftLogic extends BaseLogic {
    private static final String INIT_GIFT_PATH = "app/initUrl";

    public InitGiftLogic() {
    }

    public void getInitGiftLogic() {
        RequestExe.post(HOST_APP + INIT_GIFT_PATH, new BaseModel(), new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    String gift_url = map.get("gift_url");
                    String gift_intercept_url = map.get("gift_intercept_url");
                    if(!TextUtils.isEmpty(gift_url) && !TextUtils.isEmpty(gift_intercept_url)){
                        DataStorage.saveGiftMoreUrl(SdkSession.getInstance().getAppContext(),gift_url);
                        DataStorage.saveGiftInterceptUrl(SdkSession.getInstance().getAppContext(),gift_intercept_url);
                    }
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                LogUtil.errorLog("InitGiftLogic:" + error);
            }
        });
    }
}
