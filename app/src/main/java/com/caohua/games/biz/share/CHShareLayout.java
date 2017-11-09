package com.caohua.games.biz.share;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.bbs.ForumShareEntry;
import com.caohua.games.biz.comment.TimesPraiseEntry;
import com.caohua.games.biz.task.DoneTaskLogic;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.LogUtil;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by zhouzhou on 2017/8/3.
 */

public class CHShareLayout {
    private TimesPraiseEntry timesPraiseEntry;
    private ForumShareEntry forumShareEntry;
    private Dialog dialog;
    private Activity activity;
    private PlatformActionListener platformActionListener;
    private LoadingDialog loadingDialog;

    public CHShareLayout(Activity activity, TimesPraiseEntry timesPraiseEntry) {
        this.activity = activity;
        this.timesPraiseEntry = timesPraiseEntry;
    }

    public CHShareLayout(Activity activity, ForumShareEntry forumShareEntry) {
        this.activity = activity;
        this.forumShareEntry = forumShareEntry;
    }

    public void show() {
        String title = "";
        String icon = "";
        String url = "";
        String content = "";
        if (timesPraiseEntry != null) {
            icon = timesPraiseEntry.getArticle_icon();
            title = timesPraiseEntry.getArticle_title();
            url = timesPraiseEntry.getArticle_url();
            content = timesPraiseEntry.getContent();
        } else if (forumShareEntry != null) {
            url = forumShareEntry.getShareUrl();
            icon = forumShareEntry.getGameIcon();
            title = forumShareEntry.getGameName();
            content = forumShareEntry.getTitle();
        } else {
            icon = "https://cdn-sdk.caohua.com/www/img/shouyou.jpg";
            title = "";
            url = "https://wap.caohua.com";
            content = "";
        }

        if (TextUtils.isEmpty(icon)) {
            icon = "https://cdn-sdk.caohua.com/www/img/shouyou.jpg";
        }

        if (TextUtils.isEmpty(title)) {
            title = "草花手游";
        }

        if (TextUtils.isEmpty(url)) {
            url = "http://wap.caohua.com";
        }

        if (TextUtils.isEmpty(content)) {
            content = "草花手游";
        }
        final String shareIcon = icon;
        final String shareTitle = title;
        final String shareUrl = url;
        final String shareContent = content;
        dialog = new Dialog(activity, R.style.ActionSheetDialogStyle);
        View inflate = LayoutInflater.from(activity).inflate(R.layout.ch_share_dialog_layout, null);
        inflate.findViewById(R.id.ch_share_dialog_weibo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareSina(shareContent, shareUrl, shareIcon);
                AnalyticsHome.umOnEvent(AnalyticsHome.SINAWEIBO_SHARE_CLICK, "SinaWeibo");
                dismiss();
                dismissLoadingDialog();
            }
        });
        inflate.findViewById(R.id.ch_share_dialog_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQQ(shareTitle, shareUrl, null, shareIcon, shareContent);
                AnalyticsHome.umOnEvent(AnalyticsHome.QQ_SHARE_CLICK, "QQ");
                dismiss();
                dismissLoadingDialog();
            }
        });

        inflate.findViewById(R.id.ch_share_dialog_QZone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQZone(shareTitle, shareUrl, shareIcon, shareContent);
                AnalyticsHome.umOnEvent(AnalyticsHome.QZONE_SHARE_CLICK, "QZone");
                dismiss();
                dismissLoadingDialog();
            }
        });

        inflate.findViewById(R.id.ch_share_dialog_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wechat(shareTitle, shareContent, shareIcon, shareUrl, shareUrl);
                AnalyticsHome.umOnEvent(AnalyticsHome.WECHAT_SHARE_CLICK, "Wechat");
                dismiss();
                dismissLoadingDialog();
            }
        });

        inflate.findViewById(R.id.ch_share_dialog_wechat_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wechatFriend(shareTitle, shareContent, shareIcon, shareUrl, shareUrl);
                AnalyticsHome.umOnEvent(AnalyticsHome.WECHAT_MOMENTS_SHARE_CLICK, "WechatMoments");
                dismiss();
                dismissLoadingDialog();
            }
        });

        inflate.findViewById(R.id.ch_share_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                dismissLoadingDialog();
            }
        });

        //将布局设置给Dialog
        dialog.setContentView(inflate);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框

        platformActionListener = new PlatformActionListener() {

            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                CHToast.show(AppContext.getAppContext(), "分享成功！");
                shareSuccess(shareUrl);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                CHToast.show(AppContext.getAppContext(), "分享失败！");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                CHToast.show(AppContext.getAppContext(), "分享取消！");
            }
        };
    }

    private void shareSuccess(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.contains("game")) {
            new DoneTaskLogic(DoneTaskLogic.TASK_GAME_SHARE).getDoneTask();
        } else if (url.contains("article")) {
            new DoneTaskLogic(DoneTaskLogic.TASK_ARTICLE_SHARE).getDoneTask();
        }
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.dismiss();
                }
            }, 3000);
        }
    }

    private void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void shareSina(String text, final String shareUrl, String picUrl) {
        SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
        sp.setText(text + "\n" + shareUrl);
        if (picUrl != null) {
            sp.setImageUrl(picUrl);
        }
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
        if (weibo.isValid() && !weibo.isClientValid()) {
            final LoadingDialog loadingDialog = new LoadingDialog(activity, true);
            loadingDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CHToast.show(activity, "分享成功！");
                    loadingDialog.dismiss();
                    shareSuccess(shareUrl);
                }
            }, 2000);
        } else {
            loadingDialog = new LoadingDialog(activity, false);
            loadingDialog.show();
        }
        LogUtil.errorLog("CHShareLayout :" + weibo.isSSODisable() + "--" + weibo.hasShareCallback() + "--"
                + weibo.isClientValid() + "--" + weibo.isValid());
        weibo.setPlatformActionListener(platformActionListener); // 设置分享事件回调
        // 执行图文分享
        weibo.share(sp);
    }

    private void shareQQ(String title, String titleUrl, String musicUrl, String imageUrl, String content) {
        loadingDialog = new LoadingDialog(activity, false);
        loadingDialog.show();
        QQ.ShareParams sp = new QQ.ShareParams();
        sp.titleUrl = titleUrl;
        sp.title = title;
        sp.musicUrl = musicUrl;
        sp.imageUrl = imageUrl;
        sp.setText(content);
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        qq.setPlatformActionListener(platformActionListener);
        qq.share(sp);
    }

    private void shareQZone(String title, String titleUrl, String imageUrl, String content) {
        loadingDialog = new LoadingDialog(activity, false);
        loadingDialog.show();
        QZone.ShareParams sp = new QZone.ShareParams();
        sp.comment = "";
        sp.imageUrl = imageUrl;
        sp.site = "草花手游";
        sp.titleUrl = titleUrl;
        sp.title = title;
        sp.setText(content);
        sp.siteUrl = "http://wap.caohua.com";
        Platform qZone = ShareSDK.getPlatform(QZone.NAME);
        qZone.setPlatformActionListener(platformActionListener);
        qZone.share(sp);
    }

    private void wechat(String title, String content, String PicUrl, String titleUrl, String url) {
        loadingDialog = new LoadingDialog(activity, false);
        loadingDialog.show();
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.setTitle(title);
        sp.setText(content);
        if (titleUrl != null) {
            sp.setTitleUrl(titleUrl); // 标题的超链接
        }
        if (PicUrl != null) {
            sp.setImageUrl(PicUrl);// 图片地址
        }
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setUrl(url);
        Platform wx = ShareSDK.getPlatform(Wechat.NAME);
        wx.setPlatformActionListener(platformActionListener); // 设置分享事件回调
        // 执行图文分享
        wx.share(sp);
    }

    private void wechatFriend(String title, String content, String PicUrl, String titleUrl, String url) {
        loadingDialog = new LoadingDialog(activity, false);
        loadingDialog.show();
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setTitle(title);
        sp.setText(content);
        if (titleUrl != null) {
            sp.setTitleUrl(titleUrl); // 标题的超链接
        }
        if (PicUrl != null) {
            sp.setImageUrl(PicUrl);// 图片地址
        }
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setUrl(url);
        Platform wm = ShareSDK.getPlatform(WechatMoments.NAME);
        wm.setPlatformActionListener(platformActionListener); // 设置分享事件回调
        // 执行图文分享
        wm.share(sp);
    }
}
