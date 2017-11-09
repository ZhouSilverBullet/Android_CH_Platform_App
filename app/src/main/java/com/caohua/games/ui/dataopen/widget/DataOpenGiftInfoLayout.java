package com.caohua.games.ui.dataopen.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.biz.dataopen.DataOpenSendGiftLogic;
import com.caohua.games.biz.dataopen.entry.DataOpenGiftInfoEntry;
import com.caohua.games.ui.dataopen.DataExistCallback;
import com.chsdk.biz.BaseLogic;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.Base64PicUtil;

/**
 * Created by admin on 2017/9/26.
 */

public class DataOpenGiftInfoLayout extends RelativeLayout implements DataExistCallback {
    private Context context;
    private TextView title;
    private ImageView image;
    private TextView des;
    private Button btn;
    private DataOpenGiftInfoEntry infoEntry;

    public DataOpenGiftInfoLayout(Context context) {
        this(context, null);
    }

    public DataOpenGiftInfoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DataOpenGiftInfoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.ch_view_data_gift_info_layout, this, true);
        setVisibility(GONE);
        title = (TextView) findViewById(R.id.ch_view_data_gift_info_title);
        image = (ImageView) findViewById(R.id.ch_view_data_gift_info_image);
        des = (TextView) findViewById(R.id.ch_view_data_gift_info_des);
        btn = (Button) findViewById(R.id.ch_view_data_gift_info_submit);
    }

    public void setData(final DataOpenGiftInfoEntry entry, final String managerGameId) {
        if (entry != null) {
            this.infoEntry = entry;
            title.setText(entry.title);
            if (!TextUtils.isEmpty(entry.icon)) {
                Bitmap bitmap = Base64PicUtil.stringToBitmap(entry.icon);
                if (bitmap != null) {
                    image.setImageBitmap(bitmap);
                }
            }
            des.setText(entry.item_name);
            int i = stringToInt(entry.status);
            if (i == 0) {
                btn.setText("领取");
                btn.setBackgroundResource(R.drawable.download_button_shape_downloading);
            } else {
                btn.setText("已领取");
                btn.setBackgroundResource(R.drawable.download_button_shape_error);
            }
//            image  -->base64还是什么
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final LoadingDialog loadingDialog = new LoadingDialog(context, "");
                    loadingDialog.show();
                    new DataOpenSendGiftLogic().openSendGet(managerGameId, infoEntry.item_id, infoEntry.item_type, new BaseLogic.DataLogicListner<String>() {
                        @Override
                        public void failed(String errorMsg, int errorCode) {
                            loadingDialog.dismiss();
                            CHToast.show(context, errorMsg);
                        }

                        @Override
                        public void success(String entryResult) {
                            loadingDialog.dismiss();
                            CHToast.show(context, "领取成功！");
                            btn.setText("已领取");
                            btn.setBackgroundResource(R.drawable.download_button_shape_error);
                        }
                    });
                }
            });
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

    private int stringToInt(String value) {
        int i = 0;
        try {
            i = Integer.parseInt(value);
        } catch (Exception e) {
        }
        return i;
    }

    @Override
    public boolean existBack() {
        if (infoEntry != null) {
            return true;
        }
        return false;
    }
}
