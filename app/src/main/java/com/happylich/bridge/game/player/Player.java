package com.happylich.bridge.game.player;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import com.happylich.bridge.engine.util.Position;

import java.net.Socket;

/**
 * Created by wangt on 2018/3/22.
 */


/**
 * 这是反应棋盘上一个玩家状态的一个绘制类
 * 当这个类被远程玩家代理时如何处理？
 * 这个玩家的操作会被本地解析，同时也会被远程代理？
 * 如果一个Player被Remote代理了，应该会有标志表示这个类需要向远程传递消息
 *
 * 这个类是被代理的一个类，代表一个远程玩家
 * 所有的操作都要借用socket实现
 */
public class Player extends AbstractPlayerWithDraw {

    private boolean inOrder;

    /**
     * 构造函数
     * @param context
     */
    public Player(Context context) {
        this.context = context;
        this.direction = -1;
    }

    /**
     * 玩家是否就绪
     * @return
     */
    @Override
    public boolean isInOrder() {
        if (inOrder &&
            (this.direction == 0 || this.direction == 1 || this.direction == 2 || this.direction == 3)) {
            return true;
        }
        return false;
    }

    /**
     * 切换就绪状态
     * @param inOrder
     * @return
     */
    public void setInOrder(boolean inOrder) {
        this.inOrder = inOrder;
    }

    /**
     * 获得叫牌值
     */
    @Override
    public boolean callCard() {
        return call.isFinish();
    }

    /**
     * 获得出牌值
     */
    @Override
    public boolean dropCard() {
        return table.isFinish();
    }



}
