package com.happylich.bridge.game.player;

import android.content.Context;
import android.util.Log;

/**
 * Created by wangt on 2018/3/22.
 * 这是反应棋盘上一个玩家状态的一个绘制类
 */

public class Player extends AbstractPlayer {

    /**
     * 构造函数
     * @param context
     * @param position
     */
    public Player(Context context, int position) {
        this.context = context;
        this.position = position;
    }

    /**
     * 获得叫牌值
     */
    @Override
    public boolean callCard() {
        // getCall执行完，就进入下一个回合
//        call.setFinish(false);
        return call.isFinish();
    }

    /**
     * 获得出牌值
     */
    @Override
    public int dropCard() {
        return -1;
    }

}
