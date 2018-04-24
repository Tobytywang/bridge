package com.happylich.bridge.game.Scene;

import android.graphics.Canvas;

/**
 * Created by lich on 2018/4/24.
 */

public abstract class AbstractScene {

    // 绘制尺寸
    protected int width, height;
    protected int left, top;

    /**
     * 设置绘图基准点
     * @param position
     */
    public void setPosition(int[] position) {
        this.left = position[0];
        this.top = position[1];
    }

    /**
     * 设置宽高
     */
    public void setWidthHeight(int width, int height) {
        this.width = width;
        this.height = height;
    }


    abstract public int onTouch(int x, int y);

    abstract public void draw(Canvas canvas);
}
