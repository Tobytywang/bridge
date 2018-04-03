package com.happylich.bridge.game.framework.player;

import android.content.Context;

/**
 * Created by wangt on 2018/3/22.
 * 这是反应棋盘上一个玩家状态的一个绘制类
 */

public class Player extends AbstractPlayer {

    /**
     * 构造函数
     * @param id
     * @param cards
     */
    public Player(Context context, int id, int[] cards) {
        this.context = context;
        this.playerId = id;
        this.cards = cards;
    }
}
