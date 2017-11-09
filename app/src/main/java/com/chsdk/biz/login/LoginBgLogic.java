package com.chsdk.biz.login;

import android.content.Context;
import android.text.TextUtils;

import com.chsdk.biz.BaseLogic;
import com.chsdk.configure.SdkSession;
import com.chsdk.configure.UserDBHelper;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.RequestExe;
import com.chsdk.model.RequestSyncResult;
import com.chsdk.model.login.LoginModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.JsonUtil;
import com.chsdk.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author ZengLei <p>
 * @version 2016年8月18日 <p>
 */
public class LoginBgLogic extends BaseLogic {
    private static final String LOGIN_NORMAL_PATH = "login/normalLogin";

    public boolean loginNormal() {
        String url = HOST_PASSPORT + LOGIN_NORMAL_PATH;

        final Context context = SdkSession.getInstance().getAppContext();
        LoginUserInfo info = UserDBHelper.getLastLoginUser(context);
        LoginModel model = new LoginModel(false);
        model.setUserName(info.userName);
        model.setPasswd(info.passwd);

        String resultMsg = null;
        try {
            resultMsg = RequestExe.postSync(3000, url, model.getDataMap());
        } catch (Exception e) {
            return true;
        }

        if (TextUtils.isEmpty(resultMsg)) {
            return true;
        }

        try {
            JSONObject jsonObject = new JSONObject(resultMsg);
            if (jsonObject != null) {
                int code = JsonUtil.getStatusCode(jsonObject);
                RequestSyncResult entry = new RequestSyncResult();
                if (code == HttpConsts.CODE_SUCCESS) {
                    entry.postStatus = true;
                    JSONObject data = (JSONObject) JsonUtil.getJsonData(jsonObject);
                    String token = data.getString(HttpConsts.RESULT_PARAMS_TOKEN);
                    info.forum_name = data.optString(HttpConsts.RESULT_PARAMS_FORUM_NAME);
                    info.vip_level = data.optString("vip_level");
                    info.vip_name = data.optString("vip_name");
                    info.token = token;
                    UserDBHelper.updateUser(context, info);
                    SdkSession.getInstance().setUserInfo(info);
                    return false;
                }
            }
        } catch (JSONException e) {
            LogUtil.errorLog("LoginBgLogic e:" + e.getMessage());
        }
        return true;
    }
}
