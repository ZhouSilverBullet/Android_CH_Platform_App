package com.chsdk.utils;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class BPUtil {
    private static final char[] CHARS = {
            '0', '1', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'};
    private static BPUtil bputil;

    public static BPUtil getInstance() {
        if (bputil == null) {
            bputil = new BPUtil();
        }
        return bputil;
    }

    private static final int DEFAULT_CODE_LENGTH = 4;//默认验证码的长度
    private static final int DEFAULT_FONT_SIZE = 24;//默认字体大小
    private static final int DEFAULT_LINE_NUMBER = 3;//默认线的数量
    private static final int BASE_PADDING_LEFT = 10, //左边距
            RANGE_PADDING_LEFT = 10,//左边距范围值
            BASE_PADDING_TOP = 15, //上边距
            RANGE_PADDING_TOP = 15;  //上边距范围值
    private static final int DEFAULT_WIDTH = 80,//默认宽度，图片的总宽度
            DEFAULT_HEIGHT = 35;//默认宽、高
    private int width = DEFAULT_WIDTH, height = DEFAULT_HEIGHT;
    private int base_padding_left = BASE_PADDING_LEFT,
            range_padding_left = RANGE_PADDING_LEFT,
            base_padding_top = BASE_PADDING_TOP,
            range_padding_top = RANGE_PADDING_TOP;
    private int codeLength = DEFAULT_CODE_LENGTH,
            line_number = DEFAULT_LINE_NUMBER,
            font_size = DEFAULT_FONT_SIZE;
    private String code;
    private int padding_left, padding_top;
    private Random random = new Random(); //生成一个可以产生随机变量的对象

    public Bitmap createBitmap() {  //创建bitmap
        padding_left = 0;

        Bitmap bp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas c = new Canvas(bp);

        code = createCode();

        c.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setTextSize(font_size);  //设置字体大小

        for (int i = 0; i < code.length(); i++) {
            randomTextStyle(paint);
            randomPadding();
            c.drawText(code.charAt(i) + "", padding_left, padding_top, paint);
        }

        for (int i = 0; i < line_number; i++) {
            drawLine(c, paint);
        }

        c.save(Canvas.ALL_SAVE_FLAG);//保存
        c.restore();//
        return bp;
    }

    public String getCode() {  //从登陆界面是获得生成的验证码的数字，返回的是字符型
        return code;
    }

    //生成随机数
    private String createCode() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            buffer.append(CHARS[random.nextInt(CHARS.length)]); //利用数组站对应的位置来随机生成
            //StringBuilder将文本或对象的字符串表示形式添加到由当前 StringBuilder 对象表示的字符串的结尾处。
        }
        return buffer.toString();  //返回时将buffer转换成string类型
    }

    private void drawLine(Canvas canvas, Paint paint) {
        int color = randomColor();
        int startX = random.nextInt(width);
        int startY = random.nextInt(height);
        int stopX = random.nextInt(width);
        int stopY = random.nextInt(height);
        paint.setStrokeWidth(1); //设置线宽
        paint.setColor(color);  //给字体附加颜色
        canvas.drawLine(startX, startY, stopX, stopY, paint);  //画图开始的位置
    }

    private int randomColor() {
        return randomColor(1);
    }

    //随机生成的颜色
    private int randomColor(int rate) {  //这里面用到的颜色是根据这个红绿色蓝色
        int red = random.nextInt(256) / rate;  //通过随机生成的0-255之间的数字，这些数字再通过16进制转化，例如红色#ff0000就是是红色255，蓝色绿色0000
        int green = random.nextInt(256) / rate;  //刚好rate传的是一个1，就是16进制的
        int blue = random.nextInt(256) / rate;
        return Color.rgb(red, green, blue);
    }

    private void randomTextStyle(Paint paint) {
        int color = randomColor(); //颜色随机生成 颜色
        paint.setColor(color);  //对字体设置生成的颜色
        paint.setFakeBoldText(random.nextBoolean());  //true为粗体，false为非粗体
        float skewX = random.nextInt(11) / 10;
        skewX = random.nextBoolean() ? skewX : -skewX; //三目运算符如果random随机生成为true的话就是skeyX，否则-skewX
        paint.setTextSkewX(skewX); //float类型参数，负数表示右斜，整数左斜
//      paint.setUnderlineText(true); //true为下划线，false为非下划线
//      paint.setStrikeThruText(true); //true为删除线，false为非删除线
    }

    private void randomPadding() {
        padding_left += base_padding_left + random.nextInt(range_padding_left);
        padding_top = base_padding_top + random.nextInt(range_padding_top);
    }
}
