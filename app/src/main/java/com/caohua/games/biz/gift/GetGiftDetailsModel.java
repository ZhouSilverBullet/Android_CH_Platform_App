package com.caohua.games.biz.gift;

import com.chsdk.model.BaseModel;

/**
 * Created by admin on 2016/11/12.
 */

public class GetGiftDetailsModel extends BaseModel {
    private String userId;
    private String id;

    public GetGiftDetailsModel(String id, String userId) {
        this.id = id;
        this.userId = userId;
    }

    @Override
    public void putDataInMap() {
        put(PARAMS_GIFT_ID, id);
        put(PARAMS_USER_ID, userId);
    }
}
