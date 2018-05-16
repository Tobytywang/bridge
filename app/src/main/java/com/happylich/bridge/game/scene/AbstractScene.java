package com.happylich.bridge.game.scene;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.happylich.bridge.game.main.Game;
import com.happylich.bridge.game.player.AbstractPlayer;
import com.happylich.bridge.game.player.AbstractPlayerWithDraw;

/**
 * Created by lich on 2018/4/24.
 */

public abstract class AbstractScene {
    protected Context context;

    // 绘制尺寸
    protected int width, height;
    protected int left, top;

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


    // 这样当设置玩家的时候，可以保持一致
    protected Game game;

    // 既然所有的局限都需要玩家
    protected AbstractPlayerWithDraw playerLeft;
    protected AbstractPlayerWithDraw playerRight;
    protected AbstractPlayerWithDraw playerTop;
    protected AbstractPlayerWithDraw playerBottom;

    /**
     * 设置game引用
     * @param game
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * 通用的玩家设置函数
     * 需要设置上下左右和东南西北
     * @param player
     */
    public void setPlayer(AbstractPlayerWithDraw player) {
        if (player.position == 0) {
            playerBottom = player;
        } else if (player.position == 1) {
            playerLeft = player;
        } else if (player.position == 2) {
            playerTop = player;
        } else if (player.position == 3) {
            playerRight = player;
        }
    }

    abstract public int onTouch(int x, int y);

    abstract public void draw(Canvas canvas, Paint paint, Rect rect);
}
