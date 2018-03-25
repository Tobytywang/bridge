package com.happylich.bridge.engine.widget;

import android.graphics.Bitmap;

/**
 * Created by wangt on 2018/3/22.
 * 精灵代表一个具有绘制和消费点击事件的单元的抽象
 */

public class Sprite {

    // 精灵的位置
    private int[] position = new int[4];
    // 精灵的图像
    private Bitmap bitmap;

    public Sprite(int[] position, Bitmap bitmap) {
        this.position = position;
        this.bitmap   = bitmap;
    }

    // 精灵的绘制功能（抽象方法，需要被重写）
    public void draw() {
        throw new RuntimeException("Stub!");
    }

    /**
     * 精灵消费触摸事件
     * @param position
     * @return
     */
    public boolean onTouch(int[] position) {
        if (position[0] > this.position[0] && position[1] < this.position[1]
                && position[2] > this.position[2] && position[3] < this.position[3]) {
            return true;
        }
        return false;
    }
}
