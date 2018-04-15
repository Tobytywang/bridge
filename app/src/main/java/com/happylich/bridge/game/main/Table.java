package com.happylich.bridge.game.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;

/**
 * Created by lich on 2018/4/10.
 * 负责牌桌的绘制和结算
 */

public class Table {
    private Context context;

    // 叫牌阶段标志位
    private int dropStage;

    // 叫牌阶段完成
    private boolean finish;

    // 绘制尺寸
    private int width, height;
    private int left, top;

    // 庄家
    private int dealer = -1;
    // 领牌人（本轮首次出牌的）
    private int lead = -1;
    // 定约阶
    private int level = -1;
    private int suits = -1;

    private int cardS;
    private int cardW;
    private int cardN;
    private int cardE;

//    private ArrayList<Integer> callHistory  = new ArrayList<>();
//    private ArrayList<Integer> callHistoryN = new ArrayList<>();
//    private ArrayList<Integer> callHistoryE = new ArrayList<>();
//    private ArrayList<Integer> callHistoryS = new ArrayList<>();
//    private ArrayList<Integer> callHistoryW = new ArrayList<>();

    /**
     * 构造函数
     * @param context
     */
    public Table(Context context) {
        this.context = context;
    }

    /**
     * 结束标志
     */
    public boolean isFinish() {
        return false;
    }

    /**
     * 设置调整值
     * @param modifier
     */
    public void setModifier(int modifier) {
        if (modifier == 0) {
            this.left = this.left;
        } else if (modifier == 1) {
            this.left = this.left - 360;
        } else if (modifier == 2) {
            this.left = this.left + 360;
        }
    }

    /**
     * 设置绘图基准点
     * @param position
     */
    public void setPosition(int[] position) {
        this.left = position[0];
        this.top = position[1];
    }

    /**
     * 设置宽高
     */
    public void setWidthHeight(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 设置callstage
     * @param stage
     */
    public void setCallStage(int stage) {
        this.dropStage = stage;
    }

    /**
     * 南家出牌
     * @param card
     */
    public void dropCardS(int card) {
        this.cardS = card;
    }

    /**
     * 西家出牌
     * @param card
     */
    public void dropCardW(int card) {
        this.cardW = card;
    }

    /**
     * 北家出牌
     * @param card
     */
    public void dropCardN(int card) {
        this.cardN = card;
    }

    /**
     * 东家出牌
     * @param card
     */
    public void dropCardE(int card) {
        this.cardE = card;
    }

    /**
     * 处理触摸事件
     * @param x
     * @param y
     */
    public void onTouch(int x, int y) {

    }

    /**
     * 绘制图形界面
     * @param canvas
     */
    public void draw(Canvas canvas) {
        // TODO:do nothing
        Bitmap Image;
        Rect des = new Rect();
        Paint paint = new Paint();

        int left = this.left;
        int top = this.top;

        // 绘制底版
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        RectF round = new RectF();
        round.left = left;
        round.right = left + 720;
        round.top = top + 360 + 180;
        // 1144 = 134*7 + 8*6 + 134 + 8*3
        round.bottom = top + 360 + 720 + 180;
        canvas.drawRoundRect(round, 20, 20, paint);
    }
}
