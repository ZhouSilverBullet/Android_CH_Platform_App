package com.caohua.games.views.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.bbs.ForumShareEntry;
import com.caohua.games.biz.hot.HotEntry;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.BaseFragment;
import com.caohua.games.ui.GuideActivity;
import com.caohua.games.ui.HomePagerActivity;
import com.caohua.games.ui.StoreSecondActivity;
import com.caohua.games.ui.bbs.BBSActivity;
import com.caohua.games.ui.bbs.ForumListActivity;
import com.caohua.games.ui.find.FindContentActivity;
import com.caohua.games.ui.giftcenter.GiftDetailActivity;
import com.caohua.games.ui.hot.HotActivity;
import com.caohua.games.ui.minegame.MineGameActivity;
import com.caohua.games.ui.prefecture.GameCenterActivity;
import com.chsdk.SplashLogic;
import com.chsdk.api.CHSdk;
import com.chsdk.api.SplashDismissListener2;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.biz.download.DownloadEntry;
import com.chsdk.biz.login.AdLogoutLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.ui.WebActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.ApkUtil;
import com.chsdk.utils.LocationHelper;
import com.chsdk.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by ZengLei on 2016/10/14.
 */
public class WelcomeActivity extends BaseActivity implements Handler.Callback, EasyPermissions.PermissionCallbacks {

    private final static String MESSAGE_LAUNCH_APP = "launch";

    private final static String MESSAGE_SQUARE = "square";
    private final static String MESSAGE_FIND = "discover";
    private final static String MESSAGE_MINE = "mine";
    private final static String MESSAGE_TASK = "task";
    private final static String MESSAGE_SHOP = "shop";
    private final static String MESSAGE_GIFT = "gift";
    private final static String MESSAGE_GIFT_LIST = "gift_list";  //网页的
    private final static String MESSAGE_FORUM = "forum";
    private final static String MESSAGE_FORUM_LIST = "forum_list";
    private final static String MESSAGE_MY_GAME = "my_game";
    private final static String MESSAGE_MY_GROWTH = "my_growth";
    private final static String MESSAGE_GAME_CENTER = "my_game_center";
    private final static String MESSAGE_HOT = "hot";
    private final static String MESSAGE_ARTICLE = "article";
    private final static String MESSAGE_FORUM_ARTICLE = "forum_article";
    private final static String MESSAGE_GAME_DETAILS = "game_details";

    public final static String WELCOME_OPEN_HOME = "welcome_open_home";
    public final static String OPEN_WELCOME = "open_welcome";

    private static final int REQUEST_PERMISSION_SETTING = 102;
    private ImageView splashImage;
    private TextView splashText;
    private LinearLayout splashRightLayout;
    private int time;
    private Handler handler;
    private int leftTime;
    private ImageView bottomImg;
    private String[] perms = {Manifest.permission.READ_PHONE_STATE};
    private AlertDialog alertDialog;
    private int openValue = 0;
    private Uri skipUri;
    private String webUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_splash);
        boolean showGuide = DataStorage.getGuideShow(WelcomeActivity.this);
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        initVariables();
        initView();
        String[] perms = {Manifest.permission.READ_PHONE_STATE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            initSdk();
            new AdLogoutLogic().getAd();
            if (showGuide) { //出现过引导页，才访问splash图片
                new SplashLogic().getSplashSave();
            }
            LocationHelper.initLocation(this.getApplication());
        } else {
            EasyPermissions.requestPermissions(this, "请开启获取手机状态权限", PERMISSION_FOR_PHONE_STATE, perms);
        }
    }

    private void initVariables() {
        Intent intent = getIntent();
        if (intent != null) {
            webUrl = intent.getStringExtra("web_url");
            Uri uri = intent.getData();
            if (uri != null) {
                skipUri = uri;
            }
            LogUtil.errorLog("CouponCenterActivity web_url:" + webUrl);
        }
    }

    private void handleUri(Uri uri) {
        String message = uri.getQueryParameter("message");
//        CHToast.show(AppContext.getAppContext(), "message: " + message);
        Intent intent = null;
        Intent intent1 = null;
        Intent intent2 = null;
        String fr = null;
        switch (message) {
            case MESSAGE_LAUNCH_APP:
                openValue = 0;
                intent = new Intent(WelcomeActivity.this, HomePagerActivity.class);
                intent.putExtra(WELCOME_OPEN_HOME, openValue);
                startActivity(intent);
                break;
            case MESSAGE_SQUARE:   //专区0， 广场 1， 发现2， 我的 3.
                openValue = 1;
                intent = new Intent(WelcomeActivity.this, HomePagerActivity.class);
                intent.putExtra(WELCOME_OPEN_HOME, openValue);
                startActivity(intent);
                break;
            case MESSAGE_MINE: //我的
                openValue = 3;
                intent = new Intent(WelcomeActivity.this, HomePagerActivity.class);
                intent.putExtra(WELCOME_OPEN_HOME, openValue);
                startActivity(intent);
                break;
            case MESSAGE_FIND: //发现
                openValue = 2;
                intent = new Intent(WelcomeActivity.this, HomePagerActivity.class);
                intent.putExtra(WELCOME_OPEN_HOME, openValue);
                startActivity(intent);
                break;
            case MESSAGE_TASK: //任务
                intent1 = new Intent(this, HomePagerActivity.class);
                intent2 = new Intent(this, FindContentActivity.class);
                intent2.putExtra(FindContentActivity.KEY, FindContentActivity.KEY_TASK);
                startActivities(new Intent[]{intent1, intent2});
                break;
            case MESSAGE_SHOP: // 商城
                intent1 = new Intent(this, HomePagerActivity.class);
                intent2 = new Intent(this, FindContentActivity.class);
                intent2.putExtra(FindContentActivity.KEY, FindContentActivity.KEY_SHOP);
                startActivities(new Intent[]{intent1, intent2});
                break;
            case MESSAGE_GIFT: // 2.4 礼包领取列表页、礼包领取详情页
                intent1 = new Intent(this, HomePagerActivity.class);
                intent2 = new Intent(this, GiftDetailActivity.class);
                String gift_id = uri.getQueryParameter("gift_id");
                String data = uri.getQueryParameter("data");
                intent2.putExtra(GiftDetailActivity.GIFT_ID, gift_id);
                intent2.putExtra(GiftDetailActivity.GIFT_TYPE, GiftDetailActivity.TYPE_SPECIAL);
                intent2.putExtra("data", data);
                startActivities(new Intent[]{intent1, intent2});
                break;
            case MESSAGE_GIFT_LIST: //礼物列表
                String giftMoreUrl = DataStorage.getGiftMoreUrl(SdkSession.getInstance().getAppContext());
                String giftInterceptUrl = DataStorage.getGiftInterceptUrl(SdkSession.getInstance().getAppContext());
                AnalyticsHome.umOnEvent(AnalyticsHome.HOME_HOT_GIFT_ANALYTICS, "礼包中心二级界面");
                if (!TextUtils.isEmpty(giftMoreUrl) && !TextUtils.isEmpty(giftInterceptUrl)) {
                    WebActivity.startWelcomeWebPage(this, giftMoreUrl);
                } else {
                    WebActivity.startWelcomeWebPage(this, "https://m.caohua.com/gift/index?fr=app");
                    DataStorage.saveGiftInterceptUrl(SdkSession.getInstance().getAppContext(),
                            "https://m.caohua.com/gift/detail?id=");
                }
                break;
            case MESSAGE_FORUM: //论坛
                intent1 = new Intent(this, HomePagerActivity.class);
                //forum_id
                int forum_id = getInt(uri.getQueryParameter("forum_id"));
                intent2 = new Intent(this, BBSActivity.class);
                intent2.putExtra("forumId", forum_id);
                startActivities(new Intent[]{intent1, intent2});
                break;
            case MESSAGE_FORUM_LIST: //论坛列表
                ForumListActivity.start(WelcomeActivity.this);
                break;
            case MESSAGE_MY_GAME:
                intent = new Intent(WelcomeActivity.this, MineGameActivity.class);
                startActivity(intent);
                break;
            case MESSAGE_MY_GROWTH:
                WebActivity.startWebPageGetParam(WelcomeActivity.this, "https://passport-sdk.caohua.com/grow/index", null);
//                if (AppContext.getAppContext().isLogin()) {
//                } else {
//                    CHToast.show(AppContext.getAppContext(), "请先登录，才能进入我的成长界面");
//                    openValue = 3;
//                    intent = new Intent(WelcomeActivity.this, HomePagerActivity.class);
//                    intent.putExtra(WELCOME_OPEN_HOME, openValue);
//                    startActivity(intent);
//                }
                break;
            case MESSAGE_GAME_CENTER:
                intent1 = new Intent(this, HomePagerActivity.class);
                intent2 = new Intent(this, GameCenterActivity.class);
                startActivities(new Intent[]{intent1, intent2});
                break;
            case MESSAGE_HOT:
                intent1 = new Intent(this, HomePagerActivity.class);
                intent2 = new Intent(this, HotActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(BaseFragment.KEY_INTENT_DATA, new ArrayList<HotEntry>());
                intent2.putExtras(bundle);
                startActivities(new Intent[]{intent1, intent2});
                break;
            case MESSAGE_ARTICLE:
                String detailUrl = uri.getQueryParameter("detail_url");
                fr = uri.getQueryParameter("fr");
                if (TextUtils.isEmpty(fr)) {
                    WebActivity.startWelcomeWebPage(this, detailUrl);
                } else {
                    WebActivity.startWelcomeWebPage(this, detailUrl + "&fr=" + fr);
                }
                break;
            case MESSAGE_FORUM_ARTICLE:
                // article_id detail_url title  game_icon forum_name
                String article_id = uri.getQueryParameter("article_id");
                String detail_url = uri.getQueryParameter("detail_url");
                String title = uri.getQueryParameter("title");
                String game_icon = uri.getQueryParameter("game_icon");
                String forum_name = uri.getQueryParameter("forum_name");
                ForumShareEntry forumShareEntry = new ForumShareEntry();
                forumShareEntry.setTitle(title);
                forumShareEntry.setGameIcon(game_icon);
                forumShareEntry.setGameName(forum_name);
                fr = uri.getQueryParameter("fr");
                if (TextUtils.isEmpty(fr)) {
                    WebActivity.startWelcomeForForumPage(this, detail_url, article_id, forumShareEntry, -1);
                } else {
                    WebActivity.startWelcomeForForumPage(this, detail_url + "&fr=" + fr, article_id, forumShareEntry, -1);
                }
                break;
            case MESSAGE_GAME_DETAILS:
                // game_icon game_url package_name game_name detail_url
                String iconUrl = uri.getQueryParameter("game_icon");
                String downloadUrl = uri.getQueryParameter("game_url");
                String pkg = uri.getQueryParameter("package_name");
                String game_name = uri.getQueryParameter("game_name");
                String detail_url2 = uri.getQueryParameter("detail_url");
                DownloadEntry downloadEntry = new DownloadEntry();
                fr = uri.getQueryParameter("fr");
                if (!TextUtils.isEmpty(fr)) {
                    detail_url2 = detail_url2 + "&fr=" + fr;
                }
                downloadEntry.setDetail_url(detail_url2);
                downloadEntry.setTitle(game_name);
                downloadEntry.setPkg(pkg);
                downloadEntry.setDownloadUrl(downloadUrl);
                downloadEntry.setIconUrl(iconUrl);
                WebActivity.startWelcomeGameDetail(this, downloadEntry);
                break;
            default:
                openValue = 0;
                intent = new Intent(WelcomeActivity.this, HomePagerActivity.class);
                intent.putExtra(WELCOME_OPEN_HOME, openValue);
                startActivity(intent);
                break;
        }
        finish();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        boolean showGuide = DataStorage.getGuideShow(WelcomeActivity.this);
        initSdk();
        new AdLogoutLogic().getAd();
        if (showGuide) { //出现过引导页，才访问splash图片
            new SplashLogic().getSplashSave();
        }
        LocationHelper.initLocation(this.getApplication());
    }

    @Override
    public void onPermissionsDenied(int requestCode, final List<String> perms) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("提示");
        builder.setMessage("没有权限, 你需要去设置中开启读取手机权限");
        boolean b = ActivityCompat.shouldShowRequestPermissionRationale(WelcomeActivity.this, Manifest.permission.READ_PHONE_STATE);
        if (!b) {
            builder.setNegativeButton("去设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                    ApkUtil.skipAppMessage(WelcomeActivity.this, REQUEST_PERMISSION_SETTING);
                }
            });
        } else {
            builder.setNegativeButton("去允许", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    alertDialog.dismiss();
                    EasyPermissions.requestPermissions(WelcomeActivity.this, "请开启获取手机状态权限", PERMISSION_FOR_PHONE_STATE, WelcomeActivity.this.perms);
                }
            });
        }
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean showGuide = DataStorage.getGuideShow(WelcomeActivity.this);
        if (EasyPermissions.hasPermissions(this, perms)) {
            initSdk();
            new AdLogoutLogic().getAd();
            if (showGuide) { //出现过引导页，才访问splash图片
                new SplashLogic().getSplashSave();
            }
            LocationHelper.initLocation(this.getApplication());
        } else {
            EasyPermissions.requestPermissions(this, "请开启获取手机状态权限", PERMISSION_FOR_PHONE_STATE, perms);
        }
    }

    private void initView() {
        splashImage = getView(R.id.ch_splash_ad_image);
        splashRightLayout = getView(R.id.ch_splash_ad_layout);
        splashImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFastDoubleClick(3000)) {
                    return;
                }
                WelcomeActivity context = WelcomeActivity.this;
                String url = DataStorage.getSplashUrl(context);
                if (!TextUtils.isEmpty(url) && leftTime - time >= 1) {
                    AppContext.getAppContext().setRun(true);
                    Intent intentIndex = new Intent(context, HomePagerActivity.class);
                    Intent webIntent = null;
                    if (url.contains(StoreSecondActivity.SHOP_URL)) {
                        webIntent = new Intent(context, StoreSecondActivity.class);
                        webIntent.putExtra("url", url);
                    } else {
                        webIntent = new Intent(context, WebActivity.class);
                        webIntent.putExtra("url", url);
                    }
                    webIntent.putExtra("type", 1);
                    Intent[] intents = new Intent[2];
                    intents[0] = intentIndex;
                    intents[1] = webIntent;
                    startActivities(intents);
                    if (handler != null) {
                        handler.removeMessages(100);
                        handler = null;
                    }
                    finish();
                }
            }
        });
        splashText = getView(R.id.ch_splash_ad_text);
        splashRightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handler != null) {
                    handler.removeMessages(100);
                    handler = null;
                }
                startActivity(new Intent(WelcomeActivity.this, HomePagerActivity.class));
                finish();
            }
        });
    }

    private void jumpTo(boolean showGuide) {
        int guideShowCode = DataStorage.getGuideShowCode(this);
        if (!showGuide || guideShowCode < getVersionCode()) {
            Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (skipUri != null) {
                handleUri(skipUri);
            } else if (!TextUtils.isEmpty(webUrl)) {
                handlerVariables();
            } else {
                Intent intent = new Intent(WelcomeActivity.this, HomePagerActivity.class);
                intent.putExtra(WELCOME_OPEN_HOME, openValue);
                startActivity(intent);
                finish();
            }
        }
    }

    private void handlerVariables() {
        WebActivity.startWelcomeWebPage(this, webUrl);
        finish();
    }

    @Override
    public void onBackPressed() {
    }

    private void initSdk() {

        CHSdk.init(this, new SplashDismissListener2() {
            @Override
            public void dismiss(boolean needUpdateGame, boolean startTime) {
                boolean showGuide = DataStorage.getGuideShow(WelcomeActivity.this);
                LogUtil.errorLog("WelcomeActivity initSdk startTime: " + startTime);
                String splashPic = DataStorage.getSplashPic(AppContext.getAppContext());
                String splashLeftTime = DataStorage.getSplashLeftTime(AppContext.getAppContext());
                if (!TextUtils.isEmpty(splashLeftTime)) {
                    leftTime = Integer.parseInt(splashLeftTime);
                    if (leftTime <= 0) {
                        leftTime = 5;
                    }
                } else {
                    leftTime = 5;
                }

                if (skipUri == null && showGuide && !startTime && !TextUtils.isEmpty(splashPic)) {
                    splashRightLayout.setVisibility(View.VISIBLE);
                    splashImage.setVisibility(View.VISIBLE);
                    Glide.with(AppContext.getAppContext()).load(splashPic).placeholder(R.drawable.ch_default_pic).dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.ALL).into(splashImage);
                    handler = new Handler(WelcomeActivity.this);  //要有图片才初始化handler
                    handler.sendEmptyMessage(100);
                } else {
                    jumpTo(showGuide);
                }

//                File file = new File(getSplashSavePath());
//                if (showGuide && !startTime && !TextUtils.isEmpty(splashPic)
//                        && file.exists() && file.length() > 0) {
//                    //显示
//                    splashRightLayout.setVisibility(View.VISIBLE);
//                    splashImage.setVisibility(View.VISIBLE);
//                    splashImage.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            LogUtil.errorLog(splashImage.getHeight() + "--" + splashImage.getWidth());
//                        }
//                    });
//                    Bitmap localPic = getLocalPic(getSplashSavePath());
//                    splashImage.setImageBitmap(localPic);
//                    handler = new Handler(WelcomeActivity.this);  //要有图片才初始化handler
//                    handler.sendEmptyMessage(100);
//                } else {
//                    //不显示  //图片没下载也不显示
//                    jumpTo(showGuide);
//                }
            }
        });
    }

    public String getSplashSavePath() {
        return getCacheDir().getAbsolutePath() + "/CaoHuaSDK/ad/splash/" + "splash.jpeg";
    }

    private Bitmap getLocalPic(String filePath) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inPreferredConfig = Bitmap.Config.RGB_565;
            o.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(filePath, o);
        } catch (OutOfMemoryError error) {
            LogUtil.errorLog("getPushPic OutOfMemoryError");
        } catch (Exception e) {
            LogUtil.errorLog("getPushPic Exception:" + e.getMessage());
        }
        return null;
    }

    public int getVersionCode() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            return 20100;
        }
    }

    @Override
    protected void onDestroy() {
        if (handler != null) {
            handler.removeMessages(100);
            handler = null;
        }
        skipUri = null;
        super.onDestroy();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 100:
                time++;
                splashText.setText("" + (leftTime - time));
                if (leftTime - time == 0) {
                    if (skipUri != null) {
                        handleUri(skipUri);
                    } else if (!TextUtils.isEmpty(webUrl)) {
                        handlerVariables();
                    } else {
                        Intent intent = new Intent(WelcomeActivity.this, HomePagerActivity.class);
                        intent.putExtra(WELCOME_OPEN_HOME, openValue);
                        startActivity(intent);
                        finish();
                    }
                }
                handler.sendEmptyMessageDelayed(100, 1000);
                break;
        }
        return true;
    }

    private int getInt(String value) {
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
