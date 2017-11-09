package com.chsdk.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.chsdk.utils.ViewUtil;

/**
 * Created by admin on 2017/10/19.
 */

public class MaxHeightScrollView extends ScrollView {
    private Context mContext;

    public MaxHeightScrollView(Context context) {
        super(context);
        init(context);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            //最大高度显示为150dp
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(ViewUtil.dp2px(mContext, 150), MeasureSpec.AT_MOST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //重新计算控件高、宽
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
