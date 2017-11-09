package com.caohua.games.biz.pay;

import android.text.TextUtils;

import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.game.TokenRefreshLogic;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by CXK on 2016/11/16.
 */

public class PayLogic extends BaseLogic {
    private static final String REBAT_PATH = "chb/payActivity";
    private String userName;
    private AppLogicListner listner;

    public PayLogic(String payUserName) {
        this.userName = payUserName;
    }

    public void getRebat(final AppLogicListner listner) {
        this.listner = listner;
        PayModel model = new PayModel(userName);
        RequestExe.post(HOST_APP + REBAT_PATH, model, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                if (map != null) {
                    PayRebate payRebate = new PayRebate();
                    String alipay = map.get("2");
                    if (!TextUtils.isEmpty(alipay)) {
                        List<Rebate> alipayRebate = fromJsonArray(alipay, Rebate.class);
                        payRebate.setAlipay(alipayRebate);
                    }

                    String union = map.get("22");
                    if (!TextUtils.isEmpty(union)) {
                        List<Rebate> unionRebate = fromJsonArray(union, Rebate.class);
                        payRebate.setUnion(unionRebate);
                    }

                    String wechat = map.get("23");
                    if (!TextUtils.isEmpty(wechat)) {
                        List<Rebate> wechatRebate = fromJsonArray(wechat, Rebate.class);
                        payRebate.setWechat(wechatRebate);
                    }
                    if (listner != null) {
                        listner.success(payRebate);
                    }
                } else {
                    listner.success(null);
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                if (BaseLogic.isTokenInvialid(error)) {
                    handleTokenError(error);
                    return;
                }

                if (listner != null) {
                    listner.failed(error);
                }
            }
        });
    }

    private void handleTokenError(final String error) {
        TokenRefreshLogic.refresh(new LogicListener() {
            @Override
            public void failed(String errorMsg) {
                if (listner != null) {
                    listner.failed(error);
                }
            }

            @Override
            public void success(String... result) {
                getRebat(listner);
            }
        });
    }
}
