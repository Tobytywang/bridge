package com.happylich.bridge.game.player;

/**
 * Created by lich on 2018/4/6.
 */

import android.content.Context;

/**
 *
 */
public class Remote extends AbstractPlayer{

    /**
     * 构造函数
     * @param context
     * @param position
     */
    public Remote(Context context, int position) {
        this.context = context;
        this.position = position;
    }

    /**
     * 获得叫牌值
     */
    @Override
    public boolean callCard() {
        // getCall执行完，就进入下一个回合
        // 建立远程连接，从远程连接处获得
//        call.setFinish(false);
        return call.isFinish();
    }

    /**
     * 获得出牌值
     * 人类玩家的出牌值要调用触摸函数得到
     */
    @Override
    public boolean dropCard() {
        // 参考叫牌函数的实现
        // 建立远程连接，从远程连接处获得
        return table.isFinish();
    }
}
