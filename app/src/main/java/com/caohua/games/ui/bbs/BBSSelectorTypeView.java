package com.caohua.games.ui.bbs;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.caohua.games.R;
import com.caohua.games.biz.bbs.BBSTopEntry;
import com.chsdk.http.HttpConsts;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.RippleEffectButton;
import com.chsdk.utils.NetworkUtils;
import com.chsdk.utils.ViewUtil;

import java.util.List;

/**
 * Created by zhouzhou on 2017/5/25.
 */

public class BBSSelectorTypeView extends LinearLayout implements View.OnClickListener {
    public static final String TYPE_QUAN_BU = "type_quan_bu"; // 默认
    public static final String TYPE_JING_HUA = "type_jing_hua";
    public static final String TYPE_ZHI_DING = "type_zhi_ding";

    private RippleEffectButton typeText1;
    private RippleEffectButton typeText2;
    private RippleEffectButton typeText3;
    private RippleEffectButton typeText4;
    private int firstPosition = -1; // 选中后的值 默认为-1
    private Drawable img_off;
    private PopupWindow popupWindow;
    private GridAdapter adapter;
    private List<BBSTopEntry.DataBean.ForumTagBean> data;
    private Button submit;
    private String tagId;

    public BBSSelectorTypeView(Context context) {
        this(context, null);
    }

    public BBSSelectorTypeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BBSSelectorTypeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initEvent();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.ch_bbs_selector_type_layout, this, true);
        setBackgroundResource(R.color.ch_color_white);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        typeText1 = (RippleEffectButton) findViewById(R.id.ch_bbs_selector_type_text1);
        typeText2 = (RippleEffectButton) findViewById(R.id.ch_bbs_selector_type_text2);
        typeText3 = (RippleEffectButton) findViewById(R.id.ch_bbs_selector_type_text3);
        typeText4 = (RippleEffectButton) findViewById(R.id.ch_bbs_selector_type_text4);
        typeText1.setBackgroundResource(R.drawable.ch_prefecture_change_game_bg_selector);
        doText4LiftIcon(getResources().getColor(R.color.ch_color_download_downloading));
    }

    private void initEvent() {
        typeText1.setOnClickListener(this);
        typeText2.setOnClickListener(this);
        typeText3.setOnClickListener(this);
        typeText4.setOnClickListener(this);
    }

    private void showPop(View view) {
        if (popupWindow == null) {
            View inflate = View.inflate(getContext(), R.layout.ch_bbs_selector_type_pop_layout, null);
            submit = ((Button) inflate.findViewById(R.id.ch_bbs_selector_pop_button));
            submit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTagIdBackListener != null) {
                        if (!TextUtils.isEmpty(tagId)) {
                            reset();
                            typeText4.setBackgroundResource(R.drawable.ch_prefecture_change_game_bg_selector);
                            typeText4.setTextColor(getResources().getColor(R.color.ch_white));
                            doText4LiftIcon(getResources().getColor(R.color.ch_white));
                            onTagIdBackListener.onTagIdBackListener(tagId);
                            if (popupWindow != null) {
                                tagId = null;
                                popupWindow.dismiss();
                            }
                        } else {
                            CHToast.show(getContext(), "请选中一个，点确定");
                        }
                    }
                }
            });
            GridView gridView = (GridView) inflate.findViewById(R.id.ch_bbs_selector_pop_grid_view);
            adapter = new GridAdapter(data);
            gridView.setAdapter(adapter);
            popupWindow = new PopupWindow(inflate,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
        }
        popupWindow.showAsDropDown(view, 0, ViewUtil.dp2px(getContext(), 12));
    }

    private void doText4LiftIcon(int color) {
        img_off = new TintedBitmapDrawable(getResources(), R.drawable.ch_bbs_selector_type_button_right_icon, color);
        img_off.setBounds(0, 0, ViewUtil.dp2px(getContext(), 12), ViewUtil.dp2px(getContext(), 12));
        typeText4.setCompoundDrawables(img_off, null, null, null); //设置左图标
    }

    @Override
    public void onClick(View v) {
        if (!NetworkUtils.isNetworkConnected(getContext())) {
            CHToast.show(getContext(), HttpConsts.ERROR_NO_NETWORK);
            return;
        }
        switch (v.getId()) {
            case R.id.ch_bbs_selector_type_text1:
                reset();
                firstPosition = -1;
                if (onTagIdBackListener != null) {
                    onTagIdBackListener.onTagIdBackListener(TYPE_QUAN_BU);
                }
                typeText1.setBackgroundResource(R.drawable.ch_prefecture_change_game_bg_selector);
                typeText1.setTextColor(getResources().getColor(R.color.ch_white));
                break;
            case R.id.ch_bbs_selector_type_text2:
                reset();
                firstPosition = -1;
                if (onTagIdBackListener != null) {
                    onTagIdBackListener.onTagIdBackListener(TYPE_JING_HUA);
                }
                typeText2.setBackgroundResource(R.drawable.ch_prefecture_change_game_bg_selector);
                typeText2.setTextColor(getResources().getColor(R.color.ch_white));
                break;
            case R.id.ch_bbs_selector_type_text3:
                reset();
                firstPosition = -1;
                if (onTagIdBackListener != null) {
                    onTagIdBackListener.onTagIdBackListener(TYPE_ZHI_DING);
                }
                typeText3.setBackgroundResource(R.drawable.ch_prefecture_change_game_bg_selector);
                typeText3.setTextColor(getResources().getColor(R.color.ch_white));
                break;
            case R.id.ch_bbs_selector_type_text4:
                showPop(v);
                break;
        }
    }

    public void reset() {
        typeText1.setBackgroundResource(R.drawable.ch_bbs_selector_not_selector);
        typeText2.setBackgroundResource(R.drawable.ch_bbs_selector_not_selector);
        typeText3.setBackgroundResource(R.drawable.ch_bbs_selector_not_selector);
        typeText4.setBackgroundResource(R.drawable.ch_bbs_selector_not_selector);
        typeText1.setTextColor(getResources().getColor(R.color.ch_black));
        typeText2.setTextColor(getResources().getColor(R.color.ch_black));
        typeText3.setTextColor(getResources().getColor(R.color.ch_black));
        typeText4.setTextColor(getResources().getColor(R.color.ch_black));
        doText4LiftIcon(getResources().getColor(R.color.ch_color_download_downloading));
    }

    public void setData(List<BBSTopEntry.DataBean.ForumTagBean> data) {
        this.data = data;
    }

    public List<BBSTopEntry.DataBean.ForumTagBean> getData() {
        return data;
    }

    class GridAdapter extends BaseAdapter {
        private List<BBSTopEntry.DataBean.ForumTagBean> data;
        int lastCurrentPosition;  //最终的位置

        public GridAdapter(List<BBSTopEntry.DataBean.ForumTagBean> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            if (convertView == null) {
                convertView = new BBSRadioGridItemView(getContext());
            }
            BBSRadioGridItemView itemView = (BBSRadioGridItemView) convertView;
            if (position == firstPosition) {
                itemView.doItemSelector();
                tagId = data.get(firstPosition).getTag_id();
            } else {
                itemView.doItemNotSelector();
            }
            itemView.setData(position, data.get(position));
            itemView.setOnRadioItemClickListener(new BBSRadioGridItemView.OnRadioItemClickListener() {
                @Override
                public void onItemClick(int position, String tagId) {
                    if (NetworkUtils.isNetworkConnected(getContext())) {
                        GridView gridView = ((GridView) parent);
                        ((BBSRadioGridItemView) gridView.getChildAt(lastCurrentPosition)).doItemNotSelector();
                        ((BBSRadioGridItemView) gridView.getChildAt(position)).doItemSelector();

                        lastCurrentPosition = position;
                        firstPosition = position;
                        BBSSelectorTypeView.this.tagId = tagId;
                    } else {
                        CHToast.show(getContext(), HttpConsts.ERROR_NO_NETWORK);
                    }
                }
            });
            return convertView;
        }
    }

    private OnTagIdBackListener onTagIdBackListener;

    public void setOnTagIdBackListener(OnTagIdBackListener onTagIdBackListener) {
        this.onTagIdBackListener = onTagIdBackListener;
    }

    public interface OnTagIdBackListener {
        void onTagIdBackListener(String tagId);
    }

}
