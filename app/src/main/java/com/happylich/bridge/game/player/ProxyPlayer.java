package com.happylich.bridge.game.player;

/**
 * Created by lich on 2018/4/6.
 */

import android.content.Context;

import java.util.ArrayList;

/**
 * 使用情景：
 * 1. 主机玩家创建房间，主机玩家设置Player1
 * 2. 从机玩家加入房间，从机玩家与主机沟通，分别建立对对方的代理
 * 3. 第三个玩家加入房间，从机玩家与主机玩家沟通，设置对所有玩家的代理
 *
 * proxy的引入在游戏创建时，可以在准备阶段动态的将proxy玩家的真正内容替换为robot或者远程玩家
 *
 * 被代理的玩家在引入之前不存在
 * 代理玩家需要在真正的玩家出现之前负责界面绘制
 */
public class ProxyPlayer extends AbstractPlayerWithDraw {

    // 被代理的玩家
    private AbstractPlayer realPlayer;

    /**
     * 构造函数
     * @param context
     */
    public ProxyPlayer(Context context) {
        this.context = context;
        this.direction = -1;
    }

    /**
     * 玩家是否就绪
     * @return
     */
    @Override
    public boolean isInOrder() {
        if (this.realPlayer != null) {
            return realPlayer.isInOrder();
        }
        return false;
    }

    /**
     * 设置真正的玩家
     * @param player
     */
    public void setRealPlayer(AbstractPlayer player) {
        // 这里的realplayer可以是本地的机器人，也可以是远程玩家（人或者机器人）
        // 本地的机器人的话，就建立一个本地的Robot对象
        // 远程的话，就通过消息代理，不建立对象了

        // 这里的设置realPlayer，特指new Robot的情况

        // 然后就是如何除了setPlayer的问题？
        // Proxy代理Robot的时候，应该从callCard获得叫牌值，而不是直接把权限给Robot
        // 对于不需要代理玩家的Player，callCard同样需要获得叫牌值，而不是代替执行？
        // 对于需要获得叫牌值的情况，如果计算时间太久或者通信时间太久，需要机制保证流畅度
        // 对于交代执行的情况，其实也需要

        // 玩家之所以保存call,是要获得call维持的叫牌历史等信息
        // Proxy和Robot都可以维持这个状态，但对于远程玩家来说，维持这个类有点困难
        // 但对于远程玩家来说，有本地RemotePlayer代指，可以维持这个类

        // Robot无论如何都是被代理的，被本机或者被远程的代理类代理
        // Player

        // 参照setGamePlayer
        // 是否要设置ready
        // player.setReady(this.ready);
        player.setCall(this.call);
        player.setTable(this.table);

        player.setDirection(this.direction);
        player.position = this.position;
        player.setCards(this.cards);
        player.setPlayerStage(this.playerStage);

        // ready.setPlayer(player);
        // call.setPlayer(player);
        // table.setPlayer(player);

        this.realPlayer = player;
    }

    /**
     * 获得realPlayer
     */
    public AbstractPlayer getRealPlayer() {
        if (realPlayer != null) {
            return realPlayer;
        }
        return (AbstractPlayer) new Object();
    }

    /**
     * 玩家初始化手牌
     */
    public void setCards(ArrayList<Integer> cards) {
        this.cards = cards;
        if (realPlayer != null) {
            realPlayer.setCards(cards);
        }
    }
    /**
     * 玩家出牌
     * @param cardNumber
     */
    public int removeCard(int cardNumber) {
        this.cards.remove(cardNumber);
        return this.realPlayer.cards.remove(cardNumber);
    }

    /**
     * 获得叫牌值
     */
    @Override
    public boolean callCard() {
        // getCall执行完，就进入下一个回合
        // 建立远程连接，从远程连接处获得
        // call.setFinish(false);
        return realPlayer.callCard();
    }

    /**
     * 获得出牌值
     */
    @Override
    public boolean dropCard() {
        return realPlayer.dropCard();
    }
}
