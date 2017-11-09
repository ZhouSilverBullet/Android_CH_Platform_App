package com.caohua.games.biz.account;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.chsdk.configure.SdkSession;
import com.chsdk.configure.UserDBHelper;
import com.chsdk.http.HttpConsts;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.PicUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by ZengLei on 2016/11/7.
 */

public class AccountPicHelper {
    private static final int REQUEST_CODE_CROP = 0;
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_SELECT_PHOTO = 2;
    private static final int PERMISSION_FOR_CAMERA = 0x0001;
    private static final int PERMISSION_FOR_PIC_SELECTOR = 0x0002;
    private Fragment fragment;

    private String tmpFileSaveDir;
    private File cropSaveFile;
    private Uri uri;
    private Activity activity;
    private File takePhotoPath;

    public AccountPicHelper(Activity activity) {
        this.activity = activity;
        try {
            tmpFileSaveDir = activity.getExternalCacheDir().getAbsolutePath() + "/CaoHuaSDK/ad/";
        } catch (Exception e) {
            tmpFileSaveDir = "";
        }
    }

    public AccountPicHelper(Activity activity, Fragment fragment) {
        this.activity = activity;
        this.fragment = fragment;
        try {
            tmpFileSaveDir = activity.getExternalCacheDir().getAbsolutePath() + "/CaoHuaSDK/ad/";
        } catch (Exception e) {
            tmpFileSaveDir = "";
        }
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public void setCropSaveFile(File cropSaveFile) {
        this.cropSaveFile = cropSaveFile;
    }

    public File getCropSaveFile() {
        return cropSaveFile;
    }

    public void handleResult(int requestCode, final ImageView userIcon, Intent imageReturnIntent) {
        switch (requestCode) {
            case REQUEST_CODE_TAKE_PHOTO:
                if (uri == null) {
                    LogUtil.errorLog("AccountPicHelper REQUEST_CODE_TAKE_PHOTO:null");
                    return;
                }
                if (!takePhotoPath.exists()) {
                    CHToast.show(AppContext.getAppContext(), "选择图片错误");
                    return;
                }
                startActionCrop(REQUEST_CODE_TAKE_PHOTO);
                break;
            case REQUEST_CODE_SELECT_PHOTO:
                uri = imageReturnIntent.getData();
                if (uri == null) {
                    LogUtil.errorLog("AccountPicHelper REQUEST_CODE_SELECT_PHOTO:null");
                    return;
                }

                File saveDir = new File(tmpFileSaveDir);
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }
                if (takePhotoPath == null) {  //选择相片回来的路径
                    takePhotoPath = new File(tmpFileSaveDir, "account1.jpg");
                }
                startActionCrop(REQUEST_CODE_SELECT_PHOTO);// 选图后裁剪
                break;
            case REQUEST_CODE_CROP:
                if (cropSaveFile == null) {
                    return;
                }

                if (!cropSaveFile.exists()) {
                    CHToast.show(AppContext.getAppContext(), "出错,图片文件不存在");
                }

                if (takePhotoPath.exists()) {
                    takePhotoPath.delete();
                }

                final LoadingDialog dialog = new LoadingDialog(activity, "");
                dialog.show();

                uploadPic(new TransmitDataInterface() {
                    @Override
                    public void transmit(Object o) {
                        if (o instanceof String) {
                            String url = (String) o;
                            PicUtil.displayImg(activity.getApplicationContext(), userIcon, url, R.drawable.ch_account);
                        } else {
                            CHToast.show(activity.getApplicationContext(), "上传失败");
                        }
                        if (cropSaveFile.exists()) {
                            cropSaveFile.delete();
                        }
                        cropSaveFile = null;
                        dialog.dismiss();
                    }
                });
                break;
        }
    }

    public void showSelectPicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setItems(new String[]{"相册", "相机"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (EasyPermissions.hasPermissions(activity, permissions)) {
                        selectPhoto();
                    } else {
                        EasyPermissions.requestPermissions(activity, "请授予手机读写权限", PERMISSION_FOR_PIC_SELECTOR, permissions);
                    }
                } else {
                    String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (EasyPermissions.hasPermissions(activity, permissions)) {
                        takePhoto();
                    } else {
                        EasyPermissions.requestPermissions(activity, "请授予开启相机权限", PERMISSION_FOR_CAMERA, permissions);
                    }
                }
            }
        });
        builder.setTitle("选择图片");
        builder.show();
    }

    private void uploadPic(final TransmitDataInterface uploadFinish) {
        RequestExe.upload("https://app-sdk.caohua.com/account/changeFace", cropSaveFile, new AccountPicModel(), "af",
                new IRequestListener() {
                    @Override
                    public void success(HashMap<String, String> map) {
                        if (map != null) {
                            String imgUrl = map.get(HttpConsts.RESULT_PARAMS_ACCOUNT_FACE);
                            final AppContext context = AppContext.getAppContext();

                            String userName = SdkSession.getInstance().getUserName();
                            LoginUserInfo info = UserDBHelper.getUser(context, userName);
                            info.imgUrl = imgUrl;
                            UserDBHelper.updateUser(context, info);
                            SdkSession.getInstance().setUserInfo(info);
                            if (uploadFinish != null) {
                                uploadFinish.transmit(imgUrl);
                            }
                            return;
                        }
                        if (uploadFinish != null) {
                            uploadFinish.transmit(false);
                        }
                    }

                    @Override
                    public void failed(String error, int errorCode) {
                        LogUtil.errorLog("上传 onFailure " + error);
                        if (uploadFinish != null) {
                            uploadFinish.transmit(false);
                        }
                    }
                });

    }

    private void selectPhoto() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        intent.setType("image/*");
        fragment.startActivityForResult(Intent.createChooser(intent, "选择图片"),
                REQUEST_CODE_SELECT_PHOTO);
    }

    private void takePhoto() {
        File saveDir = new File(tmpFileSaveDir);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        takePhotoPath = new File(tmpFileSaveDir, "account1.jpg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(activity, "com.caohua.games.apps.fileprovider", takePhotoPath);
        } else {
            uri = Uri.fromFile(takePhotoPath);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        fragment.startActivityForResult(intent,
                REQUEST_CODE_TAKE_PHOTO);
    }

    private void startActionCrop(int status) {
        cropSaveFile = new File(tmpFileSaveDir, "account2.jpg");
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            switch (status) {
                case REQUEST_CODE_TAKE_PHOTO:
                    intent.setDataAndType(getImageContentUri(takePhotoPath), "image/*");
                    intent.putExtra("output", Uri.fromFile(cropSaveFile));
                    break;
                case REQUEST_CODE_SELECT_PHOTO:
                    intent.setDataAndType(uri, "image/*");
                    intent.putExtra("output", getUploadTempFile(uri));
                    break;
            }
        } else {
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("output", getUploadTempFile(uri));
        }
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);// 输出图片大小
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        fragment.startActivityForResult(intent,
                REQUEST_CODE_CROP);
        uri = null;
    }

    public Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = activity.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return activity.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return uri;
            }
        }
    }

    private Uri getUploadTempFile(Uri uri) {
        File saveDir = new File(tmpFileSaveDir);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }

        String thePath = getAbsolutePathFromNoStandardUri(uri);

        if (TextUtils.isEmpty(thePath)) {
            thePath = getAbsoluteImagePath(uri);
        }

        String ext = getFileFormat(thePath);
        ext = TextUtils.isEmpty(ext) ? "jpg" : ext;
        String cropFileName = "account." + ext;
        cropSaveFile = new File(tmpFileSaveDir, cropFileName);

        Uri notifyUri = Uri.fromFile(cropSaveFile);
        activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, notifyUri));
        return notifyUri;
    }

    private String getAbsolutePathFromNoStandardUri(Uri mUri) {
        String filePath = null;

        String mUriString = mUri.toString();
        mUriString = Uri.decode(mUriString);

        String pre1 = "file://" + "/sdcard" + File.separator;
        String pre2 = "file://" + "/mnt/sdcard" + File.separator;

        if (mUriString.startsWith(pre1)) {
            filePath = Environment.getExternalStorageDirectory().getPath()
                    + File.separator + mUriString.substring(pre1.length());
        } else if (mUriString.startsWith(pre2)) {
            filePath = Environment.getExternalStorageDirectory().getPath()
                    + File.separator + mUriString.substring(pre2.length());
        }
        return filePath;
    }

    private String getAbsoluteImagePath(Uri uri) {
        String imagePath = "";
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.managedQuery(uri, proj, // Which columns to
                // return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)

        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                imagePath = cursor.getString(column_index);
            }
        }

        return imagePath;
    }

    private String getFileFormat(String fileName) {
        if (TextUtils.isEmpty(fileName))
            return "";

        int point = fileName.lastIndexOf('.');
        return fileName.substring(point + 1);
    }
}
