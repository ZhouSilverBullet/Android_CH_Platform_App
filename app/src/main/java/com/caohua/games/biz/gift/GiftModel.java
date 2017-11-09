package com.caohua.games.biz.gift;

import com.caohua.games.app.AppContext;
import com.chsdk.model.BaseModel;

/**
 * Created by admin on 2016/11/12.
 */

public class GiftModel extends BaseModel{

    @Override
    public void putDataInMap() {
        String userId = "0";
        if (AppContext.getAppContext().getUser() != null) {
            userId = AppContext.getAppContext().getUser().userId;
        }
        put(PARAMS_USER_ID, userId);
    }
}
