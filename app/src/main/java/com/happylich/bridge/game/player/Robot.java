package com.happylich.bridge.game.player;

import android.content.Context;
import android.util.Log;

/**
 * Created by lich on 2018/3/25.
 * 同样的机器人，根据初始化的时候位置的不同，担任不同的角色
 * 1. 叫牌
 * 2. 打牌
 */

public class Robot extends AbstractPlayer{

    /**
     * 构造函数
     * @param context
     * @param position
     */
    public Robot(Context context, int position) {
        this.context = context;
        this.position = position;
    }

    /**
     * 获得叫牌值
     * TODO:这个方法至少要运行3秒钟！
     * TODO:方法结束后至少要停顿2秒钟！
     * @return
     */
    @Override
    public boolean callCard() {

        // 机器人要叫牌，需要根据
        // 1. 手里的牌力(打牌点，控制张，止张，关键张）
        // 2. 叫牌历史
        // 3. 对队友的叫牌的响应和对对手干扰的排除

        // 首先实现

        // 现在只要根据之前的叫牌历史，叫大一号的就好
        int last = call.getLastCallCard();
        Log.v(this.getClass().getName(), "机器人要叫的牌是");
        Log.v(this.getClass().getName(), String.valueOf(last));
        switch(this.position) {
            case 0:
                if (call.isFinish()) {
                    call.finish();
                } else if (call.getCallHistory().size() >= 1 &&
                        call.getCallHistory().get(call.getCallHistory().size() - 1) == 35) {
                    call.setCall(0, 35);
                } else {
                    call.setCall(0, ++last);
                }
                break;
            case 1:
                if (call.isFinish()) {
                    call.finish();
                } else if(call.getCallHistory().size() >= 1 &&
                        call.getCallHistory().get(call.getCallHistory().size() - 1) == 35) {
                    call.setCall(1, 35);
                } else {
                    call.setCall(1, ++last);
                }
                break;
            case 2:
                if (call.isFinish()) {
                    call.finish();
                } else if(call.getCallHistory().size() >= 1 &&
                        call.getCallHistory().get(call.getCallHistory().size() - 1) == 35) {
                    call.setCall(2, 35);
                } else {
                    call.setCall(2, ++last);
                }
                break;
            case 3:
                if (call.isFinish()) {
                    call.finish();
                } else if(call.getCallHistory().size() >= 1 &&
                        call.getCallHistory().get(call.getCallHistory().size() - 1) == 35) {
                    call.setCall(3, 35);
                } else {
                    call.setCall(3, ++last);
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
    public int dropCard() {
        return -1;
    }
}
