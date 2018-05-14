package com.happylich.bridge.game.player;

/**
 * Created by lich on 2018/5/14.
 *
 * 这个类用于和远程玩家的通讯（Remote,Player)
 */

public class RemotePlayer extends AbstractPlayer {


    // IP
    // 通过记录该客户端的IP进行通讯


    @Override
    public boolean callCard() {
        return false;
    }

    @Override
    public boolean dropCard() {
        return false;
    }

}
