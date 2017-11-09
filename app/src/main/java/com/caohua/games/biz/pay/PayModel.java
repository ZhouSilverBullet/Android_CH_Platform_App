package com.caohua.games.biz.pay;

import com.chsdk.configure.SdkSession;
import com.chsdk.model.BaseModel;

/**
 * Created by CXK on 2016/11/16.
 */

public class PayModel extends BaseModel {

    private String userId;
    private String receiveUserName;
    private String token;

    public PayModel(String userName) {
        this.userId = SdkSession.getInstance().getUserId();
        this.receiveUserName = userName;
        this.token = SdkSession.getInstance().getToken();
    }

    @Override
    public void putDataInMap() {
        put(PARAMS_USER_ID, userId);
        put(PARAMS_RECEVICE_USER_NAME, receiveUserName);
        put(PARAMS_TOKEN, token);
    }
}
