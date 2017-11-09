package com.caohua.games.biz.account;

import com.chsdk.configure.SdkSession;
import com.chsdk.model.BaseModel;

/**
 * Created by ZengLei on 2016/11/7.
 */

public class AccountPicModel extends BaseModel {
    public AccountPicModel() {
    }

    public void putDataInMap() {
        put(PARAMS_USER_ID, SdkSession.getInstance().getUserId());
    }
}
