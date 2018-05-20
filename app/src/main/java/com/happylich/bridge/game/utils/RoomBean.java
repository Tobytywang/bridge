package com.happylich.bridge.game.utils;

/**
 * Created by lich on 2018/5/18.
 */

/**
 * 房间类
 * 1. 房间的IP
 * 2. 房间的状态
 */
public class RoomBean {
    private String mIP;
    private String mState;

    public void setIP(String ip) {
        this.mIP = ip;
    }

    public String getIP() {
        return mIP;
    }

    public void setState(String state) {
//        if (state == "0") {
//            this.mState = "等待加入";
//        } else if (state == "1") {
//            this.mState = "房间已满";
//        } else if (state == "2") {
//            this.mState = "正在游戏";
//        }
        this.mState = state;
    }

    public String getState() {
        return this.mState;
    }
}
