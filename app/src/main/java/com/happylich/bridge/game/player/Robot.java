package com.happylich.bridge.game.player;

import android.content.Context;
import android.util.Log;

import java.util.Random;

/**
 * Created by lich on 2018/3/25.
 * 同样的机器人，根据初始化的时候位置的不同，担任不同的角色
 * 1. 叫牌
 * 2. 打牌
 */

public class Robot extends AbstractPlayer{

    /**c
     * 构造函数
     * @param context
     */
    public Robot(Context context) {
        this.context = context;
        this.direction = -1;
    }

    /**
     * 获得叫牌值
     * TODO:这个方法至少要运行3秒钟！
     * TODO:方法结束后至少要停顿2秒钟！
     * @return
     */
    @Override
    public boolean callCard() {

        // 机器人叫牌后，改变标志位，将叫牌权利转移给下一个玩家
        // 机器人要叫牌，需要根据
        // 1. 手里的牌力(打牌点，控制张，止张，关键张）
        // 2. 叫牌历史
        // 3. 对队友的叫牌的响应和对对手干扰的排除
        int last = call.getLastCallCard();
        // TODO:机器人叫牌是根据自己的direction进行判断的，是否有缺陷？
        switch(this.position) {
            case 0:
                if (call.isFinish()) {
                    call.finish();
                } else if (call.getCallHistory().size() >= 1 &&
                        call.getCallHistory().get(call.getCallHistory().size() - 1) == 35) {
                    call.setCall(0, 35);
                } else {
                    int des = new Random().nextInt(4);
                    if ((last + des + 1) >=35 ) {
                        call.setCall(0, 35);
                    } else {
                        call.setCall(0, last + des + 1);
                    }
                }
                break;
            case 1:
                if (call.isFinish()) {
                    call.finish();
                } else if(call.getCallHistory().size() >= 1 &&
                        call.getCallHistory().get(call.getCallHistory().size() - 1) == 35) {
                    call.setCall(1, 35);
                } else {
                    int des = new Random().nextInt(4);
                    if ((last + des + 1) >=35 ) {
                        call.setCall(1, 35);
                    } else {
                        call.setCall(1, last + des + 1);
                    }
                }
                break;
            case 2:
                if (call.isFinish()) {
                    call.finish();
                } else if(call.getCallHistory().size() >= 1 &&
                        call.getCallHistory().get(call.getCallHistory().size() - 1) == 35) {
                    call.setCall(2, 35);
                } else {
                    int des = new Random().nextInt(4);
                    if ((last + des + 1) >=35 ) {
                        call.setCall(2, 35);
                    } else {
                        call.setCall(2, last + des + 1);
                    }
                }
                break;
            case 3:
                if (call.isFinish()) {
                    call.finish();
                } else if(call.getCallHistory().size() >= 1 &&
                        call.getCallHistory().get(call.getCallHistory().size() - 1) == 35) {
                    call.setCall(3, 35);
                } else {
                    int des = new Random().nextInt(4);
                    if ((last + des + 1) >=35 ) {
                        call.setCall(3, 35);
                    } else {
                        call.setCall(3, last + des + 1);
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 获得出牌值
     * @return
     */
    @Override
    public boolean dropCard() {
        switch(position) {
            case 0:
                if (table.isFinish()) {
                    table.finish();
                } else if (cards.size() > 0) {
                    table.setDrop(0, cards.remove(0));
                } else if (cards.size() <= 0) {
                    table.finish();
                }
                break;
            case 1:
                if (table.isFinish()) {
                    table.finish();
                } else if (cards.size() > 0) {
                    table.setDrop(1, cards.remove(0));
                } else if (cards.size() <= 0) {
                    table.finish();
                }
                break;
            case 2:
                if (table.isFinish()) {
                    table.finish();
                } else if (cards.size() > 0) {
                    table.setDrop(2, cards.remove(0));
                } else if (cards.size() <= 0) {
                    table.finish();
                }
                break;
            case 3:
                if (table.isFinish()) {
                    table.finish();
                } else if (cards.size() > 0) {
                    table.setDrop(3, cards.remove(0));
                } else if (cards.size() <= 0) {
                    table.finish();
                }
                break;
        }
        return true;
    }
}
