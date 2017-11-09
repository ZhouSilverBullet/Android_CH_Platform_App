package com.caohua.games.biz.pay;

import com.caohua.games.ui.account.PayActionActivity;
import com.chsdk.biz.pay.PayAction;

import java.util.List;

/**
 * Created by ZengLei on 2016/11/16.
 */

public class PayRebate {
    private List<Rebate> alipay;
    private List<Rebate> wechat;
    private List<Rebate> union;

    public void setAlipay(List<Rebate> alipay) {
        this.alipay = alipay;
    }

    private int getAlipayRebate(int payMoney) {
        return getRebateValue(alipay, payMoney);
    }

    private int getWechatRebate(int payMoney) {
        return getRebateValue(wechat, payMoney);
    }

    private int getUnionRebate(int payMoney) {
        return getRebateValue(union, payMoney);
    }

    public int getRebate(int payType, int payMoney) {
        switch (payType) {
            case PayActionActivity.PAY_TYPE_ALIPAY:
                return getAlipayRebate(payMoney);
            case PayActionActivity.PAY_TYPE_WETCHAT:
            case PayActionActivity.PAY_TYPE_IPAY:
                return getWechatRebate(payMoney);
            case PayActionActivity.PAY_TYPE_UNION:
                return getUnionRebate(payMoney);
        }
        return 100;
    }

    private int getRebateValue(List<Rebate> rebateList, int payMoney) {
        if (rebateList == null || rebateList.size() == 0)
            return 100;

        for (Rebate rebate : rebateList) {
            if (rebate.hit(payMoney)) {
                return rebate.getValue();
            }
        }
        return 100;
    }

    public void setWechat(List<Rebate> wechatRebate) {
        this.wechat = wechatRebate;
    }

    public void setUnion(List<Rebate> unionRebate) {
        this.union = unionRebate;
    }

    public int getCouponForType(int payType, int payMoney) {
        switch (payType) {
            case PayActionActivity.PAY_TYPE_ALIPAY:
                return getBenefitValue(alipay, payMoney);
            case PayActionActivity.PAY_TYPE_WETCHAT:
            case PayActionActivity.PAY_TYPE_IPAY:

                return getBenefitValue(wechat, payMoney);
            case PayActionActivity.PAY_TYPE_UNION:
                return getBenefitValue(union, payMoney);
        }
        return 1;
    }

    private int getBenefitValue(List<Rebate> rebateList, int payMoney) {
        if (rebateList == null || rebateList.size() == 0)
            return 1;
        for (Rebate rebate : rebateList) {
            if (rebate.hit(payMoney)) {
                return rebate.getBenefit_type();
            }
        }
        return 1;
    }

    private Rebate getPayValue(List<Rebate> rebateList, int payMoney) {
        if (rebateList == null || rebateList.size() == 0)
            return null;
        for (Rebate rebate : rebateList) {
            if (rebate.hit(payMoney)) {
                return rebate;
            }
        }
        return null;
    }

    public Rebate getPayType(int payType, int payMoney) {
        switch (payType) {
            case PayActionActivity.PAY_TYPE_ALIPAY:
                return getPayValue(alipay, payMoney);
            case PayActionActivity.PAY_TYPE_WETCHAT:
            case PayActionActivity.PAY_TYPE_IPAY:
                return getPayValue(wechat, payMoney);
            case PayActionActivity.PAY_TYPE_UNION:
                return getPayValue(union, payMoney);
        }
        return null;
    }
}
