package com.happylich.bridge.engine.game;

/**
 * 该类作为框架提供的一个内容，将GameView和GameThread中与业务无关的部分进行抽象
 * Created by lich on 2018/3/25.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.happylich.bridge.engine.util.Position;

/**
 * 该类提供一个默认的游戏行为
 * 屏幕上显示触摸的坐标
 */
public class Game {

    // fps
    protected int fps;

    // 画布
    protected Canvas canvas;

    // 画布高度和宽度（与屏幕一样）
    protected int width;
    protected int height;

    // 压点
    private int x;
    private int y;

    /**
     * 构造函数
     */
    public Game() {
    }

    /**
     * 设置宽高
     * @param width 宽度
     * @param height 高度
     */
    public void setWidthHeight(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getFPS() {
        return fps;
    }

    /**
     * 判断触摸事件，子类重载这个函数来处理输入事件
     * 按压事件改编状态，具体状态如何变化，交给draw去响应
     * @param x
     * @param y
     */
    public void onTouch(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 子类重载这个函数来绘制游戏界面
     * @param canvas
     */
    public void process(Canvas canvas) {

    }

    /**
     *
     */
    public void draw(Canvas canvas) {
        this.canvas = canvas;

        // 新建画笔
        Paint paint = new Paint();

        // 抹除背景
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, this.width, this.height, paint);

        // 绘制：Canvas大小
        paint.setColor(Color.BLACK);
        paint.setTextSize(60);
        canvas.drawText("Canvas大小：" + String.valueOf(this.height) + "*"
                + String.valueOf(this.width), 0, 60, paint);

        // 绘制：触点大小
        paint.setColor(Color.BLACK);
        paint.setTextSize(60);
        canvas.drawText("触点位置：" + String.valueOf(y) + "*"
                + String.valueOf(x), 0, 120, paint);
    }
}
