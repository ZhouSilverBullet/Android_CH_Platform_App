package com.caohua.games.ui.account;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.ui.BaseActivity;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.LogUtil;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.message.PushAgent;

import java.util.List;

/**
 * Created by CXK on 2016/10/31.
 */

public class AboutActivity extends BaseActivity implements View.OnClickListener {
    private ImageView aboutImage;
    private TextView aboutVersion;
    private View phoneLayout;
    private View qqLayout;
    private TextView weChat;
    private long[] eggHits;
    private static final String ABOUT_CENTER = "https://gm.caohua.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_about);
        initView();
        initEvent();
    }

    private void initEvent() {
        phoneLayout.setOnClickListener(this);
        qqLayout.setOnClickListener(this);
        weChat.setOnClickListener(this);
    }

    private void initView() {
        weChat = getView(R.id.ch_wechat_service);
        aboutImage = getView(R.id.ch_activity_about_iv);
        aboutVersion = getView(R.id.ch_activity_about_version);
        phoneLayout = getView(R.id.ch_activity_about_rl);
        qqLayout = getView(R.id.ch_activity_about_rl_two);
        aboutVersion.setText(getVersion());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //拨打客服电话
            case R.id.ch_activity_about_rl:
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:07377720858"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.ch_activity_about_rl_two:
//                WebActivity.startWebPage(AboutActivity.this, ABOUT_CENTER);
                Intent baseIntent = new Intent(Intent.ACTION_VIEW);
                baseIntent.setData(Uri.parse("http://gm.caohua.com"));
                if (baseIntent.resolveActivity(getPackageManager()) != null) {
                    Intent chooserIntent = Intent.createChooser(baseIntent, "Select Application");
                    if (chooserIntent != null) {
                        startActivity(chooserIntent);
                    }
                }
//                if (isQQClientAvailable(this)) {
//                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=800032511&version=1";
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
//                } else {
//                    CHToast.show(this, "请安装手机qq");
//                }
                break;
            case R.id.ch_wechat_service:
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(weChat.getText().toString());
                CHToast.show(AboutActivity.this, "已经复制内容到剪切板!!!");
                break;
            case R.id.ch_activity_about_iv:
                handleEgg();
                break;
        }
    }

    private void handleEgg() {
        try {
            if (eggHits == null) {
                eggHits = new long[5];
            }

            System.arraycopy(eggHits, 1, eggHits, 0, eggHits.length - 1);
            eggHits[eggHits.length - 1] = SystemClock.uptimeMillis();
            if (eggHits[0] >= (SystemClock.uptimeMillis() - 800)) {
                eggHits = null;

                String pushId = PushAgent.getInstance(this).getRegistrationId();
                if (!TextUtils.isEmpty(pushId)) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(pushId);

                    CHToast.show(this, "推送设备ID已复制");
                    CHToast.show(this, "当前渠道号" + AnalyticsConfig.getChannel(this));
                }
            }
        } catch (Exception e) {
        }
    }

    private String getVersion() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
            return "V" + pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "V2.1.1";
        }
    }

    private boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }
}
