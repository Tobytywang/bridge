package com.happylich.bridge.tool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.happylich.bridge.game.Manager;

/**
 * Created by wangt on 2017/11/11.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    boolean threadFlag = true;
    Manager manager;
    Context context;
    SurfaceHolder holder;
    Canvas canvas;
    Bitmap backgroundBitmap;

    public GameView(Context context) {
        super(context);
        this.context = context;
        Manager manager = new Manager(context);
//        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.game_bg);
        this.getHolder().addCallback(this);
        this.setOnTouchListener(this);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // threadFlag = true;
        // gameThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // threadFlag = false;
        boolean retry = true;
        while(retry) {
//            try {
//                //gameThread.join();
//                retry = false;
//            } catch (InterruptedException e) {
//            }
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) {
            return true;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        // Manager.onTouch(x, y);
        return true;
    }
}
