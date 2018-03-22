package com.happylich.bridge.game.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.AttributeSet;

import com.happylich.bridge.game.engine.Manager;

/**
 * Created by wangt on 2017/10/27.
 */
public class PlayView extends SurfaceView
        implements SurfaceHolder.Callback, Runnable {

    // SurfaceView需要维持的类(I)
    private Canvas canvas = null;
    private SurfaceHolder holder = null;
    private boolean isRunning;
    private Thread  renderThread = null;

    // SurfaceView需要维持的类(II)

    // GameManager负责游戏管理
    private Manager manager;

    // 宽高
    private int screenW;
    private int screenH;

    /**
     * 构造函数
     * @param context
     */
    public PlayView(Context context, AttributeSet attrs) {
        // 这里的两个参数是必要的，否则会引起bug
        super(context, attrs);

        // 获取Holder
        holder = getHolder();
        // 添加回调
        holder.addCallback(this);
        // 新建线程
        renderThread = new Thread(this);
        // 设置常量
        // 确保能捕获到KeyEvent
        setFocusable(true);
        // setFocusableInTouchMode(true);
        // setKeepScreenOn(true);

        // 新建渲染进程
        // 新建游戏逻辑控制器
        manager = new Manager(context);
    }


    /**
     * 创建SurfaceView
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenW = this.getWidth();
        screenH = this.getHeight();

        // initGame():调用游戏初始化函数

        // 初始化线程
        isRunning = true;
        renderThread.start();
    }

    /**
     * 修改SurfaceView
     * @param holder
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // 画布发生变化
        // 例子：旋转屏幕，处理画布操作
    }

    /**
     * 销毁SurfaceView
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
        renderThread = null; // GC会及时发现并处理掉该对象
    }

    /**
     * 事件处理（通过接口）
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                manager.onTouch((int)event.getX(), (int)event.getY());
            default:
                Log.v(this.getClass().getName(), "其他事件");
        }
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            manager.onTouch((int)event.getX(), (int)event.getY());
//        }
        /**
         * 一定要将return super.onTouchEvent(event)修改为return true
         * 1): 父类的onTouchEvent(event)方法可能没有做任何处理，但是返回了false
         * 2): 一旦返回false，在该方法中就不会收到MotionEvent.ACTION_MOVE及MotionEvent.ACTION_UP事件
         */
        return true;
    }

    @Override
    public void run() {
        // 不断进行绘制
        // 每次绘制结束后休眠100ms
        while(isRunning) {
            try {
                draw();
                renderThread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 界面绘制
     */
    protected void draw() {
        try {
            // 锁定canvas实例
            canvas = holder.lockCanvas();
            if (canvas != null) {
                manager.process(canvas);
            }
        } catch (Exception e) {

        } finally {
            // 释放canvas实例
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

}
