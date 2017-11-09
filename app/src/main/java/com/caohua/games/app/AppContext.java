package com.caohua.games.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.caohua.games.R;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.shop.RefreshShopEntry;
import com.caohua.games.ui.emoji.FaceConversionUtil;
import com.chsdk.api.CHSdk;
import com.chsdk.api.LoginCallBack;
import com.chsdk.biz.game.TokenRefreshHelper;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.NetworkUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.greenrobot.eventbus.EventBus;
import org.wlf.filedownloader.FileDownloadConfiguration;
import org.wlf.filedownloader.FileDownloader;

import java.io.File;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by ZengLei on 2016/10/13.
 */
public class AppContext extends MultiDexApplication {
    private static AppContext appContext;
    public String inviteCodeShow;
    public static final String ACTION_DOWNLOAD_START = "action_ch_download_start";
    //    public static final String UPDATE_STATUS_ACTION = "com.caohua.games.apps.action.UPDATE_STATUS";
    public static String UPDATE_STATUS_ACTION;
    public static boolean EXTERNAL_STORAGE_MOUNT = true;
    public static final String TAG = "XMAppContext";

    private static final String XM_APP_ID = "2882303761517600547";
    private static final String XM_APP_KEY = "5921760038547";

    private boolean isRun;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        UPDATE_STATUS_ACTION = getAppPackageName() + ".action.UPDATE_STATUS";
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setCatchUncaughtExceptions(true);

        if (isCaoHuaProcess(getCurProcessName())) {
            FaceConversionUtil.getInstance().getFileText(this);
            LogUtil.errorLog("getPackageName------>" + getAppPackageName());
            LogUtil.errorLog("AppContext onCreate");
            CrashHandler.getInstance().init(getApplicationContext());
            initFileDownloader();
            ShareSDK.initSDK(this);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addDataScheme("package");
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            registerReceiver(new AppNetWorkChangeReceiver(), intentFilter);
            if (isLogin()) {
                getUser();
                TokenRefreshHelper.registerAlarm(getApplicationContext());
            }
            xmPush();
        }
        umengPush();
    }

    private void xmPush() {
        MiPushClient.registerPush(this, XM_APP_ID, XM_APP_KEY);
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
            }

            @Override
            public void log(String content, Throwable t) {
                LogUtil.errorLog(TAG, content + " t: " + t);
            }

            @Override
            public void log(String content) {
                LogUtil.errorLog(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
    }

    public static AppContext getAppContext() {
        return appContext;
    }

    private void initFileDownloader() {
        FileDownloadConfiguration.Builder builder = new FileDownloadConfiguration.Builder(this);
        builder.configFileDownloadDir(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                "Download");
        builder.configDownloadTaskSize(1);
        builder.configRetryDownloadTimes(2);
        FileDownloadConfiguration configuration = builder.build();
        FileDownloader.init(configuration);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        FileDownloader.release();
    }

    public boolean isLogin() {
        return DataStorage.isAppLogin(this);
    }

    public LoginUserInfo getUser() {
        LoginUserInfo info = SdkSession.getInstance().getUserInfo();
        return info;
    }

    public void login(final Activity activity, final TransmitDataInterface transmitDataInterface) {
        CHSdk.login(activity, new LoginCallBack() {
            @Override
            public void failed(String msg) {
                if (transmitDataInterface != null) {
                    transmitDataInterface.transmit(null);
                }
            }

            @Override
            public void success(String userName, String userId, String token) {
                CHSdk.accountInvalid = false;
                DataStorage.setAppLogin(activity, true);
                LoginUserInfo info = SdkSession.getInstance().getUserInfo();
                if (transmitDataInterface != null) {
                    transmitDataInterface.transmit(info);
                }
                EventBus.getDefault().post(info);
                TokenRefreshHelper.registerAlarm(getApplicationContext());
                if (AppContext.getAppContext().isLogin()) {
                    MiPushClient.setUserAccount(AppContext.getAppContext(), userId, userId);
                    LogUtil.errorLog("HomePagerActivity ", "USER_ID :" + SdkSession.getInstance().getUserId());
                }
            }

            @Override
            public void exit() {

            }
        });
    }

    public void login(final boolean isRefreshShop, final Activity activity, final TransmitDataInterface transmitDataInterface) {
        CHSdk.login(activity, new LoginCallBack() {
            @Override
            public void failed(String msg) {
                if (transmitDataInterface != null) {
                    transmitDataInterface.transmit(null);
                }
            }

            @Override
            public void success(String userName, String userId, String token) {
                CHSdk.accountInvalid = false;
                DataStorage.setAppLogin(activity, true);
                LoginUserInfo info = SdkSession.getInstance().getUserInfo();
                if (transmitDataInterface != null) {
                    transmitDataInterface.transmit(info);
                }
                EventBus.getDefault().post(info);
                if (isRefreshShop) {
                    EventBus.getDefault().post(new RefreshShopEntry());
                }
                TokenRefreshHelper.registerAlarm(getApplicationContext());
            }

            @Override
            public void exit() {

            }
        });
    }

    public void umengPush() {
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(false);
        String id = mPushAgent.getRegistrationId();
        LogUtil.errorLog("AppContext umengPush: " + id);
//        com.umeng.fb.util.Res.setPackageName(R.class.getPackage().getName());
        mPushAgent.setResourcePackageName(R.class.getName().replace(".R", ""));
        //sdk开启通知声音
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        mPushAgent.setDisplayNotificationNumber(2);
        // sdk关闭通知声音
//		mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
        // 通知声音由服务端控制
//		mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER);

//		mPushAgent.setNotificationPlayLights(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);
//		mPushAgent.setNotificationPlayVibrate(MsgConstant.NOTIFICATION_PLAY_SDK_DISABLE);

        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            /**
             * 自定义消息的回调方法
             * */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }

            /**
             * 自定义通知栏样式的回调方法
             * */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        return getOne(context, msg);
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }

            private Notification getOne(Context context, UMessage msg) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setTicker("The ticker1");
                builder.setContentTitle("The title2");
                builder.setContentText("The text3");
                builder.setSmallIcon(R.drawable.umeng_push_notification_default_small_icon);
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.umeng_push_notification_default_small_icon);
                builder.setLargeIcon(bm);
                Notification notification = builder.build();
                return notification;
            }

            private Notification getTwo(Context context, UMessage msg) {
                Notification.Builder builder = new Notification.Builder(context);
                RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.ch_push_notification_view);
                myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                builder.setContent(myNotificationView)
                        .setSmallIcon(getSmallIconId(context, msg))
                        .setTicker(msg.ticker)
                        .setAutoCancel(true);

                return builder.getNotification();
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 自定义行为的回调处理
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知
        //参考http://bbs.umeng.com/thread-11112-1-1.html
        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);

        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }

            @Override
            public void onFailure(String s, String s1) {
                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }
        });
        //此处是完全自定义处理设置
//        mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);
    }

    public boolean isCaoHuaProcess(String process) {
        return getAppPackageName().equals(process);
    }

    public String getCurProcessName() {
        try {
            int pid = android.os.Process.myPid();
            ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    return appProcess.processName;
                }
            }
        } catch (Exception e) {
            LogUtil.errorLog("getCurProcessName:" + e.getMessage());
        }
        return getAppPackageName();
    }

    public String getAppPackageName() {
        return getPackageName();
    }

    public boolean isRun() {
        return isRun;
    }

    public void setRun(boolean run) {
        isRun = run;
    }

    public boolean isNetworkConnected() {
        if (NetworkUtils.isNetworkConnected(getAppContext())) {
            return true;
        }
        return false;
    }
}



