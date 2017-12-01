package com.happylich.bridge.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.happylich.bridge.R;
import com.happylich.bridge.game.Manager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.view.MotionEvent.ACTION_DOWN;

/**
 * Created by wangt on 2017/10/27.
 * KeyView：自定义View实现
 * PlayView：自定义View实现
 * GameView：SurfaceView实现
 * BridgeView：ViewGroup实现
 */

// TODO: 先实现随机发牌的功能
// 1. PlayView负责除游戏逻辑之外的系统逻辑
// 2. Manager负责发牌逻辑
// TODO：实现选中打出功能
// 1. 选中事件负责传递信息，这些信息传递到Manager，由Manager负责逻辑（cards）和绘制（canvas)
public class PlayView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    // SurfaceView
    private Canvas canvas;
    private SurfaceHolder holder;
    // GameManager
    private Manager manager;
    // Thread
    private boolean running;
    private Thread renderThread = null;

    /**
     * 构造函数
     * @param context
     * @param attrs
     */
    public PlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);
        manager = new Manager(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    /**
     * 创建SurfaceView
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * 修改SurfaceView
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
    }

    /**
     * 销毁SurfaceView
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
    }

    /**
     * 界面绘制
     */
    protected void draw() {
        try {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                manager.process(canvas);
            }
        } catch (Exception e) {

        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * 事件处理（通过接口）
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == ACTION_DOWN) {
            manager.onTouch((int)event.getX(), (int)event.getY());
        }
        return true;
    }

    @Override
    public void run() {
        while(running) {
            draw();
        }
    }

}
