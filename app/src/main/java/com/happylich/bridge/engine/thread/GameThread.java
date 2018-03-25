package com.happylich.bridge.engine.thread;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by wangt on 2018/3/20.
 * 线程负责游戏的绘制，触摸事件的响应等
 */

public class GameThread extends Thread {

    // 游戏线程每执行一次需要睡眠的时间
    private final static int DELAY_TIME = 100;
    // 上下文，方便获取到应用的各项资源，如图片、音乐和字符串等
    private Context context;

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


    public void pause() {
        synchronized (surfaceHolder) {
            isPaused = true;
        }
    }

    public void unPause() {
        synchronized (surfaceHolder) {
            isPaused = false;
        }
    }

    public void restoreState(Bundle saveState) {
        // TODO
    }

    public void saveState(Bundle outState) {
        // TODO
    }

    public void setRunning (boolean b) {
        Log.v(this.getClass().getName(), "设置running");
        running = b;
    }

    public void setSurfaceSize(int width, int height) {
        synchronized (surfaceHolder) {
            mCanvasHeight = width;
            mCanvasWidth = height;
            // 每次画布的宽高发生改变时，就在这里对图片等资源进行缩放等相关适配工作
        }
    }

    public void run() {
        Log.v(this.getClass().getName(), "我要跑了！");
        while (running) {
            Canvas canvas = null;
            if (!isPaused) {
                try {
                    canvas = surfaceHolder.lockCanvas(null);
                    if (canvas == null) {
                        Log.v(this.getClass().getName(), "空的canvas");
                    }
                    synchronized (surfaceHolder) {
                        doDraw(canvas);
                    }
                    logic();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

                try {
                    Thread.sleep(DELAY_TIME);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.v(this.getClass().getName(), "我不跑了！");
    }

    public void logic() {
//        Log.v(this.getClass().getName(), "logic");
        // TODO:
    }

    public void doStart() {
        // TODO:
    }

    private void doDraw(Canvas canvas) {
//        Log.v(this.getClass().getName(), "doDraw");
        // TODO:
        canvas.drawColor(Color.WHITE);
        canvas.drawArc(0, 0, 100, 100, 0, 90, true, new Paint());

        canvas.save();
        canvas.scale(2f, 2f);
    }

}
