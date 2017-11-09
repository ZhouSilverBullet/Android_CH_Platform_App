package com.chsdk.model.game;

import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.VerifyFormatUtil;

/***
 * token令牌刷新
 *
 * @author CXK
 */
public class TokenRefreshModel extends BaseModel {
    private String userName;
    private String userType;
    private String pwd;

    public TokenRefreshModel() {
        userName = session.getUserName();
        userType = String.valueOf(VerifyFormatUtil.isPhoneNum(userName) ? 2 : 1);
        LoginUserInfo info = session.getUserInfo();
        if (info != null) {
            pwd = info.passwd;
        }
    }

    @Override
    public void putDataInMap() {
        put(PARAMS_USER_NAME, userName);
        put(PARAMS_USER_TYPE, userType);
        put(PARAMS_PASSWORD, pwd);
    }
}
