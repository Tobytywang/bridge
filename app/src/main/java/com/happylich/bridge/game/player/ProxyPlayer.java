package com.happylich.bridge.game.player;

/**
 * Created by lich on 2018/4/6.
 */

import android.content.Context;

/**
 * 使用情景：
 * 1. 主机玩家创建房间，主机玩家设置Player1
 * 2. 从机玩家加入房间，从机玩家与主机沟通，分别建立对对方的代理
 * 3. 第三个玩家加入房间，从机玩家与主机玩家沟通，设置对所有玩家的代理
 */
public class ProxyPlayer extends AbstractPlayer{

    /**
     * 构造函数
     * @param context
     * @param position
     */
    public ProxyPlayer(Context context, int position) {
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
