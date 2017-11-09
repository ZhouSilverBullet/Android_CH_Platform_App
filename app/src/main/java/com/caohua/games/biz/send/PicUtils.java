package com.caohua.games.biz.send;

import android.graphics.BitmapFactory;

import com.chsdk.utils.PicturesCompressor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZengLei on 2017/5/22.
 */

public class PicUtils {
    private static final long MAX_UPLOAD_LENGTH = 500 * 1024; // Max upload 860KB

    /**
     * 保存文件到缓存中
     *
     * @param cacheDir 缓存文件夹
     * @param paths    原始路径
     * @return 转存后的路径
     */
    public static String[] saveImageToCache(String cacheDir, List<String> paths) {
        List<String> ret = new ArrayList<>();
        byte[] buffer = new byte[PicturesCompressor.DEFAULT_BUFFER_SIZE];
        BitmapFactory.Options options = PicturesCompressor.createOptions();
        for (String path : paths) {
            File sourcePath = new File(path);
            if (!sourcePath.exists())
                continue;
            try {
                String name = sourcePath.getName();
                String ext = name.substring(name.lastIndexOf(".") + 1).toLowerCase();
                String tempFile = String.format("%s/IMG_%s.%s", cacheDir, System.currentTimeMillis(), ext);
                if (PicturesCompressor.compressImage(path, tempFile,
                        MAX_UPLOAD_LENGTH, 80,
                        1280, 1280 * 6,
                        buffer, options, true)) {

                    // verify the picture ext.
                    tempFile = PicturesCompressor.verifyPictureExt(tempFile);

                    ret.add(tempFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (ret.size() > 0) {
            String[] images = new String[ret.size()];
            ret.toArray(images);
            return images;
        }
        return null;
    }

}
