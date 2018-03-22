package com.happylich.bridge.game.view;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Created by wangt on 2018/3/19.
 */

public final class BitmapButton implements BitmapView {
    private onClickListener listener;
//    private final GameGraphics graphics;
//    private final int x;
//    private final int y;
//    private final LiveBitmap bkgNormal;
//    private final LiveBitmap bkgPressed;
//    private final LiveBitmap bitmapNormal;
//    private final LiveBitmap bitmapPressed;
    private boolean isPressed;

    /**
     * 构造一个没有按下效果的图片按钮
     */
//    public BitmapButton(GameGraphics graphics, int x, int y, LiveBitmap bitmap) {
//        this(graphics, x, y, bitmap, bitmap);
//    }

    /**
     * 构造一个具有按下效果的图片按钮
     */
//    public BitmapButton(GameGraphics graphics, int x, int y, LiveBitmap bitmapNormal, LiveBitmap bitmapPressed) {
//        super();
//        if (bitmapNormal == null || bitmapPressed == null) {
//            throw new NullPointerException("cannot construct a bitmap button using null bitmap");
//        }
//        this.graphics = graphics;
//        this.x = x;
//        this.y = y;
//        this.bitmapNormal = bitmapNormal;
//        this.bitmapPressed = bitmapPressed;
//        this.bkgNormal = Assets.getInstance().bitmapBtnBkg;
//        this.bkgPressed = Assets.getInstance().bitmapBtnBkgPressed;
//    }

    /**
     * 处理点击事件
     */
//    public final boolean onTouch(MappedTouchEvent event) {
//        if (inBounds(event)) {
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                isPressed = true;
//            }
//        } else if (event.getAction() == MotionEvent.ACTION_UP) {
//            isPressed = false;
//            if (listener != null) {
//                listener.onClicked(this);
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * onPaint
     */
    @Override
    public void onPaint(Canvas canvas) {
//        LiveBitmap btnBkg = isPressed ? bkgNormal : bkgPressed;
//        graphics.drawBitmap(canvas, btnBkg, x, y);
//        graphics.drawBitmapInParentCenter(canvas, isPressed ? bitmapPressed : bitmapNormal, graphics.getCenter(btnBkg, x, y));
    }

    /**
     * 判断事件是否在自己的区域中
     */
//    private boolean inBounds(MappedTouchEvent event) {
//        int width = bkgNormal.getRawWidth();
//        int height = bkgNormal.getRawHeight();
//        if (event.x > x && event.x < x + width - 1 &&
//                event.y > y && event.y < y + height - 1) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**
     * 处理点击事件
     */
    public interface onClickListener {
        void onClicked(BitmapButton btn);
    }

    /**
     * 设置监听器
     */
    public void setOnClickListener(onClickListener listener) {
        this.listener = listener;
    }
}

