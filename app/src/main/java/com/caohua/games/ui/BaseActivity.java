package com.caohua.games.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.caohua.games.app.AppContext;
import com.caohua.games.views.main.WelcomeActivity;
import com.chsdk.biz.game.TokenRefreshHelper;
import com.chsdk.configure.SdkSession;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.ApkUtil;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.StatusBarUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by ZengLei on 2016/10/13.
 */

public class BaseActivity extends FragmentActivity implements EasyPermissions.PermissionCallbacks {
    public static final int PERMISSION_FOR_PHONE_STATE = 0x0001;
    public static final int PERMISSION_FOR_LOCATION = 0x0002;
    public static final int PERMISSION_FOR_WRITE_STORAGE = 0x0003;

    private static boolean hasShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            LogUtil.errorLog("BaseActivity onCreate: savedInstanceState != null");
            SdkSession.getInstance().getUserInfo();
            if (AppContext.getAppContext().isLogin()) {
                TokenRefreshHelper.registerAlarm(getApplicationContext());
            }
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            if (StatusBarUtil.MIUISetStatusBarLightMode(getWindow(), true)) {//小米MIUI系统
                StatusBarUtil.compat(this);
            } else if (StatusBarUtil.FlymeSetStatusBarLightMode(getWindow(), true)) {//魅族flyme系统
                StatusBarUtil.compat(this);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//Android6.0以上系统
                StatusBarUtil.android6_SetStatusBarLightMode(getWindow());
                StatusBarUtil.compat(this);
            }
        }
        PushAgent.getInstance(this).onAppStart();
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LogUtil.errorLog("BaseActivity onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        LogUtil.errorLog("BaseActivity onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CHToast.cancel();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void refreshLogout(String action) {
        LogUtil.errorLog("refreshLogout:" + action + "," + getClass().getName());
        if (!hasShown) {
            hasShown = true;
            CHToast.show(AppContext.getAppContext(), "账号信息验证出错, 请重新登录", Toast.LENGTH_LONG);
        }
        refreshChildLogout();
    }

    public void refreshChildLogout() {

    }

    protected <T extends Fragment> T getFragment(Bundle savedState, Class cls) {
        return (T) getSupportFragmentManager().getFragment(savedState,
                cls.getSimpleName());
    }

    protected void saveFragment(Bundle outState, Fragment fragment) {
        try {
            if (fragment.isAdded()) {
                getSupportFragmentManager().putFragment(outState,
                        fragment.getClass().getSimpleName(), fragment);
            }
        } catch (Exception e) {
            LogUtil.errorLog("saveFragment error:" + e.getMessage());
        }
    }

    /**
     * 防止被点击两次
     */
    protected long lastClickTime;

    protected boolean isFastDoubleClick(int currentTime) {
        if (currentTime == 0) {
            currentTime = 1000;
        }
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < currentTime) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public boolean shouldShowRequestPermissionRationale(String permission) {
        if (this instanceof WelcomeActivity) {
            return super.shouldShowRequestPermissionRationale(permission);
        } else {
            return false;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        final CHAlertDialog alertDialog = new CHAlertDialog(this, true, true);
        alertDialog.show();
        alertDialog.setTitle("提示");
        switch (requestCode) {
            case PERMISSION_FOR_PHONE_STATE:
                alertDialog.setContent("没有权限, 你需要去设置中开启读取手机权限");
                break;
            case PERMISSION_FOR_LOCATION:
                alertDialog.setContent("没有权限, 你需要去设置中开启手机定位权限");
                break;
            case PERMISSION_FOR_WRITE_STORAGE:
                alertDialog.setContent("没有权限, 你需要去设置中开启手机读写权限");
                break;
        }
        alertDialog.setCancelButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setOkButton("去设置", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                ApkUtil.skipAppMessage(BaseActivity.this, 0);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 是否打开app的情况调用改方法
     * @param context
     * @param select
     */
    public void openWelcomeActivity(Context context, int select) {
        if (!AppContext.getAppContext().isRun()) {
            Intent intent = new Intent(context, WelcomeActivity.class);
            intent.putExtra(WelcomeActivity.OPEN_WELCOME, select);
            context.startActivity(intent);
        }
    }

    protected int stringToInt(String value) {
        int intValue = 0;
        if (!TextUtils.isEmpty(value)) {
            try {
                intValue = Integer.parseInt(value);
            } catch (Exception e) {
                intValue = 0;
            }
        }
        return intValue;
    }
}
