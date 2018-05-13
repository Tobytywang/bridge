package com.happylich.bridge.engine.util;

import android.util.Log;

/**
 * 这个类方便判断位置
 * Created by lich on 2018/3/25.
 */

public class Position {

    public int top;
    public int left;
    public int bottom;
    public int right;

    /**
     * 设置参数
     */
    public void set(int top, int left, int bottom, int right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    /**
     * 默认构造函数
     */
    public Position() {

    }

    /**
     * 构造函数
     * @param top
     * @param left
     * @param bottom
     * @param right
     */
    public Position(int top, int left, int bottom, int right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    /**
     * 判断target是否在position里
     * @param x
     * @param y
     * @param position
     * @return
     */
    public static boolean inPosition(int x, int y, Position position) {
        if (y > position.top && y < position.bottom
                && x > position.left && x < position.right) {
            return true;
        }
        return false;
    }

    /**
     * 将position也按比例进行变换
     * @param coefficient
     */
    public void resieze(float coefficient) {
        this.top = (int)(this.top * coefficient);
        this.left = (int)(this.left * coefficient);
        this.bottom = (int)(this.bottom * coefficient);
        this.right = (int)(this.right * coefficient);
    }
}
