package com.happylich.bridge.game.player;

import android.content.Context;
import android.util.Log;

/**
 * Created by lich on 2018/3/25.
 * 机器人类
 * 1. 叫牌
 * 2. 打牌
 */

public class Robot extends AbstractPlayer{

    /**
     * 构造函数
     * @param id
     */
    public Robot(Context context, int position) {
        this.context = context;
        this.position = position;
    }

    /**
     * 获得叫牌值
     * @return
     */
    @Override
    public boolean callCard() {
        // 机器人要叫牌，需要根据
        // 1. 手里的牌力(打牌点，控制张，止张，关键张）
        // 2. 叫牌历史

        // 首先实现

        // 现在只要根据之前的叫牌历史，叫大一号的就好
        int last = call.getLastCallCard();
        Log.v(this.getClass().getName(), "机器人要叫的牌是");
        Log.v(this.getClass().getName(), String.valueOf(last));
        switch(this.position) {
            case 0:
                call.setCallS(++last);
                break;
            case 1:
                call.setCallW(++last);
                break;
            case 2:
                call.setCallN(++last);
                break;
            case 3:
                call.setCallE(++last);
                break;
        }
        return true;
    }

    /**
     * 获得出牌值
     * @return
     */
    @Override
    public int dropCard() {
        return -1;
    }
}
