package com.caohua.games.ui.imagewatch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.caohua.games.R;
import com.caohua.games.ui.BaseActivity;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.utils.FileUtil;
import com.chsdk.utils.PicUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by zhouzhou on 2017/5/31.
 */

public class ImageWatchActivity extends BaseActivity {
    public final static String IMAGE_URL = "urlS";
    private ImageView imageView;
    private String imageUrl;
    private TextView textView;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_image_watch);
        Intent intent = getIntent();
        if (intent != null) {
            imageUrl = intent.getStringExtra(IMAGE_URL);
        } else {
            finish();
        }
        initView();
    }

    private void initView() {
        imageView = ((ImageView) findViewById(R.id.ch_activity_watch_image));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        view = findViewById(R.id.ch_activity_watch_rl);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textView = ((TextView) findViewById(R.id.ch_activity_watch_text_download));
        if (!TextUtils.isEmpty(imageUrl)) {
            Glide.with(this).load(imageUrl).error(R.drawable.ch_default_pic).dontAnimate().into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ch_default_pic);
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File directory = null;
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    directory = Environment.getExternalStorageDirectory();
                } else {
                    directory = getExternalCacheDir();
                }
                String path = directory + "/CaoHuaImage/" + imageName();
                FileUtil.createParentFolder(path);
                File file = new File(path);
                saveImage(file);
            }
        });
    }

    private void saveImage(final File file) {
        new Thread() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                InputStream is = null;
                try {
                    HttpURLConnection url = (HttpURLConnection) new URL(imageUrl).openConnection();
                    url.setConnectTimeout(5000);
                    url.setReadTimeout(5000);
                    if (url.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        is = url.getInputStream();
                        if (is != null) {
                            fos = new FileOutputStream(file);
                            int len = -1;
                            byte[] bs = new byte[1024 * 4];
                            while ((len = is.read(bs)) != -1) {
                                fos.write(bs, 0, len);
                                fos.flush();
                            }

                            String savePicPath = MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(),
                                    imageName(), "CaoHuaImage");

                            savePicPath = PicUtil.checkIsSameUserCrop(ImageWatchActivity.this, savePicPath);
                            if (!TextUtils.isEmpty(savePicPath)) {
                                sendBroadcast(
                                        new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(savePicPath))));
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CHToast.show(ImageWatchActivity.this, "保存成功");
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    private String imageName() {
        int indexOf = imageUrl.lastIndexOf("/");
        String substring = imageUrl.substring(indexOf + 1);
        return substring;
    }
}
