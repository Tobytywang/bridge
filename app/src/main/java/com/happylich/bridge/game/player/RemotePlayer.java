package com.happylich.bridge.game.player;

/**
 * Created by lich on 2018/5/14.
 *
 * 这个类用于和远程玩家的通讯（Remote,Player)
 */

public class RemotePlayer extends AbstractPlayer {


    // IP
    // 通过记录该客户端的IP进行通讯

    private boolean inOrder;

    /**
     * 玩家是否就绪
     * @return
     */
    @Override
    public boolean isInOrder() {
        // 对于人类玩家，需要手动设置ready状态
        return inOrder;
    }

    /**
     * 切换就绪状态
     * @param inOrder
     * @return
     */
    public void setInOrder(boolean inOrder) {
        this.inOrder = inOrder;
    }

    @Override
    public boolean callCard() {
        return false;
    }

    @Override
    public boolean dropCard() {
        return false;
    }

}
