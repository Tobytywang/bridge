package com.happylich.bridge.engine.widget;

import android.graphics.Bitmap;

/**
 * Created by wangt on 2018/3/22.
 */

public class SpriteGroup extends Sprite {

    // 维护的子精灵队列
//    private

    /**
     * 构造函数
     * @param position
     * @param bitmap
     */
    public SpriteGroup(int[] position, Bitmap bitmap) {
        super(position, bitmap);
        throw new RuntimeException("Stub!");
    }

    /**
     * 交给子精灵负责消费，精灵组不消费触摸事件
     * @param position
     * @return
     */
    public boolean onTouch(int[] position) {

        return false;
    }
}
