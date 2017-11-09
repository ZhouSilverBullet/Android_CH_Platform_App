package com.caohua.games.app;

import android.text.TextUtils;

import com.chsdk.biz.BaseLogic;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.InitModel;
import com.chsdk.model.game.UpdateEntry;
import com.chsdk.utils.LogUtil;

import java.util.HashMap;

/**
 * Created by admin on 2016/11/16.
 */

public class AppUpdateLogic extends BaseLogic {
    private static final String INIT_SDK_PATH = "app/init";

    public void initSdk(final AppLogicListner logicLisetner) {
        String url = HOST_APP + INIT_SDK_PATH;
        InitModel model = new InitModel();
        RequestExe.post(url, model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                UpdateEntry entry = null;
                if (map != null) {
                    String updateUrl = map.get(HttpConsts.RESULT_PARAMS_GAME_UPDATE_URL);
                    if (!TextUtils.isEmpty(updateUrl)) {
                        entry = new UpdateEntry();
                        entry.url = updateUrl;
                        try {
                            entry.updateState = map.get("update_state");
                            entry.versionCode = Integer.parseInt(map.get(HttpConsts.RESULT_PARAMS_VERSION_CODE));
                            entry.forceUpdate = "1".equals(map.get("force_update"));
                        } catch (Exception e) {
                        }
                    }
                }
                logicLisetner.success(entry);
            }

            @Override
            public void failed(String error, int errorCode) {
                LogUtil.errorLog(error);
                logicLisetner.failed(error);
            }
        });
    }
}
