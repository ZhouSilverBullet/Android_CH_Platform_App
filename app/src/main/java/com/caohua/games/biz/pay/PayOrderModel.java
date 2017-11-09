package com.caohua.games.biz.pay;

import com.chsdk.configure.SdkSession;
import com.chsdk.model.BaseModel;

/**
 * Created by CXK on 2016/11/16.
 */

public class PayOrderModel extends BaseModel {
    private String userId;
    private String receiveUserName;
    private String type;
    private String orderNumber;  //订单金额
    private String gainUserMoney;
    private String gainGiveMoney;
    private String token;

    public PayOrderModel(String userName, String orderMoney, String gainUserMoney, String gainGiveMoney, String type) {
        this.userId = SdkSession.getInstance().getUserId();
        this.receiveUserName = userName;
        this.orderNumber = orderMoney;
        this.gainUserMoney = gainUserMoney;
        this.gainGiveMoney = gainGiveMoney;
        this.token = SdkSession.getInstance().getToken();
        this.type = type;
    }

    @Override
    public void putDataInMap() {
        put(PARAMS_USER_ID, userId);
        put(PARAMS_RECEVICE_USER_NAME, receiveUserName);
        put(PARAMS_PAY_TYPE, type);
        put(PARAMS_ORDER_AMT, orderNumber);
        put(PARAMS_GAIN_USER_MONEY, gainUserMoney);
        put(PARAMS_GAIN_GIVE_MONEY, gainGiveMoney);
        put(PARAMS_TOKEN, token);
        put(PARAMS_CHANNEL_USER_ID, "639");
    }
}
