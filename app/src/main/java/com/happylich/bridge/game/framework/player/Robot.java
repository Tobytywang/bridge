package com.happylich.bridge.game.framework.player;

import android.content.Context;

/**
 * Created by lich on 2018/3/25.
 */

public class Robot extends AbstractPlayer{

    /**
     * 构造函数
     * @param id
     * @param cards
     */
    public Robot(Context context, int id, int[] cards) {
        this.context = context;
        this.playerId = id;
        this.cards = cards;
    }
}
