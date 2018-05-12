package com.happylich.bridge.game.player;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import com.happylich.bridge.engine.util.Position;

/**
 * Created by wangt on 2018/3/22.
 */


/**
 * 这是反应棋盘上一个玩家状态的一个绘制类
 * 当这个类被远程玩家代理时如何处理？
 * 这个玩家的操作会被本地解析，同时也会被远程代理？
 * 如果一个Player被Remote代理了，应该会有标志表示这个类需要向远程传递消息
 */
public class Player extends AbstractPlayer {

    /**
     * 构造函数
     * @param context
     */
    public Player(Context context) {
        this.context = context;
        this.direction = -1;
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
     * 人类玩家的出牌值要调用触摸函数得到
     */
    @Override
    public boolean dropCard() {
        // 参考叫牌函数的实现
        return table.isFinish();
    }

}
