package com.happylich.bridge.game;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by wangt on 2017/11/16.
 */

public abstract class AbstractPlayer {
    protected Game game;
    protected int playerId;
    protected boolean side;
    protected int[] cards;
    protected ArrayList<Integer> calls = new ArrayList<>();
    protected int selectCard = -1;

    protected int stage=0;// 0表示叫牌阶段，1表示出牌阶段，2表示结算阶段
    protected int top, left;
    protected Context context;

    protected AbstractPlayer last;
    protected AbstractPlayer next;

    // 构造函数
    public AbstractPlayer(int id, int[] cards, int left, int top, boolean side, Game game, Context context) {
        this.game = game;
        this.playerId = id;
        this.cards = cards;
        this.context = context;
        this.left = left;
        this.top = top;
        this.side = side;
    }

    public void setLastAndNext(AbstractPlayer last, AbstractPlayer next) {
        this.last = last;
        this.next = next;
    }

    // 屏幕绘制函数(抽象函数，由子类重写）
    public abstract void paint0(Canvas canvas);
    public abstract void paint1(Canvas canvas);
    public abstract void paint2(Canvas canvas);
    public abstract void paint3(Canvas canvas);
    public abstract void paint4(Canvas canvas);

    // 按键控制函数
    // TODO：选牌，重选
    public void onTouch(int x, int y) {
        Log.d("时间调优：", "Players.onTouch");
        for (int i=0; i<cards.length; i++) {
            if (i != cards.length - 1) {
                // 判定区域
                if (inRect(x, y, left+i*35, top,35, 160)) {
                    // 重绘
                    Log.d("坐标", String.valueOf(i));
                    selectCard = i;
                    break;
                } selectCard = -1;
            } else {
                // 判定区域
                if (inRect(x, y, left+i*35, top,120, 160)) {
                    // 重绘
                    selectCard = i;
                    break;
                } else {
                    selectCard = -1;
                }
            }
        }
    }

    /**
     * 按键辅助函数
     * @param x 触摸点x
     * @param y 触摸点y
     * @param rectX 有效区域X
     * @param rectY 有效区域Y
     * @param rectW 有效区域W
     * @param rectH 有效区域H
     */
    private boolean inRect(int x, int y, int rectX, int rectY, int rectW, int rectH) {
        if (x <= rectX || x >= rectX + rectW || y<=rectY || y >= rectY + rectH){
            return false;
        }
        return true;
    }
}
