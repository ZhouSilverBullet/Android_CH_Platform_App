package com.caohua.games.ui.send;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.send.ArticleEntry;
import com.caohua.games.biz.send.PublishArticleLogic;
import com.caohua.games.biz.send.PublishPicsHelper;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.emoji.ChatEmojiEntry;
import com.caohua.games.ui.emoji.EmojiInputFilter;
import com.caohua.games.ui.emoji.FaceAdapter;
import com.caohua.games.ui.emoji.FaceConversionUtil;
import com.chsdk.api.AppOperator;
import com.chsdk.api.CacheManager;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.ApkUtil;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.ViewUtil;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageLoader;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by zhouzhou on 2017/5/11.
 */

public class SendEditActivity extends BaseActivity implements AdapterView.OnItemClickListener, EasyPermissions.PermissionCallbacks {
    public static final int REQUSET_CODE_FOR_BBS = 90;
    public static final int RESULT_CODE_FOR_BBS = 91;
    private static final String KEY_DATA = "forum_id";
    private static final String KEY_TO_BBS = "to_bbs";
    private static final int REQUEST_CODE_CHOOSE_PHOTE = 100;
    private static final int REQUEST_CODE_TAKE_PHOTO = 101;
    private static final int REQUEST_CODE_TAG = 102;

    private static final int PERMISSION_FOR_CAMERA = 0x0001;
    private static final int PERMISSION_FOR_PIC_SELECTOR = 0x0002;

    private static final String ARTICLE_SAVE_FILE = "article_entry_file";

    private ImageView selectorPic;
    private ArrayList<String> selectedPicPath;
    private TextView sendPicCount;
    private RecyclerView galleryRecycler;
    private View gallery_layout;
    private GalleryAdapter galleryAdapter;
    private ImageConfig imageConfig;
    private EditText sendEditTitle;
    private AutoCompleteTextView sendContent;
    private View selectorCamera;
    private ViewPager emojiPager;
    private EmojiPagerAdapter emojiPagerAdapter;
    private List<FaceAdapter> faceAdapters;
    private int current;
    private List<List<ChatEmojiEntry>> emojiLists;
    private View emoji;
    private List<View> pageViews;
    private View emojiOuterLayout;
    private View sendEditOuterLayout;
    private View sendEditFinish;
    private TextView tvTag;
    private View rootView;

    private InputFilter emojiFilter = new EmojiInputFilter();  //过滤输入法的emoji表情
    public InputFilter[] emojiFilters = {emojiFilter};
    private View sendEditSend;
    private String tagName, tagId;
    private String forumId;
    private boolean toBBS;
    int isSoftShow;

    private PublishPicsHelper publishPicsHelper;
    private String picPath;
    private String tmpFileSaveDir;
    private ArticleEntry readArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_send_edit);

        if (getIntent() != null) {
            forumId = getIntent().getStringExtra(KEY_DATA);
            toBBS = getIntent().getBooleanExtra(KEY_TO_BBS, false);
        }
        if (TextUtils.isEmpty(forumId)) {
            finish();
            return;
        }

        initPicSelector();
        initView();
        readArticleCacheFile();
    }

    private void readArticleCacheFile() {
        readArticle = (ArticleEntry) CacheManager.readObject(AppContext.getAppContext(), ARTICLE_SAVE_FILE + forumId);
        if (readArticle != null) {
            if (forumId.equals(readArticle.forumId)) {
                sendContent.setText(readArticle.content);
                sendEditTitle.setText(readArticle.title);
                tvTag.setText(readArticle.tagName);
                tagId = readArticle.tag;
                tagName = readArticle.tagName;
                if (readArticle.savePics != null) {
                    handleChoosePhone(readArticle.savePics);
                }
            }
        }
    }

    private void initPicSelector() {
        if (selectedPicPath == null) {
            selectedPicPath = new ArrayList<>();
        }
        imageConfig = new ImageConfig.Builder(
                new GlideLoader())
                .steepToolBarColor(getResources().getColor(R.color.black))
                .titleBgColor(getResources().getColor(R.color.ch_black_alpha_title))
                .titleSubmitTextColor(getResources().getColor(R.color.white))
                .titleTextColor(getResources().getColor(R.color.white))
                .mutiSelect()
                .mutiSelectMaxSize(9)
                .pathList(selectedPicPath)
                .filePath("/ImageSelector/Pictures")
                .requestCode(REQUEST_CODE_CHOOSE_PHOTE)
                .build();
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    }

    private void initView() {
        rootView = getView(R.id.ch_activity_send_edit_root_layout);
        addSoftListener();
        tvTag = getView(R.id.ch_activity_send_tag_value);
        getView(R.id.ch_activity_send_tag_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.getAppContext().isNetworkConnected()) {
                    CHToast.show(v.getContext(), "请打开网络重试！");
                    return;
                }
                ChooseTagActivity.lauch(SendEditActivity.this, REQUEST_CODE_TAG, tagName, forumId);
            }
        });
        sendEditOuterLayout = getView(R.id.ch_activity_send_edit_outer_layout);
        initRecycler();
        sendEditFinish = getView(R.id.ch_activity_send_edit_finish);
        sendEditFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialog();
            }
        });

        sendEditSend = getView(R.id.ch_activity_send_edit_send);
        sendEditSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSend();
            }
        });
        selectorCamera = getView(R.id.ch_activity_send_edit_selector_camera);
        selectorCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKey(sendContent);
                hideSoftKey(sendEditTitle);
                String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (EasyPermissions.hasPermissions(SendEditActivity.this, permissions)) {
                    if (galleryAdapter != null && galleryAdapter.list != null && galleryAdapter.list.size() < 9) {
                        openTakePhoto();
                    } else {
                        CHToast.show(SendEditActivity.this, "您已经选择9张照片");
                    }
                } else {
                    EasyPermissions.requestPermissions(SendEditActivity.this, "请授予开启相机权限", PERMISSION_FOR_CAMERA, permissions);
                }
            }
        });

        sendEditTitle = getView(R.id.ch_activity_send_edit_title);
        sendEditTitle.addTextChangedListener(new MaxLengthWatcher(30, sendEditTitle));
        sendEditTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    sendEditOuterLayout.setVisibility(View.GONE);
                    gallery_layout.setVisibility(View.GONE);
                    emojiOuterLayout.setVisibility(View.GONE);
                }
                return false;
            }
        });

        sendContent = getView(R.id.ch_activity_send_autoCompleteTextView);
        sendContent.addTextChangedListener(new MaxLengthWatcher(10000, sendEditTitle));
        sendContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    sendEditOuterLayout.setVisibility(View.GONE);
                    gallery_layout.setVisibility(View.GONE);
                    emojiOuterLayout.setVisibility(View.GONE);
                }
                return false;
            }
        });
        sendEditTitle.setFilters(emojiFilters);
        sendContent.setFilters(emojiFilters);
        initEmojiPager();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        final CHAlertDialog alertDialog = new CHAlertDialog(this, true, true);
        alertDialog.show();
        alertDialog.setTitle("提示");
        switch (requestCode) {
            case PERMISSION_FOR_CAMERA:
                alertDialog.setContent("没有权限, 你需要去设置中开启开启相机权限");
                break;
            case PERMISSION_FOR_PIC_SELECTOR:
                alertDialog.setContent("没有权限, 你需要去设置中开启读取手机存储权限");
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
                ApkUtil.skipAppMessage(SendEditActivity.this, 0);
            }
        });
    }

    private void addSoftListener() {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (rootView.getHeight() > 0) {
                  /*  int bottom = rootView.getBottom();
                    if (lastAnchorViewBottom == 0) {
                        lastAnchorViewBottom = bottom;
                        return;
                    }
                    if (lastAnchorViewBottom == bottom) {
                        return;
                    }
                    if (lastAnchorViewBottom > bottom) {
                        LogUtil.errorLog("输入法展开");
                    } else {
                        LogUtil.errorLog("输入法隐藏");
                    }
                    lastAnchorViewBottom = bottom;*/

                    int screenHeight = rootView.getRootView().getHeight();
                    int myHeight = rootView.getHeight();
                    int heightDiff = screenHeight - myHeight;
                    if (heightDiff > 100) {
                        if (isSoftShow == 2) {
                            return;
                        }
                        softShowCallback();
                        isSoftShow = 2;
                    } else {
                        if (isSoftShow == 1) {
                            return;
                        }
                        softHideCallback();
                        isSoftShow = 1;
                    }
                }
            }
        });
    }

    private void softHideCallback() {
        LogUtil.errorLog("输入法隐藏");
        if (selectedPicPath != null && selectedPicPath.size() != 0) {
            sendEditOuterLayout.setVisibility(View.VISIBLE);
            gallery_layout.setVisibility(View.VISIBLE);
            emojiOuterLayout.setVisibility(View.GONE);
        } else {
            gallery_layout.setVisibility(View.GONE);
            sendEditOuterLayout.setVisibility(View.GONE);
        }
    }

    private void softShowCallback() {
        LogUtil.errorLog("输入法展开");
        sendEditOuterLayout.setVisibility(View.GONE);
        gallery_layout.setVisibility(View.GONE);
        emojiOuterLayout.setVisibility(View.GONE);
    }

    private void showExitDialog() {
        String contentText = sendContent.getText().toString();
        String titleText = sendEditTitle.getText().toString();
        if (readArticle != null) {
            boolean contentChange = true;
            boolean titleChange = true;
            boolean tagChange = true;
            if (!TextUtils.isEmpty(contentText)) {
                if (contentText.equals(readArticle.content)) {
                    contentChange = false;
                }
            } else {
                contentChange = false;
            }

            if (!TextUtils.isEmpty(titleText)) {
                if (titleText.equals(readArticle.title)) {
                    titleChange = false;
                }
            } else {
                titleChange = false;
            }

            if (!TextUtils.isEmpty(tagId)) {
                if (tagId.equals(readArticle.tag)) {
                    tagChange = false;
                }
            } else {
                tagChange = false;
            }

            if (!contentChange && !titleChange && !tagChange) {
                readArticle.savePics = selectedPicPath;
                AppOperator.runOnThread(new Runnable() {
                    @Override
                    public void run() {
                        CacheManager.saveObject(AppContext.getAppContext(), readArticle, ARTICLE_SAVE_FILE + forumId);
                    }
                });
                finish();
                return;
            }
        }
        if (!TextUtils.isEmpty(contentText)
                || !TextUtils.isEmpty(titleText)
                || (galleryAdapter != null && galleryAdapter.list.size() > 0)) {
            final CHAlertDialog chAlertDialog = new CHAlertDialog(SendEditActivity.this, true, true);
            chAlertDialog.show();
            chAlertDialog.setContent("是否保存草稿?");
            chAlertDialog.setTitle("提示");
            chAlertDialog.setOkButton("保存", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ArticleEntry articleEntry = getArticleEntry();
                    AppOperator.runOnThread(new Runnable() {
                        @Override
                        public void run() {
                            CacheManager.saveObject(AppContext.getAppContext(), articleEntry, ARTICLE_SAVE_FILE + forumId);
                        }
                    });
                    chAlertDialog.dismiss();
                    finish();
                }
            });
            chAlertDialog.setCancelButton("不保存", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (readArticle != null) {
                        deleteSaveFile();
                    }
                    finish();
                }
            });
        } else {
            finish();
        }
    }

    private void initSelectorPic() {
        selectorPic = getView(R.id.ch_activity_send_edit_selector_pic);
        sendPicCount = getView(R.id.ch_activity_send_edit_count);
        selectorPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKey(sendContent);
                hideSoftKey(sendEditTitle);
                if (galleryAdapter != null && galleryAdapter.list.size() > 0) {
                    selectorPic.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendEditOuterLayout.setVisibility(View.VISIBLE);
                            gallery_layout.setVisibility(View.VISIBLE);
                            emojiOuterLayout.setVisibility(View.GONE);
                        }
                    }, 200);
                } else {
                    emojiOuterLayout.setVisibility(View.GONE);
                    imageConfigAndOpen();
                }
            }
        });
    }

    /**
     * 初始化 表情的pager
     */
    private void initEmojiPager() {
        emojiOuterLayout = findViewById(R.id.ch_activity_send_edit_emoji_layout);
        emoji = findViewById(R.id.ch_activity_send_edit_selector_emoji);
        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKey(sendContent);
                hideSoftKey(sendEditTitle);
                selectorPic.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendEditOuterLayout.setVisibility(View.VISIBLE);
                        emojiOuterLayout.setVisibility(View.VISIBLE);
                        gallery_layout.setVisibility(View.GONE);
                    }
                }, 200);
                if (emojiLists.size() == 0) {
                    new Thread() {
                        @Override
                        public void run() {
                            FaceConversionUtil.getInstance().getFileText(SendEditActivity.this);
                            if (emojiLists != null) {
                                selectorPic.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadEmojiData(emojiLists);
                                    }
                                });
                            }
                        }
                    }.start();
                } else {
                    if (emojiPagerAdapter != null && emojiPagerAdapter.list.size() == 0) {
                        loadEmojiData(emojiLists);
                    }
                }
            }
        });
        emojiPager = (ViewPager) findViewById(R.id.ch_activity_send_edit_emoji_pager);
        emojiLists = FaceConversionUtil.getInstance().getEmojiLists();
        if (emojiLists.size() == 0) {
            new Thread() {
                @Override
                public void run() {
                    FaceConversionUtil.getInstance().getFileText(SendEditActivity.this);
                    if (emojiLists != null) {
                        selectorPic.post(new Runnable() {
                            @Override
                            public void run() {
                                loadEmojiData(emojiLists);
                            }
                        });
                    }
                }
            }.start();
        }

        emojiPagerAdapter = new EmojiPagerAdapter(new ArrayList<View>());
        emojiPager.setAdapter(emojiPagerAdapter);
        emojiPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                current = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void loadEmojiData(List<List<ChatEmojiEntry>> emojis) {
        pageViews = new ArrayList<View>();
        faceAdapters = new ArrayList<FaceAdapter>();
        for (int i = 0; i < emojis.size(); i++) {
            GridView view = new GridView(this);
            FaceAdapter adapter = new FaceAdapter(this, emojis.get(i));
            view.setAdapter(adapter);
            faceAdapters.add(adapter);
            view.setOnItemClickListener(this);
            view.setNumColumns(7);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(ViewUtil.dp2px(this, 20));
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            view.setGravity(Gravity.CENTER);
            pageViews.add(view);
        }
        emojiPagerAdapter.addAll(pageViews);
    }

    private void openTakePhoto() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            emojiOuterLayout.setVisibility(View.GONE);
            boolean canUse = true;
            if (canUse) {
                if (TextUtils.isEmpty(tmpFileSaveDir)) {
                    tmpFileSaveDir = Environment.getExternalStorageDirectory() + "/CaoHuaSDK/forum_photo/";
                }
                File saveDir = new File(tmpFileSaveDir);
                if (!saveDir.exists()) {
                    saveDir.mkdirs();
                }
                picPath = tmpFileSaveDir + System.currentTimeMillis() + ".jpeg";
                File file = new File(picPath);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(this, "com.caohua.games.apps.fileprovider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
            } else {
                CHToast.show(getApplication(), "请在设置中打开相机权限");
            }
        } else {
            CHToast.show(SendEditActivity.this, "sdcard不可用");
        }
    }

    public boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

    private void initRecycler() {
        gallery_layout = getView(R.id.ch_activity_send_edit_gallery_layout);
        galleryRecycler = getView(R.id.ch_activity_send_edit_gallery_recycler);
        galleryRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));
        int spacingInPixels = ViewUtil.dp2px(this, 5);
        galleryRecycler.addItemDecoration(new ChooseTagActivity.SpaceItemDecoration(spacingInPixels));
        galleryAdapter = new GalleryAdapter(new ArrayList<String>(), this);
        galleryRecycler.setAdapter(galleryAdapter);
        initSelectorPic();
    }

    private void imageConfigAndOpen() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(SendEditActivity.this, permissions)) {
            ImageSelector.open(SendEditActivity.this, imageConfig);   // 开启图片选择器
        } else {
            EasyPermissions.requestPermissions(SendEditActivity.this, "请授予文件读写权限", PERMISSION_FOR_PIC_SELECTOR, permissions);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FaceAdapter faceAdapter = faceAdapters.get(current);
        ChatEmojiEntry emoji = (ChatEmojiEntry) faceAdapter.getItem(position);
        if (emoji.getId() == R.drawable.ch_emoji_face_del_icon) {
            int selection = sendContent.getSelectionStart();
            String text = sendContent.getText().toString();
            if (selection > 0) {
                String text2 = text.substring(selection - 1);
                if ("]".equals(text2)) {
                    int start = text.lastIndexOf("[");
                    int end = selection;
                    sendContent.getText().delete(start, end);
                    return;
                }
                sendContent.getText().delete(selection - 1, selection);
            }
        }
        if (!TextUtils.isEmpty(emoji.getCharacter())) {
            SpannableString spannableString = FaceConversionUtil.getInstance()
                    .addFace(this, emoji.getId(), emoji.getCharacter());
            sendContent.append(spannableString);
        }
    }

    class GlideLoader implements ImageLoader {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            LogUtil.errorLog("GlideLoader path: " + path);
            if (path.endsWith(".gif")) {
                Glide.with(context)
                        .load(path).asGif()
                        .centerCrop().diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(com.yancy.imageselector.R.mipmap.imageselector_photo)
                        .into(imageView);
            } else {
                Glide.with(context)
                        .load(path)
                        .centerCrop()
                        .placeholder(com.yancy.imageselector.R.mipmap.imageselector_photo)
                        .into(imageView);
            }
        }
    }

    private void setSendPicCount(int select) {
        sendPicCount.setText("已选" + select + "张还剩" + (9 - select) + "张可选择");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_TAG && resultCode == RESULT_OK) {
            tagName = data.getStringExtra(ChooseTagActivity.KEY_DATA);
            tagId = data.getStringExtra(ChooseTagActivity.KEY_DATA_ID);
            tvTag.setText(tagName);
        } else if (requestCode == REQUEST_CODE_CHOOSE_PHOTE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            handleChoosePhone(pathList);
        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            selectedPicPath.add(picPath);
            galleryAdapter.add(picPath);
            gallery_layout.setVisibility(View.VISIBLE);
            sendEditOuterLayout.setVisibility(View.VISIBLE);
        } else {
            if (selectedPicPath != null && selectedPicPath.size() != 0) {
                sendEditOuterLayout.setVisibility(View.VISIBLE);
                gallery_layout.setVisibility(View.VISIBLE);
                emojiOuterLayout.setVisibility(View.GONE);
            } else {
                gallery_layout.setVisibility(View.GONE);
                sendEditOuterLayout.setVisibility(View.GONE);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleChoosePhone(List<String> pathList) {
        selectedPicPath.clear();
        if (pathList == null || pathList.size() == 0) {
            gallery_layout.setVisibility(View.GONE);
            sendEditOuterLayout.setVisibility(View.GONE);
            return;
        }

        galleryAdapter.addAll(pathList);
        selectedPicPath.addAll(pathList);
        gallery_layout.setVisibility(View.VISIBLE);
        sendEditOuterLayout.setVisibility(View.VISIBLE);
    }

    public void hideSoftKey(EditText editText) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void doSend() {
        final ArticleEntry args = getArticleEntry();
        if (TextUtils.isEmpty(args.title)) {
            CHToast.show(this, "请输入标题");
            return;
        }
        if (TextUtils.isEmpty(args.tag)) {
            CHToast.show(this, "请选择标签");
            return;
        }
        if (TextUtils.isEmpty(args.content)) {
            CHToast.show(this, "请输入要发表的内容");
            return;
        }

        if (args.title.length() > 30) {
            CHToast.show(this, "标题30汉字以内");
            return;
        }

        if (args.content.length() < 10) {
            CHToast.show(this, "内容不低于10汉字");
            return;
        }

        if (args.content.length() > 10000) {
            CHToast.show(this, "内容超过10000汉字");
            return;
        }

        if (selectedPicPath == null || selectedPicPath.size() == 0) {
            doPublish(args);
        } else {
            final LoadingDialog dialog = new LoadingDialog(this, "");
            dialog.show();

            publishPicsHelper = new PublishPicsHelper(this, new BaseLogic.DataLogicListner<List<String>>() {
                @Override
                public void failed(String errorMsg, int errorCode) {
                    dialog.dismiss();
                    CHToast.show(SendEditActivity.this, "图片上传失败,请重试:" + errorMsg);
                }

                @Override
                public void success(List<String> entryResult) {
                    dialog.dismiss();
                    args.pics = entryResult;
                    doPublish(args);
                    deleteSaveFile();
                }
            });
            publishPicsHelper.post(selectedPicPath);
        }
    }

    private void deleteSaveFile() {
        CacheManager.deleteObject(AppContext.getAppContext(), ARTICLE_SAVE_FILE + forumId);
    }

    private ArticleEntry getArticleEntry() {
        ArticleEntry args = new ArticleEntry();
        args.title = sendEditTitle.getText().toString().trim();
        args.content = sendContent.getText().toString();
        args.tag = tagId;
        args.savePics = selectedPicPath;
        args.tagName = tagName;
        args.forumId = forumId;
        return args;
    }

    private void doPublish(ArticleEntry args) {
        final LoadingDialog dialog = new LoadingDialog(this, "");
        dialog.show();
        PublishArticleLogic.post(args, new BaseLogic.DataLogicListner() {
            @Override
            public void failed(String errorMsg, int errorCode) {
                dialog.dismiss();
                CHToast.show(SendEditActivity.this, errorMsg);
            }

            @Override
            public void success(Object entryResult) {
                dialog.dismiss();
                CHToast.show(SendEditActivity.this, "贴子发表成功");
                if (publishPicsHelper != null) {
                    publishPicsHelper.clearCache();
                }
                if (toBBS) {
                    setResult(RESULT_CODE_FOR_BBS);
                }
                AnalyticsHome.umOnEvent(AnalyticsHome.FORUM_PUBLISH, "编辑界面的发布按钮并成功");
                finish();
            }
        });
    }

    class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
        private Context context;
        private LayoutInflater inflater;
        private List<String> list;

        public GalleryAdapter(List<String> list, Context context) {
            this.list = list;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.ch_send_recycler_item, parent, false);
            return new GalleryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(GalleryViewHolder holder, int position) {
            int itemCount = list.size();
            LogUtil.errorLog("itemCount = " + itemCount + "position = " + position);
            holder.topImage.setOnClickListener(null);
            if (itemCount < 9) {
                if (itemCount == position) {
                    holder.topImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            imageConfigAndOpen();
                        }
                    });
                    holder.topImage.setImageResource(R.drawable.ch_add_img);
                    holder.removeImage.setVisibility(View.GONE);
                } else {
                    showTopImage(holder, position);
                }
            } else if (itemCount == 9) {
                showTopImage(holder, position);
            }
        }

        private void showTopImage(GalleryViewHolder holder, final int position) {
            holder.removeImage.setVisibility(View.VISIBLE);
            holder.removeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPicPath.remove(list.get(position));
                    list.remove(position);
                    setSendPicCount(list.size());
                    notifyDataSetChanged();
                }
            });
            LogUtil.errorLog("GalleryAdapter list.get(position): " + list.get(position));
            Glide.with(context)
                    .load(list.get(position))
                    .centerCrop()
                    .placeholder(com.yancy.imageselector.R.mipmap.imageselector_photo)
                    .into(holder.topImage);
        }

        public void addAll(Collection collection) {
            if (list != null) {
                list.clear();
                list.addAll(collection);
                setSendPicCount(list.size());
                notifyDataSetChanged();
            }
        }

        public void add(String str) {
            if (list != null) {
                list.add(str);
                setSendPicCount(list.size());
                notifyDataSetChanged();
            }
        }

        @Override
        public int getItemCount() {
            if (list == null) {
                return 0;
            } else if (list.size() < 9) {
                return list.size() + 1;
            } else {
                return list.size();
            }
        }

        class GalleryViewHolder extends RecyclerView.ViewHolder {

            private ImageView removeImage;
            private ImageView topImage;

            public GalleryViewHolder(View itemView) {
                super(itemView);
                topImage = (ImageView) itemView.findViewById(R.id.ch_send_item_image_top);
                removeImage = (ImageView) itemView.findViewById(R.id.ch_send_item_remove);
            }
        }
    }

    class EmojiPagerAdapter extends PagerAdapter {
        List<View> list;

        public EmojiPagerAdapter(List<View> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        public void addAll(Collection collection) {
            if (list != null) {
                list.addAll(collection);
                notifyDataSetChanged();
            }
        }
    }

    public static void lauch(Activity activity, String forumId) {
        Intent intent = new Intent(activity, SendEditActivity.class);
        intent.putExtra(KEY_DATA, forumId);
        activity.startActivity(intent);
    }

    public static void launchForBBS(Activity activity, String forumId, boolean toBBS) {
        Intent intent = new Intent(activity, SendEditActivity.class);
        intent.putExtra(KEY_DATA, forumId);
        intent.putExtra(KEY_TO_BBS, toBBS);
        activity.startActivityForResult(intent, REQUSET_CODE_FOR_BBS);
    }
}
