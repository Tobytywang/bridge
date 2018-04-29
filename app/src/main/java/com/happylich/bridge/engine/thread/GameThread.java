package com.happylich.bridge.engine.thread;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import com.happylich.bridge.engine.game.Game;

import java.util.Date;

/**
 * Created by wangt on 2018/3/20.
 * 线程负责游戏的绘制，触摸事件的响应等
 */

public class GameThread extends Thread {


    public Date d = null;

    // 游戏线程每执行一次需要睡眠的时间
    private final static int DELAY_TIME = 16;
    // 上下文，方便获取到应用的各项资源，如图片、音乐和字符串等
    private Context context;

    // 游戏类，可以调用游戏类的绘图方法
    private Game game;

    // 与Activity其他View交互用的handler
    private Handler handler;

    // 由SurfaceView提供的SurfaceHolder
    private SurfaceHolder surfaceHolder;

    // 游戏线程运行开关
    private boolean running = false;

    // 当前surface的高度，在SurfaceChanged方法中被设置
    private int mCanvasHeight = 1;
    // 当前Surface的宽度，在SurfaceChanged方法中被设置
    private int mCanvasWidth = 1;

    // 游戏是否被暂停
    private boolean isPaused = false;

    /**
     * 线程构造函数
     * @param holder  SurfaceHolder
     * @param context Context
     * @param handler handler
     */
    public GameThread(SurfaceHolder holder, Context context, Handler handler) {
        this.surfaceHolder = holder;
        this.context = context;
        this.handler = handler;
    }

    /**
     * 设置游戏类
     * @param game
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * 线程运行标志设置函数（设置为false后停止线程）
     * @param state
     */
    public void setRunning (boolean state) {
        Log.v(this.getClass().getName(), "设置running");
        running = state;
    }

    /**
     * 在初始化的时候用到
     * @param width
     * @param height
     */
    public void setSurfaceSize(int width, int height) {
        synchronized (surfaceHolder) {
            mCanvasHeight = width;
            mCanvasWidth = height;
            // 每次画布的宽高发生改变时，就在这里对图片等资源进行缩放等相关适配工作
        }
    }

    /**
     * 线程更新函数
     */
    public void run() {
        Log.v(this.getClass().getName(), "我要跑了！");
        while (running) {
            d = new Date();
            Canvas canvas = null;
            if (!isPaused) {
                game.process(canvas);
                Log.v(this.getClass().getName(), "process " + String.valueOf((new Date().getTime() - d.getTime())));
                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    synchronized (surfaceHolder) {
                        // 这段时间最好执行和绘图有关的操作，其他代码越少越好
                        game.draw(canvas);
                    }
                    Log.v(this.getClass().getName(), "dddd    " + String.valueOf((new Date().getTime() - d.getTime())));
                    if ((new Date().getTime() - d.getTime()) < DELAY_TIME) {
                        Thread.sleep(Math.max(0, DELAY_TIME - (new Date().getTime() - d.getTime())));
                        Log.v(this.getClass().getName(), "draw    " + String.valueOf((new Date().getTime() - d.getTime())));
                    } else {
                        Log.v(this.getClass().getName(), "draw----" + String.valueOf((new Date().getTime() - d.getTime())));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
        Log.v(this.getClass().getName(), "我不跑了！");
    }

    /**
     * 绘制函数（由开发者重载）
     * @param canvas
     */
    private void doDraw(Canvas canvas) {
        // Log.v(this.getClass().getName(), "doDraw");
        // TODO:
        canvas.drawColor(Color.WHITE);
        canvas.drawArc(0, 0, 100, 100, 0, 90, true, new Paint());

        canvas.save();
        canvas.scale(2f, 2f);
    }

    /**
     * 重新调整大小
     * @param canvas
     */
    private void resize(Canvas canvas) {

    }

}
