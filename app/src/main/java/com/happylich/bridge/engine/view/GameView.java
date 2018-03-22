package com.happylich.bridge.engine.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import com.happylich.bridge.engine.thread.PlayThread;

/**
 * Created by wangt on 2018/3/20.
 * 1. 将SurfaceHolder交给主线程PlayThread，使得PlayThread完成绘画工作，并管理线程的运行状态
 * 2. 捕获按键事件交给PlayThread处理，如果有需要也可以捕捉其他事件交给PlayThread处理
 * 3. 在需要时调用协助显示其他View，如框架中的TextView
 */

public class GameView extends SurfaceView
        implements SurfaceHolder.Callback {

    private PlayThread playThread;
    private SurfaceHolder holder;
    private Context context;

    private int Width, Height;
    /**
     * 构造方法
     * @param context 上下文
     * @param attrs   额外的参数
     */
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.holder = getHolder();
        holder.addCallback(this);

        this.context = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // TODO:
                Log.v(this.getClass().getName(), "按下事件");
            default:
                Log.v(this.getClass().getName(), "其他事件");
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(this.getClass().getName(), "创建Surface");

        playThread = new PlayThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
                // TODO:
            }
        });
        playThread.setRunning(true);
        playThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(this.getClass().getName(), "改变Surface");

        Width = this.getWidth();
        Height = this.getHeight();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(this.getClass().getName(), "销毁Surface");

        playThread.setRunning(false);
    }
}
