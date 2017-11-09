package com.caohua.games.ui.vip;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.vip.VipAuthEntry;
import com.caohua.games.biz.vip.VipAuthSubmitEntry;
import com.caohua.games.biz.vip.VipAuthSubmitLogic;
import com.caohua.games.biz.vip.VipCertificationNotifyEntry;
import com.caohua.games.biz.vip.VipCertificationSendCodeLogic;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.vip.widget.VipCertificationEditView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 2017/8/23.
 */

public class VipCertificationActivity extends BaseActivity {
    private View submit;
    private VipCertificationEditView qq;
    private VipCertificationEditView phone;
    private VipCertificationEditView verification;
    private VipCertificationEditView ic;
    private VipCertificationEditView name;
    private VipCertificationEditView address;
    private VipCertificationActivity activity;
    private TextView kefu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_vip_certification);
        activity = this;
        initView();
    }

    private void initView() {
        submit = getView(R.id.ch_activity_vip_certification_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitValue();
            }
        });

        qq = getView(R.id.ch_activity_vip_certification_edit_view_qq);
        phone = getView(R.id.ch_activity_vip_certification_edit_view_phone);
        verification = getView(R.id.ch_activity_vip_certification_edit_view_verification);
        ic = getView(R.id.ch_activity_vip_certification_edit_view_ic);
        name = getView(R.id.ch_activity_vip_certification_edit_view_name);
        address = getView(R.id.ch_activity_vip_certification_edit_view_address);

        kefu = getView(R.id.ch_activity_vip_certification_kefu);
        kefu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent baseIntent = new Intent(Intent.ACTION_VIEW);
                baseIntent.setData(Uri.parse("http://gm.caohua.com"));
                if (baseIntent.resolveActivity(getPackageManager()) != null) {
                    Intent chooserIntent = Intent.createChooser(baseIntent, "Select Application");
                    if (chooserIntent != null) {
                        startActivity(chooserIntent);
                    }
                }
            }
        });
        kefu.setText(Html.fromHtml("<u>" + "联系客服" + "</u>"));

        verification.setSendCodeListener(new VipCertificationEditView.OnSendCodeListener() {
            @Override
            public void onSendCode(View v) {
                if (!TextUtils.isEmpty(phone.getEditValueText())) {
                    Pattern p = Pattern
                            .compile("[1][34578]\\d{9}");
                    Matcher m = p.matcher(phone.getEditValueText());
                    if (m.matches()) {
                        new VipCertificationSendCodeLogic().sendCode(phone.getEditValueText(), new BaseLogic.DataLogicListner<String>() {
                            @Override
                            public void failed(String errorMsg, int errorCode) {
                                CHToast.show(AppContext.getAppContext(), errorMsg);
                            }

                            @Override
                            public void success(String entryResult) {
                                verification.doCodeStatus();
                                CHToast.show(AppContext.getAppContext(), "发送验证码成功！");
                            }
                        });
                    } else {
                        CHToast.show(activity, "请填写正确手机号码之后，再点击发送验证码！");
                    }
                } else {
                    CHToast.show(activity, "请填写手机号码之后，再点击发送验证码！");
                }
            }
        });
    }

    private void submitValue() {
        String qqValue = qq.getEditValueText();
        String phoneValue = phone.getEditValueText();
        String verificationValue = verification.getEditValueText();
        String icValue = ic.getEditValueText();
        String nameValue = name.getEditValueText();
        String addressValue = address.getEditValueText();

        if (TextUtils.isEmpty(qqValue)) {
            CHToast.show(activity, "请输入qq号");
            return;
        }

        if(!qqValue.matches("[1-9][0-9]{4,14}")) {
            CHToast.show(activity, "请输入正确的qq号码");
            return;
        }

        if (TextUtils.isEmpty(phoneValue)) {
            CHToast.show(activity, "请输入手机号");
            return;
        }

        if (TextUtils.isEmpty(verificationValue)) {
            CHToast.show(activity, "请输入验证码");
            return;
        }
        VipAuthEntry entry = new VipAuthEntry();
        entry.qqValue = qqValue;
        entry.phoneValue = phoneValue;
        entry.verificationValue = verificationValue;
        entry.icValue = icValue;
        entry.nameValue = nameValue;
        entry.addressValue = addressValue;

        //vip验证接口
        new VipAuthSubmitLogic().getVipAuth(entry, new BaseLogic.DataLogicListner<VipAuthSubmitEntry>() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                if (isFinishing()) {
                    return;
                }
                CHToast.show(activity, errorMsg);
            }

            @Override
            public void success(VipAuthSubmitEntry entry) {
                if (isFinishing()) {
                    return;
                }
                AppContext.getAppContext().login(activity, new TransmitDataInterface() {
                    @Override
                    public void transmit(Object o) {
                        LogUtil.errorLog("VipCertificationActivity transmit: " + 0);
                    }
                });
                EventBus.getDefault().post(new VipCertificationNotifyEntry());
                new VipCertificationDialog(activity, entry).show();
            }
        });
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, VipCertificationActivity.class);
        context.startActivity(intent);
    }
}
