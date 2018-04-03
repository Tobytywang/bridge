package com.happylich.bridge.engine.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import com.happylich.bridge.engine.game.Game;
import com.happylich.bridge.engine.thread.GameThread;

/**
 * Created by wangt on 2018/3/20.
 * 1. 将SurfaceHolder交给主线程PlayThread，使得GameThread完成绘画工作，并管理线程的运行状态
 * 2. 捕获按键事件交给GameThread处理，如果有需要也可以捕捉其他事件交给GameThread处理
 * 3. 在需要时调用协助显示其他View，如框架中的TextView
 */

public class GameView extends SurfaceView
        implements SurfaceHolder.Callback {

    private GameThread gameThread;
    private SurfaceHolder holder;
    private Context context;

    private Game game;

    /**
     * 构造方法（自定义的）
     * @param context 上下文
     * @param game 游戏
     */
    public GameView(Context context, Game game) {
        super(context);

        this.holder = getHolder();
        holder.addCallback(this);

        this.context = context;

        this.game = game;
    }

    /**
     * 构造方法（单参数）
     * @param context 上下文
     */
    public GameView(Context context) {
        super(context);

        this.holder = getHolder();
        holder.addCallback(this);

        this.context = context;

        this.game = new Game();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // TODO:
                Log.v(this.getClass().getName(), "按下事件");
                game.onTouch((int)event.getX(), (int)event.getY());
            default:
//                Log.v(this.getClass().getName(), "其他事件");
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(this.getClass().getName(), "创建Surface");

        gameThread = new GameThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
                // TODO:
            }
        });
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(this.getClass().getName(), "改变Surface");

        // 在这里创建游戏
        // TODO:把新建游戏的过程延迟到这里执行，就可以解决宽高的问题
        game.setWidthHeight(this.getWidth(), this.getHeight());
        Log.v(this.getClass().getName(), "修改宽高");
        gameThread.setGame(game);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(this.getClass().getName(), "销毁Surface");

        gameThread.setRunning(false);
    }
}
