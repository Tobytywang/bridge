package com.happylich.bridge.game.player;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by lich on 2018/5/14.
 *
 * 这个类用于和远程玩家的通讯（Remote,Player)
 */

public class RemotePlayer extends AbstractPlayer {
    private boolean inOrder;
    private int state = -1;


    public RemotePlayer(Context context) {
    }


    /**
     * 玩家是否就绪
     * @return
     */
    @Override
    public boolean isInOrder() {
        return this.inOrder;
    }

    /**
     * 设置remoteplayer的状态
     * -1 没有任何代理的玩家 -1
     * 0 代理机器人玩家 mo
     * 1 代理主机（未就绪）zx
     * 2 代理主机（就绪）zo
     * 3 代理人类玩家（未就绪）px
     * 4 代理人类玩家（就绪）po
     * @param state
     */
    public void setState(int state) {
        this.state = state;
    }
    public int getState() {
        return this.state;
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
