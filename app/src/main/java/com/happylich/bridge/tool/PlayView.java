package com.happylich.bridge.tool;

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

import com.happylich.bridge.R;
import com.happylich.bridge.game.Manager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

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
public class PlayView extends View {
    private Manager manager;
    private Context context;
    private Bitmap bitmap;

    /**
     * 构造函数
     * @param context
     * @param attrs
     */
    public PlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        manager = new Manager(context);
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_club_1);
//        bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_club_1);
//        bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_club_2);
//        bitmap3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_club_3);
//        bitmap4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_club_4);
//        bitmap5 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_club_5);
//        bitmap6 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_club_6);
//        bitmap7 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_club_7);
//        bitmap8 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_club_8);
//        bitmap9 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_club_9);
//        bitmap10 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_club_10);
//        bitmap11 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_club_j);
//        bitmap12 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_club_q);
//        bitmap13 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_club_k);
//
//        bitmap14 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_diamond_1);
//        bitmap15 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_diamond_2);
//        bitmap16 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_diamond_3);
//        bitmap17 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_diamond_4);
//        bitmap18 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_diamond_5);
//        bitmap19 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_diamond_6);
//        bitmap20 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_diamond_7);
//        bitmap21 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_diamond_8);
//        bitmap22 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_diamond_9);
//        bitmap23 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_diamond_10);
//        bitmap24 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_diamond_j);
//        bitmap25 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_diamond_q);
//        bitmap26 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_diamond_k);
//
//        bitmap27 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_heart_1);
//        bitmap28 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_heart_2);
//        bitmap29 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_heart_3);
//        bitmap30 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_heart_4);
//        bitmap31 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_heart_5);
//        bitmap32 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_heart_6);
//        bitmap33 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_heart_7);
//        bitmap34 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_heart_8);
//        bitmap35 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_heart_9);
//        bitmap36 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_heart_10);
//        bitmap37 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_heart_j);
//        bitmap38 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_heart_q);
//        bitmap39 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_heart_k);
//
//        bitmap40 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_spade_1);
//        bitmap41 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_spade_2);
//        bitmap42 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_spade_3);
//        bitmap43 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_spade_4);
//        bitmap44 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_spade_5);
//        bitmap45 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_spade_6);
//        bitmap46 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_spade_7);
//        bitmap47 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_spade_8);
//        bitmap48 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_spade_9);
//        bitmap49 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_spade_10);
//        bitmap50 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_spade_j);
//        bitmap51 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_spade_q);
//        bitmap52 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lord_card_spade_k);
    }

    /**
     * 界面绘制
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            manager.processDraw(canvas);
        } catch (Exception e) {
            throw e;
        }
//        HashSet set1 = new HashSet();
//        HashSet set2 = new HashSet();
//        HashSet set3 = new HashSet();
//        HashSet set4 = new HashSet();
//        for (int i=1; i<=52; i++) {
//            Random rand = new Random();
//            int randNum = rand.nextInt(4) + 1;
//            while(randNum != 0) {
//                if (randNum == 1 && set1.size() <13) {
//                    set1.add(i);
//                } else if (randNum == 2 && set2.size() <13) {
//                    set2.add(i);
//                } else if (randNum == 3 && set3.size() <13) {
//                    set3.add(i);
//                } else if (randNum == 4 && set4.size() <13) {
//                    set4.add(i);
//                }
//                randNum = 0;
//            }
//        }
        Rect rect0 = new Rect(270, 260, 430, 460);
        Rect rect1 = new Rect(0, 600, 160, 800);
        Rect rect2 = new Rect(55, 640, 215, 840);
        Rect rect3 = new Rect(110, 640, 270, 840);
        Rect rect4 = new Rect(165, 640, 325, 840);
        Rect rect5 = new Rect(220, 640, 380, 840);
        Rect rect6 = new Rect(275, 640, 435, 840);
        Rect rect7 = new Rect(330, 640, 490, 840);
        Rect rect8 = new Rect(385, 640, 545, 840);
        Rect rect9 = new Rect(440, 640, 600, 840);
        Rect rect10 = new Rect(495, 640, 655, 840);
        Rect rect11 = new Rect(550, 640, 710, 840);
        Rect rect12 = new Rect(605, 640, 765, 840);
        Rect rect13 = new Rect(660, 640, 820, 840);
////        canvas.drawBitmap(bitmap, new Matrix(), new Paint());
////        for(Iterator it = set1.iterator(); it.hasNext();)
////        {
//////            System.out.println(it.next());
//////            canvas.drawBitmap()
////        }
//        canvas.drawBitmap(bitmap, null, rect0, new Paint());
//        canvas.drawBitmap(bitmap, null, rect1, new Paint());
//        canvas.drawBitmap(bitmap, null, rect2, new Paint());
//        canvas.drawBitmap(bitmap, null, rect3, new Paint());
//        canvas.drawBitmap(bitmap, null, rect4, new Paint());
//        canvas.drawBitmap(bitmap, null, rect5, new Paint());
//        canvas.drawBitmap(bitmap, null, rect6, new Paint());
//        canvas.drawBitmap(bitmap, null, rect7, new Paint());
//        canvas.drawBitmap(bitmap, null, rect8, new Paint());
//        canvas.drawBitmap(bitmap, null, rect9, new Paint());
//        canvas.drawBitmap(bitmap, null, rect10, new Paint());
//        canvas.drawBitmap(bitmap, null, rect11, new Paint());
//        canvas.drawBitmap(bitmap, null, rect12, new Paint());
//        canvas.drawBitmap(bitmap, null, rect13, new Paint());
    }

    /**
     * 事件处理
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        float x = event.getX();
//        float y = event.getY();
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//        }

//        if (event.getAction() != MotionEvent.ACTION_UP) {
//            return true;
//        }
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//        manager.onTouch(x, y);
        return true;
    }
}
