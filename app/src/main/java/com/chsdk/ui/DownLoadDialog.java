package com.chsdk.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.chsdk.http.RequestExe;
import com.chsdk.model.game.UpdateEntry;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.ApkUtil;
import com.chsdk.utils.FileUtil;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.ViewUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class DownLoadDialog {
    private String filePath;
    private TextView tvProgress;
    private ProgressBar pgb;
    private AlertDialog dialog;
    private Activity activity;
    private Button btnTry;
    private ImageView imgClose;
    //	private HttpHandler downHandler;
    private CHAlertDialog quitDialog;
    private UpdateEntry updateEntry;
    private Call call;


    public DownLoadDialog(Activity activity, UpdateEntry updateEntry, String fileName) {
        this.activity = activity;
        this.updateEntry = updateEntry;
        this.filePath = fileName;
    }

    public void showDialog() {
        dialog = new AlertDialog.Builder(activity, R.style.ch_base_style).create();
        dialog.setOnKeyListener(new BackListener());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        View view = LayoutInflater.from(activity).inflate(R.layout.ch_dialog_download, null);
        dialog.getWindow().setContentView(view);

        initView(view);

        checkDownloaded(view);
    }

    private void confirmQuitLoad() {
        if (quitDialog == null) {
            quitDialog = new CHAlertDialog(activity);
            quitDialog.show();
            quitDialog.setCancelButton("继续下载", new OnClickListener() {

                @Override
                public void onClick(View v) {
                    quitDialog.dismiss();
                    quitDialog = null;
                }
            });
            quitDialog.setContent("确认要取消下载吗?");
            quitDialog.setOkButton("取消下载", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    quitDialog.dismiss();
                    quitDialog = null;
                    dismiss();
                    if (call != null) {
                        call.cancel();
                    }
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
            });
        }
    }

    private void confirmQuitActivity() {
        if (quitDialog == null) {
            quitDialog = new CHAlertDialog(activity);
            quitDialog.show();
            quitDialog.setCancelButton("继续下载", new OnClickListener() {

                @Override
                public void onClick(View v) {
                    quitDialog.dismiss();
                    quitDialog = null;
                }
            });
            quitDialog.setContent("确认要退出app并取消下载吗?");
            quitDialog.setOkButton("退出", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    quitDialog.dismiss();
                    quitDialog = null;
                    if (call != null) {
                        call.cancel();
                    }
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    activity.finish();
                }
            });
        }
    }

    private void initView(View view) {
        imgClose = ViewUtil.getView(view, R.id.ch_dialog_download_close);
        if (updateEntry.forceUpdate) {
            imgClose.setVisibility(View.INVISIBLE);
        }
        imgClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmQuitLoad();
            }
        });
        tvProgress = ViewUtil.getView(view, R.id.ch_dialog_download_tv_process);
        pgb = ViewUtil.getView(view, R.id.ch_dialog_download_sb_process);
        btnTry = ViewUtil.getView(view, R.id.ch_dialog_download_try);
        btnTry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTry.setVisibility(View.GONE);
                checkDownloaded(v);
            }
        });
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }

    private void checkDownloaded(final View view) {
        boolean exists = new File(filePath).exists();
        LogUtil.errorLog("DownLoadDialog checkDownloaded exists : " + exists);
        if (exists) {
            if (updateEntry.versionCode <= 0) {
                new File(filePath).delete();
                downloadFile();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int code = ApkUtil.getVersionCode(activity, filePath);
                        if (updateEntry.versionCode > code) {
                            new File(filePath).delete();
                            RequestExe.getOkMainThreadHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    downloadFile();
                                }
                            });
                        } else {
                            RequestExe.getOkMainThreadHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    setProgress(100);
                                }
                            });

                            RequestExe.getOkMainThreadHandler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dismiss();
                                    if (updateEntry.forceUpdate) {
                                        activity.finish();
                                    }
                                    if (quitDialog != null) {
                                        quitDialog.dismiss();
                                    }
                                    ApkUtil.installApk(activity, filePath);
                                }
                            }, 700);
                        }
                    }
                }).start();
            }
        } else {
            downloadFile();
        }
    }

    private void downloadFile() {
        Request request = new Request.Builder().url(updateEntry.url).build();
        call = RequestExe.getClient().newCall(request);
        call.enqueue(new Callback() {
                         @Override
                         public void onFailure(Call call, final IOException e) {
                             RequestExe.getOkMainThreadHandler().post(new Runnable() {
                                 @Override
                                 public void run() {
                                     btnTry.setVisibility(View.VISIBLE);
                                     setProgress(0);
                                     new File(filePath).delete();

                                     String msg = RequestExe.getOkHttpExceptionMsg(e, "下载错误");
                                     CHToast.show(AppContext.getAppContext(), msg);
                                 }
                             });
                         }

                         @Override
                         public void onResponse(Call call, Response response) throws IOException {
                             InputStream is = null;
                             byte[] buf = new byte[2048];
                             int len = 0;
                             FileOutputStream fos = null;
                             try {
                                 is = response.body().byteStream();
                                 final long total = response.body().contentLength();

                                 long sum = 0;

                                 File dir = new File(FileUtil.getCHSdkCacheDirectory(activity));
                                 if (!dir.exists()) {
                                     dir.mkdirs();
                                 }
                                 File file = new File(dir, "UpdateGame.apk");
                                 fos = new FileOutputStream(file);
                                 while ((len = is.read(buf)) != -1) {
                                     sum += len;
                                     fos.write(buf, 0, len);
                                     final long finalSum = sum;
                                     RequestExe.getOkMainThreadHandler().post(new Runnable() {
                                         @Override
                                         public void run() {
                                             float progress = (finalSum * 1.0f / total) * 100;
                                             setProgress((int) progress);
                                         }
                                     });
                                 }
                                 fos.flush();
                                 ApkUtil.installApk(activity, filePath);
                                 if (updateEntry.forceUpdate) {
                                     dismiss();
                                     activity.finish();
                                 }
                             } finally {
                                 try {
                                     response.body().close();
                                     if (is != null) is.close();
                                 } catch (IOException e) {
                                 }
                                 try {
                                     if (fos != null) fos.close();
                                 } catch (IOException e) {
                                 }

                             }
                         }
                     }
        );
    }

    private void setProgress(int progress) {
        if (progress >= 100 || progress < 0) {
            pgb.setProgress(100);
            tvProgress.setText(100 + "%");
            return;
        }
        pgb.setProgress(progress);
        tvProgress.setText(progress + "%");
    }

    class BackListener implements OnKeyListener {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if (!updateEntry.forceUpdate) {
                    confirmQuitLoad();
                    return true;
                } else {
                    confirmQuitActivity();
                    return true;
                }
            }
            return false;
        }
    }
}
