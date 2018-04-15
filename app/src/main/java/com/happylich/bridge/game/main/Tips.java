package com.happylich.bridge.game.main;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.TextView;

/**
 * Created by lich on 2018/4/10.
 * 对显示Tips的包装
 */

public class Tips {

    /**
     *
     */
    public Tips() {

    }

    /**
     * 绘制局况信息
     */
    public static void drawMessage() {
        Bitmap Image;
        Rect des = new Rect();
        Paint paint = new Paint();

        // 局数：有局无局
        // 定约：约定和庄家
        // 墩数：
    }

    /**
     * 绘制Tips
     * @param string
     */
    public static void drawText(String string){
        // 360 = 80 * 4 + 8 * 5
        // 过长的情况下如何换行
        Bitmap Image;
        Rect des = new Rect();
        Paint paint = new Paint();
    }

}
