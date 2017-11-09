package com.caohua.games.biz.dataopen;

import com.caohua.games.biz.dataopen.entry.DataOpenBindListEntry;
import com.caohua.games.biz.dataopen.entry.DataOpenServerEntry;
import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;

import java.util.HashMap;

/**
 * Created by admin on 2017/9/27.
 */

public class DataOpenBindRoleLogic extends BaseLogic {
    public void openBindRole(final String managerGameId, final DataOpenServerEntry entry, final DataLogicListner<String> listener) {
        BaseModel baseModel = new BaseModel() {
            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                String token = session.getToken();
                put(PARAMS_USER_ID, userId == null ? "0" : getOpenUID());
                put(PARAMS_TOKEN, token == null ? "" : token);
                put(PARAMS_MANAGER_GAME_ID, managerGameId);
                put("s", entry.server_id);
                put("rid", entry.cp_roleid);
                put("rn", entry.role_name);
                put("rl", entry.role_level);
                put("csn", entry.cp_sname);
//                put("id", entry.id);
            }
        };

        RequestExe.post(HOST_APP + "push/bindRole", baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (listener != null) {
                    listener.success("");
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
