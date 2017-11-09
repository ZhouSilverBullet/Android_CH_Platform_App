package com.caohua.games.biz.dataopen;

import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by admin on 2017/9/28.
 */

public class DataOpenStartGameLogic extends BaseLogic {
    public void openStartGame(final String managerGameId, final DataLogicListener<DownloadEntry> listener) {
        BaseModel model = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_USER_ID, userId == null ? "0" : getOpenUID());
                put(PARAMS_TOKEN, token == null ? "" : token);
                put(PARAMS_MANAGER_GAME_ID, managerGameId);
            }
        };

        RequestExe.post(HOST_APP + "push/startGame", model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listener != null) {
                    if (map != null) {
                        String game_icon = map.get("game_icon");
                        String game_url = map.get("game_url");
                        String game_name = map.get("game_name");
                        String package_name = map.get("package_name");
                        String notice = map.get("notice");
                        DownloadEntry entry = new DownloadEntry();
                        entry.setDownloadUrl(game_url);
                        entry.setPkg(package_name);
                        entry.setTitle(game_name);
                        entry.setIconUrl(game_icon);
                        listener.success(entry, notice);
                        return;
                    }
                    listener.failed(HttpConsts.ERROR_CODE_PARAMS_VALID, 100);
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

    public static interface DataLogicListener<T> {
        void failed(String errorMsg, int errorCode);

        void success(T entryResult, String notice);
    }
}
