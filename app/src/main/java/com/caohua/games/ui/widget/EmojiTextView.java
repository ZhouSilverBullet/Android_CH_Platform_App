package com.caohua.games.ui.widget;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.caohua.games.ui.emoji.FaceConversionUtil;

/**
 * Created by zhouzhou on 2017/6/8.
 */

public class EmojiTextView extends TextView {
    public EmojiTextView(Context context) {
        this(context, null);
    }

    public EmojiTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmojiTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    public void setEmojiText(String content) {
//        SpannableString spannableString = FaceConversionUtil.getInstance().getExpressionString(getContext(), content);
//        setText(spannableString);
//    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (!TextUtils.isEmpty(text)) {
            SpannableString spannableString = FaceConversionUtil.getInstance().getExpressionString(getContext(), (String) text);
            super.setText(spannableString, type);
        } else {
            super.setText(text, type);
        }
    }
}
