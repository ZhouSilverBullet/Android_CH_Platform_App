package com.caohua.games.biz.gift;

import com.caohua.games.app.AppContext;
import com.chsdk.configure.SdkSession;
import com.chsdk.model.BaseModel;

/**
 * Created by zhouzhou on 2016/11/12.
 */

public class ShowCardnoModel extends BaseModel{
    private GiftEntry mGiftEntry;

    public ShowCardnoModel(GiftEntry giftEntry) {
        mGiftEntry = giftEntry;
    }

    @Override
    public void putDataInMap() {
        if (mGiftEntry == null) {
            return;
        }
        if (AppContext.getAppContext().getUser() != null) {
            put(PARAMS_USER_ID, SdkSession.getInstance().getUserId());
            put(PARAMS_TOKEN,AppContext.getAppContext().getUser().token);
        }

        put(PARAMS_GIFT_ID, mGiftEntry.getGift_id());
        put(PARAMS_GIFT_ITEM, mGiftEntry.getItem());
    }
}
